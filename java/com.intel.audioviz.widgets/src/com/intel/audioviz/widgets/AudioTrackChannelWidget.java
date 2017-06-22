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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

import com.intel.audioviz.AudioTrack;
import com.intel.audioviz.trace.AudioTraceController;

/**
 * AudioTrackChannelWidget provides the ability to manages all supported AudioTraceWidget subclasses for an AudioTrack's
 * channel. The AudioTraceWidet instantiated per channel come from the provided IAudioTraceWidgetProvider. A default
 * IAudioTraceWidgetProvider is registered in the Eclipse context as AudioTraceWidgetProviderAddon instance.
 */
public class AudioTrackChannelWidget extends Composite implements IAudioTraceControllerHolder {

    public AudioTrackChannelWidget(final Composite parent, final int style,
            final IAudioTraceWidgetProvider audioTraceWidgetProvider) {
        super(parent, style);

        mAudioTrack = null;
        mChannel = -1;
        mAudioTraceWidgets = new HashSet<AudioTraceWidget>();
        mAudioTraceController = new AudioTraceController();

        super.setLayout(new GridLayout(1, false));

        /**
         * @todo Consider to provide a way for user to select set visible or not each trace widget
         */
        final Set<AudioTraceWidget> audioTraceWidgets = audioTraceWidgetProvider.getAudioTraceWidget(this, style);
        for (final AudioTraceWidget audioTraceWidget : audioTraceWidgets) {
            audioTraceWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            mAudioTraceWidgets.add(audioTraceWidget);
        }

        /**
         * @todo The sub class of AudioTraceIndexControlWidget to be instantiated here shall be configurable
         */
        mAudioTraceIndexControlWidget =
                new ScrollBarAudioTraceIndexControlWidget(this, SWT.NONE);
        final GridData controlGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        mAudioTraceIndexControlWidget.setLayoutData(controlGridData);
    }

    public void setAudioTrackChannel(final AudioTrack audioTrack, final int channel) {
        checkWidget();
        if (channel < 0 || channel >= audioTrack.getAudioFormat().getChannelCount()) {
            SWT.error(SWT.ERROR_INVALID_ARGUMENT);
        }

        mChannel = channel;
        mAudioTrack = audioTrack;

        for (final AudioTraceWidget audioTraceWidget : mAudioTraceWidgets) {
            audioTraceWidget.setAudioTrace(mAudioTrack, mChannel);
        }
        setAudioTraceController(mAudioTraceController);
    }

    public int getChannel() {
        checkWidget();
        return mChannel;
    }

    public AudioTrack getAudioTrack() {
        checkWidget();
        return mAudioTrack;
    }

    @Override
    public void setLayout(final Layout layout) {
        // Internally control the layout of widget required to render the channel.
    }

    @Override
    public void setAudioTraceController(final AudioTraceController audioTraceController) {
        checkWidget();
        if (audioTraceController == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }

        mAudioTraceController = audioTraceController;

        mAudioTraceIndexControlWidget.setAudioTraceController(mAudioTraceController);
        for (final AudioTraceWidget audioTraceWidget : mAudioTraceWidgets) {
            audioTraceWidget.setAudioTraceController(mAudioTraceController);
        }
    }

    @Override
    public AudioTraceController getAudioTraceController() {
        checkWidget();
        return mAudioTraceController;
    }

    private AudioTrack mAudioTrack;
    private int mChannel;
    private final HashSet<AudioTraceWidget> mAudioTraceWidgets;
    private final AudioTraceIndexControlWidget mAudioTraceIndexControlWidget;
    private AudioTraceController mAudioTraceController;
}
