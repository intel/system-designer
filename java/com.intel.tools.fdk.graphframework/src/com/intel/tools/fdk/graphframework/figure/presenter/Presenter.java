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
package com.intel.tools.fdk.graphframework.figure.presenter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;

import com.intel.tools.fdk.graphframework.figure.IGraphFigure;
import com.intel.tools.fdk.graphframework.figure.LabelFigure;
import com.intel.tools.fdk.graphframework.graph.IGraphElement;
import com.intel.tools.fdk.graphframework.graph.INode;

/**
 * Abstract implementation of what a node presenter should be.</br>
 * The presenter will keep coherence between Figures which represents an INode.
 *
 * @param <T>
 *            the represented node type
 */
public abstract class Presenter<T extends INode> {

    /** The represented node */
    private final T node;

    /** List of all main displayable figures which compose the node */
    private final List<IFigure> displayableFigures = new ArrayList<>();
    /** List of all displayable decorations of the node */
    private final List<IFigure> displayableDecorations = new ArrayList<>();
    /** List of all displayable tools of the node */
    private final List<IFigure> displayableTools = new ArrayList<>();

    /**
     * @param node
     *            the represented node
     */
    public Presenter(final T node) {
        this.node = node;
    }

    /** @return the represented node */
    public T getNode() {
        return node;
    }

    /** @return the main figure of the presenter */
    public abstract IGraphFigure getNodeBody();

    /** @return a figure wrapping all presenter figure */
    public abstract IFigure getBoundsFigure();

    /**
     * Retrieves figures which compose the node
     *
     * @return list of all sub-figures to display
     */
    public List<IFigure> getDisplayableFigures() {
        return displayableFigures;
    }

    /**
     * Retrieves figures used as node decoration.</br>
     * By default, there is none, child class are free to add needed decoration.
     *
     * @return list of all sub-figures decoration to display
     */
    public List<IFigure> getDisplayableDecoration() {
        return displayableDecorations;
    }

    /**
     * Retrieves figures used as node tools (probes, etc...)</br>
     * By default, there is none, child class are free to add needed tools icons.
     *
     * @return list of all tools sub-figures to display
     */
    public List<IFigure> getDisplayableTools() {
        return displayableTools;
    }

    /**
     * Decorates a sub-figure of the node with a label.
     *
     * The label will be placed right under the binded figure and will follow its movement.
     *
     * @param element
     *            the decorated graph element which supply the label to display
     * @param boundFigure
     *            the figure under which the label will be positioned
     */
    protected void addLabel(final IGraphElement element, final IFigure boundFigure) {
        displayableDecorations.add(new LabelFigure(element, boundFigure));
    }

}
