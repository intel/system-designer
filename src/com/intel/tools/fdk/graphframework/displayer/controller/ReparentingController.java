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
package com.intel.tools.fdk.graphframework.displayer.controller;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;

import com.intel.tools.fdk.graphframework.displayer.GraphDisplayer;
import com.intel.tools.fdk.graphframework.figure.node.GroupBodyFigure;
import com.intel.tools.fdk.graphframework.figure.node.LeafBodyFigure;
import com.intel.tools.fdk.graphframework.graph.INode;
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

    public interface Listener {
        /**
         * This method is called when the controller detects a reparenting
         */
        void moveNode(final INode node, final INodeContainer oldParent, final INodeContainer newParent);
    }

    private final List<Listener> listeners = new ArrayList<>();
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
        INode node = null;
        if (figure instanceof LeafBodyFigure) {
            node = ((LeafBodyFigure) figure).getLeaf();
        } else if (figure instanceof GroupBodyFigure) {
            node = ((GroupBodyFigure) figure).getGroup();
        }

        final INodeContainer oldParent = node.getParent();
        final INodeContainer newParent = getTargetParent(destination);
        if (oldParent != newParent) {
            fireMoveNode(node, oldParent, newParent);
        }
    }

    public void fireMoveNode(final INode node, final INodeContainer oldParent, final INodeContainer newParent) {
        listeners.forEach(listener -> listener.moveNode(node, oldParent, newParent));
    }

    public void addListener(final Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(final Listener listener) {
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
