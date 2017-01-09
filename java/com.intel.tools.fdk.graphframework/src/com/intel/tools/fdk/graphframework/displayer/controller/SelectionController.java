/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
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

        default void selectReleased(final IGraphFigure figure) {
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
            private boolean justUnselected = false;

            @Override
            public void mousePressed(final MouseEvent event) {
                // background cliked, deselect
                if (currentSelection != null) {
                    currentSelection.unselect();
                    fireSelect(null);
                    currentSelection = null;
                    justUnselected = true;
                }
            }

            @Override
            public void mouseReleased(final MouseEvent event) {
                super.mouseReleased(event);
                if (justUnselected) {
                    justUnselected = false;
                    fireRelease(null);
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
                fireRelease(null);
                return;
            }
        }
    }

    /**
     * Unselect all figures
     */
    public void unselect() {
        if (currentSelection != null) {
            currentSelection.unselect();
        }
        currentSelection = null;
        fireSelect(null);
        fireRelease(null);
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

    protected void fireRelease(final IGraphFigure figure) {
        for (final IListener listener : listeners) {
            listener.selectReleased(figure);
        }
    }

    protected void fireShowContextMenu(final IGraphFigure figure) {
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
            if (currentSelection != null) {
                if (event.button == 3) {
                    fireShowContextMenu(currentSelection);
                }
                fireRelease(currentSelection);
            }
        }
    }

}