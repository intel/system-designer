/*
 * Copyright (C) 2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */

package com.intel.audioviz.example.handlers;

import org.eclipse.e4.core.contexts.IEclipseContext;

import com.intel.audioviz.widgets.DefaultAudioTrackWidget.AudioTraceControllerToolBarStyle;

public class OpenDefaultAudioTrackWidgetHandlerChannelToolBars extends OpenAudioTrackHandler {
    @Override
    protected void setupAudioTrackPart(final IEclipseContext partContext) {
        /*
         * The DefaultAudioTrackWidget supports multiple tool bar style. Inject the toolbar style to get one control
         * toolbar per channel
         */
        partContext.set(AudioTraceControllerToolBarStyle.class, AudioTraceControllerToolBarStyle.CHANNEL);
    }
}
