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

import com.intel.tools.fdk.graphframework.graph.impl.Graph;

/**
 * These interface describe the standard API of a node of a {@link Graph}
 *
 * This interface is not intended to be implemented by clients.
 */
public interface INode extends IGraphElement {

    /** @return the parent container of this Node */
    INodeContainer getParent();

    /**
     * Remove this {@link INode} from the graph
     *
     * Associated nodes are updated. Associated links are removed.
     */
    void delete();

}