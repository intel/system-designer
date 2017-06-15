/*
 * Copyright (C) 2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */

package com.intel.audioviz.trace.waveform;

import java.security.InvalidParameterException;

import com.intel.audioviz.trace.WaveformWindow;

/**
 * A WaveformWindowCache is a collection of pre computed WaveformWindow.
 */
public class WaveformWindowCache {

    /**
     * @param capacity the capacity of the WaveformWindow cache
     * @param windowSize the size of WaveformWindow which will be cached
     */
    public WaveformWindowCache(final int capacity, final long windowSize) {
        mCache = new WaveformWindow[capacity];
        mWindowSize = windowSize;
        mInitializedSize = 0;
    }

    /**
     * @return the size of WaveformWindow which are cached
     */
    public long getWindowSize() {
        return mWindowSize;
    }

    /**
     * @return the number of WaveformWindow which are cached.
     * @see getCache()
     */
    public int getCacheSize() {
        return mInitializedSize;
    }

    /**
     * @return the capacity of the cache which is the maximum number of WaveformWindow
     * which can be cached.
     */
    public int getCacheCapacity() {
        return mCache.length;
    }

    /**
     * Add a WaveformWindow to the cache. If the cache is full, an IndexOutOfBoundsException
     * is raised.
     * @param WaveformWindow
     */
    public void add(final WaveformWindow waveformWindow) {
        if (mInitializedSize == mCache.length) {
            throw new IndexOutOfBoundsException();
        }

        mCache[mInitializedSize++] = waveformWindow;
    }

    /**
     * Compute a WaveformWindow array for a given audio sample count per window using cache data.
     * @param sampleIndex The audio sample index of the first audio sample
     * @param targetWindowSize The requested number of audio sample per window
     * @param waveformWindowCount The number of requested windows
     * @return the request WaveformWindow array
     */
    public WaveformWindow[] getFromCache(final long sampleIndex, final long targetWindowSize,
            final int waveformWindowCount) {
        if (sampleIndex < 0) {
            throw new InvalidParameterException("Invalid index");
        }
        if (waveformWindowCount < 0) {
            throw new InvalidParameterException("Invalid count");
        }
        if (targetWindowSize < getWindowSize()) {
            throw new InvalidParameterException("Invalid target window size");
        }

        final int deltaSize = (int) (targetWindowSize / getWindowSize());
        final int cacheIndex = (int) (sampleIndex / getWindowSize());
        final int requiredCacheSize = cacheIndex + waveformWindowCount * deltaSize;

        int safeWaveformWindowCount = waveformWindowCount;
        if (requiredCacheSize > getCacheSize()) {
            safeWaveformWindowCount = Math.max(getCacheSize() - cacheIndex, 0) / deltaSize;
        }

        final WaveformWindow[] waveformWindows = new WaveformWindow[safeWaveformWindowCount];
        for (int i = 0; i < safeWaveformWindowCount; i++) {
            waveformWindows[i] = new WaveformWindow(
                    mCache,
                    cacheIndex + i * deltaSize,
                    deltaSize);
        }
        return waveformWindows;
    }

    private final WaveformWindow[] mCache;
    private final long mWindowSize;
    private int mInitializedSize;
}