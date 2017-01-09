/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.tools.fdk.graphframework.layout;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;

import com.intel.tools.fdk.graphframework.displayer.GraphDisplayer;
import com.intel.tools.fdk.graphframework.figure.link.LinkFigure;
import com.intel.tools.fdk.graphframework.figure.presenter.DefaultPresenterManager;
import com.intel.tools.fdk.graphframework.figure.presenter.GroupPresenter;
import com.intel.tools.fdk.graphframework.figure.presenter.IPresenterManager;
import com.intel.tools.fdk.graphframework.figure.presenter.LeafPresenter;
import com.intel.tools.fdk.graphframework.figure.presenter.Presenter;
import com.intel.tools.fdk.graphframework.graph.IGraph;
import com.intel.tools.fdk.graphframework.graph.INode;
import com.intel.tools.fdk.graphframework.graph.adapter.IAdapter;
import com.intel.tools.fdk.graphframework.graph.adapter.IAdapter.IGraphListener;
import com.intel.tools.fdk.graphframework.graph.impl.Graph;
import com.intel.tools.fdk.graphframework.graph.impl.Group;
import com.intel.tools.fdk.graphframework.graph.impl.Leaf;

/**
 * Class allowing to display a complete graph on a displayer
 */
public class LayoutGenerator implements IGraphListener {

    private final IAdapter adapter;
    private final GraphDisplayer displayer;
    private final Map<Leaf, LeafPresenter> leafPresenters = new HashMap<>();
    private final Map<Group, GroupPresenter> groupPresenters = new HashMap<>();
    private final IPresenterManager presenterManager;

    /** The hotpoint is the next location which will be used for a new presenter */
    private final Point hotPoint = new Point(0, 0);

    /**
     * Constructor using default {@link IPresenterManager} implementation.
     *
     * @param adapter
     *            the adapter to use to request the graph
     * @param displayer
     *            The displayer where to display the graph.
     */
    public LayoutGenerator(final IAdapter adapter, final GraphDisplayer displayer) {
        this(adapter, new DefaultPresenterManager(), displayer);
    }

    /**
     * Constructor containing all available configuration.
     *
     * @param adapter
     *            the adapter to use to request the graph
     * @param presenterManager
     *            The presenter manager responsible for instantiating any presenter for a node
     * @param displayer
     *            The displayer where to display the graph.
     */
    public LayoutGenerator(final IAdapter adapter, final IPresenterManager presenterManager,
            final GraphDisplayer displayer) {
        this.adapter = adapter;
        this.adapter.addGraphListener(this);
        this.displayer = displayer;
        this.presenterManager = presenterManager;

        graphUpdated(adapter.getGraph());
    }

    @Override
    public final void graphUpdated(final IGraph newGraph) {
        displayer.reset();
        final Graph graph = getGraph();
        final Set<Leaf> leaves = graph.getAllLeaves();
        final Set<Group> groups = graph.getGroups();

        // Create new presenters
        leaves.forEach(leaf -> leafPresenters.computeIfAbsent(leaf,
                key -> setupNewPresenter(presenterManager.getPresenter(key))));
        groups.forEach(group -> groupPresenters.computeIfAbsent(group,
                key -> setupNewPresenter(presenterManager.getPresenter(key))));
        // Remove presenters of nodes which are not any more in the graph
        removeOldPresenters(leafPresenters, leaves);
        removeOldPresenters(groupPresenters, groups);

        // Display figures, groups are displayed first to let leaf be at first plan
        groupPresenters.values().forEach(this::displayPresenters);
        leafPresenters.values().forEach(this::displayPresenters);
        graph.getAllLinks().forEach(link -> {
            displayer.getConnectionLayer().add(new LinkFigure(link,
                    this.leafPresenters.get(link.getInput().getLeaf()).getAnchor(link.getInput()),
                    this.leafPresenters.get(link.getOutput().getLeaf()).getAnchor(link.getOutput())));
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

    private static <N extends INode> void removeOldPresenters(
            final Map<N, ? extends Presenter<? extends INode>> presenters, final Set<N> nodes) {
        final List<N> keysToRemove = presenters.keySet().stream().filter(node -> !nodes.contains(node))
                .collect(Collectors.toList());
        keysToRemove.forEach(node -> presenters.remove(node));
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

    /**
     * Retrieve generated presenters
     *
     * @return presenters which are or will be displayed
     */
    protected Collection<GroupPresenter> getGroupPresenters() {
        return this.groupPresenters.values();
    }

    /**
     * @return the current graph
     */
    protected Graph getGraph() {
        return (Graph) adapter.getGraph();
    }

    /**
     * Setup a new presenter
     *
     * @param presenter
     *            the newly added presenter
     * @return presenter parameter for convenience
     */
    private <T extends Presenter<?>> T setupNewPresenter(final T presenter) {
        presenter.getNodeBody().setLocation(hotPoint);
        return presenter;
    }

    /**
     * @param hotPoint
     *            the point where the next presenter which will be registered will be located
     */
    public void setHotPoint(final Point hotPoint) {
        this.hotPoint.setLocation(hotPoint);
    }

}
