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
