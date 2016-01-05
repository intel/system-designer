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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represent a basic graph.</br>
 *
 * A graph is composed of {@link Node} interconnected by {@link Edge}
 */
public final class Graph {

    private final List<Node> nodes = new ArrayList<>();

    /**
     * Create a new graph.
     *
     * @param nodes
     *            nodes of the graph. All nodes of the list are added in the internal one.
     */
    public Graph(final List<Node> nodes) {
        this.nodes.addAll(nodes);
    }

    /**
     * @return an unmodifiable list of nodes which compose this graph.
     */
    public List<Node> getNodes() {
        return Collections.unmodifiableList(nodes);
    }

    /**
     * @return a set containing all {@link Edge} which interconnect {@link Node} of this graph.
     */
    public Set<Edge> getEdges() {
        return nodes.stream()
                .flatMap(node -> Stream.concat(
                        node.getLinkedInputEdges().stream(), node.getLinkedOutputEdges().stream()))
                .collect(Collectors.toSet());
    }

}
