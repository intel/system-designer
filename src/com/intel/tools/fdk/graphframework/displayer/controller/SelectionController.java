/* ============================================================================
 * INTEL CONFIDENTIAL
 *
 * Copyright 2015-2016 Intel Corporation All Rights Reserved.
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
import java.util.Arrays;
import java.util.List;

import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;

import com.intel.tools.fdk.graphframework.displayer.GraphDisplayer;
import com.intel.tools.fdk.graphframework.figure.IGraphFigure;

/**
 * Controller allowing to select an element of a displayer
 *
 * When a selection is made, select() is called on all listeners.<br/>
 * When a right-click is made, showContextMenu() is called on all listeners.
 */
public class SelectionController {

    public interface IListener {
        default void select(final IGraphFigure figure) {
        }

        default void showContextMenu(final IGraphFigure figure) {
        }
    }

    private IGraphFigure currentSelection;
    private final List<Class<? extends IGraphFigure>> selectionClasses;

    private final List<IListener> listeners = new ArrayList<>();

    @SafeVarargs
    /**
     * @param displayer
     *            the displayer which contains elements which can be selected
     * @param selectionClasses
     *            type of elements which can be selected. selectionClasses is a variadic parameter which use its order
     *            as priority. An object of a type which is passed before another one will be selected in priority. This
     *            is useful when working with composed objects.
     */
    public SelectionController(final GraphDisplayer displayer,
            final Class<? extends IGraphFigure>... selectionClasses) {
        this.selectionClasses = Arrays.asList(selectionClasses);
        final SelectionListener contentListener = new SelectionListener(displayer.getContentLayer());
        final SelectionListener toolsListener = new SelectionListener(displayer.getToolsLayer());
        displayer.getContentLayer().addMouseListener(contentListener);
        displayer.getToolsLayer().addMouseListener(toolsListener);
        displayer.getConnectionLayer().addMouseListener(new SelectionListener(displayer.getConnectionLayer()));
        displayer.getBackgroundLayer().addMouseListener(new MouseListener.Stub() {
            @Override
            public void mousePressed(final MouseEvent event) {
                // background cliked, deselect
                if (currentSelection != null) {
                    currentSelection.unselect();
                    fireSelect(null);
                    currentSelection = null;
                }
            }
        });
    }

    /**
     * Select a given figure
     *
     * @param figure
     *            the figure to select
     */
    public void select(final IGraphFigure figure) {
        for (final Class<? extends IGraphFigure> selectionClass : selectionClasses) {
            if (selectionClass.isAssignableFrom(figure.getClass())) {
                final IGraphFigure old = currentSelection;
                if (old != null) {
                    old.unselect();
                }
                currentSelection = figure;
                currentSelection.select();
                fireSelect(currentSelection);
                return;
            }
        }
    }

    public void addListener(final IListener listener) {
        listeners.add(listener);
    }

    public void removeListener(final IListener listener) {
        listeners.remove(listener);
    }

    protected void fireSelect(final IGraphFigure figure) {
        for (final IListener listener : listeners) {
            listener.select(figure);
        }
    }

    private void fireShowContextMenu(final IGraphFigure figure) {
        for (final IListener listener : listeners) {
            listener.showContextMenu(figure);
        }
    }

    /**
     * Selection listener operating on the given layer. Handles left and right clicks on figures present on the
     * specified layer.
     */
    private class SelectionListener extends MouseListener.Stub {

        private final Layer layer;

        SelectionListener(final Layer layer) {
            this.layer = layer;
        }

        @Override
        public void mousePressed(final MouseEvent event) {
            final IGraphFigure old = currentSelection;
            if (old != null) {
                old.unselect();
            }
            for (final Class<? extends IGraphFigure> selectionClass : selectionClasses) {
                currentSelection = (IGraphFigure) layer.findFigureAt(
                        event.getLocation().x, event.getLocation().y,
                        new TypeTreeSearch(selectionClass));
                if (currentSelection != null) {
                    currentSelection.select();
                    // we found a matching selection, let's stop the search
                    fireSelect(currentSelection);
                    return;
                }
            }
        }

        @Override
        public void mouseReleased(final MouseEvent event) {
            // Handle right-click
            if (currentSelection != null && event.button == 3) {
                fireShowContextMenu(currentSelection);
            }
        }
    }

}