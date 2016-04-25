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
package com.intel.tools.fdk.graphframework.graph.factory;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import com.intel.tools.fdk.graphframework.graph.GraphException;
import com.intel.tools.fdk.graphframework.graph.IGraph;
import com.intel.tools.fdk.graphframework.graph.IGroup;
import com.intel.tools.fdk.graphframework.graph.ILeaf;
import com.intel.tools.fdk.graphframework.graph.ILink;
import com.intel.tools.fdk.graphframework.graph.INode;
import com.intel.tools.fdk.graphframework.graph.INodeContainer;
import com.intel.tools.fdk.graphframework.graph.impl.Graph;
import com.intel.tools.fdk.graphframework.graph.impl.Group;
import com.intel.tools.fdk.graphframework.graph.impl.Leaf;

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

    public static ILink createLink(final ILeaf sourceNode, final int sourceOutputId,
            final ILeaf destinationNode, final int destinationInputId) throws GraphException {
        return ((Leaf) sourceNode).connect(sourceOutputId, (Leaf) destinationNode, destinationInputId);
    }

}
