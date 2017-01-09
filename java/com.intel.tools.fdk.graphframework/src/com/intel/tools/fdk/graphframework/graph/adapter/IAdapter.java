/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.tools.fdk.graphframework.graph.adapter;

import com.intel.tools.fdk.graphframework.graph.IGraph;

/**
 * Interface providing a way to generate a {@link IGraph} from a custom model.
 */
public interface IAdapter {

    public interface IGraphListener {
        /**
         * Method called after a graph modification
         *
         * @param graph
         *            the new graph
         */
        void graphUpdated(final IGraph graph);
    }

    /**
     * Retrieve the adapted graph.
     *
     * @return the adapted graph
     */
    IGraph getGraph();

    /**
     * Register a new graph listener
     *
     * @param listener
     *            the new listener
     */
    void addGraphListener(final IGraphListener listener);

    /**
     * Unregister a new graph listener
     *
     * @param listener
     *            the listener to remove
     */
    void removeGraphListener(final IGraphListener listener);

}
