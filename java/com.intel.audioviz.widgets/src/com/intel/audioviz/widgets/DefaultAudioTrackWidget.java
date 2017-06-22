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

import java.security.InvalidParameterException;
import java.util.HashSet;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

import com.intel.audioviz.AudioTrack;
import com.intel.audioviz.trace.AudioTraceController;

/**
 * DefaultAudioTrackWidget provides a default implementation of AudioTrackWidget. The DefaultAudioTrackWidget
 * instantiates one AudioTrackChannelWidget per AudioTrack's channel. An AudioTraceControllerToolBarStyle parameter
 * allows to specify if an AudioTraceControllerToolBar shall be present or not, and if it shall be present per channel
 * or as global track control only.
 */
public class DefaultAudioTrackWidget extends AudioTrackWidget {

    public enum AudioTraceControllerToolBarStyle {
        NONE, GLOBAL, CHANNEL
    }

    @Inject
    public DefaultAudioTrackWidget(final Composite parent,
            final IAudioTraceWidgetProvider audioTraceWidgetProvider,
            @Optional final AudioTraceControllerToolBarStyle toolBarStyle) {
        super(parent, SWT.NONE);

        if (audioTraceWidgetProvider == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }

        mAudioTraceController = new AudioTraceController();
        mAudioTraceControllerToolBarStyle = toolBarStyle == null ? AudioTraceControllerToolBarStyle.NONE : toolBarStyle;
        mAudioTraceControllerToolBars = new HashSet<AudioTraceControllerToolBar>();
        mAudioTraceWidgetProvider = audioTraceWidgetProvider;
    }

    @Override
    protected void doSetAudioTrack(final AudioTrack audioTrack) {
        if (getAudioTrack() != null) {
            /* @todo support AudioTrack reset ? */
            throw new InvalidParameterException("DefaultAudioTrackWidget does not support AudioTrack reset");
        }

        if (audioTrack.getAudioFormat().getChannelCount() > 0) {
            AudioTraceControllerToolBar toolBar = null;

            if (mAudioTraceControllerToolBarStyle != AudioTraceControllerToolBarStyle.NONE) {
                super.setLayout(new GridLayout(2, false));
                if (mAudioTraceControllerToolBarStyle == AudioTraceControllerToolBarStyle.GLOBAL) {
                    toolBar = new AudioTraceControllerToolBar(this, SWT.VERTICAL);
                    mAudioTraceControllerToolBars.add(toolBar);
                    toolBar.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, true, 1,
                            audioTrack.getAudioFormat().getChannelCount()));
                }
            } else {
                super.setLayout(new GridLayout(1, false));
            }

            mAudioTrackChannelWidgets = new AudioTrackChannelWidget[audioTrack.getAudioFormat().getChannelCount()];
            for (int i = 0; i < mAudioTrackChannelWidgets.length; i++) {

                if (mAudioTraceControllerToolBarStyle == AudioTraceControllerToolBarStyle.CHANNEL) {
                    toolBar = new AudioTraceControllerToolBar(this, SWT.VERTICAL);
                    toolBar.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, true));
                    toolBar.setAudioTraceController(mAudioTraceController);
                    mAudioTraceControllerToolBars.add(toolBar);
                }
                mAudioTrackChannelWidgets[i] = new AudioTrackChannelWidget(this, SWT.NONE, mAudioTraceWidgetProvider);
                mAudioTrackChannelWidgets[i].setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
                mAudioTrackChannelWidgets[i].setAudioTrackChannel(audioTrack, i);
                if (toolBar != null) {
                    toolBar.addAudioTraceControllerHolder(mAudioTrackChannelWidgets[i]);
                } else {
                    mAudioTrackChannelWidgets[i].setAudioTraceController(mAudioTraceController);
                }
            }
        }
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
        if (mAudioTraceControllerToolBars.isEmpty()) {
            for (final AudioTrackChannelWidget audioTrackChannelWidget : mAudioTrackChannelWidgets) {
                audioTrackChannelWidget.setAudioTraceController(mAudioTraceController);
            }
        } else {
            for (final AudioTraceControllerToolBar audioTraceControllerToolBar : mAudioTraceControllerToolBars) {
                audioTraceControllerToolBar.setAudioTraceController(audioTraceController);
            }
        }
    }

    @Override
    public AudioTraceController getAudioTraceController() {
        checkWidget();
        return mAudioTraceController;
    }

    private AudioTraceController mAudioTraceController;
    private AudioTrackChannelWidget[] mAudioTrackChannelWidgets;
    private final AudioTraceControllerToolBarStyle mAudioTraceControllerToolBarStyle;
    private final HashSet<AudioTraceControllerToolBar> mAudioTraceControllerToolBars;
    private final IAudioTraceWidgetProvider mAudioTraceWidgetProvider;
}
