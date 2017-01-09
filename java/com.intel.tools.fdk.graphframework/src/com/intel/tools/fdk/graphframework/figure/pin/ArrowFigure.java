/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.tools.fdk.graphframework.figure.pin;

import org.eclipse.draw2d.PolylineShape;
import org.eclipse.swt.SWT;

/** Draws an arrow which point to the right. */
public final class ArrowFigure extends PolylineShape {

    /**
     * @param size
     *            the desired size of the generated figure
     */
    public ArrowFigure(final int size, final int lineWidth) {

        setLineWidth(lineWidth);
        setSize(size, size);

        setLineJoin(SWT.JOIN_ROUND);
        setLineCap(SWT.CAP_ROUND);
        setAntialias(1);

        getPoints().addPoint(lineWidth / 2, lineWidth / 2);
        getPoints().addPoint(size - lineWidth / 2 - 1, size / 2);
        getPoints().addPoint(lineWidth / 2, size - lineWidth / 2 - 1);
    }

}
