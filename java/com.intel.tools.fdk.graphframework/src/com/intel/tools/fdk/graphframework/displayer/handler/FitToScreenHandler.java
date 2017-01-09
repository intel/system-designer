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

/**
 * Handler for a fit to screen button defined in an eclipse fragment (as Direct Tool Item)
 *
 * The button should be in "CHECK" style as this handler support the toggling capability of the FitToScreenController.
 *
 * WARNING: This handler should be associated to a direct tool item, thus it will be instantiated only after the first
 * click on the button. This is why this handler does not listen for fitToScreenController enablement state. To change
 * the style of the button after an external update of the fitToScreenController the part must retrieve the associated
 * button and set the state manually.
 */
public class FitToScreenHandler {

    @Execute
    /**
     * Enable the fit to screen
     *
     * @param fitToScreenController
     *            the controller created in the part (should be put in the eclipse context)
     */
    public void execute(final FitToScreenController fitToScreenController) {
        fitToScreenController.setFitEnabled(!fitToScreenController.isFitEnabled());
    }

}
