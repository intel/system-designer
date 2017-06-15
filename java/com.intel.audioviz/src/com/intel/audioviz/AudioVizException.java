/*
 * Copyright (C) 2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.audioviz;

public class AudioVizException extends RuntimeException {

    public AudioVizException(final String message) {
        super(message);
    }

    public AudioVizException() {
        super();
    }

    public AudioVizException(final String arg0, final Throwable arg1, final boolean arg2,
            final boolean arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    public AudioVizException(final String arg0, final Throwable arg1) {
        super(arg0 + ": " + arg1.getMessage(), arg1);
    }

    public AudioVizException(final Throwable arg0) {
        super(arg0);
    }

    private static final long serialVersionUID = 9046999551907868336L;
}
