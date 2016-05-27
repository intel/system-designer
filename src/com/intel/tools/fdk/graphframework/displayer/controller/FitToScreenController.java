/* ============================================================================
 * INTEL CONFIDENTIAL
 *
 * Copyright 2015-2016 Intel Corporation All Rights Reserved.
 *
 * The source code contained or described herein and all documents related to
 * the source code ("Material") are owned by Intel Corporation or its suppliers
 * or licensors. Title to the Material remains with Intel Corporation or its
 * suppliers and licensors. The Material contains trade secrets and proprietary
 * and confidential information of Intel or its suppliers and licensors. The
 * Material is protected by worldwide copyright and trade secret laws and
 * treaty provisions. No part of the Material may be used, copied, reproduced,
 * modified, published, uploaded, posted, transmitted, distributed, or
 * disclosed in any way without Intel's prior express written permission.
 *
 * No license under any patent, copyright, trade secret or other intellectual
 * property right is granted to or conferred upon you by disclosure or delivery
 * of the Materials, either expressly, by implication, inducement, estoppel or
 * otherwise. Any license under such intellectual property rights must be
 * express and approved by Intel in writing.
 * ============================================================================
 */
package com.intel.tools.fdk.graphframework.displayer.controller;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

import com.intel.tools.fdk.graphframework.displayer.GraphDisplayer;
import com.intel.tools.fdk.graphframework.figure.node.GroupBodyFigure;
import com.intel.tools.fdk.graphframework.figure.node.LeafBodyFigure;

public class FitToScreenController {

    public interface Listener {
        void fitToScreenChanged(boolean enabled);
    }

    private final List<Listener> listeners = new ArrayList<>();

    private static final int KEY_0 = 224;

    private boolean fitEnabled;

    private final GraphDisplayer displayer;

    public FitToScreenController(final GraphDisplayer displayer) {
        this.displayer = displayer;
        displayer.getControl().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                if ((e.stateMask & SWT.CTRL) == SWT.CTRL) {
                    if (e.keyCode == KEY_0 || e.keyCode == SWT.KEYPAD_0) {
                        // Ctrl + 0
                        fitToScreen();
                    }
                }
            }
        });
        // if the the auto fit to screen is enabled, we fit to screen at each displayer resize
        displayer.getControl().addControlListener(new ControlAdapter() {
            @Override
            public void controlResized(final ControlEvent e) {
                if (isFitEnabled()) {
                    fitToScreen();
                }
            }
        });
    }

    /**
     * Update the scale and figure position of the content layer displayer to feet the current screen
     *
     * @param displayer
     *            the displayer to work on
     */
    public void fitToScreen() {
        fitToScreen(Integer.MAX_VALUE);
    }

    /**
     * Update the scale and figure position of the content layer displayer to feet the current screen
     *
     * @param displayer
     *            the displayer to work on
     * @param scaleLimit
     *            maximum scale ratio to set to the scalable layer of the modified displayer
     */
    public void fitToScreen(final int scaleLimit) {
        setupScale(displayer, scaleLimit);
        centerContent(displayer);
    }

    /**
     * Enable/Disable the fit to screen
     *
     * This method activate the fit to screen, the screen will be fitted after each size update of the displayer. A
     * first fit to screen action is done when the capability is enabled.
     *
     * @param enabled
     *            true to enable false to disable
     */
    public void setFitEnabled(final boolean enabled) {
        this.fitEnabled = enabled;
        if (this.fitEnabled) {
            fitToScreen();
        }
        for (final Listener listener : listeners) {
            listener.fitToScreenChanged(enabled);
        }
    }

    public boolean isFitEnabled() {
        return fitEnabled;
    }

    private void setupScale(final GraphDisplayer displayer, final int limit) {
        double scale;
        final Dimension dimension = displayer.getContentLayer().getFreeformExtent().getCopy().getSize();

        if ((dimension.width > 0) && (dimension.height > 0)) {
            scale = (double) displayer.getControl().getViewport().getSize().width / (double) dimension.width;
            scale = Math.min(scale,
                    (double) displayer.getControl().getViewport().getSize().height / (double) dimension.height);
            displayer.setScale(Math.min(scale, limit));
        }
    }

    private void centerContent(final GraphDisplayer displayer) {
        final Dimension viewDimension = displayer.getControl().getViewport().getSize();

        // Retrieve topology information
        final Rectangle topologyBounds = displayer.getContentLayer().getFreeformExtent().getCopy();
        // Convert topology bounds to parent coordinate system
        displayer.getScalableLayers().translateToParent(topologyBounds);
        final Dimension topologyDimension = topologyBounds.getSize();
        final Point topologyCoordinates = topologyBounds.getLocation();
        // Retrieve the delta between desired topology position and current one
        final Dimension delta = new Dimension(
                ((viewDimension.width - topologyDimension.width) / 2) - topologyCoordinates.x,
                ((viewDimension.height - topologyDimension.height) / 2) - topologyCoordinates.y);
        // Convert the delta to child coordinate system
        displayer.getScalableLayers().translateFromParent(delta);

        // Apply the delta to each children
        for (final Object child : displayer.getContentLayer().getChildren()) {
            if (child instanceof LeafBodyFigure) {
                applyOffsetToFigure((IFigure) child, delta);
            } else if (child instanceof GroupBodyFigure) {
                final GroupBodyFigure figure = (GroupBodyFigure) child;
                // if a group is empty it should be moved manually as it as no children to follow
                if (figure.getGroup().getLeaves().isEmpty()) {
                    applyOffsetToFigure(figure, delta);
                }
            }
        }

        displayer.centerScrollBars();
    }

    private void applyOffsetToFigure(final IFigure figure, final Dimension delta) {
        final Point childBoundsCoordinates = figure.getBounds().getLocation();
        childBoundsCoordinates.x += delta.width;
        childBoundsCoordinates.y += delta.height;
        figure.setLocation(childBoundsCoordinates);
    }

    public void addListener(final Listener listener) {
        listeners.add(listener);
    }

}
