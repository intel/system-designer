/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
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
