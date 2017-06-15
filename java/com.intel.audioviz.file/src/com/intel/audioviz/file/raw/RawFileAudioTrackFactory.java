/*
 * Copyright (C) 2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.audioviz.file.raw;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import com.intel.audioviz.AudioFormat;
import com.intel.audioviz.file.FileAudioTrackFactoryException;
import com.intel.audioviz.file.IFileAudioTrackFactory;
import com.intel.audioviz.file.RawFileAudioTrack;
import com.intel.audioviz.file.ui.dialogs.AudioFormatDialog;

/**
 * Instantiate a RawFileAudioTrack for a given file using user provided AudioFormat through an
 * audio raw format selection dialog.
 */
public class RawFileAudioTrackFactory implements IFileAudioTrackFactory {

    @Override
    public List<String> getSupportedFileExtensions() {

        return Collections.unmodifiableList(new ArrayList<String>(Arrays.asList(RAW_AUDIO_FILES_EXTENSIONS)));
    }

    @Override
    public String getAudioFormatFriendlyName() {
        return RAW_FRIENDLY_NAME;
    }

    @Override
    public RawFileAudioTrack getAudioTrack(final File audioFile) throws FileAudioTrackFactoryException {
        /* Ask format details to the user */
        final AudioFormatDialog audioFormatDialog =
                new AudioFormatDialog(
                        Display.getCurrent().getActiveShell(),
                        AUDIO_FORMAT_DIALOG_TITLE,
                        true);

        if (audioFormatDialog.open() == Window.OK) {
            final AudioFormat audioFormat = audioFormatDialog.getAudioFormat();
            final long offset = audioFormatDialog.getOffset();

            try {
                return new RawFileAudioTrack(audioFormat, audioFile, offset);
            } catch (final Exception e) {
                throw new FileAudioTrackFactoryException("Cannot instantiate RawFileAudioTrack", e);
            }
        }

        return null;
    }

    /* Standards/common extension for raw audio files */
    private static final String[] RAW_AUDIO_FILES_EXTENSIONS = { "pcm", "raw" };
    private static final String RAW_FRIENDLY_NAME = "Raw Audio File";
    private static final String AUDIO_FORMAT_DIALOG_TITLE = "Open RAW Audio File";
}
