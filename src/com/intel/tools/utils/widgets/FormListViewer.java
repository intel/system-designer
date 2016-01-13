/* ============================================================================
 * INTEL CONFIDENTIAL
 *
 * Copyright 2015 Intel Corporation All Rights Reserved.
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
package com.intel.tools.utils.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.wb.swt.ResourceManager;

import com.intel.tools.utils.Activator;
import com.intel.tools.utils.IntelPalette;
import com.intel.tools.utils.widgets.FlatButton.FlatButtonStyle;
import com.intel.tools.utils.widgets.IFormListContentProvider.IFormListContentProviderListener;

/**
 * Creates a form list viewer class.
 *
 * @param <I>
 *            The type of the main elements to display
 * @param <S>
 *            The type of the subitems to display
 */
public class FormListViewer<I, S> implements IFormListContentProviderListener {
    private static final Color HIGHLIGHT_COLOR = IntelPalette.ORANGE;
    private static final Color CARD_COLOR = IntelPalette.WHITE;
    private static final Color BACKGROUND_COLOR = IntelPalette.GREY_2;
    /**
     * The content provider
     */
    private IFormListContentProvider<I, S> contentProvider;
    private Object input;
    private IFormListCompositeProvider<I, S> compositeProvider;
    private final Composite parent;
    private final Composite mainComposite;
    private Set<I> expandedSet;
    private HashMap<I, List<Control>> itemCompositeMap;
    private HashMap<I, FlatButton> expandButtonMap;


    public FormListViewer(final Composite parent) {
        this.parent = parent;

        final ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL);
        scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        scrolledComposite.setBackground(BACKGROUND_COLOR);
        scrolledComposite.setLayout(new FillLayout());
        scrolledComposite.setExpandHorizontal(true);
        mainComposite = new Composite(scrolledComposite, SWT.NONE);
        scrolledComposite.setContent(mainComposite);

        mainComposite.setLayout(new GridLayout(1, false));
        mainComposite.setBackground(BACKGROUND_COLOR);
        mainComposite.setBackgroundMode(SWT.INHERIT_FORCE);
        scrolledComposite.addListener(SWT.Activate, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                scrolledComposite.setFocus();
            }
        });
        scrolledComposite.forceFocus();
    }


    /**
     * Set the content provider to provide content to display
     *
     * @param contentProvider
     *            the content provider
     */
    public void setContentProvider(final IFormListContentProvider<I, S> contentProvider) {
        if (this.contentProvider != null) {
            this.contentProvider.removeListener(this);
        }
        contentProvider.addListener(this);
        this.contentProvider = contentProvider;
        if (input != null) {
            contentProvider.setInput(input);
        }
    }

    public void setCompositeProvider(final IFormListCompositeProvider<I, S> compositeProvider) {
        this.compositeProvider = compositeProvider;
    }

    public void setInput(final Object input) {
        this.input = input;
        expandedSet = new HashSet<>();
        if (contentProvider != null) {
            contentProvider.setInput(input);
        }
    }

    @Override
    public void contentUpdated() {
        if (compositeProvider == null || contentProvider == null || mainComposite.isDisposed()) {
            return;
        }
        for (final Control control : mainComposite.getChildren()) {
            control.dispose();
        }
        itemCompositeMap = new HashMap<>();
        expandButtonMap = new HashMap<>();
        for (final I item : contentProvider.getItemList()) {
            // sub composite contains all information relative to an Item.
            final Composite subComposite = new Composite(mainComposite, SWT.NONE);
            subComposite.setBackground(CARD_COLOR);
            final GridLayout subCompositeLayout = new GridLayout(2, false);
            subCompositeLayout.verticalSpacing = 8;
            subCompositeLayout.marginTop = 8;
            subCompositeLayout.horizontalSpacing = 0;
            subCompositeLayout.marginHeight = 0;
            subCompositeLayout.marginWidth = 0;
            subComposite.setLayout(subCompositeLayout);
            subComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

            // sc2 on the left of subComposite is for the Expand button.
            final Composite sc2 = new Composite(subComposite, SWT.NONE);
            final FillLayout fillLayout = new FillLayout();
            fillLayout.marginHeight = 4;
            fillLayout.marginWidth = 4;
            sc2.setLayout(fillLayout);
            sc2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

            // sc1, on the right of subComposite contains the UI provided by the compositeProvider.
            final Composite sc1 = new Composite(subComposite, SWT.NONE);
            sc1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
            sc1.setBackground(CARD_COLOR);
            compositeProvider.createUI(sc1, item);

            // This underlines the subComposite. with a beautifull orange line.
            final Label underLineLabel = new Label(subComposite, SWT.NONE);
            final GridData gd = new GridData(SWT.FILL, SWT.BOTTOM, true, false, 2, 1);
            gd.heightHint = 2;
            underLineLabel.setLayoutData(gd);
            underLineLabel.setBackground(HIGHLIGHT_COLOR);

            final FlatButton expandButton = new FlatButton(sc2, FlatButtonStyle.SMALL_BUTTON);
            // expandButton.setText("Expand");
            expandButton.setImage(ResourceManager.getPluginImage(Activator.getContext().getBundle().getSymbolicName(),
                    "images/expand.png"));
            expandButtonMap.put(item, expandButton);
            expandButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(final SelectionEvent e) {
                    if (expandedSet.contains(item)) {
                        collapseItem(item);
                    } else {
                        expandItem(item);
                    }
                }
            });

            itemCompositeMap.put(item, new ArrayList<Control>());
            for (final S subItem : contentProvider.getSubItemList(item)) {
                final Composite sc3 = new Composite(subComposite, SWT.NONE);
                sc3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
                ((GridData) sc3.getLayoutData()).exclude = true;
                sc3.setBackground(CARD_COLOR);
                compositeProvider.createSubUI(sc3, subItem);
                itemCompositeMap.get(item).add(sc3);

                // This underlines each sub item.. with a beautifull orange line.
                final Label underlineLabel2 = new Label(subComposite, SWT.NONE);
                final GridData gd2 = new GridData(SWT.FILL, SWT.BOTTOM, true, false, 2, 1);
                gd2.heightHint = 2;
                gd2.exclude = true;
                underlineLabel2.setLayoutData(gd2);
                underlineLabel2.setBackground(BACKGROUND_COLOR);
                itemCompositeMap.get(item).add(underlineLabel2);

            }
            // Re-expand the item if needed
            if (expandedSet.contains(item)) {
                expandItem(item);
            }
        }
        mainComposite.pack();
        parent.layout();
    }

    public void collapseItem(final I item) {
        for (final Control composite : itemCompositeMap.get(item)) {
            ((GridData) composite.getLayoutData()).exclude = true;
        }
        expandButtonMap.get(item)
        .setImage(ResourceManager.getPluginImage(Activator.getContext().getBundle().getSymbolicName(),
                "images/expand.png"));

        expandedSet.remove(item);
        mainComposite.pack();
        parent.layout();
    }

    public void expandItem(final I item) {
        for (final Control composite : itemCompositeMap.get(item)) {
            ((GridData) composite.getLayoutData()).exclude = false;
        }
        expandButtonMap.get(item)
        .setImage(ResourceManager.getPluginImage(Activator.getContext().getBundle().getSymbolicName(),
                "images/collapse.png"));

        expandedSet.add(item);
        mainComposite.pack();
        parent.layout();
    }

    /**
     * Get the composite control representing the list.
     *
     * @return
     */
    public Control getControl() {
        return mainComposite;
    }
}
