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
