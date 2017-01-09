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

import com.intel.tools.fdk.graphframework.graph.ILink;
import com.intel.tools.fdk.graphframework.graph.Style;
import com.intel.tools.utils.IntelPalette;

/**
 * Represent a graph Link (i.e Edge).</br>
 *
 * A link connects a node to another node (or itself).</br>
 * The link does not carry the information of which inputs/outputs are connected.
 */
public final class Link implements ILink {

    private final Style style = new Style();

    private final Input input;
    private final Output output;

    public Link(final Output output, final Input input) {
        this.input = input;
        this.output = output;
        this.input.connect(this);
        this.output.connect(this);

        getStyle().setForeground(IntelPalette.GREY);
    }

    @Override
    public Input getInput() {
        return input;
    }

    @Override
    public Output getOutput() {
        return output;
    }

    @Override
    public void delete() {
        this.input.disconnect();
        this.output.disconnect();
    }

    @Override
    public Style getStyle() {
        return style;
    }

}
