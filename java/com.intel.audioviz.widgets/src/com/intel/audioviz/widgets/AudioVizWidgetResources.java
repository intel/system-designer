/*
 * Copyright (C) 2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */

package com.intel.audioviz.widgets;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * AudioVizWidgetResources provides shared resources like icons.
 */
public final class AudioVizWidgetResources {

    private AudioVizWidgetResources() {
    }

    public static ImageRegistry getImageRegistry() {
        return IMAGE_REGISTRY;
    }

    private static ImageRegistry getNewLocalImageRegistry() {
        final Bundle b = FrameworkUtil.getBundle(AudioVizWidgetResources.class);
        final ImageRegistry reg = new ImageRegistry();

        reg.put(
                IMG_LOCKED,
                ImageDescriptor.createFromURL(b.getEntry(IMG_LOCKED)));
        reg.put(
                IMG_UNLOCKED,
                ImageDescriptor.createFromURL(b.getEntry(IMG_UNLOCKED)));
        reg.put(
                IMG_DISABLED_LOCK,
                ImageDescriptor.createFromURL(b.getEntry(IMG_DISABLED_LOCK)));
        reg.put(
                IMG_XFULLRANGE,
                ImageDescriptor.createFromURL(b.getEntry(IMG_XFULLRANGE)));
        reg.put(
                IMG_XZOOMIN,
                ImageDescriptor.createFromURL(b.getEntry(IMG_XZOOMIN)));
        reg.put(
                IMG_XZOOMOUT,
                ImageDescriptor.createFromURL(b.getEntry(IMG_XZOOMOUT)));

        return reg;
    }

    private static final ImageRegistry IMAGE_REGISTRY = getNewLocalImageRegistry();

    public static final String IMG_LOCKED = "icons/locked.png";
    public static final String IMG_UNLOCKED = "icons/unlocked.png";
    public static final String IMG_DISABLED_LOCK = "icons/unlockeddisabled.png";
    public static final String IMG_XFULLRANGE = "icons/xfullrange.png";
    public static final String IMG_XZOOMIN = "icons/xzoomin.png";
    public static final String IMG_XZOOMOUT = "icons/xzoomout.png";
}
