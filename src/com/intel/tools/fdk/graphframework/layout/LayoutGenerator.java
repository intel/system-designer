/* ============================================================================
 * INTEL CONFIDENTIAL
 *
 * Copyright 2016 Intel Corporation All Rights Reserved.
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
package com.intel.tools.fdk.graphframework.layout;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.draw2d.IFigure;

import com.intel.tools.fdk.graphframework.displayer.GraphDisplayer;
import com.intel.tools.fdk.graphframework.figure.controller.NodeController;
import com.intel.tools.fdk.graphframework.figure.edge.EdgeFigure;
import com.intel.tools.fdk.graphframework.graph.Graph;
import com.intel.tools.fdk.graphframework.graph.GraphException;
import com.intel.tools.fdk.graphframework.graph.Node;
import com.intel.tools.fdk.graphframework.graph.factory.IGraphFactory;

/**
 * Class allowing to display a complete graph on a displayer
 *
 * @param <T>
 *            type of node controller used
 */
public class LayoutGenerator<T extends NodeController> {

    private final Graph graph;
    private final Map<Node, T> controllers = new HashMap<>();

    /**
     * @param graphFactory
     *            the factory to use to create the graph
     */
    public LayoutGenerator(final IGraphFactory<T> graphFactory) {
        this.graph = graphFactory.createGraph();
        this.graph.getNodes().forEach(node -> controllers.put(node, graphFactory.createController(node)));
    }

    /**
     * Display the graph.
     *
     * The displayer will be reset before any action.
     *
     * @param displayer
     *            the displayer to diplay the graph on
     * @throws GraphException
     *             if an error occurs during graph generation
     */
    public void displayGraph(final GraphDisplayer displayer) throws GraphException {
        displayer.reset();

        // Display figures
        getControllers().forEach(controller -> {
            controller.getDisplayableFigures().forEach(displayer.getContentLayer()::add);
            controller.getDisplayableDecoration().forEach(displayer.getDecorationLayer()::add);
            controller.getDisplayableTools().forEach(displayer.getToolsLayer()::add);
        });
        this.graph.getEdges().forEach(edge -> {
            displayer.getConnectionLayer().add(new EdgeFigure(
                    this.controllers.get(edge.getInputNode()).getAnchor(edge),
                    this.controllers.get(edge.getOutputNode()).getAnchor(edge)));
        });

        /**
         * Listen for scale update to recalculate decoration position.</br>
         * This is done by forcing the each figure to notify their listeners by calling figure translate method
         */
        displayer.addPropertyChangeListener(new PropertyChangeListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void propertyChange(final PropertyChangeEvent event) {
                if (event.getSource().equals(displayer)
                        && event.getPropertyName().equals(GraphDisplayer.SCALE_PROPERTY)) {
                    displayer.getContentLayer().getChildren().forEach(figure -> ((IFigure) figure).translate(0, 0));
                }
            }
        });
    }

    /**
     * Retrieve generated controllers
     *
     * @return controllers which are or will be displayed
     */
    public List<T> getControllers() {
        return this.controllers.values().stream().collect(Collectors.toList());
    }

    /**
     * @return the generated graph which is or will be displayed.
     */
    protected Graph getGraph() {
        return this.graph;
    }

}
