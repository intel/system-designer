/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.tools.fdk.graphframework.figure;

import org.eclipse.draw2d.IFigure;

/** Defines basic behavior of figure which compose a graph draw. */
public interface IGraphFigure extends IFigure {

    /** Unit size of every elements of the graph */
    int SIZE_UNIT = 16;

    /** Highlight the figure */
    void select();

    /** Remove the highlight decoration of the figure */
    void unselect();

}
