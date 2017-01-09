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
package com.intel.tools.fdk.graphframework.graph;

import java.util.Set;

/**
 * Container of {@link INode} objects
 *
 * This interface is not intended to be implemented by clients.
 */
public interface INodeContainer extends IGraphElement {

    /**
     * Allows to listen events (add/remove nodes) on a node container
     */
    public interface INodeContainerListener {
        /**
         * @param addedLeaf
         *            the leaf which has been added has child
         */
        void leafAdded(final ILeaf addedLeaf);

        /**
         * @param addedGroup
         *            the group which has been added has child
         */
        void groupAdded(final IGroup addedGroup);

        /**
         * @param removedLeaf
         *            the leaf which has been removed has child
         */
        void leafRemoved(final ILeaf addedLeaf);

        /**
         * @param removedGroup
         *            the group which has been removed has child
         */
        void groupRemoved(final IGroup removedGroup);
    }

    /**
     * @return an unmodifiable set of leaf nodes which compose this graph.
     */
    Set<? extends ILeaf> getLeaves();

    /**
     * @return an unmodifiable set of group nodes which compose this graph.
     */
    Set<? extends IGroup> getGroups();

    /**
     * @return a set containing all {@link ILink} which interconnect {@link ILeaf} nodes of this graph.
     */
    Set<? extends ILink> getLinks();

    /**
     * Add a new leaf to this container
     *
     * @param leaf
     *            the added leaf
     */
    void add(final ILeaf leaf);

    /**
     * Add a new group to this container
     *
     * @param group
     *            the added leaf
     */
    void add(final IGroup group);

    /**
     * Remove a leaf from this container
     *
     * @param leaf
     *            the added leaf
     */
    void remove(final ILeaf leaf);

    /**
     * Remove a group from this container
     *
     * @param group
     *            the removed leaf
     */
    void remove(final IGroup group);

    /**
     * Register a new node container listener
     *
     * @param listener
     *            the new listener
     */
    void addListener(final INodeContainerListener listener);

    /**
     * Unregister a new node container listener
     *
     * @param listener
     *            the listener to remove
     */
    void removeListener(final INodeContainerListener listener);

}
