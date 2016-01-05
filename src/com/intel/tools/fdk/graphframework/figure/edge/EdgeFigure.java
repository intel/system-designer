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
package com.intel.tools.fdk.graphframework.figure.edge;

import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.swt.SWT;

import com.intel.tools.fdk.graphframework.figure.IGraphFigure;

/** Represent a graph edge */
public class EdgeFigure extends PolylineConnection implements IGraphFigure {

    public EdgeFigure(final EdgeAnchor source, final EdgeAnchor target) {
        setSourceAnchor(source);
        setTargetAnchor(target);

        setLineCap(SWT.CAP_ROUND);
        setLineJoin(SWT.JOIN_ROUND);
        setColor(IGraphFigure.DEFAULT_COLOR);
    }

}
