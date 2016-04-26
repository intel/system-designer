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

import com.intel.tools.fdk.graphframework.graph.GraphException;
import com.intel.tools.fdk.graphframework.graph.factory.GraphFactory;
import com.intel.tools.fdk.graphframework.graph.impl.Graph;
import com.intel.tools.fdk.graphframework.graph.impl.Group;
import com.intel.tools.fdk.graphframework.graph.impl.Input;
import com.intel.tools.fdk.graphframework.graph.impl.Leaf;
import com.intel.tools.fdk.graphframework.graph.impl.Link;
import com.intel.tools.fdk.graphframework.graph.impl.NodeContainer;
import com.intel.tools.fdk.graphframework.graph.impl.Output;
import com.intel.tools.fdk.graphframework.graph.impl.Pin;

/** Copy the graph and give the impression that groups are simple nodes */
public class GraphCompacter {

    private final Map<Leaf, Leaf> leavesOrig = new HashMap<>();
    private final Map<Leaf, Leaf> leavesCopy;
    private final Map<Group, Leaf> compactedGroups;

    private final Graph compactedGraph;

    public GraphCompacter(final NodeContainer container) {
        this.compactedGroups = container.getGroups().stream().collect(
                Collectors.toMap(Function.identity(), this::compactGroup));
        this.leavesCopy = container.getLeaves().stream().collect(Collectors.toMap(Function.identity(), Leaf::new));
        this.leavesCopy.forEach((key, value) -> leavesOrig.put(value, key));

        final Set<Link> linkToCopy = new HashSet<>(container.getLinks());
        container.getGroups().forEach(group -> linkToCopy.addAll(group.getExternalLinks()));
        linkToCopy.forEach(this::copyLink);

        this.compactedGraph = new Graph(Stream.concat(
                this.compactedGroups.values().stream(), this.leavesCopy.values().stream()).collect(Collectors.toSet()),
                Collections.emptySet());
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
        final Optional<Input> input = retrieveCopiedPin(link.getInput(), Leaf::getInputs);
        final Optional<Output> output = retrieveCopiedPin(link.getOutput(), Leaf::getOutputs);

        try {
            if (input.isPresent() && output.isPresent()) {
                GraphFactory.createLink(input.get(), output.get());
            }
        } catch (final GraphException e) {
            assert false : "During graph copy, no connection errors can happen";
        }
    }

    private Leaf compactGroup(final Group group) {
        return new Leaf(getCompositeIOCounts(group, Leaf::getInputs, Link::getInput),
                getCompositeIOCounts(group, Leaf::getOutputs, Link::getOutput));
    }

    /**
     * Count input or outputs of a compacted Node
     *
     * The calculated count correspond to all Group leaves free input/output and all connected input/output which are
     * going outside the group.
     *
     ********
     * FIXME: The right prototype of the method sould be:</br>
     * private static <IOType extends Pin> int getCompositeIOCounts(final Group subGraph, </br>
     * final Function<Leaf, List<IOType>> getter, final Function<Link, IOType> pinGetter) { </br>
     *
     * Due to a compiler issue, this prototype compile in eclipse but not with Maven. In a wait for a fix we can keep
     * this method as it. Nevertheless, we have to be careful as we can use this method with a pin list getter which
     * give Inputs and a pinGetter which give Outputs. This behavior is always wrong and should be avoided.
     ********
     *
     * @param subGraph
     *            the group to compact
     * @param getter
     *            the getter allowing to retrieve inputs or outputs of leaves
     * @param pinGetter
     *            the getter allowing to retrieve an input or an output of a leaves
     * @return the count of inputs or outputs (depending on arguments) of the compacted group
     */
    private static int getCompositeIOCounts(final Group subGraph,
            final Function<Leaf, List<? extends Pin>> getter, final Function<Link, Pin> pinGetter) {
        final long freeIO = subGraph.getLeaves().stream()
                .map(getter)
                .flatMap(List::stream)
                .map(Pin::getLink)
                .filter(link -> !link.isPresent())
                .count();
        final long externalLinkedIo = subGraph.getExternalLinks().stream()
                .filter(link -> subGraph.getAllLeaves().contains(pinGetter.apply(link).getLeaf())).count();
        return (int) (freeIO + externalLinkedIo);
    }

    /**
     * Retrieve a pin copy which match the one given as arguments
     *
     * @param pin
     *            the pin to found the copy of
     * @param pinGetter
     *            leaf getter method to retrieve pins
     * @return the pin copy or Optional.empty() if none is found
     */
    private <IOType extends Pin> Optional<IOType> retrieveCopiedPin(final IOType pin,
            final Function<Leaf, List<IOType>> pinGetter) {
        Leaf nodeCopy = leavesCopy.get(pin.getLeaf());
        if (nodeCopy == null) {
            nodeCopy = compactedGroups.get(pin.getLeaf().getParent());
            if (nodeCopy != null) {
                // find the fist free pin
                return pinGetter.apply(nodeCopy).stream().filter(copiedPin -> !copiedPin.getLink().isPresent())
                        .findFirst();
            }
        } else {
            return Optional.of(pinGetter.apply(nodeCopy).get(pin.getId()));
        }
        return Optional.empty();
    }

}
