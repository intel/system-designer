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

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;

import com.intel.audioviz.AudioTrack;

/**
 * AudioTrackPart provides the ability to display an AudioTrack using an AudioTrackWidget subclass. The AudioTrackWidget
 * subclass to be used should be specified in the Part's context by name AUDIO_TRACK_WIDGET_CLASS. If not specified in
 * the Part's context, a DefaultAudioTrackWidget will be instantiated by the AudioTrackPart. The AudioTrackWidget
 * subclass is instantiated using the ContextInjectionFactory, which allows the AudioTrackWidget to interact with the
 * AudioTrackPart's IEclipseContext.
 */
public class AudioTrackPart implements DisposeListener {

    @PostConstruct
    public void postConstruct(final Composite parent, final AudioTrack audioTrack, final IEclipseContext context,
            @Optional @Named(AUDIO_TRACK_WIDGET_CLASS) final Class<? extends AudioTrackWidget> audioTrackWidgetClass) {

        mContext = context;

        Class<? extends AudioTrackWidget> safeAudioTrackWidgetClass = audioTrackWidgetClass;
        if (safeAudioTrackWidgetClass == null) {
            safeAudioTrackWidgetClass = DefaultAudioTrackWidget.class;
        }
        final AudioTrackWidget mAudioTrackWidget = ContextInjectionFactory.make(safeAudioTrackWidgetClass, mContext);
        mAudioTrackWidget.setAudioTrack(audioTrack);
        mContext.set(AudioTrackWidget.class, mAudioTrackWidget);

        parent.addDisposeListener(this);
    }

    @Override
    public void widgetDisposed(final DisposeEvent e) {
        ContextInjectionFactory.uninject(mAudioTrackWidget, mContext);
    }

    private IEclipseContext mContext;
    private DefaultAudioTrackWidget mAudioTrackWidget;

    public static final String AUDIO_TRACK_WIDGET_CLASS = "AudioTrackWidgetClass";
}
