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

import com.intel.tools.fdk.graphframework.displayer.controller.FitToScreenController;
import com.intel.tools.fdk.graphframework.layout.AutoLayoutGenerator;

/**
 * Handler allowing to run the auto layout algorithm on a displayed graph
 */
public class AutoLayoutHandler {

    @Execute
    /**
     * Run the auto layout algorithm on the currently displayed graph
     *
     * @param layoutGenerator
     *            the layout generator used to display the current graph (should be put in the eclipse context)
     * @param fitToScreenController
     *            the controller created in the part (should be put in the eclipse context)
     */
    public void execute(final AutoLayoutGenerator layoutGenerator, final FitToScreenController fitToScreenController) {
        layoutGenerator.layout();
        fitToScreenController.fitToScreen();
    }

}
