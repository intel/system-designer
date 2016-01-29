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
package com.intel.tools.fdk.graphframework.layout;

import org.eclipse.draw2d.geometry.Point;

import com.intel.tools.fdk.graphframework.displayer.GraphDisplayer;
import com.intel.tools.fdk.graphframework.figure.controller.NodeController;
import com.intel.tools.fdk.graphframework.graph.GraphException;
import com.intel.tools.fdk.graphframework.graph.factory.IGraphFactory;

/**
 * Graph layout which choose a position for each graph node.
 *
 * The algorithm used is the one defined in {@link AutoLayoutComputer}.
 *
 * @param <T>
 *            type of node controller used
 */
public class AutoLayoutGenerator<T extends NodeController> extends LayoutGenerator<T> {

    public AutoLayoutGenerator(final IGraphFactory<T> graphFactory) {
        super(graphFactory);
    }

    @Override
    public void displayGraph(final GraphDisplayer displayer) throws GraphException {
        super.displayGraph(displayer);
        final AutoLayoutComputer computer = new AutoLayoutComputer(getGraph());
        for (final NodeController controller : getControllers()) {
            final Point coord = computer.getCoordinate(controller.getNode());
            /**
             * FIXME: Let the layout adapt the coordinate and do not use hardcoded value to create absolute
             * coordinates
             */
            controller.getNodeBody().setLocation(new Point(coord.x * 90, coord.y * 45));
        }
    }

}
