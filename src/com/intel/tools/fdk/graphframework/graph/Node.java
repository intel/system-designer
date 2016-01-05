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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.intel.tools.fdk.graphframework.utils.ListUtils;

/**
 * Represent the graph base element. </br>
 *
 * A node can be connected to many other nodes of the same graph.</br>
 * A node is defined with a defined input/output numbers.</br>
 */
public final class Node {

    /**
     * Unmodifiable list (by construction) of {@link Edge} representing inputs.</br>
     * If the value of an element is {@link Optional#empty()} the input is not linked. The wrapped {@link Edge} is
     * connected to the input otherwise.
     */
    private final List<Optional<Edge>> inputEdges;
    /**
     * Unmodifiable list (by construction) of {@link Edge} representing outputs.</br>
     * If the value of an element is {@link Optional#empty()} the output is not linked. The wrapped {@link Edge} is
     * connected to the output otherwise.
     */
    private final List<Optional<Edge>> outputEdges;

    public Node(final int inputNumber, final int outputNumber) {
        assert inputNumber >= 0 : "Node input number should be positive or zero";
        assert outputNumber >= 0 : "Node output number should be positive or zero";

        this.inputEdges = ListUtils.<Edge> initializeFixedSizeList(inputNumber);
        this.outputEdges = ListUtils.<Edge> initializeFixedSizeList(outputNumber);
    }

    /**
     * Retrieves potentially empty {@link Optional} of input {@link Edge}
     *
     * @return an unmodifiable list of potentially empty {@link Edge} place connected on inputs.
     */
    public List<Optional<Edge>> getInputEdges() {
        return inputEdges;
    }

    /**
     * Retrieves potentially empty {@link Optional} of output {@link Edge}
     *
     * @return an unmodifiable list of potentially empty {@link Edge} place connected on outputs.
     */
    public List<Optional<Edge>> getOutputEdges() {
        return outputEdges;
    }

    /**
     * Retrieves all {@link Edge} connected on inputs
     *
     * @return an unmodifiable list of connected {@link Edge} connected on inputs.
     */
    public List<Edge> getLinkedInputEdges() {
        return getLinkedEdges(this.inputEdges);
    }

    /**
     * Retrieves all {@link Edge} connected on outputs
     *
     * @return an unmodifiable list of connected {@link Edge} connected on outputs.
     */
    public List<Edge> getLinkedOutputEdges() {
        return getLinkedEdges(this.outputEdges);
    }

    /**
     * Connect an output of this node to an input of another one (or itself).
     *
     * @param outputId
     *            the id of the output to connect
     * @param destinationNode
     *            the node to connect to
     * @param destinationInputId
     *            the id of the input to connect to
     * @throws GraphException
     *             if the output of this node or the input of the destination node is already linked to an edge
     */
    public void connect(final int outputId, final Node destinationNode, final int destinationInputId)
            throws GraphException {
        if (outputEdges.get(outputId).isPresent() || destinationNode.inputEdges.get(destinationInputId).isPresent()) {
            throw new GraphException("While connecting nodes: I/O are already used");
        }

        final Optional<Edge> edge = Optional.of(new Edge(destinationNode, this));
        outputEdges.set(outputId, edge);
        destinationNode.inputEdges.set(destinationInputId, edge);
    }

    /**
     * Filter a list of {@link Optional} of {@link Edge} and retrieves all connected edges.
     *
     * @return an unmodifiable list of connected {@link Edge}.
     */
    private List<Edge> getLinkedEdges(final List<Optional<Edge>> edges) {
        return edges.stream().filter(Optional::isPresent).map(edge -> edge.get()).collect(Collectors.toList());
    }

}
