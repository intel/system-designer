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

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.intel.audioviz.file.IFileAudioTrackFactory;
import com.intel.audioviz.file.raw.RawFileAudioTrackFactory;

/**
 * FileAudioTrackFactoryManager maintains a register of available FileAudioTrackFactory and allows
 * to find the appropriate factory for a given file based on its extension.
 * By default, a RawAudioFileTrackFactory is systematically registered.
 */
public class FileAudioTrackFactoryManager {

    /**
     * Instantiate a FileAudioTrackFactoryManager
     * @note By default, a default RawFileAudioTrackFactory is added to the register.
     */
    public FileAudioTrackFactoryManager() {
        mAudioTrackFactoryRegister = new HashSet<IFileAudioTrackFactory>();
        mDefaultAudioTrackFactory = new RawFileAudioTrackFactory();
        addFileAudioTrackFactory(mDefaultAudioTrackFactory);
    }

    /**
     * Add an AudioTrackFactory to the registry. Hence, this factory will be
     * called to instantiate AudioTrack for the file format its supports.
     * @param audioTrackFactory to be added
     */
    public boolean addFileAudioTrackFactory(final IFileAudioTrackFactory audioTrackFactory) {
        if (audioTrackFactory.getAudioFormatFriendlyName() == null) {
            throw new IllegalArgumentException(
                    "Factory does not provide friendly name");
        }
        if (audioTrackFactory.getSupportedFileExtensions().size() == 0) {
            throw new IllegalArgumentException(
                    "Factory does not support any file extension ("
                            + audioTrackFactory.getAudioFormatFriendlyName()
                            + ')');
        }

        return mAudioTrackFactoryRegister.add(audioTrackFactory);
    }

    /**
     * Remove an AudioTrackFactory from the registry.
     * @param audioTrackFactory to be removed
     */
    public boolean removeAudioTrackFactory(final IFileAudioTrackFactory audioTrackFactory) {
        return mAudioTrackFactoryRegister.remove(audioTrackFactory);
    }

    /**
     * @return The set of registered IFileAudioTrackFactory
     */
    public Set<IFileAudioTrackFactory> getFactoryRegister() {
        return mAudioTrackFactoryRegister;
    }

    /**
     * Returns the appropriate factory based on file's extension. If no explicit support
     * is provided for the file's extension, then the default RAW factory is returned.
     * @param file The file for which a factory is looked up
     * @return the factory which has been registered for this file based on its extension
     */
    public IFileAudioTrackFactory getAudioTrackFactoryForFile(final File file) {
        final String fileExtension = getFileExtension(file).toLowerCase();
        /* look for the file extension in registered factories */
        for (final IFileAudioTrackFactory factory : mAudioTrackFactoryRegister) {
            for (final String extension : factory.getSupportedFileExtensions()) {
                if (fileExtension.equals(extension.toLowerCase())) {
                    return factory;
                }
            }
        }

        return mDefaultAudioTrackFactory;
    }

    private String getFileExtension(final File file) {
        final int dotIndex = file.getName().lastIndexOf('.');

        if (dotIndex >= 0 && dotIndex + 1 < file.getName().length()) {
            return file.getName().substring(dotIndex + 1);
        } else {
            return "";
        }
    }

    private final RawFileAudioTrackFactory mDefaultAudioTrackFactory;
    private final HashSet<IFileAudioTrackFactory> mAudioTrackFactoryRegister;
}
