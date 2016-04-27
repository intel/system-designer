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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.intel.tools.fdk.graphframework.graph.ILeaf;

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
    /** Unmodifiable list (by construction) of {@link Pin} representing inputs. */
    private final List<Input> inputLinks;
    /** Unmodifiable list (by construction) of {@link Pin} representing outputs. */
    private final List<Output> outputLinks;

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

        this.inputLinks = Arrays.asList(new Input[inputNumber]);
        for (int i = 0; i < inputNumber; i++) {
            this.inputLinks.set(i, new Input(i, this));
        }
        this.outputLinks = Arrays.asList(new Output[outputNumber]);
        for (int i = 0; i < outputNumber; i++) {
            this.outputLinks.set(i, new Output(i, this));
        }
        this.id = id;
    }

    /**
     * Retrieve node inputs.</br>
     * The index of those pins match their id.
     *
     * @return an unmodifiable list of potentially not linked {@link Input}
     */
    @Override
    public List<Input> getInputs() {
        return inputLinks;
    }

    /**
     * Retrieve node outputs.</br>
     * The index of those pins match their id.
     *
     * @return an unmodifiable list of potentially not linked {@link Output}
     */
    @Override
    public List<Output> getOutputs() {
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
     * Filter a list of {@link Pin} and retrieves all connected links.
     *
     * @return an unmodifiable list of connected {@link Link}.
     */
    private List<Link> getLinkedLinks(final List<? extends Pin> pins) {
        return pins.stream().map(Pin::getLink).filter(Optional::isPresent).map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public NodeContainer getParent() {
        return parent;
    }

    protected void setParent(final NodeContainer parent) {
        if (this.parent != null) {
            this.parent.remove(this);
        }
        this.parent = parent;
    }

    @Override
    public int compareTo(final Leaf leaf) {
        return this.id - leaf.id;
    }

}
