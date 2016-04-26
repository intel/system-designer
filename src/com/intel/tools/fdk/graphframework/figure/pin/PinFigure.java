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
import org.eclipse.draw2d.PolylineShape;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Color;

import com.intel.tools.fdk.graphframework.figure.IGraphFigure;
import com.intel.tools.fdk.graphframework.graph.IPin;

/** Abstract class representing a node I/O */
public abstract class PinFigure<IOType extends IPin> extends PolylineShape implements IGraphFigure {

    private static final int LINE_WIDTH = 1;

    /** Pin arrow height in {@link IGraphFigure#SIZE_UNIT} */
    private static final int ARROW_HEIGHT = SIZE_UNIT / 2;
    /** Pin connector height in {@link IGraphFigure#SIZE_UNIT} */
    private static final int CONNECTOR_HEIGHT = SIZE_UNIT / 2;
    /** Pin line length in {@link IGraphFigure#SIZE_UNIT} */
    private static final int LINE_LENGTH = SIZE_UNIT;

    private final IOType pin;

    private final ArrowFigure arrow = new ArrowFigure(ARROW_HEIGHT);
    private final Ellipse connector = new Ellipse();
    private final PolylineShape line = new PolylineShape();

    /**
     * @param pin
     *            the pin graph element represented by this figure
     */
    public PinFigure(final IOType pin) {
        this.pin = pin;
        add(arrow);
        add(connector);
        add(line);

        connector.setSize(CONNECTOR_HEIGHT, CONNECTOR_HEIGHT);
        setSize(getWidth(), getHeight());
        line.setBounds(getBounds());

        connector.setOutline(false);
        connector.setAntialias(1);

        setColor(DEFAULT_COLOR);
        setLineWidth(LINE_WIDTH);
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
        setLineWidth(getLineWidth() + 1);
    }

    @Override
    public void unselect() {
        setLineWidth(getLineWidth() - 1);
    }

    /**
     * @return the pin graph element represented by this figure
     */
    public IOType getPin() {
        return pin;
    }

}
