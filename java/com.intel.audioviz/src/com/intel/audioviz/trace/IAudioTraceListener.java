/*
 * Copyright (C) 2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */

package com.intel.audioviz.trace;

/**
 * An IAudioTraceListener is notified each time an AudioTrace data may have changed.
 */
public interface IAudioTraceListener {
    /**
     * Called each time an AudioTrace may have updated its date.
     * @param audioTrace The trace which may have changed its data
     */
    void traceChanged(AudioTrace audioTrace);
}
