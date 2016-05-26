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
package com.intel.tools.fdk.graphframework.figure.pin;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

import com.intel.tools.fdk.graphframework.figure.IGraphFigure;
import com.intel.tools.fdk.graphframework.figure.ghost.GhostPinFigure;
import com.intel.tools.fdk.graphframework.graph.IPin;
import com.intel.tools.fdk.graphframework.graph.Style.IStyleListener;
import com.intel.tools.utils.IntelPalette;

/** Abstract class representing a node I/O */
public abstract class PinFigure<IOType extends IPin> extends GhostPinFigure implements IGraphFigure, IStyleListener {

    private final IOType pin;

    private final RectangleFigure selection = new RectangleFigure();

    private final boolean debug = false;

    /**
     * @param pin
     *            the pin graph element represented by this figure
     */
    public PinFigure(final IOType pin) {
        this.pin = pin;
        setColor(pin.getStyle().getForeground());

        selection.setAlpha(128);
        selection.setFill(true);
        selection.setOutline(true);
        selection.setLineWidth(0);
        selection.setForegroundColor(IntelPalette.INTEL_BLUE);
        selection.setBackgroundColor(IntelPalette.LIGHT_BLUE);
        selection.setVisible(false);
        selection.setBounds(getBounds());

        if (debug) {
            showPixelGrid(getArrow());
        }

        add(selection);
        this.pin.getStyle().addListener(this);
    }

    private void showPixelGrid(final IFigure figure) {
        for (int i = 0; i < figure.getBounds().width - 1; i++) {
            for (int j = 0; j < figure.getBounds().height - 1; j++) {
                final RectangleFigure r = new RectangleFigure();
                r.setBounds(new Rectangle(i, j, 2, 2));
                r.setFill(false);
                r.setOutline(true);
                r.setLineWidth(0);
                r.setForegroundColor(IntelPalette.RED);
                add(r);
            }
        }
    }

    @Override
    public void setAlpha(final int value) {
        getArrow().setAlpha(value);
        getConnector().setAlpha(value);
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
     * @return the pin graph element represented by this figure
     */
    public IOType getPin() {
        return pin;
    }

    @Override
    public void foregroundUpdated(final Color color) {
        setColor(pin.getStyle().getForeground());
        invalidate();
    }

}
