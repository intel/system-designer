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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidParameterException;

import com.intel.audioviz.AudioFormat;
import com.intel.audioviz.AudioFormat.Coding;
import com.intel.audioviz.ISampleProvider;

/**
 * The ByteBufferSampleProvider implements the SampleProvider interface on ByteBuffer.
 */
public class ByteBufferSampleProvider implements ISampleProvider {

    /**
     * @param byteBuffer The ByteBuffer which holds raw Audio data
     * @param audioFormat The AudioFormat of the raw Audio data
     */
    public ByteBufferSampleProvider(final ByteBuffer byteBuffer, final AudioFormat audioFormat) {
        mByteBuffer = byteBuffer;
        mAudioFormat = audioFormat;

        /* PCM support up to 32bits */
        if (mAudioFormat.getCodingFormat() == Coding.FORMAT_PCM
                && mAudioFormat.getBytesPerSample() > Integer.BYTES) {
            throw new InvalidParameterException("Unsupported PCM format");
        } else if (mAudioFormat.getCodingFormat() == Coding.FORMAT_IEEE_FLOAT
                && !(mAudioFormat.getBytesPerSample() == Float.BYTES)
                && !(mAudioFormat.getBytesPerSample() == Double.BYTES)) {
            throw new InvalidParameterException("Unsupported IEEE FLOAT format");
        }

        /* Compute constants which will be used for PCM normalization.
         * For instance for 16 bits per samples:
         * PCM_UNSIGNED_HALF_RANGE = 32768
         * PCM_SIGNED_MAX = 32767
         */
        mPcmUnsignedHalfRange = 1 << (mAudioFormat.getBitsPerSample() - 1);
        mPcmSignedMax = mPcmUnsignedHalfRange - 1;
    }

    public ByteBuffer getByteBuffer() {
        return mByteBuffer;
    }

    @Override
    public long getSamplesPerChannel() {
        final long samplePerChannel =
                mByteBuffer.capacity() / mAudioFormat.getFrameSize();

        return samplePerChannel;
    }

    @Override
    public double getSampleAsDouble(final int channel, final long index) {
        final int offset = getSampleOffset(channel, index);

        if (mAudioFormat.getCodingFormat() == Coding.FORMAT_PCM) {
            final long sampleValue = getPcmSample(offset);

            return mAudioFormat.isSigned() ? normalizeSignedPcmToDouble(sampleValue)
                    : normalizeUnsignedPcmToDouble(sampleValue);
        } else {
            return getIeeeDoubleSample(offset);
        }
    }

    @Override
    public float getSampleAsFloat(final int channel, final long index) {
        final int offset = getSampleOffset(channel, index);

        if (mAudioFormat.getCodingFormat() == Coding.FORMAT_PCM) {
            final long sampleValue = getPcmSample(offset);

            return mAudioFormat.isSigned() ? normalizeSignedPcmToFloat(sampleValue)
                    : normalizeUnsignedPcmToFloat(sampleValue);
        } else {
            return getIeeeFloatSample(offset);
        }
    }

    /**
     * Calculate the offset of a Sample within the ByteBuffer
     * @param channel Channel number of the Sample
     * @param index Index of the Sample within the Channel
     * @return the offset of the Sample value within the raw Audio data
     */
    private int getSampleOffset(final int channel, final long index) {
        if (channel < 0 || channel >= mAudioFormat.getChannelCount()) {
            throw new InvalidParameterException("Invalid channel");
        }
        if (index < 0 || index > getSamplesPerChannel()) {
            throw new InvalidParameterException("Invalid sample index");
        }

        final long offset = mAudioFormat.getFrameSize() * index
                + channel * mAudioFormat.getBytesPerSample();

        if (offset >= mByteBuffer.capacity()) {
            throw new IndexOutOfBoundsException("Invalid offset: " + offset);
        }

        return (int) offset;
    }

    /**
     * Read a PCM Sample in the raw Audio data
     * @param offset the offset of the Sample within the raw Audio data
     * @return the PCM Sample long value
     */
    private long getPcmSample(final int offset) {
        long sampleValue;

        if (mAudioFormat.getBytesPerSample() == Short.BYTES) {
            sampleValue = mAudioFormat.isSigned() ? mByteBuffer.getShort(offset)
                    : Short.toUnsignedLong(mByteBuffer.getShort(offset));
        } else if (mAudioFormat.getBytesPerSample() == Integer.BYTES) {
            sampleValue = mAudioFormat.isSigned() ? mByteBuffer.getInt(offset)
                    : Integer.toUnsignedLong(mByteBuffer.getInt(offset));
        } else if (mAudioFormat.getBytesPerSample() == Byte.BYTES) {
            sampleValue = mAudioFormat.isSigned() ? mByteBuffer.get(offset)
                    : Byte.toUnsignedLong(mByteBuffer.get(offset));
        } else {
            /* For 24bits samples, byte per byte access is required */
            if (mByteBuffer.order() == ByteOrder.BIG_ENDIAN) {
                sampleValue = 0
                        | mByteBuffer.get(offset + 2)
                        | (mByteBuffer.get(offset + 1) << 8)
                        | (mByteBuffer.get(offset) << 16);
            } else {
                sampleValue = 0
                        | mByteBuffer.get(offset)
                        | (mByteBuffer.get(offset + 1) << 8)
                        | (mByteBuffer.get(offset + 2) << 16);
            }
        }
        return sampleValue;
    }

    /**
     * Read a IEEE 64 bits float Sample in the raw Audio data
     * @param offset the offset of the Sample within the raw Audio data
     * @return the Sample value as double
     */
    private double getIeeeDoubleSample(final int offset) {
        if (mAudioFormat.getBytesPerSample() == Float.BYTES) {
            return mByteBuffer.getFloat(offset);
        } else {
            return mByteBuffer.getDouble(offset);
        }
    }

    /**
     * Read a IEEE 32 bits float Sample in the raw Audio data
     * @param offset the offset of the Sample within the raw Audio data
     * @return the Sample value as float
     */
    private float getIeeeFloatSample(final int offset) {
        if (mAudioFormat.getBytesPerSample() == Float.BYTES) {
            return mByteBuffer.getFloat(offset);
        } else {
            return (float) mByteBuffer.getDouble(offset);
        }
    }

    private double normalizeSignedPcmToDouble(final long sampleValue) {
        return Math.max(-1.0d, (double) sampleValue / mPcmSignedMax);
    }

    private double normalizeUnsignedPcmToDouble(final long sampleValue) {
        return normalizeSignedPcmToDouble(sampleValue - mPcmUnsignedHalfRange);
    }

    private float normalizeSignedPcmToFloat(final long sampleValue) {
        return Math.max(-1.0f, (float) sampleValue / mPcmSignedMax);
    }

    private float normalizeUnsignedPcmToFloat(final long sampleValue) {
        return normalizeSignedPcmToFloat(sampleValue - mPcmUnsignedHalfRange);
    }

    private final long mPcmSignedMax;
    private final long mPcmUnsignedHalfRange;
    private final ByteBuffer mByteBuffer;
    private final AudioFormat mAudioFormat;
}

