/*
 * Copyright (C) 2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */

package com.intel.audioviz.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.intel.audioviz.AudioTrack;
import com.intel.audioviz.trace.AudioTrace;
import com.intel.audioviz.trace.AudioTraceController;
import com.intel.audioviz.trace.IAudioTraceListener;

/**
 * An AudioTraceWidget is the base class for any AudioTrace widget.
 */
public abstract class AudioTraceWidget extends Composite
implements IAudioTraceControllerHolder, IAudioTraceListener, DisposeListener {

    public AudioTraceWidget(final Composite parent, final int style) {
        super(parent, style);
        mAudioTraceController = new AudioTraceController();
        mAudioTrace = null;
        addDisposeListener(this);
    }

    /**
     * Set the AudioTrace of the Widget.
     *
     * @param audioTrack
     *            The AudioTrack the AudioTrace belongs to
     * @param channel
     *            The channel the AudioTrace is about
     */
    public final void setAudioTrace(final AudioTrack audioTrack, final int channel) {
        checkWidget();
        if (audioTrack == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }
        if (channel < 0 || channel >= audioTrack.getAudioFormat().getChannelCount()) {
            SWT.error(SWT.ERROR_INVALID_ARGUMENT);
        }

        if (mAudioTrace != null) {
            mAudioTraceController.remove(mAudioTrace);
            mAudioTrace.removeAudioTraceListener(this);
        }
        mAudioTrace = doSetAudioTrace(audioTrack, channel);
        if (mAudioTrace != null) {
            mAudioTraceController.add(mAudioTrace, getVisibleTraceSampleCount());
            mAudioTrace.addAudioTraceListener(this);
        }
    }

    /**
     * @return the AudioTrace of the widget
     */
    public final AudioTrace getAudioTrace() {
        checkWidget();
        return mAudioTrace;
    }

    @Override
    public void setAudioTraceController(final AudioTraceController audioTraceController) {
        checkWidget();
        if (audioTraceController == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }

        if (mAudioTrace != null) {
            mAudioTraceController.remove(mAudioTrace);
        }

        mAudioTraceController = audioTraceController;

        if (mAudioTrace != null) {
            mAudioTraceController.add(mAudioTrace, getVisibleTraceSampleCount());
        }
    }

    @Override
    public AudioTraceController getAudioTraceController() {
        checkWidget();
        return mAudioTraceController;
    }

    @Override
    public void widgetDisposed(final DisposeEvent e) {
        if (mAudioTrace != null) {
            mAudioTraceController.remove(mAudioTrace);
            mAudioTrace.removeAudioTraceListener(this);
        }
    }

    @Override
    public final void traceChanged(final AudioTrace audioTrace) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                if (!isDisposed()) {
                    update();
                }
            }
        });
    }

    /**
     * Get the AudioTrace managed by the Widget
     *
     * @param audioTrack
     *            AudioTrack the AudioTrace belongs to
     * @param channel
     *            Channel number of the AudioTrace is about
     * @return the AudioTrace managed by the Widget
     */
    protected abstract AudioTrace doSetAudioTrace(AudioTrack audioTrack, int channel);

    /**
     * @return The number of Trace Sample visible in the Widget
     */
    protected abstract int getVisibleTraceSampleCount();

    /**
     * @return the human friendly name of the AudioTraceWidget
     */
    protected abstract String getAudioTraceWidgetFriendlyName();

    private AudioTraceController mAudioTraceController;
    private AudioTrace mAudioTrace;
}
