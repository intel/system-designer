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
import org.eclipse.draw2d.geometry.PrecisionPoint;

import com.intel.tools.fdk.graphframework.displayer.GraphDisplayer;
import com.intel.tools.fdk.graphframework.figure.presenter.LeafPresenter;
import com.intel.tools.fdk.graphframework.graph.GraphException;
import com.intel.tools.fdk.graphframework.graph.factory.IGraphFactory;

/**
 * Graph layout which choose a position for each graph node.
 *
 * The algorithm used is the one defined in {@link AutoLayoutComputer}.
 *
 * @param <T>
 *            type of node presenter used
 */
public class AutoLayoutGenerator extends LayoutGenerator {

    public AutoLayoutGenerator(final IGraphFactory graphFactory) {
        super(graphFactory);
    }

    @Override
    public void displayGraph(final GraphDisplayer displayer) throws GraphException {
        super.displayGraph(displayer);
        final AutoLayoutComputer computer = new AutoLayoutComputer(getGraph());
        final int widthMax = getLeafPresenters().stream().mapToInt(leaf -> leaf.getBoundsFigure().getBounds().width)
                .max().orElse(0);
        final int heightMin = getLeafPresenters().stream().mapToInt(leaf -> leaf.getBoundsFigure().getBounds().height)
                .min().orElse(0);
        for (final LeafPresenter presenter : getLeafPresenters()) {
            final Point coord = computer.getCoordinate(presenter.getNode());
            presenter.getBoundsFigure().setLocation(new PrecisionPoint(coord.x * widthMax * 1.5, coord.y * heightMin));
        }
    }

    /**
     * Updates the graph, keeping current zoom location and scale
     */
    public void updateGraph(final GraphDisplayer displayer) throws GraphException {
        super.displayGraph(displayer);
    }

}
