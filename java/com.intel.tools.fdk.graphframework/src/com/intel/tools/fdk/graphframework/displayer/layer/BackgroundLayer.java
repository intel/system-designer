/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.tools.fdk.graphframework.displayer.layer;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Point;

import com.intel.tools.fdk.graphframework.figure.IGraphFigure;
import com.intel.tools.utils.IntelPalette;

/** A layer intended to be in the background.
 *  This layer will always capture click events (even if no child figure is at the clicked point).
 */
public class BackgroundLayer extends FreeformLayer {

    private boolean isGridVisible = false;

    public BackgroundLayer() {
        super();
        setForegroundColor(IntelPalette.LIGHT_GREY);
    }

    @Override
    /** All click event made on a background layer is captured */
    public boolean containsPoint(final int x, final int y) {
        return true;
    }

    public void setGridVisible(final boolean visible) {
        isGridVisible = visible;
    }

    @Override
    protected void paintFigure(final Graphics graphics) {
        super.paintFigure(graphics);
        if (isGridVisible) {
            FigureUtilities.paintGrid(graphics, this, new Point(0, 0), IGraphFigure.SIZE_UNIT, IGraphFigure.SIZE_UNIT);
        }
    }

}
