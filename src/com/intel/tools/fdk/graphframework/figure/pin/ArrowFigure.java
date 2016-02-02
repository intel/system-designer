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

import org.eclipse.draw2d.PolygonShape;
import org.eclipse.swt.SWT;

/** Defines a an equilateral triangle which point to the right. */
public final class ArrowFigure extends PolygonShape {

    /**
     * @param size
     *            the desired size of the generated figure
     */
    public ArrowFigure(final int size) {
        setLineJoin(SWT.JOIN_ROUND);
        setAntialias(1);
        // if the line width is changed, you must adapt triangle point accordingly
        setLineWidth(2);

        setSize(size, size);
        getPoints().addPoint(1, 1);
        getPoints().addPoint(1, size - 1);
        getPoints().addPoint(size - 1, size / 2);
    }

}
