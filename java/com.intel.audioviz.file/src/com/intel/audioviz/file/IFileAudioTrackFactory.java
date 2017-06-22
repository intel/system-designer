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

import com.intel.audioviz.file.raw.RawFileAudioTrackFactory;

/**
 * An IFileAudioTrackFactory provides the ability to instantiate an RawFileAudioTrack from
 * a specific file format.
 *
 * An IFileAudioTrackFactory is intended to be registered to a FileAudioTrackFactoryManager.
 *
 * The default factory is the RawFileAudioTrackFactory which considers any file as raw
 * audio data, asking format details to the user.
 *
 * A basic factory example is the WavFileAudioTrackFactory which does the exact same thing
 * than the RawFileAudioTrackFactory expect that audio format details are retrieved from the
 * WAV file header. An AIFF factory would be as much simple as it for instance.
 *
 * A "complex" factory would be required for any coded audio file such as MP3 of FLAC.
 * Such a factory would have to first decode the audio file to a temporary raw audio file
 * and then instantiate a RawFileAudioTrack on this temporary file, or, instantiate a specific
 * ISampleProvider and AudioTrack subclass.
 *
 * @see RawFileAudioTrackFactory
 * @see WavFileAudioTrackFactory
 */
public interface IFileAudioTrackFactory {

    /**
     * List common extensions of the file format supported by this factory.
     * For instance, an AIFF format factory would return {"aif", "aiff", "aifc"} which
     * are the common file extension for this file format.
     * @return array of supported file extensions.
     */
    List<String> getSupportedFileExtensions();

    /**
     * @return the friendly name of the file format supported by this factory.
     * For instance, an AIFF format factory would return "Audio Interchange File".
     */
    String getAudioFormatFriendlyName();

    /**
     * Instantiate a RawFileAudioTrack from the file given as argument.
     * @param audioFile the file to open as RawFileAudioTrack
     * @return The RawFileAudioTrack
     * @throws FileAudioTrackFactoryException
     */
    RawFileAudioTrack getAudioTrack(File audioFile) throws FileAudioTrackFactoryException;
}
