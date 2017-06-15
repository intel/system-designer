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

import java.util.Set;

import org.eclipse.swt.widgets.Composite;

/**
 * An IAudioTraceWidgetProvider provides AudioTraceWidget instances.
 */
public interface IAudioTraceWidgetProvider {
    /**
     * Create a collection of AudioTraceWidget. Each AudioTraceWidget have the same parent Composite and the same SWT
     * style.
     *
     * @param parent
     *            The parent Composite for the widget
     * @param style
     *            The SWT widget style
     * @return An array of new AudioTraceWidget
     */
    Set<AudioTraceWidget> getAudioTraceWidget(Composite parent, int style);
}
