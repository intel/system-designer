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
 * A WaveformWindowCacheDSP generates WaveformWindow while processing audio sample.
 */
public class WaveformWindowCacheDSP {

    /**
     * @param waveformWindowSize The size of WaveformWindow this WaveformWindowCacheDSP will
     * produce. In other words, the number of audio sample required to generate a WaveformWindow.
     */
    public WaveformWindowCacheDSP(final long waveformWindowSize) {
        if (waveformWindowSize < 2L) {
            throw new InvalidParameterException("Window size must be greater than two");
        }

        mWaveformWindowSize = waveformWindowSize;
        reset();
    }

    /**
     * Process an audio sample.
     *
     * @param sample
     *            The current audio sample to be processed
     * @return if enough audio sample has been accumulated, return a new WaveformWindow, null otherwise
     */
    public WaveformWindow push(final float sample) {
        WaveformWindow newWaveformWindow = null;

        // Compute min and max
        mMin = Math.min(mMin, sample);
        mMax = Math.max(mMax, sample);

        if (++mComputedSampleCount >= mWaveformWindowSize) {
            newWaveformWindow = new WaveformWindow(mMin, mMax);
            reset();
        }

        return newWaveformWindow;
    }

    /**
     * Flush the processing. This method shall be called after the last audio sample has just been
     * processed.
     * @return a WaveformWindow generated with remaining audio samples, or null if no audio sample
     * remains.
     */
    public WaveformWindow flush() {
        WaveformWindow newWaveformWindow = null;

        if (mComputedSampleCount >= 1L) {
            /* Depending on track size, we may not have enough samples to complete the
             * last window. Anyway, we cannot remove the last sample(s) from the cache
             * and must take them into consideration.
             */
            newWaveformWindow = new WaveformWindow(mMin, mMax);
            reset();
        }

        return newWaveformWindow;
    }

    private void reset() {
        mMin = 2.0f;
        mMax = -2.0f;
        mComputedSampleCount = 0L;
    }

    private float mMin;
    private float mMax;
    private long mComputedSampleCount;
    private final long mWaveformWindowSize;
}
