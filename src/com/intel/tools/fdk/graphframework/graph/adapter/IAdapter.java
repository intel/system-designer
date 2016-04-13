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
package com.intel.tools.fdk.graphframework.graph.adapter;

import com.intel.tools.fdk.graphframework.figure.presenter.GroupPresenter;
import com.intel.tools.fdk.graphframework.figure.presenter.LeafPresenter;
import com.intel.tools.fdk.graphframework.graph.Graph;
import com.intel.tools.fdk.graphframework.graph.Group;
import com.intel.tools.fdk.graphframework.graph.Leaf;

/**
 * Interface providing a way to generate a {@link Graph} from a custom model.
 *
 * TODO: Adds graph action API
 */
public interface IAdapter {

    public interface IGraphListener {
        /**
         * Method called after a graph modification
         *
         * @param graph
         *            the new graph
         */
        void graphUpdated(final Graph graph);
    }

    /**
     * Retrieve the adapted graph.
     *
     * @return the adapted graph
     */
    Graph getGraph();

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

    /**
     * This method allows to notify implementors that a graph has been updated.</br>
     *
     * Basically it should consist to call {@link IGraphListener#graphUpdated(Graph)} method of each listeners.
     */
    void fireGraphUpdate();

    /**
     * Create a new leaf presenter.
     *
     * This method provide a way to let the user introduce custom node presenters when using framework's layout to
     * display their graph.</br>
     *
     * @param leaf
     *            the node to represent
     * @return a presenter representing the node
     */
    LeafPresenter createPresenter(final Leaf leaf);

    /**
     * Create a new group presenter.
     *
     * This method provide a way to let the user introduce custom node presenters when using framework's layout to
     * display their graph.</br>
     *
     * @param group
     *            the node to represent
     * @return a presenter representing the node
     */
    GroupPresenter createPresenter(final Group group);

}
