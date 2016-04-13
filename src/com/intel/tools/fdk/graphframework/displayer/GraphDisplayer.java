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
package com.intel.tools.fdk.graphframework.displayer;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayeredPane;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.ScalableFreeformLayeredPane;
import org.eclipse.draw2d.Viewport;
import org.eclipse.swt.widgets.Composite;

import com.intel.tools.fdk.graphframework.displayer.layer.BackgroundLayer;

/** Hold a canvas which can display connected component */
public class GraphDisplayer {

    /** Name of the pane containing all scalable layers. */
    private static final String SCALABLE_PANE = "scalable";

    /** Name of the layer containing the background */
    private static final String BACKGROUND_LAYER = "background";
    /** Name of the layer containing main component */
    private static final String CONTENT_LAYER = "content";
    /** Name of the layer containing connection between component of content layer */
    private static final String CONNECTION_LAYER = "connection";
    /** Name of the layer containing indication for the user */
    private static final String FEEDBACK_LAYER = "feedback";
    /** Name of the layer containing decoration of content. This layer is not scaled */
    private static final String DECORATION_LAYER = "decoration";
    /** Name of the layer containing tools (probes, etc...) of content. This layer is not scaled */
    private static final String TOOLS_LAYER = "tools";

    /** Property allowing to subscribe to display scale updates */
    public static final String SCALE_PROPERTY = "scale";

    private final PropertyChangeSupport changeSupport;

    private final FigureCanvas canvas;
    private final FreeformLayeredPane layers = new FreeformLayeredPane();
    private final ScalableFreeformLayeredPane scalablePane = new ScalableFreeformLayeredPane();

    public GraphDisplayer(final Composite parent, final int style) {
        this.changeSupport = new PropertyChangeSupport(this);
        // Create a new canvas with scrollbars
        canvas = new FigureCanvas(parent, style);
        canvas.setScrollBarVisibility(FigureCanvas.AUTOMATIC);

        // Add needed layers
        scalablePane.add(new BackgroundLayer(), BACKGROUND_LAYER);
        scalablePane.addLayerAfter(new ConnectionLayer(), CONNECTION_LAYER, BACKGROUND_LAYER);
        scalablePane.addLayerAfter(new FreeformLayer(), CONTENT_LAYER, CONNECTION_LAYER);
        scalablePane.addLayerAfter(new FreeformLayer(), FEEDBACK_LAYER, CONTENT_LAYER);
        scalablePane.getLayer(CONTENT_LAYER).setLayoutManager(new FreeformLayout());
        ((ConnectionLayer) scalablePane.getLayer(CONNECTION_LAYER)).setConnectionRouter(
                new ManhattanConnectionRouter());

        // Add the scalable pane to the main layer
        layers.add(scalablePane, SCALABLE_PANE);
        // Add the decoration pane which is not scalable
        layers.addLayerAfter(new FreeformLayer(), DECORATION_LAYER, SCALABLE_PANE);
        // Add the tools pane which is not scalable
        layers.addLayerAfter(new FreeformLayer(), TOOLS_LAYER, DECORATION_LAYER);

        // Associate the viewport to the canvas
        final Viewport viewport = new FreeformViewport();
        viewport.setContents(layers);
        canvas.setViewport(viewport);
    }

    public FigureCanvas getControl() {
        return canvas;
    }

    /** Retrieves Layer holder of this displayer */
    public Layer getScalableLayers() {
        return scalablePane;
    }

    /** Main content layer
     *  This layer will hold components which are really manipulated
     *
     * @return the content layer
     */
    public FreeformLayer getContentLayer() {
        return (FreeformLayer) scalablePane.getLayer(CONTENT_LAYER);
    }

    /** Retrieves the feedback layer
     *  This layer hold decoration which gives information to the user.
     *  This layer is not intended to be clickable.
     *
     * @return the feedback layer
     */
    public IFigure getFeedbackLayer() {
        return scalablePane.getLayer(FEEDBACK_LAYER);
    }

    /** Retrieves the background layer.
     *  This layer is under all other layer and all click events which are not captured by upper layers
     *  are forwarded to this one.
     *  This layer will detect any click event which come to it.
     *
     * @return the background layer
     */
    public IFigure getBackgroundLayer() {
        return scalablePane.getLayer(BACKGROUND_LAYER);
    }

    /** Retrieves the connection layer
     *  This layer is right under the content layer. It aims to hold all connections between components.
     *
     * @return the connection layer
     */
    public IFigure getConnectionLayer() {
        return scalablePane.getLayer(CONNECTION_LAYER);
    }

    /**
     * Retrieves the decoration layer This layer is not scalable.
     *
     * @return the decoration layer
     */
    public IFigure getDecorationLayer() {
        return layers.getLayer(DECORATION_LAYER);
    }

    /**
     * Retrieves the tools layer This layer is not scalable. It is over all other layers.
     *
     * @return the tools layer
     */
    public Layer getToolsLayer() {
        return layers.getLayer(TOOLS_LAYER);
    }

    /** Retrieves the current scale of the layers holder */
    public double getScale() {
        return scalablePane.getScale();
    }

    /** Set a new scale value to the layers holder */
    public void setScale(final double newZoom) {
        final double old = scalablePane.getScale();
        scalablePane.setScale(newZoom);
        changeSupport.firePropertyChange(SCALE_PROPERTY, old, newZoom);
    }

    public void reset() {
        getContentLayer().removeAll();
        scalablePane.getLayer(CONNECTION_LAYER).removeAll();
        scalablePane.getLayer(FEEDBACK_LAYER).removeAll();
        scalablePane.getLayer(BACKGROUND_LAYER).removeAll();
        layers.getLayer(DECORATION_LAYER).removeAll();
        layers.getLayer(TOOLS_LAYER).removeAll();
        for (final PropertyChangeListener listener : changeSupport.getPropertyChangeListeners()) {
            changeSupport.removePropertyChangeListener(listener);
        }
    }

    public synchronized void addPropertyChangeListener(final PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public synchronized void removePropertyChangeListener(final PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

}
