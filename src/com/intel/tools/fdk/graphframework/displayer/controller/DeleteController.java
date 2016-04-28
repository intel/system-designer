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