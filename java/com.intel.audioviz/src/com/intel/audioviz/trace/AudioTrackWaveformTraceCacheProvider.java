/*
 * Copyright (C) 2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */

package com.intel.audioviz.trace;

import java.security.InvalidParameterException;

import com.intel.audioviz.AudioTrack;
import com.intel.audioviz.ISampleProvider;
import com.intel.audioviz.trace.waveform.WaveformWindowCache;
import com.intel.audioviz.trace.waveform.WaveformWindowCacheDSP;

/**
 * An AudioTrackWaveformTraceCacheProvider setup and holds pre-computed WaveformWindow for each channel of an
 * AudioTrack.
 */
public class AudioTrackWaveformTraceCacheProvider {

    /**
     * Instantiate a cache for an AudioTrack. One cache will be set up for each channel.
     * Each cache may have multiple level of pre computed data.
     * Each cache will be initialized with a dedicated thread to speed up cache initialization
     * procedure which may takes a lot of time for long AudioTrack.
     * @param audioTrack The audio track
     * @note The init() method must be called to initialize the cache
     */
    public AudioTrackWaveformTraceCacheProvider(final AudioTrack audioTrack) {
        /* CACHE_WAVEFORM_WINDOW_RECURSIVE_SIZE must be a power of two */
        assert Integer.bitCount(
                CACHE_WAVEFORM_WINDOW_RECURSIVE_SIZE) == 1 : "CACHE_WAVEFORM_WINDOW_RECURSIVE_SIZE not a power of 2";

        mAudioTrack = audioTrack;
        mCacheInitThreads = null;
        mCacheInitMonitorThread = null;
        mIsCacheInitialized = false;

        mCache = new WaveformWindowCache[mAudioTrack.getAudioFormat().getChannelCount()][];
        /* How much cache levels are needed per channel for the AudioTrack ? */
        int cacheLevelCount = 0;
        long levelLenght = mAudioTrack.getSampleProvider().getSamplesPerChannel();

        while (levelLenght / CACHE_WAVEFORM_WINDOW_RECURSIVE_SIZE > 0) {
            cacheLevelCount++;
            levelLenght /= CACHE_WAVEFORM_WINDOW_RECURSIVE_SIZE;
        }

        for (int channel = 0; channel < mAudioTrack.getAudioFormat().getChannelCount(); channel++) {
            /* Allocate caches */
            mCache[channel] = new WaveformWindowCache[cacheLevelCount];
            levelLenght = mAudioTrack.getSampleProvider().getSamplesPerChannel();

            for (int i = 0, levelWindowSize = 1; i < cacheLevelCount; i++) {
                levelLenght = alignToPowerOfTwo(levelLenght, CACHE_WAVEFORM_WINDOW_RECURSIVE_SIZE);
                levelLenght /= CACHE_WAVEFORM_WINDOW_RECURSIVE_SIZE;
                levelWindowSize *= CACHE_WAVEFORM_WINDOW_RECURSIVE_SIZE;

                /* Cannot instantiate a cache Array with more than Integer.MAX_VALUE elements.
                 * This will occur if channel size divided by CACHE_WAVEFORM_WINDOW_RECURSIVE_SIZE
                 * is greater than Integer.MAX_VALUE.
                 * For instance, with CACHE_WAVEFORM_WINDOW_RECURSIVE_SIZE == 1024, maximum channel
                 * size is 2TB.
                 */
                if (levelLenght > Integer.MAX_VALUE) {
                    throw new IndexOutOfBoundsException("Too long audio track");
                }
                final int cacheSize = (int) levelLenght;

                mCache[channel][i] = new WaveformWindowCache(cacheSize, levelWindowSize);
            }
        }
    }

    /**
     * Get the Waveform cache for a given channel having the highest audio sample count per
     * window according to the audio sample count per window requested.
     * @param channel the audio channel the requested cache shall belong to
     * @param audioSamplePerWindow the maximum audio sample count per window the requested cache
     * shall have.
     * @return a WaveformWindowCache matching requested criteria, or null if no cache matches.
     */
    public WaveformWindowCache getWaveformCache(final int channel, final long audioSamplePerWindow) {
        if (channel < 0 || channel > mCache.length) {
            throw new InvalidParameterException("Invalid channel number");
        }
        if (audioSamplePerWindow < 0 || Long.bitCount(audioSamplePerWindow) != 1) {
            throw new InvalidParameterException("Invalid audio sample count: must be a power of two");
        }

        final WaveformWindowCache[] cacheLevels = mCache[channel];

        int level = cacheLevels.length - 1;
        while (level >= 0 && cacheLevels[level].getWindowSize() > audioSamplePerWindow) {
            level--;
        }
        if (level >= 0) {
            return cacheLevels[level];
        } else {
            return null;
        }
    }

    /**
     * Dispose the cache. If the cache is being initializing itself, initialization threads are
     * canceled.
     */
    public synchronized void dispose() {
        if (mCacheInitThreads != null) {
            assert (mCacheInitMonitorThread != null);

            /* Stop each init thread */
            for (final Thread thread : mCacheInitThreads) {
                thread.interrupt();
            }
            /* Wait for init monitor thread to be completed */
            try {
                mCacheInitMonitorThread.join();
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Start the cache initialization. The initialization may take a lot of time and is handle in dedicated threads.
     */
    public synchronized void init() {
        if (!mIsCacheInitialized && mCacheInitThreads == null) {
            assert (mCacheInitMonitorThread != null);

            final int nbChannel = mAudioTrack.getAudioFormat().getChannelCount();
            mCacheInitThreads = new Thread[nbChannel];
            // One thread per channel
            for (int i = 0; i < nbChannel; i++) {
                final int channel = i;
                mCacheInitThreads[i] = new Thread(() -> {
                    initializeChannelCacheLevels(channel);
                });
                mCacheInitThreads[i].setPriority(Thread.MIN_PRIORITY);
                mCacheInitThreads[i].start();
            }
            /* Start a monitor thread to signal end of cache initialization */
            mCacheInitMonitorThread = new Thread(() -> {

                for (int i = 0; i < nbChannel; i++) {
                    try {
                        mCacheInitThreads[i].join();
                    } catch (final InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                mIsCacheInitialized = true;
            });
            mCacheInitMonitorThread.start();
        }
    }

    private void initializeChannelCacheLevels(final int channel) {
        if (channel < 0 || channel > mCache.length) {
            throw new InvalidParameterException("Invalid channel number");
        }

        final WaveformWindowCache[] cacheLevels = mCache[channel];
        final WaveformWindowCacheDSP[] cacheLevelComputers = new WaveformWindowCacheDSP[cacheLevels.length];

        /* Initialize each level computer */
        for (int level = 0; level < cacheLevels.length; level++) {
            cacheLevelComputers[level] = new WaveformWindowCacheDSP(cacheLevels[level].getWindowSize());
        }

        final ISampleProvider sampleProvider = mAudioTrack.getSampleProvider();
        /**
         * Loop over the entire track's channel and feed each cache level sample by samples.
         *
         * This loop will consume a lot of CPU time. Whatever the origin of audio data is, some optimizations are
         * required to speed it.
         *
         * Let's consider the case of a RawFileAudioTrack for instance. The maximum theoretical speed depends on the
         * file device speed. In order to stuck as much as possible to this speed, few optimizations shall be in place.
         *
         * While all channel thread are looping over the entire audio data, each page of the memory mapped file will be
         * *at least* loaded one time to physical memory .
         *
         * Keep in mind that each page loaded from file to physical memory will be used by all channel threads since
         * audio channels are interleaved.
         *
         * In order to avoid any risk of having a page loaded more than a single time, all threads shall wait for
         * themselves regularly. This would avoid any risk to have one channel thread being late and reloading pages
         * which were previously loaded by another thread but trashed by kernel since.
         *
         * This way, the more advanced thread in the loop produces the page miss, and following channel thread(s)
         * benefit(s) from it having each a page hit.
         *
         * Such rendez-vous would also reduce the minimal physical memory required to loop over the entire audio data,
         * limiting the risk to make the kernel swaps memory to page file.
         *
         * Avoiding situation in which no more than 8MB or 16MB separates threads would decrease the risk of having a
         * lot of page miss.
         *
         * Having a much more close distance would provide another similar advantage thanks to CPU cache. While the more
         * advanced thread produces CPU cache miss, the subsequent thread(s) may benefit(s) of CPU cache hit if they are
         * not too far. The idea is not to avoid L1 DATA cache miss, but it could be possible to avoid most of L3 cache
         * miss and maybe some L2 cache miss as well.
         *
         * Avoiding situation in which no more than 256KB or 512KB separates threads would decrease the risk of having a
         * lot of CPU cache miss.
         *
         * On the other hand, the closest the distance is, the more the overhead of the synchronization mechanism will
         * decrease the speed of this loop.
         *
         * @todo Implement the synchronization mechanism and bench mark result with different distances.
         */
        for (long index = 0; index < sampleProvider.getSamplesPerChannel(); index++) {
            final float sample = sampleProvider.getSampleAsFloat(channel, index);

            for (int level = 0; level < cacheLevels.length; level++) {
                final WaveformWindow sampleGroup = cacheLevelComputers[level].push(sample);
                if (sampleGroup != null) {
                    cacheLevels[level].add(sampleGroup);
                }
            }

            /** @todo It's a shame to do this test at each audio sample iteration... */
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
        }
        /* Flush each level for last element (happens when samples number is not
         * a multiple of threshold) */
        for (int level = 0; level < cacheLevels.length; level++) {
            final WaveformWindow sampleGroup = cacheLevelComputers[level].flush();
            if (sampleGroup != null) {
                cacheLevels[level].add(sampleGroup);
            }
        }
    }

    private long alignToPowerOfTwo(final long number, final int powerOfTwoNumber) {
        // Design of the class shall avoid such a situation
        assert powerOfTwoNumber >= 1 && Integer.bitCount(powerOfTwoNumber) == 1 : "Not a power of two";

        return (number + powerOfTwoNumber - 1L) & ~(powerOfTwoNumber - 1L);
    }

    private final WaveformWindowCache[][] mCache;
    private final AudioTrack mAudioTrack;
    private boolean mIsCacheInitialized;
    private Thread mCacheInitMonitorThread;
    private Thread[] mCacheInitThreads;
    /**
     * Must be a power of two.
     */
    private static final int CACHE_WAVEFORM_WINDOW_RECURSIVE_SIZE = 512;
}
