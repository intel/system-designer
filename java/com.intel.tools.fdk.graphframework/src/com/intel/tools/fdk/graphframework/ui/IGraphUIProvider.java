/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.tools.fdk.graphframework.ui;

import org.eclipse.swt.widgets.Composite;

import com.intel.tools.fdk.graphframework.graph.IGraph;
import com.intel.tools.fdk.graphframework.graph.IGroup;
import com.intel.tools.fdk.graphframework.graph.ILeaf;
import com.intel.tools.fdk.graphframework.graph.ILink;
import com.intel.tools.fdk.graphframework.graph.INode;

/**
 * This interface provides a UI for any object in the graph framework. <br>
 * It should be able to build a UI for :<br>
 * <li>{@link INode}
 * <li>{@link ILink}
 * <li>{@link IGraph}
 * <li>{@link IGroup}
 * <li>IPaletteEntry?
 *
 * When a createUI method is called, the parent composite is empty, and the layout may be changed the the client.
 */
public interface IGraphUIProvider {
    /**
     * Create a UI for a given leaf node.
     *
     * @param parent
     *            The parent SWT composite.
     * @param node
     *            The node for which to build the UI.
     */
    void createUI(Composite parent, ILeaf leafNode);

    /**
     * Create a UI for a given link.
     *
     * @param parent
     *            The parent SWT composite.
     * @param link
     *            The link for which to build the UI.
     */
    void createUI(Composite parent, ILink link);

    /**
     * Create a UI for a given Graph.
     *
     * @param parent
     *            The parent SWT composite.
     * @param graph
     *            The graph for which to build the UI.
     */
    void createUI(Composite parent, IGraph graph);

    /**
     * Create a UI for a given group.
     *
     * @param parent
     *            The parent SWT composite.
     * @param graph
     *            The group for which to build the UI.
     */
    void createUI(Composite parent, IGroup group);
}
