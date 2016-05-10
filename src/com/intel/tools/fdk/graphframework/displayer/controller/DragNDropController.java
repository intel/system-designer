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
package com.intel.tools.fdk.graphframework.displayer.controller;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;

import com.intel.tools.fdk.graphframework.displayer.GraphDisplayer;
import com.intel.tools.fdk.graphframework.figure.node.GroupBodyFigure;
import com.intel.tools.fdk.graphframework.graph.INodeContainer;
import com.intel.tools.fdk.graphframework.graph.adapter.IAdapter;

/**
 * Controller allowing to manage drop events on a {@link GraphDisplayer}
 *
 * @param <T>
 *            the type of dropped elements to handle
 */
public class DragNDropController<T> {

    /**
     * Listener allowing an object to be informed of a dropped event
     *
     * @param <T>
     *            the type of dropped elements to handle
     */
    public interface IDropListener<T> {
        /**
         * Method called when a drop event is detected
         *
         * @param element
         *            the dropped element
         * @param node
         *            the node on which the element has been dropped
         * @param dropLocation
         *            the location where the the element has been dropped
         */
        void elementDropped(final T element, final INodeContainer node, final Point dropLocation);
    }

    private final List<IDropListener<T>> listeners = new ArrayList<>();

    /**
     * @param displayer
     *            the displayer used
     * @param adapter
     *            the adapter handling the displayed graph
     * @param droppedType
     *            the Class object of the generic type argument
     */
    public DragNDropController(final GraphDisplayer displayer, final IAdapter adapter, final Class<T> droppedType) {
        final DropTarget target = new DropTarget(displayer.getControl(), DND.DROP_COPY | DND.DROP_MOVE);
        target.setTransfer(new Transfer[] { LocalSelectionTransfer.getTransfer() });
        target.addDropListener(new DropTargetAdapter() {
            @Override
            public void drop(final DropTargetEvent event) {
                final ISelection selection = LocalSelectionTransfer.getTransfer().getSelection();
                if (selection instanceof IStructuredSelection) {
                    for (final Object object : ((IStructuredSelection) selection).toList()) {
                        if (droppedType.isInstance(object)) {
                            // Retrieve the drop location
                            final Point drop = getDropRelativeLocation(event);
                            displayer.getContentLayer().translateToRelative(drop);

                            // Find the figure on which the drop occurs
                            final GroupBodyFigure parentFigure = (GroupBodyFigure) displayer.getContentLayer()
                                    .findFigureAt(drop.x, drop.y, new TypeTreeSearch(GroupBodyFigure.class));
                            final INodeContainer parent;
                            if (parentFigure != null) {
                                parent = parentFigure.getGroup();
                            } else {
                                parent = adapter.getGraph();
                            }
                            listeners.forEach(
                                    listener -> listener.elementDropped(droppedType.cast(object), parent, drop));
                        }
                    }
                }
            }
        });
    }

    public void addListener(final IDropListener<T> listener) {
        this.listeners.add(listener);
    }

    public void removeListener(final IDropListener<T> listener) {
        this.listeners.add(listener);
    }

    /**
     * Convert location of a drop event to a location relative to the displayer canvas
     *
     * @param event
     *            the received drop event
     * @return the drop location relative to the canvas
     */
    private Point getDropRelativeLocation(final DropTargetEvent event) {
        final DropTarget target = (DropTarget) event.widget;
        final org.eclipse.swt.graphics.Point location = target.getControl().toControl(event.x, event.y);
        return new Point(location.x, location.y);
    }

}
