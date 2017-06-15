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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.widgets.Composite;

import com.intel.audioviz.widgets.AudioTraceWidget;
import com.intel.audioviz.widgets.IAudioTraceWidgetProvider;
import com.intel.audioviz.widgets.audiotrace.waveform.AudioWaveformTraceWidget;

/**
 * The AudioWaveformTraceWidgetProvider is an implementation of IAudioTraceWidgetProvider which provides only
 * AudioWaveformTraceWidget.
 *
 * It's an example alternative to the AudioTraceWidgetProviderAddon which provides all available AudioTraceWidget
 * subclasses.
 *
 * It shows how it is possible to control the widget which will be instantiated by the AudioTrackChannelWidget.
 */
public class AudioWaveformTraceWidgetProvider implements IAudioTraceWidgetProvider {

    public AudioWaveformTraceWidgetProvider() {
    }

    @Override
    public Set<AudioTraceWidget> getAudioTraceWidget(final Composite parent, final int style) {
        final HashSet<AudioTraceWidget> audioTraceWidget = new HashSet<AudioTraceWidget>(1);
        audioTraceWidget.add(new AudioWaveformTraceWidget(parent, style));
        return Collections.unmodifiableSet(audioTraceWidget);
    }

}
