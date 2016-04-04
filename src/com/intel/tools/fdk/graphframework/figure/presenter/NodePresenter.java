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
package com.intel.tools.fdk.graphframework.figure.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import com.intel.tools.fdk.graphframework.figure.IGraphFigure;
import com.intel.tools.fdk.graphframework.figure.link.LinkAnchor;
import com.intel.tools.fdk.graphframework.figure.node.NodeBodyFigure;
import com.intel.tools.fdk.graphframework.figure.pin.InputFigure;
import com.intel.tools.fdk.graphframework.figure.pin.OutputFigure;
import com.intel.tools.fdk.graphframework.figure.pin.PinFigure;
import com.intel.tools.fdk.graphframework.graph.Leaf;
import com.intel.tools.fdk.graphframework.graph.Link;

/**
 * Basic Presenter which link some figures to create a complete and functional graph node.</br>
 * This class can be extended by the user to enable some customization.
 */
public class NodePresenter extends Presenter<Leaf> {

    /** Body Width in {@link IGraphFigure#SIZE_UNIT} */
    private static final int BODY_WIDTH = 4 * IGraphFigure.SIZE_UNIT;
    /** Body start Height in {@link IGraphFigure#SIZE_UNIT} */
    private static final int BODY_BASE_HEIGHT = BODY_WIDTH;
    /** Distance between two pins in {@link IGraphFigure#SIZE_UNIT} */
    public static final int PIN_DISTANCE = 2 * IGraphFigure.SIZE_UNIT;
    /** Distance between body upper/lower link and first/last pin in {@link IGraphFigure#SIZE_UNIT} */
    public static final int PIN_OFFSET = IGraphFigure.SIZE_UNIT;

    /**
     * Union of bounds of all figures which compose the node </br>
     *
     * This field is necessary to use connections router correctly, it is used as parent anchor in order to take the
     * entire node into account for routing.
     */
    private final RectangleFigure boundsFigure = new RectangleFigure();

    private final NodeBodyFigure body;
    private final List<InputFigure> inputs = new ArrayList<>();
    private final List<OutputFigure> outputs = new ArrayList<>();
    private final Map<Link, LinkAnchor> anchors = new HashMap<>();

    /**
     * @param node
     *            the graph node to represent
     */
    public NodePresenter(final Leaf node) {
        super(node);
        // hides the global bounds figure which is used only for technical purpose
        this.boundsFigure.setVisible(false);

        // body configuration
        int height = Integer.max(node.getInputLinks().size(), node.getOutputLinks().size());
        if (height <= 1) {
            height = BODY_BASE_HEIGHT;
        } else {
            height = height * PIN_DISTANCE + PIN_OFFSET;
        }
        this.body = new NodeBodyFigure(BODY_WIDTH, height);
        node.getInputLinks().forEach(link -> setupPinFigure(link, InputFigure.class, this.inputs));
        node.getOutputLinks().forEach(link -> setupPinFigure(link, OutputFigure.class, this.outputs));

        getDisplayableFigures().add(boundsFigure);
        getDisplayableFigures().addAll(inputs);
        getDisplayableFigures().addAll(outputs);
        getDisplayableFigures().add(body);

        // Track body movement to keep sub-elements together
        body.addFigureListener(new FigureListener() {
            @Override
            public void figureMoved(final IFigure source) {
                inputs.forEach(figure -> {
                    figure.setLocation(new Point(body.getLocation().x - figure.getSize().width,
                            body.getLocation().y + PIN_OFFSET + inputs.indexOf(figure) * PIN_DISTANCE));
                });
                outputs.forEach(figure -> {
                    figure.setLocation(new Point(body.getLocation().x - 1 + body.getBounds().width,
                            body.getLocation().y + PIN_OFFSET + outputs.indexOf(figure) * PIN_DISTANCE));
                });
                updateBoundsFigure();
            }
        });
        updateBoundsFigure();
    }

    @Override
    public IGraphFigure getNodeBody() {
        return body;
    }

    protected PinFigure getInput(final int id) {
        return this.inputs.get(id);
    }

    protected PinFigure getOutput(final int id) {
        return this.outputs.get(id);
    }

    /**
     * Retrieve an anchor associated to a {@link Link}
     *
     * @param link
     *            the connected link
     * @return the anchor connected to the given link or null if link link is not linked to the input
     */
    public LinkAnchor getAnchor(final Link link) {
        return anchors.get(link);
    }

    /**
     * Recalculate complete union of sub-figures bounds and update dedicated field.
     */
    private void updateBoundsFigure() {
        final Rectangle rectangle = new Rectangle();
        // Make bounds figure empty
        boundsFigure.setBounds(rectangle);
        // Determine new bounds
        getDisplayableFigures().forEach(figure -> {
            if (rectangle.isEmpty()) {
                rectangle.setBounds(figure.getBounds());
            } else {
                rectangle.union(figure.getBounds());
            }
        });
        boundsFigure.setBounds(rectangle);
    }

    /**
     * Setup a PinFigure
     *
     * @param link
     *            the link linked to the pin to create
     * @param clazz
     *            the type of pin to create
     * @param pinList
     *            the list to store the created pin
     */
    private <T extends PinFigure> void setupPinFigure(final Optional<Link> link, final Class<T> clazz,
            final List<T> pinList) {
        try {
            final T pin = clazz.newInstance();
            pinList.add(pin);
            if (link.isPresent()) {
                this.anchors.put(link.get(), new LinkAnchor(boundsFigure, pin));
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException("When configuring node pin: The class used does not match expectation;");
        }
    }

}
