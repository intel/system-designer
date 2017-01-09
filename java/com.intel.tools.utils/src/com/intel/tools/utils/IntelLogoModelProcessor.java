/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.tools.utils;

import java.util.List;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.SideValue;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimBar;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MToolControl;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

/**
 * 
 */
public class IntelLogoModelProcessor {

    @Execute
    public void execute(EPartService partService, EModelService modelService, MApplication app) {

        List<MTrimmedWindow> windowLst = modelService.findElements(app, "IDEWindow", MTrimmedWindow.class, null);

        for (MTrimmedWindow window : windowLst) {
            MTrimBar trimbar = getTopTrim(window, modelService);

            MToolControl toolControl = modelService.createModelElement(MToolControl.class);
            toolControl.setElementId("com.intel.tools.intellogo.toolcontrol");

            toolControl.setContributionURI("bundleclass://"
                + Activator.getContext().getBundle().getSymbolicName() + "/"
                + IntelLogoToolItem.class.getCanonicalName());

            trimbar.getChildren().add(toolControl);
        }
    }

    /**
     * Find the top trim bar.
     * 
     * @param window
     *            The windows where to search.
     * @param modelService
     *            the model service.
     * @return The top trim bar.
     */
    public final MTrimBar getTopTrim(final MTrimmedWindow window, final EModelService modelService) {
        if (window == null) {
            return null;
        }

        // Left trimbar doesn't exist, create it.
        MTrimBar trimBar = modelService.createModelElement(MTrimBar.class);
        trimBar.setElementId("com.intel.tools.intelLogo.trimbar");
        trimBar.setSide(SideValue.TOP);
        window.getTrimBars().add(trimBar);

        return trimBar;
    }

}
