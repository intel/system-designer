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

/**
 * Represent a graph Link (i.e Edge).</br>
 *
 * A link connects a node to another node (or itself).</br>
 * The link does not carry the information of which inputs/outputs are connected.
 *
 * This interface is not intended to be implemented by clients.
 */
public interface ILink extends IGraphElement {

    /**
     * @return the input node
     */
    IInput getInput();

    /**
     * @return the output node
     */
    IOutput getOutput();

    /**
     * Remove this {@link ILink} from the graph
     *
     * Associated pins are disconnected from the link
     */
    void delete();

}
