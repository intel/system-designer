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
package com.intel.tools.fdk.graphframework.figure.pin;

import org.eclipse.draw2d.geometry.Point;

import com.intel.tools.fdk.graphframework.graph.IInput;

/**
 * Represent an input of a graph node
 *
 * This figure looks like: o>
 */
public class InputFigure extends PinFigure<IInput> {

    public InputFigure(final IInput input) {
        super(input);

        // Center the connector on pin height and put it on the left
        getConnector().setLocation(new Point(getPadding(),
                (getDesiredHeight() - getConnector().getBounds().height) / 2));
        // Center the arrow on pin height and put it on the right
        getArrow().setLocation(new Point(getDesiredWidth() - getArrow().getBounds().width - getPadding(),
                (getDesiredHeight() - getArrow().getBounds().height) / 2));
    }

}
