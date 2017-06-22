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

import com.intel.audioviz.AudioTrack;

/**
 * An AudioTrackWidget is an SWT Composite widget which holds an AudioTrack and an AudioTraceController.
 */
public abstract class AudioTrackWidget extends Composite implements IAudioTraceControllerHolder, DisposeListener {

    /**
     * @param parent
     *            The parent Composite widget
     * @param style
     *            The SWT widget style
     * @param audioTrack
     *            The AudioTrack the widget will expose
     */
    public AudioTrackWidget(final Composite parent, final int style) {
        super(parent, style);
        mAudioTrack = null;
        parent.addDisposeListener(this);
    }

    /**
     * @return The AudioTrackWidget's AudioTrack
     */
    public AudioTrack getAudioTrack() {
        return mAudioTrack;
    }

    /**
     * Set the widget AudioTrack. The widget takes the ownership of the AudioTrack and will dispose when it will be
     * itself disposed or when another AudioTrack will be set.
     *
     * @param audioTrack
     *            The AudioTrack the widget shall expose.
     */
    public void setAudioTrack(final AudioTrack audioTrack) {
        if (audioTrack == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }
        if (mAudioTrack != null) {
            mAudioTrack.dispose();
        }
        doSetAudioTrack(audioTrack);
        mAudioTrack = audioTrack;
    }

    @Override
    public void widgetDisposed(final DisposeEvent e) {
        mAudioTrack.dispose();
    }

    /**
     * Subclass implementation of the setAudioTrack()
     *
     * @param audioTrack
     *            Non null AudioTrack to be set.
     */
    protected abstract void doSetAudioTrack(AudioTrack audioTrack);

    private AudioTrack mAudioTrack;
}
