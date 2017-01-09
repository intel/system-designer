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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;

import com.intel.tools.fdk.graphframework.displayer.GraphDisplayer;
import com.intel.tools.fdk.graphframework.figure.node.GroupBodyFigure;
import com.intel.tools.fdk.graphframework.figure.node.LeafBodyFigure;
import com.intel.tools.fdk.graphframework.graph.IGroup;
import com.intel.tools.fdk.graphframework.graph.ILeaf;
import com.intel.tools.fdk.graphframework.graph.INodeContainer;
import com.intel.tools.fdk.graphframework.graph.adapter.IAdapter;

/**
 * Controller responsible for moving a node from a parent group to another. The displaced node can be a leaf or a group.
 *
 * This controller does not handle the moving of the figures, which is handled by {@link NodeMoveController}. Instead it
 * is triggered by {@link NodeMoveController} and calls its listeners if the current figure move implies a re-parenting.
 *
 * The {@link NodeMoveController} must be set with useGhost = true to function properly with the
 * {@link ReparentingController}
 */
public class ReparentingController implements NodeMoveController.FigureMoveListener {

    public interface ReparentingListener {
        /**
         * This method is called when the controller detects a reparenting of a leaf
         *
         * @param leaf
         *            the moved leaf
         * @param oldParent
         *            the old parent
         * @param newParent
         *            the new parent proposal
         * @param newLocation
         *            the new location of the node figure
         */
        void move(final ILeaf leaf, final INodeContainer oldParent, final INodeContainer newParent,
                final Point newLocation);

        /**
         * This method is called when the controller detects a reparenting of a group
         *
         * @param group
         *            the moved group
         * @param oldParent
         *            the old parent
         * @param newParent
         *            the new parent proposal
         * @param newLocation
         *            the new location of the node figure
         */
        void move(final IGroup group, final INodeContainer oldParent, final INodeContainer newParent,
                final Point newLocation);
    }

    private final List<ReparentingListener> listeners = new ArrayList<>();
    private final GraphDisplayer displayer;

    /** The provider for the graph. The graph is used as last parent if no parent figure is found */
    private IAdapter graphAdapter;

    /**
     * Creates a new Reparenting controller
     *
     * @param displayer
     *            the displayer containing figures to observe
     */
    public ReparentingController(final GraphDisplayer displayer) {
        this.displayer = displayer;
    }

    @Override
    public void figureMoved(final IFigure figure, final Point destination) {
        final INodeContainer newParent = getTargetParent(destination);
        if (figure instanceof LeafBodyFigure) {
            final ILeaf node = ((LeafBodyFigure) figure).getLeaf();
            final INodeContainer oldParent = node.getParent();
            if (oldParent != newParent && newParent != node) {
                listeners.forEach(listener -> listener.move(node, oldParent, newParent, destination));
            }
        } else if (figure instanceof GroupBodyFigure) {
            final IGroup node = ((GroupBodyFigure) figure).getGroup();
            final INodeContainer oldParent = node.getParent();
            if (oldParent != newParent && newParent != node) {
                listeners.forEach(listener -> listener.move(node, node.getParent(), newParent, destination));
            }
        }

    }

    public void addListener(final ReparentingListener listener) {
        listeners.add(listener);
    }

    public void removeListener(final ReparentingListener listener) {
        listeners.remove(listener);
    }

    private INodeContainer getTargetParent(final Point destination) {
        final IFigure targetGroupFigure = displayer.getContentLayer().findFigureAt(destination.x, destination.y,
                new TypeTreeSearch(GroupBodyFigure.class));
        if (targetGroupFigure != null) {
            return ((GroupBodyFigure) targetGroupFigure).getGroup();
        } else {
            // If we have no parent figure, that means the final parent is the graph itself
            return graphAdapter.getGraph();
        }
    }

    public IAdapter getGraphAdapter() {
        return graphAdapter;
    }

    public void setGraphAdapter(final IAdapter graphAdapter) {
        this.graphAdapter = graphAdapter;
    }

}
