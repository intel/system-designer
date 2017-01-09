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
package com.intel.tools.fdk.graphframework.figure.link;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Point;


public class BeveledPolyLineConnection extends PolylineConnection {

    private static final int BEVEL_SIZE = 8;
    /**
     * Draws a polyline with beveled edges at inflexion points.
     *
     * @see Shape#outlineShape(Graphics)
     */
    @Override
    protected void outlineShape(final Graphics g) {

        if (getPoints().size() == 2) {
            g.drawLine(getPoints().getFirstPoint(), getPoints().getLastPoint());
        } else {

            Point lastLineTip = null;
            int bevel = BEVEL_SIZE;

            // First pass to compute min size of the bevel
            // Not necessary if there are no inflexion in the line
            if (getPoints().size() > 3) {
                for (int i = 1; i < getPoints().size() - 1; i++) {
                    // Current point
                    final Point point = getPoints().getPoint(i);
                    // Next point
                    final Point nextPoint = getPoints().getPoint(i + 1);

                    bevel = (int) Math.min(bevel, point.getDistance(nextPoint) / 2.0);
                }
            }

            for (int i = 1; i < getPoints().size(); i++) {
                // Inflexion point
                final Point ip = getPoints().getPoint(i);
                // Prev point
                final Point pp = getPoints().getPoint(i - 1);

                // Compute segment from previous to current point
                final Point a = new Point(pp);
                final Point b = new Point(ip);

                if (pp.y == ip.y) {
                    // segment is horizontal
                    if (ip.x - pp.x > 0) {
                        // segment is toward positive x
                        // shorten it
                        if (i > 1) {
                            a.x += bevel;
                        }
                        if (i < getPoints().size() - 1) {
                            b.x -= bevel;
                        }
                    } else {
                        if (i > 1) {
                            a.x -= bevel;
                        }
                        if (i < getPoints().size() - 1) {
                            b.x += bevel;
                        }
                    }

                } else {
                    // segment is vertical
                    if (ip.y - pp.y > 0) {
                        // segment is toward positive y
                        // shorten it
                        if (i > 1) {
                            a.y += bevel;
                        }
                        if (i < getPoints().size() - 1) {
                            b.y -= bevel;
                        }
                    } else {
                        if (i > 1) {
                            a.y -= bevel;
                        }
                        if (i < getPoints().size() - 1) {
                            b.y += bevel;
                        }
                    }
                }

                // Draw connection to the previous segment tip if any
                if (lastLineTip != null) {
                    g.drawLine(lastLineTip, a);
                }
                // Draw current segment
                g.drawLine(a, b);
                // And remember its tip
                lastLineTip = b;
            }

        }
    }

}
