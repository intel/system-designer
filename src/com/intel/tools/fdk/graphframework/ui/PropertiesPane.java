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
package com.intel.tools.fdk.graphframework.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.intel.tools.fdk.graphframework.displayer.controller.ModelSelectionController;
import com.intel.tools.fdk.graphframework.displayer.controller.ModelSelectionController.IModelSelectionListener;
import com.intel.tools.fdk.graphframework.graph.IGraph;
import com.intel.tools.fdk.graphframework.graph.IGroup;
import com.intel.tools.fdk.graphframework.graph.ILeaf;
import com.intel.tools.fdk.graphframework.graph.ILink;
import com.intel.tools.fdk.graphframework.graph.IPin;
import com.intel.tools.utils.IntelPalette;

public class PropertiesPane extends ScrolledComposite {

    private IGraphUIProvider uiProvider;
    private final Composite containerComposite;

    public PropertiesPane(final Composite parent, final int style, final IGraphUIProvider uiProvider,
            final ModelSelectionController selectionController) {
        super(parent, style | SWT.V_SCROLL);
        setModelSelectionController(selectionController);
        setGraphUIProvider(uiProvider);

        final GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        setLayout(gridLayout);

        setBackground(IntelPalette.WHITE);
        setBackgroundMode(SWT.INHERIT_FORCE);

        containerComposite = new Composite(this, SWT.NONE);
        setContent(containerComposite);
        setExpandHorizontal(true);

        containerComposite.setBackground(IntelPalette.WHITE);
        containerComposite.setBackgroundMode(SWT.INHERIT_FORCE);

        addListener(SWT.Activate, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                setFocus();
            }
        });
        forceFocus();
    }

    /**
     * Sets the provider for graph items UI.
     *
     * @param uiProvider
     *            The UI provider.
     */
    public void setGraphUIProvider(final IGraphUIProvider uiProvider) {
        this.uiProvider = uiProvider;
    }

    public void setModelSelectionController(final ModelSelectionController selectionController) {
        selectionController.addModelSelectionListener(new IModelSelectionListener() {
            @Override
            public void graphSelected(final IGraph graph) {
                setRedraw(false);
                resetUI();
                if (uiProvider != null) {
                    uiProvider.createUI(containerComposite, graph);
                }
                relayout();
                setRedraw(true);
            }

            @Override
            public void groupSelected(final IGroup group) {
                setRedraw(false);
                resetUI();
                if (uiProvider != null) {
                    uiProvider.createUI(containerComposite, group);
                }
                relayout();
                setRedraw(true);
            }

            @Override
            public void leafSelected(final ILeaf leaf) {
                setRedraw(false);
                resetUI();
                if (uiProvider != null) {
                    uiProvider.createUI(containerComposite, leaf);
                }
                relayout();
                setRedraw(true);
            }

            @Override
            public void pinSelected(final IPin pin) {
                setRedraw(false);
                resetUI();
                // We don't have any layout to create for a pin selection.
                relayout();
                setRedraw(true);
            }

            @Override
            public void linkSelected(final ILink link) {
                setRedraw(false);
                resetUI();
                if (uiProvider != null) {
                    uiProvider.createUI(containerComposite, link);
                }
                relayout();
                setRedraw(true);
            }

        });
    }

    protected void resetUI() {
        for (final Control control : containerComposite.getChildren()) {
            control.dispose();
        }
    }

    protected void relayout() {
        containerComposite.pack();
        layout();
    }
}
