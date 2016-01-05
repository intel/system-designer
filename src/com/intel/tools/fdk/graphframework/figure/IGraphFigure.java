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

import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Color;

import com.intel.tools.utils.IntelPalette;

/** Defines basic behavior of figure which compose a graph draw. */
public interface IGraphFigure extends IFigure {

    /** Unit size of every elements of the graph */
    int SIZE_UNIT = 16;
    /** Graph Figure default color */
    Color DEFAULT_COLOR = IntelPalette.INTEL_BLUE;

    /** Highlight the figure */
    default void select() {
        setLineWidth(getLineWidth() + 1);
    }

    /** Remove the highlight decoration of the figure */
    default void unselect() {
        setLineWidth(getLineWidth() - 1);
    }

    default void setColor(final Color color) {
        setForegroundColor(color);
    }

    /**
     * Allows to change the thickness of a figure borders.</br>
     * Required method for selection behavior
     *
     * @param w
     *            the width to apply on figure border
     */
    void setLineWidth(int w);

    /**
     * Allows to retrieve the thickness of a figure borders.</br>
     * Required method for selection behavior
     */
    int getLineWidth();

}
