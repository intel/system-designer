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

import java.util.List;

public interface IFormListContentProvider<I, S> {
    /**
     * Listener interface
     */
    public static interface IFormListContentProviderListener {
        /**
         * Method fired when the content of the content provider has been updated.
         */
        public void contentUpdated();
    }

    /**
     * Add a listener to be notified when the content of the content provider is updated.
     *
     * @param listener
     *            The listener to notify
     */
    public void addListener(IFormListContentProviderListener listener);

    /**
     * Remove a listener from the list of notified listeners
     *
     * @param listener
     *            The listener to remove
     */
    public void removeListener(IFormListContentProviderListener listener);

    /**
     * get the list of main items to display
     *
     * @return
     */
    public List<I> getItemList();

    /**
     * Get the list of subItems to display
     *
     * @param item
     *            The parent item to get sub items for.
     * @return
     */
    public List<S> getSubItemList(I item);

    /**
     * Set the input object used as a source of data.
     * 
     * @param input
     */
    public void setInput(Object input);
}
