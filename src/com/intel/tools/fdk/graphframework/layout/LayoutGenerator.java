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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.draw2d.IFigure;

import com.intel.tools.fdk.graphframework.displayer.GraphDisplayer;
import com.intel.tools.fdk.graphframework.figure.link.LinkFigure;
import com.intel.tools.fdk.graphframework.figure.presenter.GroupPresenter;
import com.intel.tools.fdk.graphframework.figure.presenter.LeafPresenter;
import com.intel.tools.fdk.graphframework.figure.presenter.Presenter;
import com.intel.tools.fdk.graphframework.graph.Graph;
import com.intel.tools.fdk.graphframework.graph.Group;
import com.intel.tools.fdk.graphframework.graph.INode;
import com.intel.tools.fdk.graphframework.graph.Leaf;
import com.intel.tools.fdk.graphframework.graph.adapter.IAdapter;
import com.intel.tools.fdk.graphframework.graph.adapter.IAdapter.IGraphListener;

/**
 * Class allowing to display a complete graph on a displayer
 */
public class LayoutGenerator implements IGraphListener {

    private final IAdapter adapter;
    private final GraphDisplayer displayer;
    private final Map<Leaf, LeafPresenter> leafPresenters = new HashMap<>();
    private final Map<Group, GroupPresenter> groupPresenters = new HashMap<>();

    /**
     * @param adapter
     *            the adapter to use to request the graph
     */
    public LayoutGenerator(final IAdapter adapter, final GraphDisplayer displayer) {
        this.adapter = adapter;
        this.adapter.addGraphListener(this);
        this.displayer = displayer;

        graphUpdated(this.adapter.getGraph());
    }

    @Override
    public void graphUpdated(final Graph graph) {
        displayer.reset();
        final Set<Leaf> leaves = graph.getAllLeaves();
        final Set<Group> groups = graph.getGroups();

        // Create new presenters
        leaves.forEach(leaf -> leafPresenters.putIfAbsent(leaf, adapter.createPresenter(leaf)));
        groups.forEach(group -> groupPresenters.putIfAbsent(group, adapter.createPresenter(group)));
        // Remove presenters of nodes which are not any more in the graph
        removeOldPresenters(leafPresenters, leaves);
        removeOldPresenters(groupPresenters, groups);

        // Display figures, groups are displayed first to let leaf be at first plan
        groupPresenters.values().forEach(this::displayPresenters);
        leafPresenters.values().forEach(this::displayPresenters);
        graph.getAllLinks().forEach(link -> {
            displayer.getConnectionLayer().add(new LinkFigure(
                    this.leafPresenters.get(link.getInputNode()).getAnchor(link),
                    this.leafPresenters.get(link.getOutputNode()).getAnchor(link)));
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

    private static <N extends INode, P extends Presenter<N>> void removeOldPresenters(
            final Map<N, P> presenters, final Set<N> nodes) {
        presenters.keySet().forEach(node -> {
            if (!nodes.contains(node)) {
                presenters.remove(node);
            }
        });
    }

    private void displayPresenters(final Presenter<?> presenter) {
        presenter.getDisplayableFigures().forEach(displayer.getContentLayer()::add);
        presenter.getDisplayableDecoration().forEach(displayer.getDecorationLayer()::add);
        presenter.getDisplayableTools().forEach(displayer.getToolsLayer()::add);
    }

    /**
     * Retrieve generated presenters
     *
     * @return presenters which are or will be displayed
     */
    protected Collection<LeafPresenter> getLeafPresenters() {
        return this.leafPresenters.values();
    }

}
