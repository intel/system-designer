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

import com.intel.audioviz.trace.AudioTraceController;

/**
 * IAudioTraceControllerHolder objects hold an AudioTraceController they register their AudioTrace(s) to.
 */
public interface IAudioTraceControllerHolder {

    /**
     * Set a new AudioController.
     *
     * @param audioTraceController
     *            The AudioController on which AudioTraces shall be added. The AudioTraces shall be removed from
     *            previous AudioController
     */
    void setAudioTraceController(AudioTraceController audioTraceController);

    /**
     * @return the current AudioController in use.
     */
    AudioTraceController getAudioTraceController();
}
