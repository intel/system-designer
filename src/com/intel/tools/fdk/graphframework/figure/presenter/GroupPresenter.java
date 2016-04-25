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

import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import com.intel.tools.fdk.graphframework.figure.IGraphFigure;
import com.intel.tools.fdk.graphframework.figure.node.GroupBodyFigure;
import com.intel.tools.fdk.graphframework.graph.IGroup;
import com.intel.tools.fdk.graphframework.graph.INode;

/**
 * Wrap leaf of a group under a {@link GroupBodyFigure} to highlight the group
 */
public class GroupPresenter extends Presenter<IGroup> {

    /** The offset used between the group figure bounds and its child */
    private static final int OFFSET = 60;

    private final GroupBodyFigure boundsFigure = new GroupBodyFigure();
    private final List<Presenter<? extends INode>> childrenPresenters = new ArrayList<>();

    private boolean blockEvents = false;
    private Point boundsLocation = new Point(0, 0);

    /**
     * @param node
     *            the represented node
     * @param childrenPresenters
     *            presenters of children nodes.
     */
    public GroupPresenter(final IGroup node, final List<Presenter<? extends INode>> childrenPresenters) {
        super(node);
        this.childrenPresenters.addAll(childrenPresenters);
        this.childrenPresenters.forEach(presenter -> {
            // Track body movement to keep sub-elements together
            presenter.getNodeBody().addFigureListener(new FigureListener() {
                @Override
                public void figureMoved(final IFigure source) {
                    if (!blockEvents) {
                        updateBoundsFigure();
                    }
                }
            });

        });
        boundsFigure.addFigureListener(new FigureListener() {
            @Override
            public void figureMoved(final IFigure source) {
                if (!blockEvents) {
                    blockEvents = true;
                    childrenPresenters.forEach(
                            p -> p.getBoundsFigure().translate(source.getBounds().x - boundsLocation.x,
                                    source.getBounds().y - boundsLocation.y));
                    blockEvents = false;
                    updateBoundsFigure();
                }
            }
        });
        updateBoundsFigure();
        getDisplayableFigures().add(boundsFigure);
    }

    /**
     * Recalculate complete union of sub-figures bounds and update dedicated field.
     */
    private void updateBoundsFigure() {
        this.blockEvents = true;
        final Rectangle rectangle = new Rectangle();
        // Make bounds figure empty
        boundsFigure.setBounds(rectangle);
        // Determine new bounds
        this.childrenPresenters.forEach(presenter -> {
            if (rectangle.isEmpty()) {
                rectangle.setBounds(presenter.getBoundsFigure().getBounds());
            } else {
                rectangle.union(presenter.getBoundsFigure().getBounds());
            }
        });
        rectangle.x -= OFFSET / 2;
        rectangle.y -= OFFSET / 2;
        rectangle.width += OFFSET;
        rectangle.height += OFFSET;
        boundsFigure.setBounds(rectangle);
        boundsLocation = boundsFigure.getLocation().getCopy();
        this.blockEvents = false;
    }

    public List<Presenter<? extends INode>> getChildrenPresenters() {
        return childrenPresenters;
    }

    @Override
    public IGraphFigure getNodeBody() {
        return boundsFigure;
    }

    @Override
    public IFigure getBoundsFigure() {
        return boundsFigure;
    }

}
