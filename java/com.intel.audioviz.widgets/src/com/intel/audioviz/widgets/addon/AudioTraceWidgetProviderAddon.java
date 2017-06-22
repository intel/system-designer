/*
 * Copyright (C) 2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.audioviz.widgets.addon;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.swt.widgets.Composite;
import org.osgi.framework.Bundle;

import com.intel.audioviz.widgets.AudioTraceWidget;
import com.intel.audioviz.widgets.IAudioTraceWidgetProvider;

/**
 * The AudioTraceWidgetProviderAddon is the default IAudioTraceWidgetProvider which register itself to the IEclipse
 * context. It provides all AudioTraceWidget registered to the com.intel.audioviz.widgets.audiotracewidget extension
 * point.
 */
public class AudioTraceWidgetProviderAddon implements IAudioTraceWidgetProvider {

    /**
     * @param context
     *            The Eclipse context
     * @param reg
     *            The Eclipse IExtensionRegistry
     */
    @Inject
    public AudioTraceWidgetProviderAddon(final IEclipseContext context, final IExtensionRegistry reg) {
        mExtensionRegistry = reg;
        context.set(IAudioTraceWidgetProvider.class, this);
    }

    @Override
    public Set<AudioTraceWidget> getAudioTraceWidget(final Composite parent, final int style) {

        final HashSet<AudioTraceWidget> audioTraceWidgets = new HashSet<AudioTraceWidget>();

        // Loads each factory and register them to the provider
        for (final IConfigurationElement elt : mExtensionRegistry
                .getConfigurationElementsFor("com.intel.audioviz.widgets.audiotracewidget")) {

            final Bundle b = Platform.getBundle(elt.getNamespaceIdentifier());
            Class<?> cz;
            try {
                cz = b.loadClass(elt.getAttribute("audioTraceWidgetClass"));
                final Constructor<?> audioTraceWidgetConstructor = cz.getConstructor(Composite.class, int.class);
                if (audioTraceWidgetConstructor != null) {
                    final AudioTraceWidget audioTraceWidget = (AudioTraceWidget) audioTraceWidgetConstructor
                            .newInstance(parent, style);
                    audioTraceWidgets.add(audioTraceWidget);
                }

            } catch (ClassNotFoundException | InvalidRegistryObjectException | ClassCastException
                    | NoSuchMethodException | InvocationTargetException | IllegalAccessException
                    | InstantiationException | NullPointerException e) {
                LOGGER.error("Fail to instantiate AudioTraceWidget", e);
            }
        }
        return Collections.unmodifiableSet(audioTraceWidgets);
    }

    private static final Logger LOGGER = Logger.getLogger(AudioTraceWidgetProviderAddon.class);
    private final IExtensionRegistry mExtensionRegistry;
}
