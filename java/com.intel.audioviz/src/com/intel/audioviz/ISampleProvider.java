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

/**
 * An Interface to get normalized Audio Samples from Audio data.
 * @note AudioViz is designed with the assumption that all channel(s) of an AudioTrack have
 * the same length.
 */
public interface ISampleProvider {

    /**
     * @return The number of Audio Samples available per channel
     */
    long getSamplesPerChannel();

    /**
     * @param channel The channel number
     * @param index The Audio Sample index
     * @return the Audio Sample normalized as double
     */
    double getSampleAsDouble(int channel, long index);

    /**
     * @param channel The channel number
     * @param index The Audio Sample index
     * @return the Audio Sample normalized as float
     */
    float getSampleAsFloat(int channel, long index);
}
