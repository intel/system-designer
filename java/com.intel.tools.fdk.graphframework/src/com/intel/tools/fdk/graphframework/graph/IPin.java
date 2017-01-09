/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.tools.fdk.graphframework.graph;

import java.util.Optional;

/**
 * Represent a connection point of a {@link ILeaf} of an {@link IGraph}
 *
 * This connection point can be empty or filled with a {@link ILink} which leads to another {@link IPin}
 */
public interface IPin extends IGraphElement {

    /**
     * @return the id of the pin
     */
    int getId();

    /**
     * @return the leaf which owns this pin
     */
    ILeaf getLeaf();

    /**
     * If the value of the {@link ILink} is {@link Optional#empty()} the pin is not linked. The wrapped {@link Link} is
     * connected to the pin otherwise.
     *
     * @return an optional {@link ILink} object which leads to another {@link IPin}
     */
    Optional<? extends ILink> getLink();

}
