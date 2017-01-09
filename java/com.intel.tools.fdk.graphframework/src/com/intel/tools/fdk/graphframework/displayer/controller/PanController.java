/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.tools.fdk.graphframework.displayer.controller;

import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.SWT;

import com.intel.tools.fdk.graphframework.displayer.GraphDisplayer;

/** Controller allowing to pan in a viewport by dragging the background */
public class PanController {

    /** Location of the last mouse click */
    private Point click = new Point(0, 0);

    /** Indicate if we are currently panning in the viewport */
    private boolean isPanning = false;

    public PanController(final GraphDisplayer displayer) {

        displayer.getBackgroundLayer().addMouseListener(new MouseListener.Stub() {
            @Override
            public void mouseReleased(final MouseEvent event) {
                // Reset state
                isPanning = false;
            }

            @Override
            public void mousePressed(final MouseEvent event) {
                event.consume();
                // Get the click position
                click = event.getLocation();
                isPanning = true;
            }
        });
        displayer.getBackgroundLayer().addMouseMotionListener(new MouseMotionListener.Stub() {
            @Override
            public void mouseDragged(final MouseEvent event) {
                if (isPanning && (event.getState() & SWT.CTRL) == SWT.CTRL) {
                    final int hOffset = click.x - event.x;
                    final int vOffset = click.y - event.y;
                    final Point currentLocation = displayer.getControl().getViewport().getViewLocation();
                    displayer.getControl().getViewport().setViewLocation(currentLocation.x + hOffset,
                            currentLocation.y + vOffset);
                }
            }
        });
    }

}
