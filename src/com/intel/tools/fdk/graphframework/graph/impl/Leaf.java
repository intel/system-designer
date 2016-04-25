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
package com.intel.tools.fdk.graphframework.graph.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.intel.tools.fdk.graphframework.graph.GraphException;
import com.intel.tools.fdk.graphframework.graph.ILeaf;
import com.intel.tools.fdk.graphframework.utils.ListUtils;

/**
 * Represent the graph base element. </br>
 *
 * A leaf node can be connected to many other leaves of the same graph.</br>
 * A leaf is defined with a defined input/output numbers.</br>
 */
public final class Leaf implements ILeaf, Comparable<Leaf> {

    /** Instance counter used to generate instanceId, this is required to handle Leaf ordered Set */
    private static int instanceCounter = 0;

    private final int id;

    private NodeContainer parent;
    /**
     * Unmodifiable list (by construction) of {@link Link} representing inputs.</br>
     * If the value of an element is {@link Optional#empty()} the input is not linked. The wrapped {@link Link} is
     * connected to the input otherwise.
     */
    private final List<Optional<Link>> inputLinks;
    /**
     * Unmodifiable list (by construction) of {@link Link} representing outputs.</br>
     * If the value of an element is {@link Optional#empty()} the output is not linked. The wrapped {@link Link} is
     * connected to the output otherwise.
     */
    private final List<Optional<Link>> outputLinks;

    /**
     * Create a leaf with desired inputs and outputs numbers
     *
     * @param inputNumber
     *            desired input number
     * @param outputNumber
     *            desired output number
     */
    public Leaf(final int inputNumber, final int outputNumber) {
        this(instanceCounter++, inputNumber, outputNumber);
    }

    /**
     * Create a new leaf with the same IO number than the copied one </br>
     * Both leaves share the same id.</br>
     * Links are not copied.
     *
     * @param leaf
     *            the leaf to copy
     */
    public Leaf(final Leaf leaf) {
        this(leaf.id, leaf.inputLinks.size(), leaf.outputLinks.size());
    }

    private Leaf(final int id, final int inputNumber, final int outputNumber) {
        assert inputNumber >= 0 : "Node input number should be positive or zero";
        assert outputNumber >= 0 : "Node output number should be positive or zero";

        this.inputLinks = ListUtils.<Link> initializeFixedSizeList(inputNumber);
        this.outputLinks = ListUtils.<Link> initializeFixedSizeList(outputNumber);
        this.id = id;
    }

    /**
     * Retrieves potentially empty {@link Optional} of input {@link Link}
     *
     * @return an unmodifiable list of potentially empty {@link Link} place connected on inputs.
     */
    @Override
    public List<Optional<Link>> getInputLinks() {
        return inputLinks;
    }

    /**
     * Retrieves potentially empty {@link Optional} of output {@link Link}
     *
     * @return an unmodifiable list of potentially empty {@link Link} place connected on outputs.
     */
    @Override
    public List<Optional<Link>> getOutputLinks() {
        return outputLinks;
    }

    /**
     * Retrieves all {@link Link} connected on inputs
     *
     * @return an unmodifiable list of connected {@link Link} connected on inputs.
     */
    public List<Link> getLinkedInputLinks() {
        return getLinkedLinks(this.inputLinks);
    }

    /**
     * Retrieves all {@link Link} connected on outputs
     *
     * @return an unmodifiable list of connected {@link Link} connected on outputs.
     */
    public List<Link> getLinkedOutputLinks() {
        return getLinkedLinks(this.outputLinks);
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
     *             if the output of this node or the input of the destination node is already linked to a link
     */
    public Link connect(final int outputId, final Leaf destinationNode, final int destinationInputId)
            throws GraphException {
        if (outputLinks.get(outputId).isPresent() || destinationNode.inputLinks.get(destinationInputId).isPresent()) {
            throw new GraphException("While connecting nodes: I/O are already used");
        }

        final Optional<Link> link = Optional.of(new Link(destinationNode, this));
        outputLinks.set(outputId, link);
        destinationNode.inputLinks.set(destinationInputId, link);
        return link.get();
    }

    /**
     * Filter a list of {@link Optional} of {@link Link} and retrieves all connected links.
     *
     * @return an unmodifiable list of connected {@link Link}.
     */
    private List<Link> getLinkedLinks(final List<Optional<Link>> links) {
        return links.stream().filter(Optional::isPresent).map(link -> link.get()).collect(Collectors.toList());
    }

    @Override
    public NodeContainer getParent() {
        return parent;
    }

    protected void setParent(final NodeContainer parent) {
        this.parent = parent;
    }

    @Override
    public int compareTo(final Leaf leaf) {
        return this.id - leaf.id;
    }

}
