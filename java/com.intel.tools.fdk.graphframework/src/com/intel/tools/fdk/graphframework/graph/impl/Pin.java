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

import java.util.Optional;

import com.intel.tools.fdk.graphframework.graph.IPin;
import com.intel.tools.fdk.graphframework.graph.Style;
import com.intel.tools.utils.IntelPalette;

/**
 * Represent a connection point of a {@link Leaf}
 */
public abstract class Pin implements IPin {

    private final Style style = new Style();

    private final int id;
    private final Leaf leaf;
    private Optional<Link> link = Optional.empty();

    public Pin(final int id, final Leaf leaf) {
        this.id = id;
        this.leaf = leaf;

        getStyle().setForeground(IntelPalette.GREY);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Leaf getLeaf() {
        return leaf;
    }

    @Override
    public Optional<Link> getLink() {
        return link;
    }

    /**
     * Connect this pin to a link.</br>
     * This method is used at {@link Link} creation and thus is not exposed everywhere
     *
     * @param link
     *            to connect
     */
    void connect(final Link link) {
        this.link = Optional.of(link);
    }

    /**
     * Disonnect this pin from a link.</br>
     * This method is used at {@link Link} deletion and thus is not exposed everywhere
     */
    void disconnect() {
        this.link = Optional.empty();
    }

    @Override
    public Style getStyle() {
        return style;
    }

}
