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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.InvalidParameterException;

import com.intel.audioviz.AudioFormat;
import com.intel.audioviz.AudioTrack;
import com.intel.audioviz.ISampleProvider;

/**
 * Expose a raw Audio file as AudioTrack.
 */
public class RawFileAudioTrack extends AudioTrack {

    /**
     * Open a raw Audio file.
     * @param audioFormat the audio raw format
     * @param file the file which contains the audio raw data
     * @throws FileNotFoundException
     * @throws IOException
     */
    public RawFileAudioTrack(final AudioFormat audioFormat, final File file)
            throws FileNotFoundException, IOException {
        this(audioFormat, file, 0, file.length());
    }

    /**
     * Open a raw Audio file.
     * @param audioFormat the audio raw format
     * @param file the file which contains the audio raw data
     * @param offset the offset of Audio raw data within the file
     * @throws FileNotFoundException
     * @throws IOException
     */
    public RawFileAudioTrack(final AudioFormat audioFormat, final File file, final long offset)
            throws FileNotFoundException, IOException {
        this(audioFormat, file, offset, file.length() - offset);
    }

    /**
     * Open a raw Audio file specifying the offset and the size of the raw data within the file.
     * @param audioFormat the audio raw format
     * @param file the file which contains the audio raw data
     * @param offset the offset of Audio raw data within the file
     * @param size the size of Audio raw data within the file
     * @throws FileNotFoundException
     * @throws IOException
     */
    public RawFileAudioTrack(
            final AudioFormat audioFormat, final File file, final long offset, final long size)
                    throws FileNotFoundException, IOException {
        super(audioFormat);
        mAudioFormat = audioFormat;
        mFile = file;

        // Check file
        if (!file.exists() || file.isDirectory()) {
            throw new InvalidParameterException("File not found");
        }
        // Check data offset is within file length
        final long fileSize = file.length();
        if (offset >= fileSize || offset < 0) {
            throw new InvalidParameterException("Invalid file offset");
        }
        // Ignore incomplete frame
        final long safeSize = size - size % mAudioFormat.getFrameSize();
        if (safeSize <= 0 || safeSize > fileSize - offset) {
            throw new InvalidParameterException("Invalid file size");
        }

        if (safeSize > Integer.MAX_VALUE) {
            mSampleProvider = new BigFileSampleProvider(file, offset, safeSize, mAudioFormat);
        } else {
            try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
                try (FileChannel fileChannel = raf.getChannel()) {

                    final ByteBuffer buffer = fileChannel.map(
                            FileChannel.MapMode.READ_ONLY,
                            offset,
                            safeSize);
                    buffer.order(audioFormat.getByteOrder());

                    mSampleProvider = new ByteBufferSampleProvider(buffer, mAudioFormat);
                }
            }
        }
    }

    /**
     * @return The file name
     */
    public String getName() {
        return mFile.getName();
    }

    /**
     * @return The file absolute path
     */
    public String getAbsolutePath() {
        return mFile.getAbsolutePath();
    }

    @Override
    public void dispose() {
        super.dispose();
        /**
         * The memory mapping of the file and its associated file system lock remains until the MappedByteBuffer is
         * garbage collected.
         */
        mSampleProvider = null;
        System.gc();
    }

    @Override
    public AudioFormat getAudioFormat() {
        return mAudioFormat;
    }

    @Override
    public ISampleProvider getSampleProvider() {
        return mSampleProvider;
    }

    private final AudioFormat mAudioFormat;
    private ISampleProvider mSampleProvider;
    private final File mFile;
}
