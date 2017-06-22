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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.intel.audioviz.trace.AudioTraceController;

/**
 * AudioTraceControllerToolBar provides user control of an AudioTraceController which applies on a collection of
 * IAudioTraceControllerHolder.
 */
public class AudioTraceControllerToolBar extends Composite implements IAudioTraceControllerHolder {

    public AudioTraceControllerToolBar(final Composite parent, final int style) {
        super(parent, SWT.NONE);
        super.setLayout(new FillLayout());

        isUnlocked = false;
        mAudioTraceControllerHolders = new HashSet<IAudioTraceControllerHolder>();
        mMasterAudioTraceController = new AudioTraceController();
        mCurrentAudioTraceController = mMasterAudioTraceController;

        final ToolBar toolBar = new ToolBar(this, style);

        mLockButton = new ToolItem(toolBar, SWT.CHECK);
        mLockButton.setEnabled(false);
        mLockButton.setDisabledImage(
                AudioVizWidgetResources.getImageRegistry().get(AudioVizWidgetResources.IMG_DISABLED_LOCK));
        mLockButton.setToolTipText("Lock/Unlock local trace control");
        mLockButton.setImage(AudioVizWidgetResources.getImageRegistry().get(AudioVizWidgetResources.IMG_LOCKED));
        mLockButton.addSelectionListener(new SelectionListener() {

            private final Image mLockedImage = AudioVizWidgetResources.getImageRegistry()
                    .get(AudioVizWidgetResources.IMG_LOCKED);
            private final Image mUnlockedImage = AudioVizWidgetResources.getImageRegistry()
                    .get(AudioVizWidgetResources.IMG_UNLOCKED);

            @Override
            public void widgetSelected(final SelectionEvent e) {
                mLockButton.setImage(mLockButton.getSelection() ? mUnlockedImage : mLockedImage);
                if (mLockButton.getSelection()) {
                    mCurrentAudioTraceController = new AudioTraceController(mMasterAudioTraceController);
                    isUnlocked = true;
                } else {
                    mCurrentAudioTraceController = mMasterAudioTraceController;
                    isUnlocked = false;
                }
                broadcastCurrentAudioTraceController();
            }

            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {
                mLockButton.setImage(mLockedImage);
            }
        });
        new ToolItem(toolBar, SWT.SEPARATOR);

        final ToolItem fullRangeButton = new ToolItem(toolBar, SWT.NONE);
        fullRangeButton
        .setImage(AudioVizWidgetResources.getImageRegistry().get(AudioVizWidgetResources.IMG_XFULLRANGE));
        fullRangeButton.setToolTipText("Automatic zoom to full trace view");
        fullRangeButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                mCurrentAudioTraceController.setFullRange();
            }
        });

        final ToolItem xZoomInButton = new ToolItem(toolBar, SWT.NONE);
        xZoomInButton.setImage(AudioVizWidgetResources.getImageRegistry().get(AudioVizWidgetResources.IMG_XZOOMIN));
        xZoomInButton.setToolTipText("X Axis Zoom In");
        xZoomInButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                mCurrentAudioTraceController
                .zoomIn(mCurrentAudioTraceController.getAudioSampleIndex()
                        + (long) Math.floor(
                                        (mCurrentAudioTraceController.getMinimumVisibleTraceSampleCount() - 1) / 2
                                                * mCurrentAudioTraceController.getUnit()));
            }
        });

        final ToolItem xZoomOutButton = new ToolItem(toolBar, SWT.NONE);
        xZoomOutButton.setImage(AudioVizWidgetResources.getImageRegistry().get(AudioVizWidgetResources.IMG_XZOOMOUT));
        xZoomOutButton.setToolTipText("X Axis Zoom Out");
        xZoomOutButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                mCurrentAudioTraceController
                .zoomOut(mCurrentAudioTraceController.getAudioSampleIndex()
                        + (long) Math.floor(
                                        (mCurrentAudioTraceController.getMinimumVisibleTraceSampleCount() - 1) / 2
                                                * mCurrentAudioTraceController.getUnit()));
            }
        });
    }

    /**
     * Add a new IAudioTraceControllerHolder to be controlled. The current AudioTraceController is injected to the
     * IAudioTraceControllerHolder.
     *
     * @param audioTraceControllerHolder
     *            The IAudioTraceControllerHolder to be added
     */
    public void addAudioTraceControllerHolder(final IAudioTraceControllerHolder audioTraceControllerHolder) {
        checkWidget();
        if (audioTraceControllerHolder == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }
        if (mAudioTraceControllerHolders.add(audioTraceControllerHolder)) {
            audioTraceControllerHolder.setAudioTraceController(mCurrentAudioTraceController);
        }
    }

    /**
     * Remove an IAudioTraceControllerHolder. A new independent AudioTraceController is provided to the released
     * IAudioTraceControllerHolder.
     *
     * @param audioTraceControllerHolder
     *            The IAudioTraceControllerHolder to be released
     */
    public void removeAudioTraceControllerHolder(final IAudioTraceControllerHolder audioTraceControllerHolder) {
        checkWidget();
        if (audioTraceControllerHolder == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }
        if (mAudioTraceControllerHolders.remove(audioTraceControllerHolder)) {
            audioTraceControllerHolder.setAudioTraceController(new AudioTraceController(mCurrentAudioTraceController));
        }
    }

    @Override
    public void setAudioTraceController(final AudioTraceController audioTraceController) {
        checkWidget();
        if (audioTraceController == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }
        mMasterAudioTraceController = audioTraceController;
        mLockButton.setEnabled(true);
        if (!isUnlocked) {
            mCurrentAudioTraceController = mMasterAudioTraceController;
            broadcastCurrentAudioTraceController();
        }
    }

    @Override
    public AudioTraceController getAudioTraceController() {
        return mCurrentAudioTraceController;
    }

    @Override
    public void setLayout(final Layout layout) {
        // Internally control the layout of widget required to render the channel.
    }

    private void broadcastCurrentAudioTraceController() {
        for (final IAudioTraceControllerHolder audioTraceControllerHolder : mAudioTraceControllerHolders) {
            audioTraceControllerHolder.setAudioTraceController(mCurrentAudioTraceController);
        }
    }

    private final ToolItem mLockButton;
    private AudioTraceController mMasterAudioTraceController;
    private AudioTraceController mCurrentAudioTraceController;
    private final HashSet<IAudioTraceControllerHolder> mAudioTraceControllerHolders;
    private boolean isUnlocked;
}
