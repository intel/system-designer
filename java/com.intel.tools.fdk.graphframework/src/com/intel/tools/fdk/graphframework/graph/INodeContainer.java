/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
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
