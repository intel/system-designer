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
package com.intel.tools.fdk.graphframework.layout;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.intel.tools.fdk.graphframework.graph.Graph;
import com.intel.tools.fdk.graphframework.graph.GraphException;
import com.intel.tools.fdk.graphframework.graph.Group;
import com.intel.tools.fdk.graphframework.graph.Leaf;
import com.intel.tools.fdk.graphframework.graph.Link;
import com.intel.tools.fdk.graphframework.graph.NodeContainer;

/** Copy the graph and give the impression that groups are simple nodes */
public class GraphCompacter {

    private final Map<Leaf, Leaf> leavesOrig = new HashMap<>();
    private final Map<Leaf, Leaf> leavesCopy;
    private final Map<Group, Leaf> compactedGroups;

    private final Graph compactedGraph;

    public GraphCompacter(final NodeContainer container) {
        this.compactedGroups = container.getGroups().stream().collect(
                Collectors.toMap(Function.identity(), this::compactGroup));
        this.leavesCopy = container.getLeaves().stream().collect(Collectors.toMap(Function.identity(),
                leaf -> new Leaf(leaf.getInputLinks().size(), leaf.getOutputLinks().size())));
        this.leavesCopy.forEach((key, value) -> leavesOrig.put(value, key));

        final Set<Link> linkToCopy = new HashSet<>(container.getLinks());
        container.getGroups().forEach(group -> linkToCopy.addAll(group.getExternalLinks()));
        linkToCopy.forEach(this::copyLink);

        this.compactedGraph = new Graph(Stream.concat(
                this.compactedGroups.values().stream(), this.leavesCopy.values().stream()).collect(Collectors.toList()),
                Collections.emptyList());
    }

    public Graph getCompactedGraph() {
        return compactedGraph;
    }

    public Leaf getOriginalOf(final Leaf copy) {
        return leavesOrig.get(copy);
    }

    public Leaf getCompactedGroup(final Group group) {
        return compactedGroups.get(group);
    }

    public Collection<Leaf> getCompactedGroups() {
        return compactedGroups.values();
    }

    private void copyLink(final Link link) {
        final Leaf input = link.getInputNode();
        final Leaf output = link.getOutputNode();
        int inputPinIndex = -1;
        Leaf inputCopy = leavesCopy.get(input);
        int outputPinIndex = -1;
        Leaf outputCopy = leavesCopy.get(output);

        if (inputCopy == null) {
            inputCopy = compactedGroups.get(input.getParent());
            inputPinIndex = inputCopy != null ? inputCopy.getInputLinks().indexOf(Optional.empty()) : -1;
        } else {
            inputPinIndex = input.getInputLinks().indexOf(Optional.of(link));
        }
        if (outputCopy == null) {
            outputCopy = compactedGroups.get(output.getParent());
            outputPinIndex = outputCopy != null ? outputCopy.getOutputLinks().indexOf(Optional.empty()) : -1;
        } else {
            outputPinIndex = output.getOutputLinks().indexOf(Optional.of(link));
        }

        try {
            if (inputPinIndex != -1 && outputPinIndex != -1) {
                outputCopy.connect(outputPinIndex, inputCopy, inputPinIndex);
            }
        } catch (final GraphException e) {
            assert false : "During graph copy, no connection errors can happen";
        }
    }

    private Leaf compactGroup(final Group group) {
        return new Leaf(getCompositeIOCounts(group, Leaf::getInputLinks, Link::getInputNode),
                getCompositeIOCounts(group, Leaf::getOutputLinks, Link::getOutputNode));
    }

    private static int getCompositeIOCounts(final Group subGraph, final Function<Leaf, List<Optional<Link>>> getter,
            final Function<Link, Leaf> nodeGetter) {
        final long freeIO = subGraph.getLeaves().stream()
                .map(getter)
                .flatMap(List::stream)
                .filter(edge -> !edge.isPresent())
                .count();
        final long externalLinkedIo = subGraph.getExternalLinks().stream()
                .filter(edge -> subGraph.getAllLeaves().contains(nodeGetter.apply(edge))).count();
        return (int) (freeIO + externalLinkedIo);
    }

}
