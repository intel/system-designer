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
 * Represent a basic graph.</br>
 *
 * A graph is composed of {@link INode}.</br>
 * Those nodes can be {@link ILeaf} interconnected by {@link ILink} or {@link IGroup} which can contains other
 * {@link INode}
 *
 * This is only a marker interface extending {@link INodeContainer}
 *
 * This interface is not intended to be implemented by clients.
 */
public interface IGraph extends INodeContainer {

}
