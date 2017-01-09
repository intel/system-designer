/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.tools.fdk.graphframework.figure.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import com.intel.tools.fdk.graphframework.figure.IGraphFigure;
import com.intel.tools.fdk.graphframework.figure.link.LinkAnchor;
import com.intel.tools.fdk.graphframework.figure.node.LeafBodyFigure;
import com.intel.tools.fdk.graphframework.figure.pin.InputFigure;
import com.intel.tools.fdk.graphframework.figure.pin.OutputFigure;
import com.intel.tools.fdk.graphframework.figure.pin.PinFigure;
import com.intel.tools.fdk.graphframework.graph.ILeaf;
import com.intel.tools.fdk.graphframework.graph.IPin;

/**
 * Basic Presenter which link some figures to create a complete and functional graph node.</br>
 * This class can be extended by the user to enable some customization.
 */
public class LeafPresenter extends Presenter<ILeaf> {

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

    private final LeafBodyFigure body;
    private final List<InputFigure> inputs = new ArrayList<>();
    private final List<OutputFigure> outputs = new ArrayList<>();
    private final Map<IPin, LinkAnchor> anchors = new HashMap<>();

    private boolean blockEvents = false;
    private Point boundsLocation = new Point(0, 0);

    /**
     * @param leaf
     *            the graph node to represent
     */
    public LeafPresenter(final ILeaf leaf) {
        super(leaf);
        // hides the global bounds figure which is used only for technical purpose
        this.boundsFigure.setVisible(false);

        // body configuration
        int height = Integer.max(leaf.getInputs().size(), leaf.getOutputs().size());
        if (height <= 1) {
            height = BODY_BASE_HEIGHT;
        } else {
            height = height * PIN_DISTANCE + PIN_OFFSET;
        }
        this.body = new LeafBodyFigure(leaf, BODY_WIDTH, height);
        leaf.getInputs().forEach(pin -> setupPinFigure(pin, new InputFigure(pin), this.inputs));
        leaf.getOutputs().forEach(pin -> setupPinFigure(pin, new OutputFigure(pin), this.outputs));

        getDisplayableFigures().add(boundsFigure);
        getDisplayableFigures().addAll(inputs);
        getDisplayableFigures().addAll(outputs);
        getDisplayableFigures().add(body);

        // Track body movement to keep sub-elements together
        body.addFigureListener(this::layoutFigures);
        boundsFigure.addFigureListener(new FigureListener() {
            @Override
            public void figureMoved(final IFigure source) {
                if (!blockEvents) {
                    body.translate(source.getBounds().x - boundsLocation.x, source.getBounds().y - boundsLocation.y);
                }
            }
        });
        layoutFigures(body);

        // Apply Style
        addLabel(leaf, body);
    }

    private void layoutFigures(final IFigure source) {
        assert source == body : "The source figure is not the presenter body";
        inputs.forEach(figure -> {
            figure.setLocation(new Point(body.getLocation().x - figure.getSize().width,
                    body.getLocation().y + PIN_OFFSET + inputs.indexOf(figure) * PIN_DISTANCE));
        });
        outputs.forEach(figure -> {
            figure.setLocation(new Point(body.getLocation().x + body.getBounds().width,
                    body.getLocation().y + PIN_OFFSET + outputs.indexOf(figure) * PIN_DISTANCE));
        });
        updateBoundsFigure();
    }

    @Override
    public IGraphFigure getNodeBody() {
        return body;
    }

    protected InputFigure getInput(final int id) {
        return this.inputs.get(id);
    }

    protected OutputFigure getOutput(final int id) {
        return this.outputs.get(id);
    }

    /**
     * Retrieve an anchor associated to a {@link IPin}
     *
     * @param pin
     *            the connected pin
     * @return the anchor connected to the given pin
     */
    public LinkAnchor getAnchor(final IPin pin) {
        return anchors.get(pin);
    }

    /**
     * Recalculate complete union of sub-figures bounds and update dedicated field.
     */
    private void updateBoundsFigure() {
        this.blockEvents = true;
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
        boundsLocation = boundsFigure.getLocation().getCopy();
        this.blockEvents = false;
    }

    /**
     * Setup a PinFigure
     *
     * @param pin
     *            the pin to represent
     * @param pinFigure
     *            the figure which represent the pin
     * @param pinList
     *            the list to store the created pin figure
     */
    private <P extends IPin, F extends PinFigure<P>> void setupPinFigure(final P pin, final F pinFigure,
            final List<F> pinList) {
        pinList.add(pinFigure);
        this.anchors.put(pin, new LinkAnchor(boundsFigure, pinFigure));
    }

    @Override
    public IFigure getBoundsFigure() {
        return boundsFigure;
    }

}
