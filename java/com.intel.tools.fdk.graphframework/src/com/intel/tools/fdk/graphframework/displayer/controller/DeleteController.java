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

import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

import com.intel.tools.fdk.graphframework.displayer.GraphDisplayer;
import com.intel.tools.fdk.graphframework.graph.action.IGraphAction;
import com.intel.tools.fdk.graphframework.graph.impl.Group;
import com.intel.tools.fdk.graphframework.graph.impl.Leaf;
import com.intel.tools.fdk.graphframework.graph.impl.Link;

/**
 * This controller listens to the DEL key to remove the currently selected node or link using {@link IGraphAction}
 */
public class DeleteController {

    /**
     * @param graphAction
     *            the {@link IGraphAction} to use to remove graph objects
     * @param displayer
     *            the displayer containing elements that can be deleted
     * @param selectionController
     *            the controller used to retrieve current selection
     */
    public DeleteController(final IGraphAction<?> graphAction, final GraphDisplayer displayer,
            final ESelectionService selectionService) {
        displayer.getControl().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                if (SWT.DEL == e.keyCode) {
                    final Object selection = selectionService.getSelection();
                    if (selection != null) {
                        if (selection instanceof Leaf) {
                            graphAction.removeNode((Leaf) selection);
                        } else if (selection instanceof Group) {
                            graphAction.removeNode(((Group) selection));
                        } else if (selection instanceof Link) {
                            graphAction.removeLink(((Link) selection));
                        }
                    }
                }
            }
        });
    }

}