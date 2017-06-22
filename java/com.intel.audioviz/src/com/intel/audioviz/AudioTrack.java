/*
 * Copyright (C) 2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */

package com.intel.audioviz;

import java.lang.ref.WeakReference;
import java.security.InvalidParameterException;
import java.util.HashSet;

import com.intel.audioviz.trace.AudioTrace;
import com.intel.audioviz.trace.AudioTrackWaveformTraceCacheProvider;
import com.intel.audioviz.trace.AudioWaveformTrace;

/**
 * An AudioTrack is Audio Data having a given AudioFormat and providing access
 * to Audio data through a SampleProvider.
 */
public abstract class AudioTrack {

    /**
     * @param audioFormat The Audio Format of the Audio Track.
     */
    public AudioTrack(final AudioFormat audioFormat) {
        mAudioFormat = audioFormat;
        mAudioTrackTraceCacheWeakReference = null;
        mAudioTraces = new HashSet<WeakReference<AudioTrace>>();
        mIsDisposed = false;
    }

    /**
     * @return The AudioFormat of the AudioTrack
     */
    public AudioFormat getAudioFormat() {
        return mAudioFormat;
    }

    /**
     * @param channel The channel number the Waveform Trace is about
     * @return The Waveform Trace
     */
    public synchronized AudioWaveformTrace getAudioWaveformTrace(final int channel) {
        if (channel < 0 || channel >= mAudioFormat.getChannelCount()) {
            throw new InvalidParameterException("Invalid channel number");
        }
        if (mIsDisposed) {
            throw new AudioVizException("AudioTrack is disposed");
        }
        final AudioWaveformTrace audioWaveFormTrace = new AudioWaveformTrace(this, channel);
        mAudioTraces.add(new WeakReference<AudioTrace>(audioWaveFormTrace));
        return audioWaveFormTrace;
    }

    /**
     * @return The AudioTrackTraceCache
     */
    public synchronized AudioTrackWaveformTraceCacheProvider getAudioTrackTraceCache() {
        if (mIsDisposed) {
            throw new AudioVizException("AudioTrack is disposed");
        }
        AudioTrackWaveformTraceCacheProvider audioTrackTraceCache =
                mAudioTrackTraceCacheWeakReference == null ? null : mAudioTrackTraceCacheWeakReference.get();

        if (audioTrackTraceCache == null) {
            audioTrackTraceCache = new AudioTrackWaveformTraceCacheProvider(this);
            /* Start cache initialization */
            audioTrackTraceCache.init();

            mAudioTrackTraceCacheWeakReference =
                    new WeakReference<AudioTrackWaveformTraceCacheProvider>(audioTrackTraceCache);
        }
        return audioTrackTraceCache;
    }

    /**
     * Dispose AudioTrack resources.
     */
    public synchronized void dispose() {
        if (!mIsDisposed) {
            for (final WeakReference<AudioTrace> audioTraceRef : mAudioTraces) {
                final AudioTrace audioTrace = audioTraceRef.get();
                if (audioTrace != null) {
                    audioTrace.dispose();
                }
            }
            final AudioTrackWaveformTraceCacheProvider audioTrackTraceCache =
                    mAudioTrackTraceCacheWeakReference == null ? null : mAudioTrackTraceCacheWeakReference.get();
            if (audioTrackTraceCache != null) {
                audioTrackTraceCache.dispose();
            }
            mIsDisposed = true;
        }
    }

    /**
     * @return The SampleProvider of the AudioTrack which gives access to the track's audio
     * samples.
     */
    public abstract ISampleProvider getSampleProvider();

    private boolean mIsDisposed;
    private final AudioFormat mAudioFormat;
    private final HashSet<WeakReference<AudioTrace>> mAudioTraces;
    private WeakReference<AudioTrackWaveformTraceCacheProvider> mAudioTrackTraceCacheWeakReference;
}
