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

import com.intel.audioviz.AudioTrack;
import com.intel.audioviz.trace.waveform.WaveformWindowCache;

public class AudioWaveformTrace extends AudioTrace {

    public AudioWaveformTrace(final AudioTrack audioTrack, final int channel) {
        super(audioTrack, channel);
        mAudioTrackTraceCache = audioTrack.getAudioTrackTraceCache();
        doClear();
    }

    /**
     * @return the collection of audio sample values
     * @warning caller shall lock the AudioTrace
     */
    public float[] getAudioSampleTrace() {
        return mAudioSampleTrace;
    }

    /**
     * @return the collection of WaveformWindow
     */
    public WaveformWindow[] getWaveformWindowTrace() {
        return mWaveformWindowTrace;
    }

    @Override
    protected void doClear() {
        mAudioSampleTrace = new float[0];
        mWaveformWindowTrace = new WaveformWindow[0];
        mCompleted = false;
    }

    @Override
    protected void doSetTraceView(final AudioTraceView audioTraceRange) {
        final long channelSampleCount = getAudioTrack().getSampleProvider().getSamplesPerChannel();

        final long audioSampleIndex = audioTraceRange.getAudioSampleIndex();
        final long audioSamplePerTraceSample = audioTraceRange.getAudioSamplePerTraceSample();
        int traceSampleCount = audioTraceRange.getTraceSampleCount();

        /* Limit traceSampleCount according to the real audio channel length */
        traceSampleCount = (int) Math.max(0,
                Math.min(traceSampleCount, (channelSampleCount - audioSampleIndex) / audioSamplePerTraceSample));

        if (traceSampleCount > 0) {
            doClear();

            if (audioSamplePerTraceSample == 1) {
                mAudioSampleTrace = new float[traceSampleCount];
                for (int i = 0; i < traceSampleCount; i++) {
                    mAudioSampleTrace[i] =
                            getAudioTrack().getSampleProvider().getSampleAsFloat(
                                    getChannel(), audioSampleIndex + i);
                }
                mCompleted = true;
            } else {
                /* Compute from cache ? */
                final WaveformWindowCache sampleGroupCache =
                        mAudioTrackTraceCache.getWaveformCache(
                                getChannel(), audioSamplePerTraceSample);

                if (sampleGroupCache == null) {
                    mWaveformWindowTrace = new WaveformWindow[traceSampleCount];
                    for (int i = 0; i < traceSampleCount; i++) {
                        mWaveformWindowTrace[i] = new WaveformWindow(
                                getAudioTrack().getSampleProvider(),
                                getChannel(),
                                audioSampleIndex + i * audioSamplePerTraceSample,
                                audioSamplePerTraceSample);
                    }
                    mCompleted = true;
                } else {
                    mWaveformWindowTrace = sampleGroupCache.getFromCache(
                            audioSampleIndex, audioSamplePerTraceSample, traceSampleCount);

                    mCompleted = mWaveformWindowTrace.length >= traceSampleCount;
                }
            }
        } else {
            mCompleted = true;
        }
    }

    @Override
    public boolean isAudioTraceViewRequestCompleted() {
        return mCompleted;
    }

    private float[] mAudioSampleTrace;
    private WaveformWindow[] mWaveformWindowTrace;
    private final AudioTrackWaveformTraceCacheProvider mAudioTrackTraceCache;
    private boolean mCompleted;
}
