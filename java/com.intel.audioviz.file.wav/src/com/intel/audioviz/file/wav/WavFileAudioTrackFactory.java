/*
 * Copyright (C) 2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.audioviz.file.wav;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.intel.audioviz.file.FileAudioTrackFactoryException;
import com.intel.audioviz.file.IFileAudioTrackFactory;
import com.intel.audioviz.file.RawFileAudioTrack;

public class WavFileAudioTrackFactory implements IFileAudioTrackFactory {

    @Override
    public List<String> getSupportedFileExtensions() {
        return Collections.unmodifiableList(new ArrayList<String>(Arrays.asList(WAV_FILES_EXTENSIONS)));
    }

    @Override
    public String getAudioFormatFriendlyName() {
        return WAV_FRIENDLY_NAME;
    }

    @Override
    public RawFileAudioTrack getAudioTrack(final File audioFile) throws FileAudioTrackFactoryException {
        try {
            final WavHeaderParser wav = new WavHeaderParser(audioFile);

            return new RawFileAudioTrack(
                    wav.getAudioDataFormat(),
                    audioFile,
                    wav.getAudioDataOffset(),
                    wav.getAudioDataSize());
        } catch (final Exception e) {
            throw new FileAudioTrackFactoryException("Cannot instantiate RawFileAudioTrack", e);
        }
    }

    private static final String[] WAV_FILES_EXTENSIONS = { "wav" };
    private static final String WAV_FRIENDLY_NAME = "Microsoft WAV";
}
