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
 * Represent an entry connection point of a {@link ILeaf} of an {@link IGraph}
 *
 * This connection point can be empty or filled with a {@link ILink} which leads to an {@link IOutput}
 */
public interface IInput extends IPin {

}
