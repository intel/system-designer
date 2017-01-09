/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.tools.fdk.graphframework.figure;

import java.util.Optional;
import java.util.function.Consumer;

import org.eclipse.draw2d.AncestorListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import com.intel.tools.fdk.graphframework.graph.IGraphElement;
import com.intel.tools.fdk.graphframework.graph.Style.IStyleListener;
import com.intel.tools.utils.IntelPalette;

/**
 * Decorates a sub-figure of the graph with a label.
 *
 * The label will be placed right under the binded figure and will follow its movement.
 */
public class LabelFigure extends Label {

    private final Consumer<Object> layoutLabel;

    /**
     * @param element
     *            the decorated graph element which supply the label to display
     * @param boundFigure
     *            the figure under which the label will be positioned
     */
    public LabelFigure(final IGraphElement element, final IFigure boundFigure) {
        setForegroundColor(IntelPalette.INTEL_BLUE);

        element.getStyle().addListener(new IStyleListener() {
            @Override
            public void labelUpdated(final Optional<String> label) {
                updateLabel(label);
            }
        });

        this.layoutLabel = source -> {
            final Rectangle bounds = boundFigure.getBounds().getCopy();
            if (getParent() != null && boundFigure.getParent() != null) {
                boundFigure.getParent().translateToAbsolute(bounds);
                getParent().translateToRelative(bounds);
                setLocation(new Point(bounds.x + (bounds.width - getSize().width) / 2, bounds.y + bounds.height));
            }
        };

        final AncestorListener ancestorListener = new AncestorListener() {
            @Override
            public void ancestorAdded(final IFigure ancestor) {
                layoutLabel.accept(boundFigure);
            }

            @Override
            public void ancestorMoved(final IFigure ancestor) {
            }

            @Override
            public void ancestorRemoved(final IFigure ancestor) {
            }
        };
        boundFigure.addFigureListener(layoutLabel::accept);
        boundFigure.addAncestorListener(ancestorListener);
        addAncestorListener(ancestorListener);

        updateLabel(element.getStyle().getLabel());
    }

    /**
     * Update label text and position
     *
     * @param label
     *            the new label to display
     */
    private void updateLabel(final Optional<String> label) {
        setText(label.orElse(""));
        // Label width is expanded to avoid cutting some text, then it is centered under the main rectangle
        final int labelWidth = (int) Math.round(getTextBounds().width * 1.1);
        setSize(labelWidth, getTextBounds().height);
        layoutLabel.accept(this);
    }

}
