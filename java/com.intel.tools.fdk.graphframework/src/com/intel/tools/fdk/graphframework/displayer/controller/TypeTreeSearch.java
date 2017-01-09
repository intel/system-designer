/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.tools.fdk.graphframework.displayer.controller;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.TreeSearch;

import com.intel.tools.fdk.graphframework.figure.IGraphFigure;

/** Allows to search all figures of a given type */
public class TypeTreeSearch implements TreeSearch {

    private final Class<? extends IGraphFigure> type;

    /** @param type class object of the searched type */
    public TypeTreeSearch(final Class<? extends IGraphFigure> type) {
        this.type = type;
    }

    @Override
    public boolean accept(final IFigure figure) {
        return type.isInstance(figure);
    }

    @Override
    public boolean prune(final IFigure figure) {
        return false;
    }

}
