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
import org.eclipse.swt.widgets.Composite;

import com.intel.audioviz.trace.AudioTraceController;
import com.intel.audioviz.trace.IAudioTraceControllerListener;

/**
 * An AudioTraceIndexControlWidget is a widget that provides the ability to set the current trace audio sample index.
 */
public abstract class AudioTraceIndexControlWidget extends Composite
        implements IAudioTraceControllerHolder, IAudioTraceControllerListener {

    public AudioTraceIndexControlWidget(final Composite parent, final int style) {
        super(parent, style);

        mAudioTraceControl = new AudioTraceController();
    }

    @Override
    public void setAudioTraceController(final AudioTraceController audioTraceController) {
        checkWidget();
        if (audioTraceController == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }
        if (mAudioTraceControl != null) {
            mAudioTraceControl.remove(this);
        }
        mAudioTraceControl = audioTraceController;
        if (mAudioTraceControl != null) {
            mAudioTraceControl.add(this);
        }
    }

    @Override
    public AudioTraceController getAudioTraceController() {
        checkWidget();
        return mAudioTraceControl;
    }

    @Override
    public final void traceViewChanged() {
        update();
    }

    private AudioTraceController mAudioTraceControl;
}
