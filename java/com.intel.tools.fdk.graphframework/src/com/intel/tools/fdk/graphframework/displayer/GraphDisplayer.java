/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.tools.fdk.graphframework.displayer;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayeredPane;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.ScalableFreeformLayeredPane;
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

    private final FDKViewPort viewport;

    public GraphDisplayer(final Composite parent, final int style) {
        this.changeSupport = new PropertyChangeSupport(this);
        // Create a new canvas with scrollbars
        canvas = new FigureCanvas(parent, style);
        canvas.setScrollBarVisibility(FigureCanvas.AUTOMATIC);

        // Add needed layers
        scalablePane.add(new BackgroundLayer(), BACKGROUND_LAYER);
        scalablePane.addLayerAfter(new FreeformLayer(), CONTENT_LAYER, BACKGROUND_LAYER);
        scalablePane.addLayerAfter(new ConnectionLayer(), CONNECTION_LAYER, CONTENT_LAYER);
        scalablePane.addLayerAfter(new FreeformLayer(), FEEDBACK_LAYER, CONNECTION_LAYER);
        scalablePane.getLayer(CONTENT_LAYER).setLayoutManager(new FreeformLayout());
        getConnectionLayer().setConnectionRouter(new ManhattanConnectionRouter());

        // Add the scalable pane to the main layer
        layers.add(scalablePane, SCALABLE_PANE);
        // Add the decoration pane which is not scalable
        layers.addLayerAfter(new FreeformLayer(), DECORATION_LAYER, SCALABLE_PANE);
        // Add the tools pane which is not scalable
        layers.addLayerAfter(new FreeformLayer(), TOOLS_LAYER, DECORATION_LAYER);

        viewport = new FDKViewPort();
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

    /**
     * Main content layer This layer will hold components which are really manipulated
     *
     * @return the content layer
     */
    public FreeformLayer getContentLayer() {
        return (FreeformLayer) scalablePane.getLayer(CONTENT_LAYER);
    }

    /**
     * Retrieves the feedback layer This layer hold decoration which gives information to the user. This layer is not
     * intended to be clickable.
     *
     * @return the feedback layer
     */
    public IFigure getFeedbackLayer() {
        return scalablePane.getLayer(FEEDBACK_LAYER);
    }

    /**
     * Retrieves the background layer. This layer is under all other layer and all click events which are not captured
     * by upper layers are forwarded to this one. This layer will detect any click event which come to it.
     *
     * @return the background layer
     */
    public IFigure getBackgroundLayer() {
        return scalablePane.getLayer(BACKGROUND_LAYER);
    }

    /**
     * Retrieves the connection layer This layer is right under the content layer. It aims to hold all connections
     * between components.
     *
     * @return the connection layer
     */
    public ConnectionLayer getConnectionLayer() {
        return (ConnectionLayer) scalablePane.getLayer(CONNECTION_LAYER);
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

    /**
     * Set to force the scroll bar visibility for the displayer.
     */
    public void forceScrollBarVisible(final boolean visibility) {
        if (visibility) {
            canvas.setHorizontalScrollBarVisibility(FigureCanvas.ALWAYS);
            canvas.setVerticalScrollBarVisibility(FigureCanvas.ALWAYS);
        } else {
            canvas.setHorizontalScrollBarVisibility(FigureCanvas.AUTOMATIC);
            canvas.setVerticalScrollBarVisibility(FigureCanvas.AUTOMATIC);
        }
    }

    /**
     * Specify a margin around the displayed figures to force over scrolling
     */
    public void setViewportMargin(final int margin) {
        viewport.setMargin(margin);
    }

    /**
     * Put scroll bars at the center of their space
     */
    public void centerScrollBars() {
        viewport.getVerticalRangeModel().setValue(0);
        viewport.getHorizontalRangeModel().setValue(0);
    }

}
