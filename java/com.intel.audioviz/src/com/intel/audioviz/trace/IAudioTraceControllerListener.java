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
 * An IAudioTraceControllerListener is notified each time an AudioTraceController applies an AudioTraceRange on an
 * AudioTrace.
 */
public interface IAudioTraceControllerListener {
    /**
     * Called each time an AudioTraceController applies an AudioTraceRange on an AudioTrace.
     *
     * @note the AudioTrace might have not yet execute the AudioTraceRange request.
     */
    void traceViewChanged();
}
