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

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;

import com.intel.tools.fdk.graphframework.figure.ghost.GhostPinFigure;

/** Represent the anchor of a pin where a link is connected */
public class LinkAnchor extends AbstractConnectionAnchor {

    private final GhostPinFigure pin;

    /**
     * @param node
     *            the complete node figure (not only the body)
     * @param pin
     *            the pin to link on
     */
    public LinkAnchor(final IFigure node, final GhostPinFigure pin) {
        super(node);
        this.pin = pin;
    }

    @Override
    public Point getLocation(final Point reference) {
        // The translation is there to take into account the way a line is rendered in draw2d
        return pin.getConnectorCenterLocation().getTranslated(new Point(1, 1));
    }

}
