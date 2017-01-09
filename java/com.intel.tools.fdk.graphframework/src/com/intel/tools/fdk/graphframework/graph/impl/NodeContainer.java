/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.tools.fdk.graphframework.graph.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.intel.tools.fdk.graphframework.graph.IGroup;
import com.intel.tools.fdk.graphframework.graph.ILeaf;
import com.intel.tools.fdk.graphframework.graph.INode;
import com.intel.tools.fdk.graphframework.graph.INodeContainer;
import com.intel.tools.fdk.graphframework.graph.Style;

/**
 * Container of {@link INode} objects
 */
public abstract class NodeContainer implements INodeContainer {

    private final Style style = new Style();

    private final List<INodeContainerListener> listeners = new ArrayList<>();

    private final Set<Leaf> leaves = new HashSet<>();
    private final Set<Group> groups = new HashSet<>();

    /**
     * Create a new container.
     *
     * @param leaves
     *            leaf nodes of the graph. All nodes of the list are added in the internal one.
     * @param groups
     *            group nodes of the graph. All nodes of the list are added in the internal one.
     */
    public NodeContainer(final Set<Leaf> leaves, final Set<Group> groups) {
        leaves.forEach(this::add);
        groups.forEach(this::add);
    }

    /**
     * @return an unmodifiable set of leaf nodes which compose this graph.
     */
    @Override
    public Set<Leaf> getLeaves() {
        return Collections.unmodifiableSet(leaves);
    }

    /**
     * @return an unmodifiable set of leaf nodes which compose this graph (including leaves of {@link Group} children.
     */
    public Set<Leaf> getAllLeaves() {
        return Stream.concat(leaves.stream(), groups.stream().flatMap(group -> group.getAllLeaves().stream()))
                .collect(Collectors.toSet());
    }

    /**
     * @return an unmodifiable set of group nodes which compose this graph.
     */
    @Override
    public Set<Group> getGroups() {
        return Collections.unmodifiableSet(groups);
    }

    /**
     * @return a set containing all {@link Link} which interconnect {@link Leaf} nodes of this graph.
     */
    @Override
    public Set<Link> getLinks() {
        return leaves.stream()
                .flatMap(node -> Stream.concat(
                        node.getLinkedInputLinks().stream(), node.getLinkedOutputLinks().stream()))
                .collect(Collectors.toSet());
    }

    /**
     * @return a set containing all {@link Link} of this graph (even those which interconnects {@link Leaf} of
     *         {@link Group} children.
     */
    public Set<Link> getAllLinks() {
        return Stream.concat(getLinks().stream(), getGroups().stream().flatMap(group -> group.getAllLinks().stream()))
                .collect(Collectors.toSet());
    }

    /**
     * @return a set containing all {@link Link} which interconnect {@link Leaf} of this container and {@link Group}
     *         children or parent leaves.
     */
    public Set<Link> getExternalLinks() {
        return getAllLinks().stream()
                .filter(link -> !(getAllLeaves().contains(link.getInput().getLeaf())
                        && getAllLeaves().contains(link.getOutput().getLeaf())))
                .collect(Collectors.toSet());
    }

    @Override
    public void add(final ILeaf leaf) {
        final Leaf leafToAdd = (Leaf) leaf;
        leafToAdd.setParent(this);
        this.leaves.add(leafToAdd);
        listeners.forEach(l -> l.leafAdded(leaf));
    }

    @Override
    public void add(final IGroup group) {
        final Group groupToAdd = (Group) group;
        groupToAdd.setParent(this);
        this.groups.add(groupToAdd);
        listeners.forEach(l -> l.groupAdded(group));
    }

    @Override
    public void remove(final ILeaf leaf) {
        final Leaf leafToRemove = (Leaf) leaf;
        if (this.leaves.remove(leafToRemove)) {
            leafToRemove.setParent(null);
            listeners.forEach(l -> l.leafRemoved(leaf));
        }
    }

    @Override
    public void remove(final IGroup group) {
        final Group groupToRemove = (Group) group;
        if (this.groups.remove(groupToRemove)) {
            groupToRemove.setParent(null);
            listeners.forEach(l -> l.groupRemoved(group));
        }
    }

    @Override
    public void addListener(final INodeContainerListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(final INodeContainerListener listener) {
        listeners.remove(listener);
    }

    @Override
    public Style getStyle() {
        return style;
    }

}
