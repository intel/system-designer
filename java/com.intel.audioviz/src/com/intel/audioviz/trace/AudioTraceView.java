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


public class AudioTraceView {

    /**
     * @param audioSampleIndex The Audio Sample index the trace starts from
     * @param audioSamplePerTraceSample The number of Audio Samples per Trace Sample
     * @param traceSampleCount The length of the Trace range in Trace Sample
     */
    public AudioTraceView(
            final long audioSampleIndex,
            final long audioSamplePerTraceSample,
            final int traceSampleCount) {
        if (audioSampleIndex < 0) {
            throw new InvalidParameterException("Invalid audio sample index");
        }
        if (audioSamplePerTraceSample < 1) {
            throw new InvalidParameterException("Invalid audio sample per trace sample count");
        }
        if (traceSampleCount < 0) {
            throw new InvalidParameterException("Invalid sample count");
        }

        mAudioSampleIndex = audioSampleIndex;
        mAudioSamplePerTraceSample = audioSamplePerTraceSample;
        mTraceSampleCount = traceSampleCount;
    }

    /**
     * Invalid AudioTraceView
     */
    private AudioTraceView() {
        mAudioSampleIndex = INVALID_AUDIO_SAMPLE_INDEX;
        mAudioSamplePerTraceSample = INVALID_AUDIO_SAMPLE_PER_TRACE_SAMPLE;
        mTraceSampleCount = INVALID_TRACE_SAMPLE_COUNT;
    }

    /**
     * @param audioTraceView The AudioTraceView to compare with
     * @return true if this AudioTraceRanges includes the audioTraceView and if they have both
     * the same audio sample per trace sample value, false otherwise
     */
    public boolean include(final AudioTraceView audioTraceView) {
        if (mAudioSamplePerTraceSample == audioTraceView.mAudioSamplePerTraceSample
                && mAudioSampleIndex <= audioTraceView.mAudioSampleIndex
                && mAudioSampleIndex + (mTraceSampleCount - 1)
                * audioTraceView.mAudioSamplePerTraceSample >= audioTraceView.mAudioSampleIndex
                + (audioTraceView.mTraceSampleCount - 1)
                * audioTraceView.mAudioSamplePerTraceSample) {
            return true;
        }
        return false;
    }

    /**
     * @param audioTraceView The AudioTraceView to compare with
     * @return true if this AudioTraceRanges equals the audioTraceView (same index, same sample
     * count) and if they have both the same audio sample per trace sample value, false otherwise
     */
    public boolean equals(final AudioTraceView audioTraceView) {
        if (mAudioSamplePerTraceSample == audioTraceView.mAudioSamplePerTraceSample
                && mAudioSampleIndex == audioTraceView.mAudioSampleIndex
                && mTraceSampleCount == audioTraceView.mTraceSampleCount) {
            return true;
        }
        return false;
    }

    /**
     * @return true if the view is valid
     */
    public boolean isValid() {
        return mAudioSampleIndex != INVALID_AUDIO_SAMPLE_INDEX
                && mAudioSamplePerTraceSample != INVALID_AUDIO_SAMPLE_PER_TRACE_SAMPLE
                && mTraceSampleCount != INVALID_TRACE_SAMPLE_COUNT;
    }

    /**
     * @return the first Audio Sample index of the view
     */
    public long getAudioSampleIndex() {
        return mAudioSampleIndex;
    }

    /**
     * @return The number of Audio Sample per Trace Sample
     */
    public long getAudioSamplePerTraceSample() {
        return mAudioSamplePerTraceSample;
    }

    /**
     * @return The number of Trace Sample for this view
     */
    public int getTraceSampleCount() {
        return mTraceSampleCount;
    }

    private final long mAudioSampleIndex;
    private final long mAudioSamplePerTraceSample;
    private final int mTraceSampleCount;

    public static final int INVALID_AUDIO_SAMPLE_INDEX = -1;
    public static final int INVALID_AUDIO_SAMPLE_PER_TRACE_SAMPLE = -1;
    public static final int INVALID_TRACE_SAMPLE_COUNT = -1;
    public static final AudioTraceView INVALID_AUDIO_TRACE_RANGE = new AudioTraceView();
}
