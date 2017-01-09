/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.tools.fdk.graphframework.graph.factory;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import com.intel.tools.fdk.graphframework.graph.GraphException;
import com.intel.tools.fdk.graphframework.graph.IGraph;
import com.intel.tools.fdk.graphframework.graph.IGroup;
import com.intel.tools.fdk.graphframework.graph.IInput;
import com.intel.tools.fdk.graphframework.graph.ILeaf;
import com.intel.tools.fdk.graphframework.graph.ILink;
import com.intel.tools.fdk.graphframework.graph.INode;
import com.intel.tools.fdk.graphframework.graph.INodeContainer;
import com.intel.tools.fdk.graphframework.graph.IOutput;
import com.intel.tools.fdk.graphframework.graph.impl.Graph;
import com.intel.tools.fdk.graphframework.graph.impl.Group;
import com.intel.tools.fdk.graphframework.graph.impl.Input;
import com.intel.tools.fdk.graphframework.graph.impl.Leaf;
import com.intel.tools.fdk.graphframework.graph.impl.Link;
import com.intel.tools.fdk.graphframework.graph.impl.Output;

/**
 * Allows to instantiate Graph elements.
 *
 * This factory allows to create Graph Framework interface objects.</br>
 * A private implementation is used internally, thus no custom implementation of graph element interfaces:
 * {@link IGraph} , {@link ILeaf}, {@link IGroup}, {@link INodeContainer}, {@link INode} should be provided by the user.
 */
public final class GraphFactory {

    private GraphFactory() {
    }

    public static IGraph createGraph(final Set<? extends ILeaf> leaves, final Set<? extends IGroup> groups) {
        return new Graph(leaves.stream().map(Leaf.class::cast).collect(Collectors.toSet()),
                groups.stream().map(Group.class::cast).collect(Collectors.toSet()));
    }

    public static IGraph createGraph(final Set<? extends ILeaf> leaves) {
        return createGraph(leaves, Collections.emptySet());
    }

    public static IGraph createGraph() {
        return createGraph(Collections.emptySet(), Collections.emptySet());
    }

    public static ILeaf createLeaf(final int inputNumber, final int outputNumber) {
        return new Leaf(inputNumber, outputNumber);
    }

    public static IGroup createGroup(final Set<? extends ILeaf> leaves, final Set<? extends IGroup> groups) {
        return new Group(leaves.stream().map(Leaf.class::cast).collect(Collectors.toSet()),
                groups.stream().map(Group.class::cast).collect(Collectors.toSet()));
    }

    public static IGroup createGroup(final Set<? extends ILeaf> leaves) {
        return createGroup(leaves, Collections.emptySet());
    }

    public static IGroup createGroup() {
        return createGroup(Collections.emptySet(), Collections.emptySet());
    }

    /**
     * Connect an output of a node to an input of another one (or itself).
     *
     * @param output
     *            the output pin to connect
     * @param input
     *            the input pin to connect
     * @return the {@link ILink} object which bounds the two pins together
     * @throws GraphException
     *             if the output or the input is already linked to a link
     */
    public static ILink createLink(final IOutput output, final IInput input) throws GraphException {
        if (input.getLink().isPresent() || output.getLink().isPresent()) {
            throw new GraphException("While connecting nodes: I/O are already used");
        }
        return new Link((Output) output, (Input) input);
    }

}
