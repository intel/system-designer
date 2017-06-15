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

import com.intel.audioviz.example.widgets.CustomStereoAudioTrackWidget;
import com.intel.audioviz.widgets.AudioTrackPart;

public class OpenCustomStereoAudioTrackWidgetHandler extends OpenAudioTrackHandler {
    @Override
    protected void setupAudioTrackPart(final IEclipseContext partContext) {
        /*
         * Inject CustomStereoAudioTrackWidget.class to get it instantiated by the AudioTrackPart instead of the default
         * DefaultAudioTrackWidget class
         */
        partContext.set(AudioTrackPart.AUDIO_TRACK_WIDGET_CLASS, CustomStereoAudioTrackWidget.class);
    }
}
