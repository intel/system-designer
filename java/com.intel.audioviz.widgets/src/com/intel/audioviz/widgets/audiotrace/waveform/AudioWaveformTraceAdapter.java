/*
 * Copyright (C) 2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.audioviz.widgets.audiotrace.waveform;

import java.util.HashSet;

import org.eclipse.nebula.visualization.xygraph.dataprovider.IDataProvider;
import org.eclipse.nebula.visualization.xygraph.dataprovider.IDataProviderListener;
import org.eclipse.nebula.visualization.xygraph.dataprovider.ISample;
import org.eclipse.nebula.visualization.xygraph.dataprovider.Sample;
import org.eclipse.nebula.visualization.xygraph.linearscale.Range;
import org.eclipse.swt.widgets.Display;

import com.intel.audioviz.trace.AudioTrace;
import com.intel.audioviz.trace.AudioTraceView;
import com.intel.audioviz.trace.AudioWaveformTrace;
import com.intel.audioviz.trace.IAudioTraceListener;
import com.intel.audioviz.trace.WaveformWindow;

/**
 * AudioWaveformTraceAdapter exposes an AudioWaveformTrace as nebula visualization.xygraph.dataprovider.IDataProvider to
 * render the AudioWaveformTrace as a XYGraph Trace.
 */
public class AudioWaveformTraceAdapter implements IDataProvider, IAudioTraceListener {

    public AudioWaveformTraceAdapter(final AudioWaveformTrace audioWaveformTrace) {
        if (audioWaveformTrace == null) {
            throw new IllegalArgumentException("null AudioWaveformTrace");
        }

        mAudioWaveformTrace = audioWaveformTrace;
        mAudioTraceView = null;
        mTrace = new Sample[0];
        mListeners = new HashSet<IDataProviderListener>();
        audioWaveformTrace.addAudioTraceListener(this);
    }

    @Override
    public int getSize() {
        return mTrace.length;
    }

    @Override
    public ISample getSample(final int index) {
        return mTrace[index];
    }

    @Override
    public Range getXDataMinMax() {
        if (mAudioTraceView != null && mAudioTraceView.isValid()) {
            return new Range(
                    mAudioTraceView.getAudioSampleIndex(),
                    mAudioTraceView.getAudioSampleIndex()
                    + Math.max(0, (mTrace.length - 1) * mAudioTraceView.getAudioSamplePerTraceSample()));

        }
        return null;
    }

    @Override
    public Range getYDataMinMax() {
        return NORMALIZED_RANGE;
    }

    @Override
    public boolean isChronological() {
        return true;
    }

    @Override
    public synchronized void addDataProviderListener(final IDataProviderListener listener) {
        mListeners.add(listener);
    }

    @Override
    public synchronized boolean removeDataProviderListener(final IDataProviderListener listener) {
        return mListeners.remove(listener);
    }

    @Override
    public synchronized void traceChanged(final AudioTrace audioTrace) {
        synchronized (mAudioWaveformTrace) {
            mAudioTraceView = mAudioWaveformTrace.getAudioTraceView();
            /*
             * For one audio sample per trace sample, just create a Trace where points are audio samples values. For
             * more than one audio samples per trace sample, the Trace is a succession of one point for minimum audio
             * sample value and one point for maximum audio sample value per WaveformWindow.
             */
            if (mAudioTraceView.getAudioSamplePerTraceSample() == 1) {
                final float[] audioSampleTrace = mAudioWaveformTrace.getAudioSampleTrace();
                mTrace = new Sample[audioSampleTrace.length];
                for (int i = 0; i < mTrace.length; i++) {
                    mTrace[i] = new Sample(
                            mAudioTraceView.getAudioSampleIndex() + i,
                            audioSampleTrace[i]);
                }
            } else if (mAudioTraceView.getAudioSamplePerTraceSample() > 1) {
                final WaveformWindow[] waveformWindowTrace = mAudioWaveformTrace.getWaveformWindowTrace();
                mTrace = new Sample[waveformWindowTrace.length * 2];
                for (int i = 0; i < mAudioWaveformTrace.getWaveformWindowTrace().length; i++) {
                    mTrace[i * 2] = new Sample(
                            mAudioTraceView.getAudioSampleIndex() + i * mAudioTraceView.getAudioSamplePerTraceSample(),
                            waveformWindowTrace[i].getMin());

                    mTrace[i * 2 + 1] = new Sample(
                            mAudioTraceView.getAudioSampleIndex() + i * mAudioTraceView.getAudioSamplePerTraceSample(),
                            waveformWindowTrace[i].getMax());
                }
            } else {
                mTrace = new Sample[0];
            }

            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    notifyListeners();
                }
            });
        }
    }

    private synchronized void notifyListeners() {
        for (final IDataProviderListener listener : mListeners) {
            listener.dataChanged(AudioWaveformTraceAdapter.this);
        }
    }

    private AudioTraceView mAudioTraceView;
    private final AudioWaveformTrace mAudioWaveformTrace;
    private Sample[] mTrace;
    private final HashSet<IDataProviderListener> mListeners;

    private static final Range NORMALIZED_RANGE = new Range(-1.0d, 1.0d);
}
