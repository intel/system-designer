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

import com.intel.audioviz.ISampleProvider;

/**
 * A WaveformWindow holds statistics for a consecutive number of Audio Sample such as the
 * minimum and maximum.
 */
public class WaveformWindow {
    /**
     * @param min The minimum sample value within the window
     * @param max The maximum sample value within the window
     * sample which contribute to this window
     */
    public WaveformWindow(final float min, final float max) {
        mMin = min;
        mMax = max;
    }

    /**
     * Instantiate a WaveformWindow computing statistic from an AudioTrack's channel
     *
     * @param sampleProvider
     *            The AudioTrack's ISampleProvider
     * @param channel
     *            The AudioTracks's channel number
     * @param index
     *            The index of Audio Sample from which the window starts
     * @param size
     *            The size of the window in Audio Sample
     */
    public WaveformWindow(final ISampleProvider sampleProvider, final int channel, final long index, final long size) {
        if (channel < 0) {
            throw new InvalidParameterException("Invalid channel number");
        }
        if (index < 0) {
            throw new IndexOutOfBoundsException("Negative index");
        }
        if (size < 2) {
            throw new InvalidParameterException("Invalid size");
        }

        final float firstSample = sampleProvider.getSampleAsFloat(channel, index);
        mMin = firstSample;
        mMax = firstSample;

        for (long i = index + 1; i < index + size; i++) {
            final float sampleValue = sampleProvider.getSampleAsFloat(channel, i);

            mMin = Math.min(mMin, sampleValue);
            mMax = Math.max(mMax, sampleValue);
        }
    }

    /**
     * Instantiate a WaveformWindow which compute statistics of an array of WaveformWindow
     * @param waveformWindow The array of WaveformWindow to be computed
     */
    public WaveformWindow(final WaveformWindow[] waveformWindow) {
        this(waveformWindow, 0, waveformWindow.length);
    }

    /**
     * Instantiate a WaveformWindow which compute statistics of a part of an array of
     * WaveformWindow
     * @param waveformWindow The array of WaveformWindow to be computed
     * @param index The index in the array from where the window starts
     * @param size The size of the window in WaveformWindow array elements
     */
    public WaveformWindow(final WaveformWindow[] waveformWindow, final int index, final long size) {
        if (size < 1) {
            throw new InvalidParameterException("Size must be non null positive");
        }
        if (index < 0) {
            throw new InvalidParameterException("Negative index");
        }
        if (index + size > waveformWindow.length) {
            throw new InvalidParameterException("Out of waveformWindow array");
        }

        mMin = waveformWindow[index].mMin;
        mMax = waveformWindow[index].mMax;

        for (int i = index + 1; i < index + size; i++) {
            mMin = Math.min(mMin, waveformWindow[i].mMin);
            mMax = Math.max(mMax, waveformWindow[i].mMax);
        }
    }

    /**
     * @return The minimum Audio Sample value in the window
     */
    public float getMin() {
        return mMin;
    }

    /**
     * @return The maximum Audio Sample value in the window
     */
    public float getMax() {
        return mMax;
    }

    private float mMin;
    private float mMax;
}
