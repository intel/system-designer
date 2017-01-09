/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.tools.fdk.graphframework.figure.pin;

import com.intel.tools.fdk.graphframework.graph.IInput;

/**
 * Represent an input of a graph node
 *
 * This figure looks like: o>
 */
public class InputFigure extends PinFigure<IInput> {

    public InputFigure(final IInput input) {
        super(input);
        setupInputLayout();
    }

}
