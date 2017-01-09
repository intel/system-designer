/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.tools.fdk.graphframework.figure.presenter;

import com.intel.tools.fdk.graphframework.graph.IGroup;
import com.intel.tools.fdk.graphframework.graph.ILeaf;

/**
 * A presenter manager is responsible for creating presenters based on node form the Graph Framework.
 */
public interface IPresenterManager {

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
    LeafPresenter getPresenter(final ILeaf leaf);

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
    GroupPresenter getPresenter(final IGroup group);

}
