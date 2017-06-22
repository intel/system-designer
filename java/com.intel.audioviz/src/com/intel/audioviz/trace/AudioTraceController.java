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
import java.util.HashMap;
import java.util.HashSet;

/**
 * An AudioTraceControl has the ability to control one or more AudioTrace
 * synchronously. The control consist in selecting the trace range and trace
 * zoom.
 */
public class AudioTraceController {

    /**
     * Instantiate a new AudioTraceController. By default, the new controller has its audio sample index initialized to
     * 0 and an invalid zoom level. The first AudioTrace which will declare its required trace sample count will trigger
     * the computation of an initial zoom factor.
     */
    public AudioTraceController() {
        mAudioTraces = new HashMap<AudioTrace, Integer>();
        mListeners = new HashSet<IAudioTraceControllerListener>();
        mZoomFactor = INVALID_ZOOM_FACTOR;
        mAudioSampleIndex = 0;
    }

    /**
     * Instantiate a new AudioTraceController initialized with the audio sample index and
     * the zoom level of the AudioTraceController provided as argument.
     * @param audioTraceController the audioTraceController from which zoom level and audio
     * sample index will be initialized.
     */
    public AudioTraceController(final AudioTraceController audioTraceController) {
        mAudioTraces = new HashMap<AudioTrace, Integer>();
        mListeners = new HashSet<IAudioTraceControllerListener>();
        mZoomFactor = audioTraceController.mZoomFactor;
        mAudioSampleIndex = audioTraceController.mAudioSampleIndex;
    }

    /**
     * @param audioTrace An audioTrace which will be controlled by this controller
     * @note Current implementation does not support to manage AudioTraces not having
     * the exact same sample rate.
     */
    public void add(final AudioTrace audioTrace, final int visibleRange) {
        if (audioTrace == null) {
            throw new InvalidParameterException("null AudioTrace");
        }

        /** @todo consider to handle such situation ? */
        if (!canAccept(audioTrace)) {
            throw new InvalidParameterException("Cannot control heterogeneous sample rates");
        }

        mAudioTraces.put(audioTrace, new Integer(visibleRange));
        doControlTrace();
    }

    /**
     * @param audioTrace An audioTrace which will no more be controlled by this controller
     */
    public void remove(final AudioTrace audioTrace) {
        if (audioTrace == null) {
            throw new InvalidParameterException("null AudioTrace");
        }

        mAudioTraces.remove(audioTrace);
    }

    /**
     * In order to compute the initial zoom value and compute the required trace range for each
     * trace, the controller requires the number of desired trace sample.
     * @note Depending on zoom factor, a trace sample is one or more than a single Audio Sample.
     * @param audioTrace The audio trace for which the desired trace sample count is set
     * @param visibleTraceSampleCount The number of desired trace sample
     */
    public void setVisibleTraceSampleCount(final AudioTrace audioTrace, final int visibleTraceSampleCount) {
        if (doSetVisibleTraceSampleCount(audioTrace, visibleTraceSampleCount)) {
            doControlTrace();
        }
    }

    /**
     * @return The minimum visible trace sample count of all registered AudioTrace
     */
    public int getMinimumVisibleTraceSampleCount() {
        int minimumTraceSampleCount = Integer.MAX_VALUE;
        for (final AudioTrace audioTrace : mAudioTraces.keySet()) {
            minimumTraceSampleCount = Math.min(minimumTraceSampleCount, mAudioTraces.get(audioTrace).intValue());
        }
        return minimumTraceSampleCount;
    }

    /**
     * Set a zoom factor ensuring each trace shows the entire audio data of the AudioTrack's
     * channel they belong to.
     */
    public void setFullRange() {
        final long zoomFactor = getFullRangeZoomFactor();
        if (zoomFactor != INVALID_ZOOM_FACTOR) {
            mAudioSampleIndex = 0;
            mZoomFactor = zoomFactor;
            doControlTrace();
        }
    }

    /**
     * @return The Audio Sample index the trace(s) start(s) from
     */
    public long getAudioSampleIndex() {
        return mAudioSampleIndex;
    }

    /**
     * Set the Audio Sample index the trace(s) shall start(s) from
     * @param sampleIndex Audio Sample index
     * @note the value is limited in order to have at least one trace not empty
     */
    public void setAudioSampleIndex(final long sampleIndex) {
        if (sampleIndex < 0) {
            throw new InvalidParameterException("Invalid audio sample index");
        }

        doSetAudioSampleIndex(sampleIndex);
        doControlTrace();
    }

    /**
     * @return the maximum audio sample index value in order to have at least one trace not empty
     */
    public long getMaximumAudioSampleIndex() {
        long maximumAudioSampleIndex = 0;
        for (final AudioTrace audioTrace : mAudioTraces.keySet()) {
            long traceMaximumAudioSampleIndex = audioTrace.getAudioTrack().getSampleProvider().getSamplesPerChannel();

            if (mZoomFactor > 1) {
                traceMaximumAudioSampleIndex -= mZoomFactor;
            } else {
                traceMaximumAudioSampleIndex--;
            }
            maximumAudioSampleIndex = Math.max(maximumAudioSampleIndex, traceMaximumAudioSampleIndex);
        }
        return maximumAudioSampleIndex;
    }

    /**
     * @return The unit of the Trace Sample. In other words, the number of Audio Sample per trace Sample.
     * @remark Unit is always a power of two, including negative powers (0,5 ; 0,25 ; 0,125...)
     */
    public double getUnit() {
        if (mZoomFactor != INVALID_ZOOM_FACTOR) {
            if (mZoomFactor > 0) {
                return mZoomFactor;
            } else {
                return -1.0f / mZoomFactor;
            }
        } else {
            return INVALID_UNIT;
        }
    }

    /**
     * Zoom in trace(s). A zoom in doubles the range and the unit of the trace(s).
     *
     * @param centerIndex
     *            The index of the Audio Sample the zoom is centered on
     */
    public void zoomIn(final long centerIndex) {
        if (mZoomFactor != INVALID_ZOOM_FACTOR) {
            if (mZoomFactor > 1) {
                // Design makes mZoomFactor always been a power of two
                assert Long.bitCount(mZoomFactor) == 1;
                mZoomFactor /= 2;
            } else if (mZoomFactor == 1) {
                mZoomFactor = -2;
            } else if (mZoomFactor <= -2) {
                mZoomFactor *= 2;
            }

            doSetAudioSampleIndex(mAudioSampleIndex + (centerIndex - mAudioSampleIndex) / 2);
            doControlTrace();
        }
    }

    /**
     * Zoom out trace(s). A zoom out divides by two the range and the unit of the trace(s).
     *
     * @param centerIndex
     *            The index of the Audio Sample the zoom is centered on
     */
    public void zoomOut(final long centerIndex) {
        if (mZoomFactor != INVALID_ZOOM_FACTOR) {
            if (mZoomFactor < -2) {
                // Design makes mZoomFactor always been a negative power of two
                assert Long.bitCount(mZoomFactor) == 2;
                mZoomFactor /= 2;
            } else if (mZoomFactor == -2) {
                mZoomFactor = 1;
            } else if (mZoomFactor >= 0) {
                mZoomFactor *= 2;
            }

            doSetAudioSampleIndex(mAudioSampleIndex - (centerIndex - mAudioSampleIndex));
            doControlTrace();
        }
    }

    /**
     * Add a listener to this controller. The listener will be notified each time the trace(s)
     * is/are controlled.
     * @param listener The listener to be added
     * @return true if the listener has been added, false if the listener was already registered
     */
    public boolean add(final IAudioTraceControllerListener listener) {
        return mListeners.add(listener);
    }

    /**
     * Remove a listener of this controller.
     * @param listener The listener to be removed
     * @return true if the listener has been removed, false if the listener was not registered
     */
    public boolean remove(final IAudioTraceControllerListener listener) {
        return mListeners.remove(listener);
    }

    private void notifyListeners() {
        for (final IAudioTraceControllerListener listener : mListeners) {
            listener.traceViewChanged();
        }
    }

    private boolean doSetVisibleTraceSampleCount(final AudioTrace audioTrace, final int visibleTraceSampleCount) {
        if (visibleTraceSampleCount < 0) {
            throw new InvalidParameterException("Invalid trace sample count");
        }

        if (mAudioTraces.containsKey(audioTrace)) {
            mAudioTraces.put(audioTrace, new Integer(visibleTraceSampleCount));
            return true;
        }
        return false;
    }

    private void doControlTrace() {
        /* If zoom has not been yet computed, try to compute it now. */
        if (mZoomFactor == INVALID_ZOOM_FACTOR) {
            mZoomFactor = getFullRangeZoomFactor();
        }
        /* If zoom is still invalid (actually we could say still unknown), cannot apply range */
        if (mZoomFactor != INVALID_ZOOM_FACTOR) {
            for (final AudioTrace audioTrace : mAudioTraces.keySet()) {
                if (mAudioTraces.get(audioTrace) != null) {
                    audioTrace.setTraceView(
                            new AudioTraceView(
                                    mAudioSampleIndex,
                                    Math.max(1, mZoomFactor),
                                    mAudioTraces.get(audioTrace).intValue()),
                            true);
                }
            }
            notifyListeners();
        }
    }

    private void doSetAudioSampleIndex(final long sampleIndex) {
        mAudioSampleIndex = Math.max(sampleIndex, 0);
        if (mZoomFactor != INVALID_ZOOM_FACTOR) {
            /* Limit the maximum index in order to have at list one trace with something rendered */
            mAudioSampleIndex = Math.min(mAudioSampleIndex, getMaximumAudioSampleIndex());
            /* Align start sample index on a multiple of current zoomFactor in order to avoid rendering
             * artifacts.
             */
            if (mZoomFactor > 1) {
                mAudioSampleIndex -= mAudioSampleIndex % mZoomFactor;
            }
        }
    }

    private long getFullRangeZoomFactor(final AudioTrace audioTrace, final int viewPixelSize) {
        if (viewPixelSize == 0) {
            return INVALID_ZOOM_FACTOR;
        }

        final long sampleCount = audioTrace.getAudioTrack().getSampleProvider().getSamplesPerChannel();

        if (sampleCount < viewPixelSize) {
            long zoomFactor;
            if (viewPixelSize % sampleCount != 0 || Long.bitCount(viewPixelSize / sampleCount) != 1) {
                zoomFactor = -roundToNextPowerOf2(viewPixelSize / sampleCount) / 2;
            } else {
                zoomFactor = -(viewPixelSize / sampleCount);
            }
            if (zoomFactor == -1) {
                zoomFactor = 1;
            }
            return zoomFactor;
        } else
            if (sampleCount % viewPixelSize != 0 || Long.bitCount(sampleCount / viewPixelSize) != 1) {
                return roundToNextPowerOf2(sampleCount / viewPixelSize);
            } else {
                return sampleCount / viewPixelSize;
            }
    }

    private long getFullRangeZoomFactor() {
        long zoomFactor = MINIMUM_ZOOM_FACTOR;
        for (final AudioTrace audioTrace : mAudioTraces.keySet()) {
            final Integer audioTracePixelWidth = mAudioTraces.get(audioTrace);
            if (audioTracePixelWidth != null) {
                zoomFactor = Math.max(zoomFactor, getFullRangeZoomFactor(audioTrace, audioTracePixelWidth.intValue()));
            }
        }
        if (zoomFactor == MINIMUM_ZOOM_FACTOR) {
            zoomFactor = INVALID_ZOOM_FACTOR;
        }
        return zoomFactor;
    }

    private long roundToNextPowerOf2(final long number) {
        /*
         * Cannot round if no more than one leading 0 since it would return a negative number. Assert since the design
         * of this class shall prevent such a situation.
         */
        assert Long.numberOfLeadingZeros(number) > 1;

        return 1L << (Long.SIZE - Long.numberOfLeadingZeros(number));
    }

    private boolean canAccept(final AudioTrace candidateAudioTrace) {
        if (!mAudioTraces.keySet().isEmpty()) {
            for (final AudioTrace audioTrace : mAudioTraces.keySet()) {
                if (candidateAudioTrace.getAudioTrack().getAudioFormat().getSampleFrequency() != audioTrace
                        .getAudioTrack().getAudioFormat().getSampleFrequency()) {
                    // At least on trace in place is not compliant
                    return false;
                }
            }
        }
        return true;
    }


    private final HashMap<AudioTrace, Integer> mAudioTraces;
    private final HashSet<IAudioTraceControllerListener> mListeners;
    private long mAudioSampleIndex;
    /**
     * The zoom factor indicates the number of Audio Sample for one Trace Sample.
     * If positive, it can be interpreted directly.
     * If negative, it must be interpreted as (-1/x).
     */
    private long mZoomFactor;
    private static final long INVALID_ZOOM_FACTOR = 0;
    private static final long MINIMUM_ZOOM_FACTOR = Long.MIN_VALUE;
    public static final float INVALID_UNIT = .0f;
}
