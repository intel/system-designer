/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.tools.fdk.graphframework.displayer.handler;

import org.eclipse.e4.core.di.annotations.Execute;

import com.intel.tools.fdk.graphframework.displayer.controller.ZoomController;

/** Handler for a zoom button defined in an eclipse fragment (as Direct Tool Item) */
public class ZoomInHandler {

    @Execute
    /**
     * Zoom in the the given graph displayer
     *
     * @param zoomController
     *            the controller to use to apply the zoom (should be in the eclipse context)
     */
    public void execute(final ZoomController zoomController) {
        zoomController.zoomIn();
    }

}
