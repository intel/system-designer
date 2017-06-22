/*
 * Copyright (C) 2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */

package com.intel.audioviz.example.widgets;

import javax.inject.Inject;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;

import com.intel.audioviz.AudioTrack;
import com.intel.audioviz.trace.AudioTraceController;
import com.intel.audioviz.widgets.AudioTraceControllerToolBar;
import com.intel.audioviz.widgets.AudioTrackChannelWidget;
import com.intel.audioviz.widgets.AudioTrackWidget;
import com.intel.audioviz.widgets.IAudioTraceWidgetProvider;;

/**
 * CustomStereoAudioTrackWidget is an example of custom AudioTrackWidget subclass. It provides side to side the two
 * channels of a stereo track separated by a SashForm. A single AudioTraceControllerToolBar is displayed for a
 * synchronized navigation in channels.
 */
public class CustomStereoAudioTrackWidget extends AudioTrackWidget {

    @Inject
    public CustomStereoAudioTrackWidget(final Composite parent,
            final IAudioTraceWidgetProvider audioTraceWidgetProvider) {
        super(parent, SWT.NONE);

        super.setLayout(new GridLayout(1, false));

        mAudioTraceControllerToolBar = new AudioTraceControllerToolBar(this, SWT.HORIZONTAL);
        mAudioTraceControllerToolBar.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));

        final SashForm sashForm = new SashForm(this, SWT.HORIZONTAL | SWT.BORDER);
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        mChannelZeroWidget = new AudioTrackChannelWidget(sashForm, SWT.CENTER,
                audioTraceWidgetProvider);
        mAudioTraceControllerToolBar.addAudioTraceControllerHolder(mChannelZeroWidget);

        mChannelOneWidget = new AudioTrackChannelWidget(sashForm, SWT.CENTER,
                audioTraceWidgetProvider);

        mAudioTraceControllerToolBar.addAudioTraceControllerHolder(mChannelOneWidget);
    }

    @Override
    public void setLayout(final Layout layout) {
        // Internally control the layout of widget required to render the channel.
    }

    @Override
    public void setAudioTraceController(final AudioTraceController audioTraceController) {
        mAudioTraceControllerToolBar.setAudioTraceController(audioTraceController);
    }

    @Override
    public AudioTraceController getAudioTraceController() {
        return mAudioTraceControllerToolBar.getAudioTraceController();
    }

    @Override
    protected void doSetAudioTrack(final AudioTrack audioTrack) {

        if (audioTrack.getAudioFormat().getChannelCount() == 2) {
            mChannelZeroWidget.setAudioTrackChannel(audioTrack, 0);
            mChannelOneWidget.setAudioTrackChannel(audioTrack, 1);
        } else {
            MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",
                    "AudioTrackCustomStereoWidget hanldes stereo track only.");
        }
    }

    private final AudioTraceControllerToolBar mAudioTraceControllerToolBar;
    private final AudioTrackChannelWidget mChannelZeroWidget;
    private final AudioTrackChannelWidget mChannelOneWidget;
}
