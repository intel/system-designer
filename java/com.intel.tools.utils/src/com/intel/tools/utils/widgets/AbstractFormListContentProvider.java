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

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public abstract class AbstractFormListContentProvider<I, S> implements IFormListContentProvider<I, S> {

    private final List<IFormListContentProviderListener> listeners = new ArrayList<>();

    @Override
    public void addListener(
            final com.intel.tools.utils.widgets.IFormListContentProvider.IFormListContentProviderListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(
            final com.intel.tools.utils.widgets.IFormListContentProvider.IFormListContentProviderListener listener) {
        listeners.remove(listener);
    }

    /**
     * Call this method to fire a reresh to all listeners.
     */
    protected void fireRefresh() {
        for (final IFormListContentProviderListener iFormListContentProviderListener : listeners) {
            iFormListContentProviderListener.contentUpdated();
        }
    }
}
