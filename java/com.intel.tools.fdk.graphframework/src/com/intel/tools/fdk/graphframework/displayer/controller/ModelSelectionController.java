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
package com.intel.tools.fdk.graphframework.displayer.controller;

import java.util.ArrayList;
import java.util.List;

import com.intel.tools.fdk.graphframework.displayer.GraphDisplayer;
import com.intel.tools.fdk.graphframework.figure.IGraphFigure;
import com.intel.tools.fdk.graphframework.figure.link.LinkFigure;
import com.intel.tools.fdk.graphframework.figure.node.GroupBodyFigure;
import com.intel.tools.fdk.graphframework.figure.node.LeafBodyFigure;
import com.intel.tools.fdk.graphframework.figure.pin.PinFigure;
import com.intel.tools.fdk.graphframework.graph.IGraph;
import com.intel.tools.fdk.graphframework.graph.IGroup;
import com.intel.tools.fdk.graphframework.graph.ILeaf;
import com.intel.tools.fdk.graphframework.graph.ILink;
import com.intel.tools.fdk.graphframework.graph.IPin;
import com.intel.tools.fdk.graphframework.graph.adapter.IAdapter;

/**
 * Super class to the selection controller to provide
 */
public class ModelSelectionController extends SelectionController {
    private final List<IModelSelectionListener> pressListeners = new ArrayList<>();
    private final List<IModelSelectionListener> releaseListeners = new ArrayList<>();
    private IAdapter adapter;

    public interface IModelSelectionListener {
        /**
         * Callback called whenever the graph is selected
         *
         * @param graph
         *            the selected graph.
         */
        default void graphSelected(final IGraph graph) {
        }

        /**
         * Callback called whenever a leaf is selected
         *
         * @param leaf
         *            the selected leaf.
         */
        default void leafSelected(final ILeaf leaf) {
        }

        /**
         * Callback called whenever a group is selected
         *
         * @param group
         *            the selected group.
         */
        default void groupSelected(final IGroup group) {
        }

        /**
         * Callback called whenever a link is selected
         *
         * @param link
         *            the selected link.
         */
        default void linkSelected(final ILink link) {
        }

        /**
         * Callback called whenever a pin is selected
         *
         * @param pin
         *            the selected pin.
         */
        default void pinSelected(final IPin pin) {
        }
    }

    /**
     * @param displayer
     */
    public ModelSelectionController(final GraphDisplayer displayer, final IAdapter adapter) {
        super(displayer, LeafBodyFigure.class, PinFigure.class, GroupBodyFigure.class, LinkFigure.class);
        setAdapter(adapter);
    }

    @Override
    protected void fireSelect(final IGraphFigure figure) {
        super.fireSelect(figure);
        notifySelectedElement(figure, pressListeners);
    }

    @Override
    protected void fireRelease(final IGraphFigure figure) {
        super.fireRelease(figure);
        notifySelectedElement(figure, releaseListeners);
    }

    /**
     * Sets the adapter used to retreive the graph when selecting the root of the displayer.
     *
     * @param adapter
     *            The adapter to use. (if null, the graph will not be selectable)
     */
    public void setAdapter(final IAdapter adapter) {
        assert (adapter != null) : "Model selection controller can't be set without an Adapter.";
        this.adapter = adapter;
    }

    /**
     * Find the Graph element corresponding to a given figure and sends a notification for this.
     */
    private void notifySelectedElement(final IGraphFigure figure, final List<IModelSelectionListener> listenerList) {
        if (figure == null && adapter != null) {
            for (final IModelSelectionListener iModelSelectionListener : listenerList) {
                iModelSelectionListener.graphSelected(adapter.getGraph());
            }
        } else if (figure instanceof LeafBodyFigure) {
            for (final IModelSelectionListener iModelSelectionListener : listenerList) {
                iModelSelectionListener.leafSelected(((LeafBodyFigure) figure).getLeaf());
            }
        } else if (figure instanceof GroupBodyFigure) {
            for (final IModelSelectionListener iModelSelectionListener : listenerList) {
                iModelSelectionListener.groupSelected(((GroupBodyFigure) figure).getGroup());
            }
        } else if (figure instanceof LinkFigure) {
            for (final IModelSelectionListener iModelSelectionListener : listenerList) {
                iModelSelectionListener.linkSelected(((LinkFigure) figure).getLink());
            }
        } else if (figure instanceof PinFigure) {
            for (final IModelSelectionListener iModelSelectionListener : listenerList) {
                iModelSelectionListener.pinSelected(((PinFigure<?>) figure).getPin());
            }
        }
    }

    public void addModelSelectionListener(final IModelSelectionListener listener) {
        pressListeners.add(listener);
    }

    public void removeModelSelectionListener(final IModelSelectionListener listener) {
        pressListeners.remove(listener);
    }

    public void addModelSelectionReleaseListener(final IModelSelectionListener listener) {
        releaseListeners.add(listener);
    }

    public void removeModelSelectionReleaseListener(final IModelSelectionListener listener) {
        releaseListeners.remove(listener);
    }

}
