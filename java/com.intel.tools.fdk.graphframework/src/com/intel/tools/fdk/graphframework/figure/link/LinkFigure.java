/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.tools.fdk.graphframework.figure.link;

import org.eclipse.swt.graphics.Color;

import com.intel.tools.fdk.graphframework.figure.IGraphFigure;
import com.intel.tools.fdk.graphframework.figure.ghost.GhostLinkFigure;
import com.intel.tools.fdk.graphframework.graph.ILink;
import com.intel.tools.fdk.graphframework.graph.Style.IStyleListener;

/** Represent a graph link */
public class LinkFigure extends GhostLinkFigure implements IGraphFigure, IStyleListener {

    private final ILink link;

    /**
     * @param link
     *            the represented link
     * @param source
     *            the source anchor
     * @param target
     *            the target anchor
     */
    public LinkFigure(final ILink link, final LinkAnchor source, final LinkAnchor target) {
        super(source, target);
        this.link = link;
        setForegroundColor(link.getStyle().getForeground());
        this.link.getStyle().addListener(this);
    }

    @Override
    public void select() {
        setLineWidth(getLineWidth() + 1);
    }

    @Override
    public void unselect() {
        setLineWidth(getLineWidth() - 1);
    }

    /**
     * @return the link graph element associated to the figure
     */
    public ILink getLink() {
        return link;
    }

    @Override
    public void foregroundUpdated(final Color color) {
        setForegroundColor(link.getStyle().getForeground());
        invalidate();
    }

}
