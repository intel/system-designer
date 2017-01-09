/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.tools.fdk.graphframework.graph.impl;

import java.util.Set;

import com.intel.tools.fdk.graphframework.graph.IGraph;
import com.intel.tools.fdk.graphframework.graph.INode;

/**
 * Represent a basic graph.</br>
 *
 * A graph is composed of {@link INode}.</br>
 * Those nodes can be {@link Leaf} interconnected by {@link Link} or {@link Group} which can contains other
 * {@link INode}
 */
public final class Graph extends NodeContainer implements IGraph {

    public Graph(final Set<Leaf> leaves, final Set<Group> groups) {
        super(leaves, groups);
    }

}
