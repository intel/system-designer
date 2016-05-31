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
