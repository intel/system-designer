/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.tools.fdk.graphframework.graph.action;

import com.intel.tools.fdk.graphframework.graph.IGroup;
import com.intel.tools.fdk.graphframework.graph.IInput;
import com.intel.tools.fdk.graphframework.graph.ILeaf;
import com.intel.tools.fdk.graphframework.graph.ILink;
import com.intel.tools.fdk.graphframework.graph.INode;
import com.intel.tools.fdk.graphframework.graph.INodeContainer;
import com.intel.tools.fdk.graphframework.graph.IOutput;

/**
 * Interface of actions available on an editable Graph
 */
public interface IGraphAction<T> {

    /**
     * Add a new node on the graph
     *
     * @param item
     *            the item describing the type to add
     * @param parent
     *            the parent node of the new node to add to the graph
     */
    void addNode(final T item, INodeContainer parent);

    /**
     * Remove a node from the Graph
     *
     * @param node
     *            the node to remove from the Graph
     */
    void removeNode(final INode node);

    /**
     * Move a leaf into another parent container
     *
     * @param leaf
     *            the leaf to move
     * @param container
     *            the new parent destination
     */
    void moveLeaf(final ILeaf leaf, final INodeContainer container);

    /**
     * Move a group into another parent container
     *
     * @param group
     *            the group to move
     * @param container
     *            the new parent destination
     */
    void moveGroup(final IGroup group, final INodeContainer container);

    /**
     * Add a new Link between two node Leaves of the Graph
     *
     * @param output
     *            the connected output pin
     * @param input
     *            the connected input pin
     */
    void addLink(final IOutput output, final IInput input);

    /**
     * Remove a Link of the Graph
     *
     * @param link
     *            the link to remove
     */
    void removeLink(final ILink link);

}
