/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.tools.utils.widgets;

import org.eclipse.swt.widgets.Composite;

/**
 * The composite provider implementations should provide a composite representing items or sub items of a FormListViewer
 */
public interface IFormListCompositeProvider<I, S> {
    public void createUI(Composite parent, I item);

    public void createSubUI(Composite parent, S subItem);
}
