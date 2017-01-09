/* ============================================================================
 * INTEL CONFIDENTIAL
 * 
 * Copyright 2013 - 2014 Intel Corporation All Rights Reserved.
 * 
 * The source code contained or described herein and all documents related to
 * the source code ("Material") are owned by Intel Corporation or its suppliers
 * or licensors. Title to the Material remains with Intel Corporation or its
 * suppliers and licensors. The Material contains trade secrets and proprietary
 * and confidential information of Intel or its suppliers and licensors. The
 * Material is protected by worldwide copyright and trade secret laws and treaty
 * provisions. No part of the Material may be used, copied, reproduced,
 * modified, published, uploaded, posted, transmitted, distributed, or disclosed
 * in any way without Intel's prior express written permission.
 * 
 * No license under any patent, copyright, trade secret or other intellectual
 * property right is granted to or conferred upon you by disclosure or delivery
 * of the Materials, either expressly, by implication, inducement, estoppel or
 * otherwise. Any license under such intellectual property rights must be
 * express and approved by Intel in writing.
 * ============================================================================ */
/* -------------------------------------------------------------------------
 * Copyright (C) 2013 - 2014 Intel Mobile Communications GmbH
 * 
 * Sec Class: Intel Confidential (IC)
 * ---------------------------------------------------------------------- */
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
