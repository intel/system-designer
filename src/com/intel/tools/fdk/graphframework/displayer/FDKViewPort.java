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
package com.intel.tools.fdk.graphframework.displayer;

import org.eclipse.draw2d.FreeformFigure;
import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.geometry.Rectangle;

public class FDKViewPort extends FreeformViewport {

    private int margin;

    public FDKViewPort() {
        super();
    }

    /**
     * This method readjusts the scroll bars from the viewport including some margins.
     */
    @Override
    protected void readjustScrollBars() {
        if ((getContents() != null) && (getContents() instanceof FreeformFigure)) {
            final Rectangle clientArea = getClientArea();
            final Rectangle freeformExtent = ((FreeformFigure) getContents()).getFreeformExtent().getCopy();
            freeformExtent.union(0, 0, clientArea.width, clientArea.height);
            freeformExtent.setBounds(freeformExtent.x - margin, freeformExtent.y - margin,
                    freeformExtent.width + 2 * margin, freeformExtent.height + 2 * margin);
            ((FreeformFigure) getContents()).setFreeformBounds(freeformExtent);

            getVerticalRangeModel().setAll(freeformExtent.y, clientArea.height,
                    freeformExtent.bottom());
            getHorizontalRangeModel().setAll(freeformExtent.x, clientArea.width,
                    freeformExtent.right());
        }
    }

    public void setMargin(final int margin) {
        this.margin = margin;
    }
}
