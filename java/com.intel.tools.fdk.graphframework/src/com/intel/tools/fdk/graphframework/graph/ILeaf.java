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

import java.util.List;
import java.util.Optional;

/**
 * Represent the graph base element. </br>
 *
 * A leaf node can be connected to many other leaves of the same graph.</br>
 * A leaf is defined with a defined input/output numbers.</br>
 *
 * This interface is not intended to be implemented by clients.
 */
public interface ILeaf extends INode {

    /**
     * Retrieves potentially empty {@link Optional} of input {@link ILink}
     *
     * @return an unmodifiable list of potentially empty {@link ILink} place connected on inputs.
     */
    List<? extends IInput> getInputs();

    /**
     * Retrieves potentially empty {@link Optional} of output {@link ILink}
     *
     * @return an unmodifiable list of potentially empty {@link ILink} place connected on outputs.
     */
    List<? extends IOutput> getOutputs();

}
