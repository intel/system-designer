/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.tools.utils.widgets.viewerfiltercomposite;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.wb.swt.ResourceManager;

import com.intel.tools.utils.Activator;
import com.intel.tools.utils.IntelPalette;
import com.intel.tools.utils.widgets.FlatButton;
import com.intel.tools.utils.widgets.FlatButton.FlatButtonStyle;

/**
 * A composite used to display and configure a filter. This composite requires a filter that derives from
 * {@link ExtendedViewerFilter} in order to be effective.
 */
public class ViewerFilterComposite extends Composite {

    private static final String BUNDLE_SYMBOLIC_NAME = "com.intel.tools.utils";

    /**
     * The default text displayed in the search text box.
     */
    private static final String DEFAULT_FILTER_TEXT = "type filter text";

    /**
     * Field used to filter data in the viewer
     */
    private Text textSearch;

    /**
     * The button to clear the filter string
     */
    private FlatButton buttonClear;

    /**
     * Button to configure the filter string
     */
    private FlatButton buttonConfigure;

    /**
     * The filter that will perform the search
     */
    private ExtendedViewerFilter filter;

    /**
     * Default constructor
     *
     * @param parent
     *            the parent composite
     * @param style
     *            the style to apply
     */
    public ViewerFilterComposite(final Composite parent, final int style) {
        super(parent, style);

        createUi(parent);
    }

    /**
     * Add a modification listener.
     *
     * @param listener
     *            the new modification listener
     * @throws SWTException
     */
    public void addModifyListener(final ModifyListener listener) {
        // Check
        checkWidget();
        if (listener == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }

        // Add a new listener
        addListener(SWT.Modify, new TypedListener(listener));
    }

    /**
     * Remove a modification listener.
     *
     * @param listener
     *            the modification listener to remove
     * @throws SWTException
     */
    public void removeModifyListener(final ModifyListener listener) {
        // Check
        checkWidget();
        if (listener == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }

        // Remove the listener
        removeListener(SWT.Modify, listener);
    }

    /**
     * Define the content filter.
     *
     * @param filter
     *            the filter
     */
    public void setViewerFilter(final ExtendedViewerFilter filter) {
        this.filter = filter;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.swt.widgets.Control#setToolTipText(java.lang.String)
     */
    @Override
    public void setToolTipText(final String string) {
        textSearch.setToolTipText(string);
    }

    public void showConfigureButton(final boolean visible) {
        ((GridData) buttonConfigure.getLayoutData()).exclude = (!visible) || (filter == null);
    }

    /**
     * Create the GUI of this composite.
     *
     * @param parent
     *            the parent composite
     */
    private void createUi(final Composite parent) {
        // Initialize the layout
        final GridLayout filterLayout = new GridLayout(3, false);
        filterLayout.verticalSpacing = 0;
        filterLayout.horizontalSpacing = 0;
        filterLayout.marginHeight = 0;
        filterLayout.marginWidth = 0;
        setLayout(filterLayout);

        // Create the filter composite containing the text filter, the clean button and the configure button
        createFilterText(this);
        createClearButton(this);
        createConfigureButton(this);

        setBackground(IntelPalette.WHITE);
    }

    /**
     * Set the filter text
     *
     * @param text
     *            the text to set
     */
    private void setFilterText(final String text) {
        textSearch.setText(text);
    }

    /**
     * Notify all listeners that the filter string has changed.
     */
    private void notifyModifyListeners() {
        final Event event = new Event();
        event.display = this.getDisplay();
        event.widget = this;
        event.type = SWT.Modify;
        notifyListeners(SWT.Modify, event);
    }

    /**
     * Create the filter text.
     *
     * @param textComposite
     */
    private void createFilterText(final Composite textComposite) {
        // Create the control
        textSearch = new Text(textComposite, SWT.NONE);
        textSearch.setText(DEFAULT_FILTER_TEXT);
        textSearch.setForeground(IntelPalette.GREY);
        textSearch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        // Add events
        textSearch.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(final FocusEvent e) {
                updateFilterText(true);
            }

            @Override
            public void focusLost(final FocusEvent e) {
                updateFilterText(false);
            }
        });

        textSearch.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(final MouseEvent e) {
                if (textSearch.getText().equals(DEFAULT_FILTER_TEXT)) {
                    // XXX: We cannot call clearText() due to
                    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=260664
                    setFilterText("");
                }
            }
        });

        textSearch.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent e) {
                updateClearButton();

                final String searchedText = textSearch.getText();

                if (!searchedText.equals(DEFAULT_FILTER_TEXT)) {
                    boolean toBeNotified = false;
                    if (filter != null) {
                        if (!filter.isModified) {
                            filter.setFilterString(textSearch.getText());
                        }
                        toBeNotified = filter.isModified;
                    }

                    if (toBeNotified) {
                        notifyModifyListeners();
                        filter.setModified(false);
                    }
                }
            }
        });
    }

    /**
     * Create the button that clears the text.
     *
     * @param parent
     *            parent <code>Composite</code> of toolbar button
     */
    private void createClearButton(final Composite parent) {
        final Image clearImage = ResourceManager.getPluginImage(BUNDLE_SYMBOLIC_NAME, "images/clear_search.png");
        final Image inactiveImage = new Image(getDisplay(), clearImage, SWT.IMAGE_GRAY);
        buttonClear = new FlatButton(parent, FlatButtonStyle.SMALL_BUTTON);
        buttonClear.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        buttonClear.setImage(inactiveImage);
        buttonClear.setToolTipText("Clear");
        buttonClear.setVisible(false);

        buttonClear.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                textSearch.setText("");
                textSearch.setFocus();
            }
        });
    }

    /**
     * Create the button that opens the configuration dialog
     *
     * @param parent
     *            the parent composite
     */
    private void createConfigureButton(final Composite parent) {
        buttonConfigure = new FlatButton(parent, FlatButtonStyle.SMALL_BUTTON);
        final Image image = ResourceManager.getPluginImage(BUNDLE_SYMBOLIC_NAME, "images/configure_search.png");
        final Image grayedImage = new Image(getDisplay(), image, SWT.IMAGE_GRAY);
        buttonConfigure.setImage(grayedImage);
        buttonConfigure.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        buttonConfigure.setToolTipText("Configure...");
        showConfigureButton(false);

        buttonConfigure.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                if (new FilterDialog(new Shell(), filter).open() == IDialogConstants.OK_ID) {
                    textSearch.setText(filter.getFilterString());
                    textSearch.setSelection(textSearch.getText().length());
                    updateFilterText(true);
                }
            }
        });
    }

    /**
     * Check if the search is valid -- i.e. ready to be launched --
     *
     * @return true if valid
     */
    private boolean isValidSearch() {
        final String searchedText = textSearch.getText();

        return (!searchedText.isEmpty()) && (!searchedText.equals(DEFAULT_FILTER_TEXT));
    }

    /**
     * Update the appearance of the filter text
     *
     * @param hasFocus
     *            the focus flag
     */
    private void updateFilterText(final boolean hasFocus) {
        String filterText = null;
        Color filterColor = IntelPalette.BLACK;

        if (hasFocus) {
            if (textSearch.getText().equals(DEFAULT_FILTER_TEXT)) {
                filterText = "";
            }
        } else {
            if (textSearch.getText().equals(DEFAULT_FILTER_TEXT) || textSearch.getText().isEmpty()) {
                filterText = DEFAULT_FILTER_TEXT;
                filterColor = IntelPalette.GREY;
            }
        }

        // Overwrite the text
        if (filterText != null) {
            textSearch.setText(filterText);
        }
        textSearch.setForeground(filterColor);
    }

    /**
     * Update the appearance of the clear button.
     */
    private void updateClearButton() {
        buttonClear.setVisible(isValidSearch());
    }

}
