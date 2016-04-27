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
package com.intel.tools.fdk.graphframework.graph.action;

import com.intel.tools.fdk.graphframework.graph.IGroup;
import com.intel.tools.fdk.graphframework.graph.ILeaf;
import com.intel.tools.fdk.graphframework.graph.ILink;
import com.intel.tools.fdk.graphframework.graph.INode;
import com.intel.tools.fdk.graphframework.graph.INodeContainer;

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
     *            the node owning the connected output
     * @param outputId
     *            the connected output id
     * @param input
     *            the node owning the connected output
     * @param inputId
     *            the connected input id
     */
    void addLink(final ILeaf output, final int outputId, final ILeaf input, final int inputId);

    /**
     * Remove a Link of the Graph
     *
     * @param link
     *            the link to remove
     */
    void removeLink(final ILink link);

}
