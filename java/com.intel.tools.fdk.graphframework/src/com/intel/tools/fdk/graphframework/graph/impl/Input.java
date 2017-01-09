/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.tools.fdk.graphframework.graph.impl;

import com.intel.tools.fdk.graphframework.graph.IInput;

public class Input extends Pin implements IInput {

    public Input(final int id, final Leaf leaf) {
        super(id, leaf);
    }

}
