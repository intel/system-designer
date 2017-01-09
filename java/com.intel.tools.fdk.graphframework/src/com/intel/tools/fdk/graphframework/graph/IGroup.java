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
 * Represents a group of {@link INode} which are part of a {@link IGraph}.</br>
 *
 * This is only a marker interface extending {@link INode} and {@link INodeContainer}
 *
 * This interface is not intended to be implemented by clients.
 */
public interface IGroup extends INodeContainer, INode {

}
