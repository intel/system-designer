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

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.widgets.Display;

import com.intel.tools.fdk.graphframework.displayer.GraphDisplayer;

/** Zoom/Unzoom a ComponentLayoutDisplayer by when CTRL+Scroll is realized by the user */
public class ZoomController {

    /** Zoom step applied after a zoom action is required */
    private static double zoomStep = 1.1;

    private final GraphDisplayer displayer;
    private FitToScreenController fitToScreenController;

    public ZoomController(final GraphDisplayer displayer) {
        this.displayer = displayer;
        displayer.getControl().addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseScrolled(final MouseEvent e) {
                // CTRL + mouse wheel -> zoom
                if ((e.stateMask & SWT.CTRL) == SWT.CTRL) {
                    zoomAt(e.count > 0 ? zoomStep : 1 / zoomStep, e.x, e.y);
                }
            }
        });
        displayer.getControl().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                if (e.keyCode == SWT.CTRL) {
                    displayer.getControl().setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_SIZEALL));
                } else {
                    if ((e.stateMask & SWT.CTRL) == SWT.CTRL) {

                        if (e.keyCode == SWT.KEYPAD_ADD) {
                            // Ctrl +
                            zoomIn();
                        } else if (e.keyCode == SWT.KEYPAD_SUBTRACT) {
                            // Ctrl -
                            zoomOut();
                        }
                    } else {
                        displayer.getControl().setCursor(null);
                    }
                }
            }

            @Override
            public void keyReleased(final KeyEvent e) {
                if (e.keyCode == SWT.CTRL) {
                    displayer.getControl().setCursor(null);
                }
            }
        });
    }

    /**
     * Zoom the specified factor to a specific point.
     *
     * @param factor
     *            Zoom factor.
     * @param x
     *            X position on the viewport.
     * @param y
     *            Y position on the viewport.
     */
    protected void zoomAt(final double factor, final double x, final double y) {

        // This is a manual zoom, we must disable fit to screen if we have one
        if (fitToScreenController != null) {
            fitToScreenController.setFitEnabled(false);
        }

        final Point location = toScaledLayerPosition(x, y);
        displayer.setScale(displayer.getScale() * factor);
        final Point locationAfterZoom = toScaledLayerPosition(x, y);
        translate(new Point(location.x - locationAfterZoom.x, location.y - locationAfterZoom.y));
    }

    public void setFitToScreenController(final FitToScreenController fitToScreenController) {
        this.fitToScreenController = fitToScreenController;
    }

    /**
     * Convert location on the viewport to an absolute location.
     *
     * @param event
     *            the received drop event
     * @return the drop location relative to the canvas
     */
    private Point toScaledLayerPosition(final double x, final double y) {
        final Point location = new PrecisionPoint(x, y);
        displayer.getContentLayer().translateToRelative(location);
        return location;
    }

    /**
     * Return the center position of the visible area.
     *
     * @return The point corresponding to the center.
     */
    private Point getCenterPosition() {
        return displayer.getContentLayer().getClientArea().getCenter();
    }

    private void translate(final Point point) {
        final Point currentLocation = displayer.getControl().getViewport().getViewLocation();
        final int xmove = (int) (point.x * displayer.getScale());
        final int ymove = (int) (point.y * displayer.getScale());
        displayer.getControl().getViewport().setViewLocation(currentLocation.x + xmove,
                currentLocation.y + ymove);

    }

    private void zoom(final double factor) {
        zoomAt(factor, getCenterPosition().x(), getCenterPosition().y());
    }

    public void zoomIn() {
        zoom(zoomStep);
    }

    public void zoomOut() {
        zoom(1 / zoomStep);
    }

}
