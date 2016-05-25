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

import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;

import com.intel.tools.fdk.graphframework.figure.IGraphFigure;
import com.intel.tools.fdk.graphframework.graph.IGroup;
import com.intel.tools.utils.IntelPalette;

public class GroupBodyFigure extends RoundedRectangle implements IGraphFigure {

    private static final int LINE_WIDTH = 2;

    /** The graph group this figure represents */
    private final IGroup group;

    private final RectangleFigure selection = new RectangleFigure();

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
        setBackgroundColor(group.getStyle().getBackground());
        setForegroundColor(group.getStyle().getForeground());

        selection.setAlpha(128);
        selection.setFill(true);
        selection.setOutline(true);
        selection.setLineWidth(0);
        selection.setForegroundColor(IntelPalette.INTEL_BLUE);
        selection.setBackgroundColor(IntelPalette.LIGHT_BLUE);
        selection.setVisible(false);

        add(selection);
    }

    @Override
    public void setBounds(final Rectangle rect) {
        super.setBounds(rect);
        final Rectangle inclusiveBounds = new Rectangle(bounds);
        inclusiveBounds.width += LINE_WIDTH / 2;
        inclusiveBounds.height += LINE_WIDTH / 2;
        selection.setBounds(inclusiveBounds);

    }

    @Override
    public void select() {
        selection.setVisible(true);
    }

    @Override
    public void unselect() {
        selection.setVisible(false);
    }

    /**
     * @return the graph element associated to this figure
     */
    public IGroup getGroup() {
        return group;
    }

}
