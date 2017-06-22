/*
 * Copyright (C) 2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */

package com.intel.audioviz;

import java.nio.ByteOrder;
import java.security.InvalidParameterException;

/**
 * An AudioFormat describes the characteristics of Audio raw data including:
 * - Number of Channels
 * - Sample Frequency
 * - Bytes per Sample
 * - Signed Sample or not
 * - Coding PCM or IEEE Float
 * - Byte order of Audio Samples
 * AudioFormat assumes the Audio raw data is a series of Audio Frames without
 * any padding between themselves.
 * An Audio Frame is a series of one Audio Sample per Audio Channel without padding between
 * themselves.
 * The Audio Samples within a Frame are ordered by Channel number starting by
 * Channel 0.
 * @todo consider using javax.sound.sampled.AudioFormat in AudioViz
 */
public class AudioFormat {

    /**
     * Sample coding format.
     */
    public enum Coding {
        FORMAT_PCM,
        FORMAT_IEEE_FLOAT
    }

    /**
     * @param channelCount
     *            Number of Channels
     * @param sampleFrequency
     *            The Sample Frequency in Hertz
     * @param bytesPerSample
     *            The number of bytes per Audio Sample
     * @param signed
     *            true if Audio Samples are signed, false otherwise
     * @param codingFormat
     *            Coding format of Audio Samples
     * @param byteOrder
     *            The byte order of the Audio Sample
     * @remark FORMAT_IEEE_FLOAT Audio Sample must be signed
     */
    public AudioFormat(
            final int channelCount,
            final int sampleFrequency,
            final int bytesPerSample,
            final boolean signed,
            final Coding codingFormat,
            final ByteOrder byteOrder) {

        if (channelCount < 1) {
            throw new InvalidParameterException("Invalid Channel count");
        }

        if (bytesPerSample < 1) {
            throw new InvalidParameterException("Invalid bit per Sample");
        }

        if (sampleFrequency < 1) {
            throw new InvalidParameterException("Invalid Sample Frequency");
        }

        if (!signed && codingFormat == Coding.FORMAT_IEEE_FLOAT) {
            throw new InvalidParameterException("IEEE Float Audio Sample must be signed.");
        }

        mChannelCount = channelCount;
        mSampleFrequency = sampleFrequency;
        mBytesPerSample = bytesPerSample;
        mSigned = signed;
        mCodingFormat = codingFormat;
        mByteOrder = byteOrder;
    }

    /**
     * @return Number of Audio Channels
     */
    public int getChannelCount() {
        return mChannelCount;
    }

    /**
     * @return The Sample Frequency in Hertz
     */
    public int getSampleFrequency() {
        return mSampleFrequency;
    }

    /**
     * @return Number of Bytes per Audio Sample
     * @see getBitsPerSample()
     */
    public int getBytesPerSample() {
        return mBytesPerSample;
    }

    /**
     * @return Number of Bits per Audio Sample
     * @see getBytePerSample()
     */
    public int getBitsPerSample() {
        return mBytesPerSample * Byte.SIZE;
    }

    /**
     * An Audio Frame is a series of one Audio Sample per Audio Channel without padding
     * between themselves.
     * The Audio Samples within a Frame are ordered by Channel number starting by
     * Channel 0.
     * @return the frame size in bytes
     */
    public int getFrameSize() {
        return mBytesPerSample * mChannelCount;
    }

    /**
     * @return true if audio samples are signed, false otherwise
     * @remark IEEE float encoded samples are always signed.
     */
    public boolean isSigned() {
        return mSigned;
    }

    /**
     * @return Audio Sample coding format
     */
    public Coding getCodingFormat() {
        return mCodingFormat;
    }

    /**
     * @return Audio Sample byte order
     */
    public ByteOrder getByteOrder() {
        return mByteOrder;
    }

    private final int mChannelCount;
    private final int mSampleFrequency;
    private final int mBytesPerSample;
    private final boolean mSigned;
    private final Coding mCodingFormat;
    private final ByteOrder mByteOrder;
}
