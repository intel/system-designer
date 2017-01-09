/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.tools.fdk.graphframework.figure.ghost;

import org.eclipse.swt.SWT;

import com.intel.tools.fdk.graphframework.figure.link.BeveledPolyLineConnection;
import com.intel.tools.fdk.graphframework.figure.link.LinkAnchor;
import com.intel.tools.utils.IntelPalette;

/** Represent a graph link with no relation with a graph element */
public class GhostLinkFigure extends BeveledPolyLineConnection {

    protected static final int LINE_WIDTH = 4;

    /**
     * @param source
     *            the source anchor
     * @param target
     *            the target anchor
     */
    public GhostLinkFigure(final LinkAnchor sourceAnchor, final LinkAnchor destAnchor) {
        setSourceAnchor(sourceAnchor);
        setTargetAnchor(destAnchor);

        setLineCap(SWT.CAP_ROUND);
        setLineJoin(SWT.JOIN_ROUND);
        setAntialias(1);
        setForegroundColor(IntelPalette.GREY);

        setLineWidth(LINE_WIDTH);
    }

}

