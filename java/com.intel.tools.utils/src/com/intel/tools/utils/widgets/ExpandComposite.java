/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.tools.utils.widgets;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;

import com.intel.tools.utils.Activator;
import com.intel.tools.utils.IntelPalette;
import com.intel.tools.utils.widgets.FlatButton.FlatButtonStyle;

/**
 * Creates a form list viewer class.
 */
public class ExpandComposite {

    /** Expandable section holder */
    private class Section {
        private final String title;
        private final FlatButton expandButton;
        private final List<Control> controls = new ArrayList<>();

        public Section(final String title, final FlatButton expandButton) {
            this.title = title;
            this.expandButton = expandButton;
        }

        public String getTitle() {
            return title;
        }

        public FlatButton getExpandButton() {
            return expandButton;
        }

        public List<Control> getControls() {
            return controls;
        }
    }

    /** Property allowing to subscribe to "section expand" events */
    public static final String EXPAND_PROPERTY = "expand";
    /** Property allowing to subscribe to "section collapse" events */
    public static final String COLLAPSE_PROPERTY = "collapse";

    private static final Color HIGHLIGHT_COLOR = IntelPalette.ORANGE;
    private static final Color CARD_COLOR = IntelPalette.WHITE;
    private static final Color BACKGROUND_COLOR = IntelPalette.WHITE;

    private final PropertyChangeSupport changeSupport;

    private final Composite parent;
    private final Composite mainComposite;
    private final Set<Composite> expandedSet = new HashSet<>();
    private final Map<Composite, Section> sections = new HashMap<>();
    private final ScrolledComposite scrolledComposite;

    public ExpandComposite(final Composite parent) {
        this.parent = parent;
        this.changeSupport = new PropertyChangeSupport(this);

        scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
        final GridData scrollGridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);

        scrolledComposite.setLayoutData(scrollGridData);
        scrolledComposite.setBackground(BACKGROUND_COLOR);
        scrolledComposite.setExpandHorizontal(true);
        mainComposite = new Composite(scrolledComposite, SWT.NONE);
        scrolledComposite.setContent(mainComposite);
        final GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        mainComposite.setLayout(gridLayout);
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
     * Set the layout data to the expand composite. (This will set the layout data to the main composite of the expand
     * "meta" composite
     *
     * @param layoutData
     *            Layout data to set.
     */
    public void setLayoutData(final Object layoutData) {
        scrolledComposite.setLayoutData(layoutData);
    }

    /**
     * Add a new composite.
     *
     * @param title
     *            the title of the composite
     * @return the newly created composite
     */
    public Composite addComposite(final String title) {

        // sub composite contains all information relative to an Item.
        final Composite subComposite = new Composite(mainComposite, SWT.NONE);
        subComposite.setBackground(CARD_COLOR);
        final GridLayout subCompositeLayout = new GridLayout(2, false);
        subCompositeLayout.verticalSpacing = 0;
        subCompositeLayout.marginTop = 0;
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
        sc1.setLayout(new GridLayout(1, true));

        final TitleLabel titleLabel = new TitleLabel(sc1);
        final Font titleFont = SWTResourceManager.getFont("Intel Clear", 10, SWT.BOLD);
        titleLabel.setTitleFont(titleFont);
        titleLabel.setTitleText(title);

        // This underlines the subComposite... with a beautiful orange line.
        final Label underLineLabel = new Label(subComposite, SWT.NONE);
        final GridData gd = new GridData(SWT.FILL, SWT.BOTTOM, true, false, 2, 1);
        gd.heightHint = 2;
        underLineLabel.setLayoutData(gd);
        underLineLabel.setBackground(HIGHLIGHT_COLOR);

        final FlatButton expandButton = new FlatButton(sc2, FlatButtonStyle.SMALL_BUTTON);
        // expandButton.setText("Expand");
        expandButton.setImage(ResourceManager.getPluginImage(Activator.getContext().getBundle().getSymbolicName(),
                "images/expand.png"));
        final Section section = new Section(title, expandButton);

        final Composite sc3 = new Composite(subComposite, SWT.NONE);
        sc3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
        ((GridData) sc3.getLayoutData()).exclude = true;
        sc3.setBackground(CARD_COLOR);
        section.getControls().add(sc3);

        // This underlines each sub item... with this still beautiful orange line.
        final Label underlineLabel2 = new Label(subComposite, SWT.NONE);
        final GridData gd2 = new GridData(SWT.FILL, SWT.BOTTOM, true, false, 2, 1);
        gd2.heightHint = 2;
        gd2.exclude = true;
        underlineLabel2.setLayoutData(gd2);
        underlineLabel2.setBackground(BACKGROUND_COLOR);
        section.getControls().add(underlineLabel2);

        expandButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                if (expandedSet.contains(sc3)) {
                    collapseItem(sc3);
                } else {
                    expandItem(sc3);
                }
            }
        });
        sections.put(sc3, section);
        refreshLayouting();
        return sc3;
    }

    /**
     * Remove a composite.
     *
     * @param title
     *            the title of the composite
     */
    public void removeComposite(final String title) {
        scrolledComposite.setRedraw(false);

        final Composite itemComposite = findComposite(title);
        if (itemComposite != null) {
            sections.remove(itemComposite);
            itemComposite.getParent().dispose();
        }
        refreshLayouting();
        scrolledComposite.setRedraw(true);
    }

    /**
     * Remove all composites.
     */
    public void removeAllComposites() {
        scrolledComposite.setRedraw(false);

        // Clear all the sections
        final Iterator<Entry<Composite, Section>> iterator = sections.entrySet().iterator();
        while (iterator.hasNext()) {
            final Entry<Composite, Section> entry = iterator.next();
            // Clear the item's parent composite (and all its children)
            entry.getKey().getParent().dispose();
            iterator.remove();
        }
        refreshLayouting();
        scrolledComposite.setRedraw(true);
    }

    /**
     * Search for an existing composite.
     *
     * @param title
     *            the title of the composite
     * @return the composite instance, otherwise {@code null}
     */
    public Composite findComposite(final String title) {
        Composite itemComposite = null;
        for (final Entry<Composite, Section> entry : sections.entrySet()) {
            if (entry.getValue().getTitle().equalsIgnoreCase(title)) {
                itemComposite = entry.getKey();
                break;
            }
        }

        return itemComposite;
    }

    /**
     * Collapse an item of this composite
     *
     * @param item
     *            the item to collapse
     */
    public void collapseItem(final Composite item) {
        scrolledComposite.setRedraw(false);

        for (final Control composite : sections.get(item).getControls()) {
            ((GridData) composite.getLayoutData()).exclude = true;
        }
        sections.get(item).getExpandButton()
        .setImage(ResourceManager.getPluginImage(Activator.getContext().getBundle().getSymbolicName(),
                "images/expand.png"));

        expandedSet.remove(item);
        refreshLayouting();
        scrolledComposite.setRedraw(true);

        // Notify the collapse
        changeSupport.firePropertyChange(COLLAPSE_PROPERTY, null, item);
    }

    /**
     * Refreshes the layout of the widget.
     */
    private void refreshLayouting() {
        mainComposite.pack();
        scrolledComposite.setMinSize(mainComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        parent.layout();
    }

    /**
     * Expand an item of this composite
     *
     * @param item
     *            the item to expand
     */
    public void expandItem(final Composite item) {
        scrolledComposite.setRedraw(false);
        for (final Control composite : sections.get(item).getControls()) {
            ((GridData) composite.getLayoutData()).exclude = false;
        }
        sections.get(item).getExpandButton()
        .setImage(ResourceManager.getPluginImage(Activator.getContext().getBundle().getSymbolicName(),
                "images/collapse.png"));

        expandedSet.add(item);
        refreshLayouting();
        scrolledComposite.setRedraw(true);

        // Notify the expand
        changeSupport.firePropertyChange(EXPAND_PROPERTY, null, item);
    }

    /** Expand all items of this composite */
    public void collapseAllItems() {
        scrolledComposite.setRedraw(false);
        for (final Composite item : sections.keySet()) {
            collapseItem(item);
        }
        scrolledComposite.setRedraw(true);
    }

    /** Expand all items of this composite */
    public void expandAllItems() {
        scrolledComposite.setRedraw(false);
        for (final Composite item : sections.keySet()) {
            expandItem(item);
        }
        scrolledComposite.setRedraw(true);
    }

    /** Retrieve the title of a section */
    public String getSectionTitle(final Composite item) {
        return sections.get(item).getTitle();
    }

    /**
     * Add a new listener.
     *
     * This method allows a developer to be notified of an expand/collapse events through through EXPAND_PROPERTY and
     * COLLAPSE_PROPERTY properties. The new value linked to the event will always be the section composite which can be
     * edited by the user (i.e: the one returned by the addComposite method). The old value will always be set to null.
     *
     * @param listener
     *            the listener to notify
     */
    public synchronized void addPropertyChangeListener(final PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public synchronized void removePropertyChangeListener(final PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Get the composite control representing the list.
     *
     * @return the composite control representing the list
     */
    public Control getControl() {
        return mainComposite;
    }

}
