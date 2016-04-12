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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Container of {@link INode} objects
 */
public abstract class NodeContainer {

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
        this.leaves.addAll(leaves);
        this.leaves.forEach(leaf -> leaf.setParent(this));
        this.groups.addAll(groups);
        this.groups.forEach(group -> group.setParent(this));
    }

    /**
     * @return an unmodifiable list of leaf nodes which compose this graph.
     */
    public Set<Leaf> getLeaves() {
        return Collections.unmodifiableSet(leaves);
    }

    /**
     * @return an unmodifiable list of leaf nodes which compose this graph (including leaves of {@link Group} children.
     */
    public Set<Leaf> getAllLeaves() {
        return Stream.concat(leaves.stream(), groups.stream().flatMap(group -> group.getAllLeaves().stream()))
                .collect(Collectors.toSet());
    }

    /**
     * @return an unmodifiable list of group nodes which compose this graph.
     */
    public Set<Group> getGroups() {
        return Collections.unmodifiableSet(groups);
    }

    /**
     * @return a set containing all {@link Link} which interconnect {@link Leaf} nodes of this graph.
     */
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
                .filter(link -> !(getAllLeaves().contains(link.getInputNode())
                        && getAllLeaves().contains(link.getOutputNode())))
                .collect(Collectors.toSet());
    }

}
