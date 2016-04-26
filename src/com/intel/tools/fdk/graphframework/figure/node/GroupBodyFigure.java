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
package com.intel.tools.fdk.graphframework.figure.node;

import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.swt.SWT;

import com.intel.tools.fdk.graphframework.figure.IGraphFigure;
import com.intel.tools.fdk.graphframework.graph.IGroup;
import com.intel.tools.utils.IntelPalette;

public class GroupBodyFigure extends RoundedRectangle implements IGraphFigure {

    private static final int LINE_WIDTH = 2;

    /** The graph group this figure represents */
    private final IGroup group;

    /**
     * Creates a new {@link GroupBodyFigure}
     *
     * @param group
     *            the group this figure represents
     */
    public GroupBodyFigure(final IGroup group) {
        this.group = group;
        setFill(true);
        setAntialias(1);
        setLineWidth(LINE_WIDTH);
        setLineStyle(SWT.LINE_DASHDOT);
        setBackgroundColor(IntelPalette.GREY_1);
        setForegroundColor(IGraphFigure.DEFAULT_COLOR);
    }

    @Override
    public void select() {
        setLineWidth(getLineWidth() + 1);
    }

    @Override
    public void unselect() {
        setLineWidth(getLineWidth() - 1);
    }

    /**
     * @return the graph element associated to this figure
     */
    public IGroup getGroup() {
        return group;
    }

}
