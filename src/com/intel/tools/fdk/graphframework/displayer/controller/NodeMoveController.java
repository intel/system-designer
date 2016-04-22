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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

import com.intel.tools.fdk.graphframework.displayer.GraphDisplayer;
import com.intel.tools.fdk.graphframework.figure.node.GroupBodyFigure;
import com.intel.tools.fdk.graphframework.figure.node.LeafBodyFigure;

/**
 * Controller allowing to move a figure which is in the content layer.</br>
 * Only Node Figure of the {@link GraphDisplayer} will be moved.
 */
public class NodeMoveController {

    private static final TypeTreeSearch NODE_BODY_SEARCHER = new TypeTreeSearch(LeafBodyFigure.class);
    private static final TypeTreeSearch GROUP_BODY_SEARCHER = new TypeTreeSearch(GroupBodyFigure.class);

    /** The clicked figure which is moving */
    private IFigure movedFigure;
    /** The Ghost displayed on the feedback layer during the move */
    private final RectangleFigure movingGhost = new RectangleFigure();
    /** Offset between the mouse click and the clicked Figure */
    private final Dimension offset = new Dimension(0, 0);

    /** @param displayer the displayer which will allow component move */
    public NodeMoveController(final GraphDisplayer displayer) {
        // The ghost is not visible, it is here only to avoid scrollbars shrink during the move
        movingGhost.setVisible(false);

        displayer.getContentLayer().addMouseListener(new MouseListener.Stub() {
            @Override
            public void mouseReleased(final MouseEvent event) {
                if (movedFigure != null) {
                    // Reset state
                    offset.setWidth(0);
                    offset.setHeight(0);

                    // Remove the ghost
                    displayer.getFeedbackLayer().remove(movingGhost);
                    displayer.getContentLayer().repaint();
                    movedFigure = null;
                }
            }

            @Override
            public void mousePressed(final MouseEvent event) {
                movedFigure = displayer.getContentLayer().findFigureAt(event.getLocation().x, event.getLocation().y,
                        NODE_BODY_SEARCHER);
                if (movedFigure == null) {
                    movedFigure = displayer.getContentLayer().findFigureAt(event.getLocation().x, event.getLocation().y,
                            GROUP_BODY_SEARCHER);
                }
                if (movedFigure != null) {
                    // We have something to move, let's consume the event
                    event.consume();
                    movingGhost.setBounds(movedFigure.getBounds().getCopy());
                    displayer.getFeedbackLayer().add(movingGhost);

                    // Get the click position
                    offset.setWidth(event.x - movedFigure.getBounds().x);
                    offset.setHeight(event.y - movedFigure.getBounds().y);
                } else {
                    movedFigure = null;
                }
            }
        });
        displayer.getContentLayer().addMouseMotionListener(new MouseMotionListener.Stub() {
            @Override
            public void mouseDragged(final MouseEvent event) {
                if (movedFigure != null) {
                    final Rectangle bounds = movedFigure.getBounds().getCopy();
                    bounds.setLocation(event.x - offset.width(), event.y - offset.height());
                    movedFigure.setBounds(bounds);
                }
            }
        });
    }

}
