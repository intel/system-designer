/*
 * Copyright (C) 2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.audioviz.file;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import com.intel.audioviz.file.addon.FileAudioTrackFactoryManager;

/**
 * RawFileAudioTrackProvider provides ability to instantiate RawFileAudioTrack from file using
 * a register of available IFileAudioTrackFactory in a FileAudioTrackFactoryManager.
 */
public class RawFileAudioTrackProvider {

    /**
     * @param fileAudioTrackFactoryManager The manager of registered factories
     */
    public RawFileAudioTrackProvider(final FileAudioTrackFactoryManager fileAudioTrackFactoryManager) {
        mFileAudioTrackFactoryManager = fileAudioTrackFactoryManager;
    }

    /**
     * Get the RawFileAudioTrack corresponding to the File provided as argument.
     * The registered AudioTrackFactory for the file format is used to instantiate
     * the AudioTrack.
     * @param file The file to be opened as RawFileAudioTrack instance.
     * @return The RawFileAudioTrack for the file format
     * @throws FileAudioTrackFactoryException
     */
    public RawFileAudioTrack getAudioTrack(final File file) throws FileAudioTrackFactoryException {
        if (file == null) {
            throw new IllegalArgumentException("Invalid file");
        }

        final IFileAudioTrackFactory factory =
                mFileAudioTrackFactoryManager.getAudioTrackFactoryForFile(file);

        return factory.getAudioTrack(file);
    }

    /**
     * Get the RawFileAudioTrack corresponding to the File which name is provided as argument.
     * The registered AudioTrackFactory for the file format is used to instantiate
     * the AudioTrack.
     * @param fileName The name of the file to be opened as AudioTrack instance.
     * @return The RawFileAudioTrack for the file format
     * @throws FileAudioTrackFactoryException
     */
    public RawFileAudioTrack getAudioTrack(final String fileName) throws FileAudioTrackFactoryException {
        if (fileName == null) {
            throw new IllegalArgumentException("Invalid file name");
        }

        return getAudioTrack(new File(fileName));
    }

    /**
     * Get the RawFileAudioTrack corresponding to the File which is chosen by user in a
     * standard open file dialog.
     * The dialog is populated with filters based on supported file format of each registered
     * IFileAudioTrackFactory in the FileAudioTrackFactoryManager.
     * @param file The file to be opened as RawFileAudioTrack instance.
     * @return The RawFileAudioTrack or null if operation cancelled by user
     * @throws FileAudioTrackFactoryException
     */
    public RawFileAudioTrack getAudioTrack() throws FileAudioTrackFactoryException {
        final Set<IFileAudioTrackFactory> audioTrackFactoryRegister =
                mFileAudioTrackFactoryManager.getFactoryRegister();

        final String[] formatExtensions =
                new String[audioTrackFactoryRegister.size() + 2];
        final String[] formatFriendlyNames =
                new String[audioTrackFactoryRegister.size() + 2];


        /* Setup file filters from supported format by registered factories */
        int formatIndex = 1;
        for (final IFileAudioTrackFactory factory : audioTrackFactoryRegister) {

            final List<String> fileExtensionsFilter = factory.getSupportedFileExtensions().stream().map(s -> "*." + s)
                    .collect(Collectors.toList());
            final String factoryFormat = String.join(";", fileExtensionsFilter);

            formatExtensions[formatIndex] = factoryFormat;
            formatFriendlyNames[formatIndex] =
                    factory.getAudioFormatFriendlyName()
                    + " ("
                    + factoryFormat
                    + ")";
            formatIndex++;
        }
        final String allSupportedFormat = String.join(";", formatExtensions);

        /* Insert a entry to filter all supported file format */
        formatExtensions[0] = allSupportedFormat;
        formatFriendlyNames[0] = ALL_SUPPORTED_FILES_FRIENDLY_NAME;

        /* Add all file filter at the end of list for any file (*.*) */
        formatExtensions[formatExtensions.length - 1] = ALL_FILES_FILTER;
        formatFriendlyNames[formatFriendlyNames.length - 1] = ALL_FILES_FILTER_FRIENDLY_NAME;

        /* Setup the file open dialog */
        final Shell shell = Display.getCurrent().getActiveShell();
        final FileDialog fileOpenDialog = new FileDialog(shell);
        fileOpenDialog.setFilterExtensions(formatExtensions);
        fileOpenDialog.setFilterNames(formatFriendlyNames);

        final String fileName = fileOpenDialog.open();
        if (fileName != null) {
            return getAudioTrack(fileName);
        }

        return null;
    }

    private final FileAudioTrackFactoryManager mFileAudioTrackFactoryManager;

    private static final String ALL_FILES_FILTER = "*.*";
    private static final String ALL_FILES_FILTER_FRIENDLY_NAME = "All files (" + ALL_FILES_FILTER + ")";
    private static final String ALL_SUPPORTED_FILES_FRIENDLY_NAME = "All supported files";
}
