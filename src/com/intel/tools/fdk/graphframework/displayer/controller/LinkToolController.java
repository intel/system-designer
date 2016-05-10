/* ============================================================================
 * INTEL CONFIDENTIAL
 *
 * Copyright 2015-2016 Intel Corporation All Rights Reserved.
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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;

import com.intel.tools.fdk.graphframework.displayer.GraphDisplayer;
import com.intel.tools.fdk.graphframework.figure.IGraphFigure;
import com.intel.tools.fdk.graphframework.figure.link.BeveledPolyLineConnection;
import com.intel.tools.fdk.graphframework.figure.link.LinkAnchor;
import com.intel.tools.fdk.graphframework.figure.pin.InputFigure;
import com.intel.tools.fdk.graphframework.figure.pin.OutputFigure;
import com.intel.tools.fdk.graphframework.figure.pin.PinFigure;
import com.intel.tools.fdk.graphframework.graph.IPin;

/**
 * Controller allowing to create links by dragging a pin figure and dropping it on another pin.</br>
 */
public class LinkToolController {

    public class GhostLinkFigure extends BeveledPolyLineConnection implements IGraphFigure {

        private static final int LINE_WIDTH = 4;

        public GhostLinkFigure(final LinkAnchor sourceAnchor, final LinkAnchor destAnchor) {
            setSourceAnchor(sourceAnchor);
            setTargetAnchor(destAnchor);

            // Reminder: this style is copy pasted from LinkFigure, please update both
            setLineCap(SWT.CAP_ROUND);
            setLineJoin(SWT.JOIN_ROUND);
            setAntialias(1);
            setForegroundColor(LINKS_COLOR);

            setLineWidth(LINE_WIDTH);
        }

        @Override
        public void select() {
        }

        @Override
        public void unselect() {
        }
    }

    private static final TypeTreeSearch PIN_SEARCHER = new TypeTreeSearch(PinFigure.class);
    protected static final int GHOST_ALPHA = 128;

    public interface LinkToolListener {
        /** This event is fired when the movement has ended (once the mouse is released) */
        void linkCreated(OutputFigure sourceFigure, InputFigure destinationFigure);
    }

    /** The clicked pin figure from which the link starts */
    private IFigure sourcePinFigure;
    /** The pin figure to which the link ends */
    private IFigure destinationPinFigure;
    /** The figure really moved during drag */
    private PinFigure<? extends IPin> ghostFigure;
    private GhostLinkFigure ghostLinkFigure;
    /** Offset between the mouse click and the clicked Figure */
    private final Dimension offset = new Dimension(0, 0);

    private boolean dragging = false;

    private final List<LinkToolListener> listeners = new ArrayList<>();

    /** @param displayer the displayer which will allow component move */
    public LinkToolController(final GraphDisplayer displayer) {

        final MouseMotionListener.Stub ghostDraggedListener = new MouseMotionListener.Stub() {

            @Override
            public void mouseDragged(final MouseEvent event) {

                if (!dragging) {
                    dragging = true;
                    displayer.getConnectionLayer().add(ghostLinkFigure);
                    displayer.getFeedbackLayer().add(ghostFigure);
                }

                final Rectangle bounds = ghostFigure.getBounds().getCopy();
                bounds.setLocation(event.x - offset.width(), event.y - offset.height());
                ghostFigure.setBounds(bounds);
            }
        };

        displayer.getContentLayer().addMouseListener(new MouseListener.Stub() {

            @Override
            public void mouseReleased(final MouseEvent event) {
                // Remove mouse motion listener
                displayer.getContentLayer().removeMouseMotionListener(ghostDraggedListener);

                if (dragging) {
                    destinationPinFigure = displayer.getContentLayer().findFigureAt(event.x, event.y, PIN_SEARCHER);

                    // Remove ghost (do this before firing the event in case the graph is refreshed)
                    displayer.getFeedbackLayer().remove(ghostFigure);
                    // Remove the "fake" connection and anchor, no matter what
                    displayer.getConnectionLayer().remove(ghostLinkFigure);

                    // If the ghost is dropped on another pin figure, fire a "link created" event
                    if (destinationPinFigure != null && !destinationPinFigure.equals(sourcePinFigure)) {

                        if (sourcePinFigure instanceof OutputFigure && destinationPinFigure instanceof InputFigure) {
                            final OutputFigure output = (OutputFigure) sourcePinFigure;
                            final InputFigure input = (InputFigure) destinationPinFigure;
                            // Create link
                            fireLinkCreated(output, input);
                        } else if (sourcePinFigure instanceof InputFigure
                                && destinationPinFigure instanceof OutputFigure) {
                            final InputFigure input = (InputFigure) sourcePinFigure;
                            final OutputFigure output = (OutputFigure) destinationPinFigure;
                            // Create link
                            fireLinkCreated(output, input);
                        }

                    }

                    // Reset state
                    dragging = false;
                    offset.setWidth(0);
                    offset.setHeight(0);
                    sourcePinFigure = null;
                    destinationPinFigure = null;
                }
            }

            @Override
            public void mousePressed(final MouseEvent event) {

                sourcePinFigure = displayer.getContentLayer().findFigureAt(event.getLocation().x, event.getLocation().y,
                        PIN_SEARCHER);

                // If a pin was clicked, prepare a link drag
                if (sourcePinFigure != null) {

                    // let's consume the event
                    event.consume();

                    // Create an output pin ghost to connect to an input or vice versa
                    if (sourcePinFigure instanceof OutputFigure) {
                        ghostFigure = new InputFigure(null);
                    } else {
                        ghostFigure = new OutputFigure(null);
                    }
                    ghostFigure.setAlpha(GHOST_ALPHA);
                    ghostFigure.setBounds(sourcePinFigure.getBounds().getCopy());

                    // Create link figure ghost
                    final LinkAnchor ghostAnchor = new LinkAnchor(ghostFigure, ghostFigure);
                    final LinkAnchor sourceAnchor = new LinkAnchor(sourcePinFigure, (PinFigure<?>) sourcePinFigure);
                    ghostLinkFigure = new GhostLinkFigure(sourceAnchor, ghostAnchor);
                    ghostLinkFigure.setAlpha(GHOST_ALPHA);

                    // Remember the click position for further use
                    offset.setWidth(event.x - sourcePinFigure.getBounds().x);
                    offset.setHeight(event.y - sourcePinFigure.getBounds().y);

                    // Register the mouse move listener now to enter dragging mode if necessary
                    displayer.getContentLayer().addMouseMotionListener(ghostDraggedListener);
                }
            }
        });

    }

    private void fireLinkCreated(final OutputFigure outputFigure, final InputFigure inputFigure) {
        listeners.forEach(listener -> listener.linkCreated(outputFigure, inputFigure));
    }

    public void addLinkToolListener(final LinkToolListener listener) {
        listeners.add(listener);
    }

    public void removeLinkToolListener(final LinkToolListener listener) {
        listeners.remove(listener);
    }

}
