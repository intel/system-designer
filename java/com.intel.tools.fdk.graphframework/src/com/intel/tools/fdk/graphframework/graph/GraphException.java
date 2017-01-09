/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.tools.fdk.graphframework.graph;

public class GraphException extends Exception {

    private static final long serialVersionUID = -8222579166047855900L;

    public GraphException() {
        super();
    }

    public GraphException(final String message, final Throwable cause, final boolean enableSuppression,
            final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public GraphException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public GraphException(final String message) {
        super(message);
    }

    public GraphException(final Throwable cause) {
        super(cause);
    }

}
