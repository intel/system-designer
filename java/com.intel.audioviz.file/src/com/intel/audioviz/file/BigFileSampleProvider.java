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
import com.intel.audioviz.ISampleProvider;

/**
 * BigFileSampleProvider implements the SampleProvider Interface over
 * a collection of memory mapped part of a file.
 * This class allows memory mapping of a file with a size >2GB using a collection
 * of MappedByteBuffer which can individually memory map only 2GB.
 */
public class BigFileSampleProvider implements ISampleProvider {

    public BigFileSampleProvider(
            final File file, final long offset, final long size, final AudioFormat audioFormat)
                    throws FileNotFoundException, IOException {
        mSize = size;
        mAudioFormat = audioFormat;
        /* Align chunk size to a multiple of frame size */
        mChunkSizeInBytes = Integer.MAX_VALUE - Integer.MAX_VALUE % audioFormat.getFrameSize();
        mChunkSizeInFrames = mChunkSizeInBytes / audioFormat.getFrameSize();

        long bufferCount = mSize / mChunkSizeInBytes;
        if (mSize % mChunkSizeInBytes != 0) {
            bufferCount++;
        }
        if (bufferCount > Integer.MAX_VALUE) {
            throw new InvalidParameterException("File too large");
        }
        if (bufferCount == 0) {
            throw new InvalidParameterException("Empty file");
        }

        mByteBufferSampleProviders = new ByteBufferSampleProvider[(int) bufferCount];

        /* Get file channel in read only mode */
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            try (FileChannel fileChannel = randomAccessFile.getChannel()) {
                long byteBufferOffset = offset;

                for (int index = 0; index < bufferCount; index++, byteBufferOffset += mChunkSizeInBytes) {
                    final ByteBuffer buffer = fileChannel.map(
                            FileChannel.MapMode.READ_ONLY,
                            byteBufferOffset,
                            Math.min(mChunkSizeInBytes, mSize - mChunkSizeInBytes * index));
                    buffer.order(audioFormat.getByteOrder());

                    mByteBufferSampleProviders[index] = new ByteBufferSampleProvider(
                            buffer,
                            mAudioFormat);
                }
            }
        }
    }

    @Override
    public long getSamplesPerChannel() {
        return mSize / mAudioFormat.getFrameSize();
    }

    @Override
    public double getSampleAsDouble(final int channel, final long index) {
        final int sampleProviderindex = getByteBufferSampleProviderIndex(index);

        return mByteBufferSampleProviders[sampleProviderindex].getSampleAsDouble(
                channel,
                index - sampleProviderindex * mChunkSizeInFrames);
    }

    @Override
    public float getSampleAsFloat(final int channel, final long index) {
        final int sampleProviderindex = getByteBufferSampleProviderIndex(index);

        return mByteBufferSampleProviders[sampleProviderindex].getSampleAsFloat(
                channel,
                index - sampleProviderindex * mChunkSizeInFrames);
    }

    private int getByteBufferSampleProviderIndex(final long sampleIndex) {
        if (sampleIndex < 0 || sampleIndex >= getSamplesPerChannel()) {
            throw new InvalidParameterException("Invalid sample index");
        }

        return (int) (sampleIndex / mChunkSizeInFrames);
    }

    private final int mChunkSizeInBytes;
    private final int mChunkSizeInFrames;
    private final ByteBufferSampleProvider[] mByteBufferSampleProviders;
    private final AudioFormat mAudioFormat;
    private final long mSize;
}
