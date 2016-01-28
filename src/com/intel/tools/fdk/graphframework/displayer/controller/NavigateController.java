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

import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.SWT;

import com.intel.tools.fdk.graphframework.displayer.GraphDisplayer;

/** Controller allowing to navigate in a viewport by dragging the background */
public class NavigateController {

    /** Location of the last mouse click */
    private Point click = new Point(0, 0);

    /** Indicate if we are currently navigating in the viewport */
    private boolean isNavigating = false;

    public NavigateController(final GraphDisplayer displayer) {

        displayer.getBackgroundLayer().addMouseListener(new MouseListener.Stub() {
            @Override
            public void mouseReleased(final MouseEvent event) {
                // Reset state
                isNavigating = false;
            }

            @Override
            public void mousePressed(final MouseEvent event) {
                event.consume();
                // Get the click position
                click = event.getLocation();
                isNavigating = true;
            }
        });
        displayer.getBackgroundLayer().addMouseMotionListener(new MouseMotionListener.Stub() {
            @Override
            public void mouseDragged(final MouseEvent event) {
                if (isNavigating && (event.getState() & SWT.CTRL) == SWT.CTRL) {
                    final int hOffset = click.x - event.x;
                    final int vOffset = click.y - event.y;
                    final Point currentLocation = displayer.getControl().getViewport().getViewLocation();
                    displayer.getControl().getViewport().setViewLocation(currentLocation.x + hOffset,
                                                                         currentLocation.y + vOffset);
                }
            }
        });
    }

}
