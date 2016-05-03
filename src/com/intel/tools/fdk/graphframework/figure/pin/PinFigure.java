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

import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineShape;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

import com.intel.tools.fdk.graphframework.figure.IGraphFigure;
import com.intel.tools.fdk.graphframework.graph.IPin;
import com.intel.tools.utils.IntelPalette;

/** Abstract class representing a node I/O */
public abstract class PinFigure<IOType extends IPin> extends PolylineShape implements IGraphFigure {

    private static final int LINE_WIDTH = 4;

    /** Pin arrow height in {@link IGraphFigure#SIZE_UNIT} */
    private static final int ARROW_HEIGHT = SIZE_UNIT;
    /** Pin connector height in {@link IGraphFigure#SIZE_UNIT} */
    private static final int CONNECTOR_HEIGHT = SIZE_UNIT / 2;
    /** Pin line length in {@link IGraphFigure#SIZE_UNIT} */
    private static final int LINE_LENGTH = SIZE_UNIT;

    private final IOType pin;

    private final ArrowFigure arrow = new ArrowFigure(ARROW_HEIGHT);
    private final Ellipse connector = new Ellipse();
    private final PolylineShape line = new PolylineShape();
    private final RectangleFigure selection = new RectangleFigure();

    private final boolean debug = false;

    /**
     * @param pin
     *            the pin graph element represented by this figure
     */
    public PinFigure(final IOType pin) {
        this.pin = pin;

        add(arrow);
        add(line);
        add(selection);
        add(connector);

        connector.setLineWidth(4);
        connector.setOutline(true);
        connector.setAntialias(1);
        connector.setSize(CONNECTOR_HEIGHT, CONNECTOR_HEIGHT);

        setSize(getWidth(), getHeight());
        line.setBounds(getBounds());
        line.setAntialias(1);
        line.setLineCap(SWT.CAP_ROUND);

        selection.setAlpha(128);
        selection.setFill(true);
        selection.setOutline(true);
        selection.setLineWidth(0);
        selection.setForegroundColor(IntelPalette.INTEL_BLUE);
        selection.setBackgroundColor(IntelPalette.LIGHT_BLUE);
        selection.setVisible(false);
        selection.setBounds(getBounds());

        setColor(IntelPalette.LIGHT_BLUE);
        setLineWidth(LINE_WIDTH);

        if (debug) {
            showPixelGrid(connector);
        }
    }

    private void showPixelGrid(final IFigure figure) {
        final int u = figure.getBounds().width / 8;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                final RectangleFigure r = new RectangleFigure();
                r.setBounds(new Rectangle(i * u, j * u, u + 1, u + 1));
                r.setFill(false);
                r.setOutline(true);
                r.setLineWidth(0);
                r.setForegroundColor(IntelPalette.RED);
                add(r);
            }
        }
    }

    /**
     * Retrieves the position of the connector center where link will be connected.
     *
     * @return the connector center location in absolute coordinates
     */
    public Point getConnectorCenterLocation() {
        final Point center = connector.getBounds().getCenter();
        translateToAbsolute(center);
        return center;
    }

    protected int getWidth() {
        return LINE_LENGTH + arrow.getBounds().width + connector.getBounds().width;
    }

    protected int getHeight() {
        return Math.max(arrow.getBounds().height, connector.getBounds().height);
    }

    protected ArrowFigure getArrow() {
        return arrow;
    }

    protected Ellipse getConnector() {
        return connector;
    }

    protected PolylineShape getLine() {
        return line;
    }

    @Override
    public void setLineWidth(final int w) {
        line.setLineWidth(w);
    }

    @Override
    public int getLineWidth() {
        return line.getLineWidth();
    }

    public void setColor(final Color color) {
        arrow.setForegroundColor(color);
        arrow.setBackgroundColor(color);
        line.setForegroundColor(color);
        line.setBackgroundColor(color);
        connector.setBackgroundColor(color);
        connector.setForegroundColor(color);
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

}
