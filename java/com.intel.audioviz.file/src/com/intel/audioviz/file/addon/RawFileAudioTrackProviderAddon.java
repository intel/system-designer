/*
 * Copyright (C) 2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.audioviz.file.addon;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.osgi.framework.Bundle;

import com.intel.audioviz.file.IFileAudioTrackFactory;
import com.intel.audioviz.file.RawFileAudioTrackProvider;

public class RawFileAudioTrackProviderAddon {
    /**
     * The RawFileAudioTrackProviderAddon instantiate a RawFileAudioTrackProvider singleton and add it to the Eclipse
     * context. Each IFileAudioTrackFactory detected in installed format extensions are instantiated and added to the
     * RawFileAudioTrackProvider.
     *
     * @param context
     *            The Eclipse context
     * @param reg
     *            the IExtensionRegistry
     */
    @Inject
    public RawFileAudioTrackProviderAddon(final IEclipseContext context, final IExtensionRegistry reg) {
        // Instantiate the RawFileAudioTrackProvider
        final FileAudioTrackFactoryManager fileAudioTrackFactoryManager = new FileAudioTrackFactoryManager();

        // Loads each factory and register them to the provider
        for (final IConfigurationElement elt : reg.getConfigurationElementsFor("com.intel.audioviz.file.format")) {

            final Bundle b = Platform.getBundle(elt.getNamespaceIdentifier());
            Class<?> cz;
            try {
                cz = b.loadClass(elt.getAttribute("factoryClass"));
                final IFileAudioTrackFactory factory = (IFileAudioTrackFactory) ContextInjectionFactory.make(cz,
                        context);
                fileAudioTrackFactoryManager.addFileAudioTrackFactory(factory);
            } catch (ClassNotFoundException | InvalidRegistryObjectException | ClassCastException
                    | NullPointerException e) {
                LOGGER.error("Cannot instantiate IFileAudioTrackFactory", e);
            }
        }

        // Instantiate the RawFileAudioTrackProvider
        final RawFileAudioTrackProvider provider = new RawFileAudioTrackProvider(fileAudioTrackFactoryManager);
        // Publish provider in Eclipse Context
        context.set(RawFileAudioTrackProvider.class, provider);
    }

    private static final Logger LOGGER = Logger.getLogger(RawFileAudioTrackProviderAddon.class);
}
