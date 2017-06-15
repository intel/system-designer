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
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidParameterException;

import com.intel.audioviz.AudioFormat;

/**
 * This class allows to parse basic data required to use a WAV file.
 * Two WAV chunk types are parsed: "fmt " and "data". Other chunk types are ignored
 * such as "INFO" or "LIST" for instance.
 *
 * @fixme Add support for WaveFormatExtensable WAV format.
 */
public class WavHeaderParser {

    /**
     * Open and parse basic information about a WAV file.
     * @param wavFile A File instance of the WAV file to be parsed.
     * @throws IOException In case the WAV file is malformed or unsupported
     */
    public WavHeaderParser(final File wavFile) throws IOException {
        if (!wavFile.exists() || wavFile.isDirectory()) {
            throw new InvalidParameterException("Not a file");
        }

        try (FileInputStream fis = new FileInputStream(wavFile)) {
            /* Read first 4Ko of the WAV file: "fmt " and "data" chunks
             * are always in the first 4Ko */
            final byte[] headerBloc = new byte[HEADER_BLOC_SIZE];

            final int read = fis.read(headerBloc, 0, HEADER_BLOC_SIZE);
            /* Wrap a little endian ByteBuffer on the WAV data (RIFF files are LE) */
            final ByteBuffer header = ByteBuffer.wrap(headerBloc, 0, read);
            header.order(ByteOrder.LITTLE_ENDIAN);

            checkWavSignatures(header);
            parseDataChunk(header);

            AudioFormat.Coding sampleCoding;
            switch (Short.toUnsignedInt(header.getShort(SAMPLE_CODING_OFFSET))) {
                case WAVE_FORMAT_PCM:
                    sampleCoding = AudioFormat.Coding.FORMAT_PCM;
                    break;
                case WAVE_FORMAT_IEEE_FLOAT:
                    sampleCoding = AudioFormat.Coding.FORMAT_IEEE_FLOAT;
                    break;
                case WAVE_FORMAT_EXTENSIBLE:
                    throw new InvalidParameterException(
                            "WAVE Format Extensible is not supported.");
                default:
                    throw new InvalidParameterException(
                            "Unknown WAV coding format");
            }

            mDataFormat = new AudioFormat(
                    header.getShort(CHANNEL_COUNT_OFFSET),
                    header.getInt(SAMPLE_FREQ_OFFSET),
                    header.getShort(BIT_PER_SAMPLE_OFFSET) / 8,
                    /* 8 bits sample are unsigned in WAV files, all other coding format are
                     * signed. */
                    Short.reverseBytes(header.getShort(BIT_PER_SAMPLE_OFFSET)) != 8 ? true : false,
                            sampleCoding,
                            /* Only "RIFF" files are supported, which are little endian WAV files. */
                            ByteOrder.LITTLE_ENDIAN);
        }
    }

    /**
     * Check the WAV header starts with "RIFF" tags and then check the first chunk is
     * the "WAVE" chunk including its "fmt " subchunk.
     * @param header The ByteBuffer containing the WAV file content to be parsed
     */
    private void checkWavSignatures(final ByteBuffer header) {
        /** @fixme This implementation assumes the "fmt " tag is always at the same
         * offset in all WAV files
         */
        /* chunk tags are the only big endian data in RIFF files */
        header.order(ByteOrder.BIG_ENDIAN);
        if (header.getInt(RIFF_TAG_OFFSET) != RIFF_TAG
                || header.getInt(WAVE_TAG_OFFSET) != WAVE_TAG
                || header.getInt(FMT_TAG_OFFSET) != FMT_TAG) {
            throw new InvalidParameterException("Unsupported WAV file");
        }
        header.order(ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * Look for the "data" chunks to gets audio data size & offset within file
     * @param header The ByteBuffer containing the WAV file content to be parsed
     */
    private void parseDataChunk(final ByteBuffer header) {
        /* WAV pcm data are in the "data" chunk. In some WAV file, special chunk are
         * inserted before the "data" chunk. As consequences, no assumption can be
         * done about its offset.
         */
        boolean dataFound = false;
        int offset = 0;
        /** @remarks This loop looks for dword "data" at each byte offset. It could
         * be simplified looking only at dword offset, but without clear specifications
         * we are sure to find it this way even in exotic WAV files...
         */
        /* chunk tags are the only big endian data in RIFF files */
        header.order(ByteOrder.BIG_ENDIAN);
        for (; offset < header.capacity() - Integer.BYTES; offset++) {
            if (header.getInt(offset) == DATA_TAG) {
                dataFound = true;
                break;
            }
        }
        if (!dataFound) {
            throw new InvalidParameterException("Unsupported WAV file format: no data section");
        }
        header.order(ByteOrder.LITTLE_ENDIAN);
        /* Data offset is then 8 bytes after the "data" tag */
        mDataOffset = offset + DATA_OFFSET_FROM_DATA_TAG_OFFSET;
        /* Data size is in the 4 bytes just after "data" tag */
        mDataSize = Integer.toUnsignedLong(header.getInt(offset + DATA_SIZE_OFFSET_FROM_DATA_TAG_OFFSET));
    }

    /**
     * @return The RawAudioFormat of the WAV file audio data
     */
    public AudioFormat getAudioDataFormat() {
        return mDataFormat;
    }

    /**
     * @return The offset of raw audio data within the WAV file
     */
    public long getAudioDataOffset() {
        return mDataOffset;
    }

    /**
     * @return The size of raw audio data within the WAV file
     */
    public long getAudioDataSize() {
        return mDataSize;
    }

    private long mDataOffset;
    private long mDataSize;
    private AudioFormat mDataFormat;

    private static final int DATA_TAG = 0x64617461; // "data"
    private static final int RIFF_TAG = 0x52494646; // "RIFF"
    private static final int WAVE_TAG = 0x57415645; // "WAVE"
    private static final int FMT_TAG = 0x666d7420; // "fmt "

    private static final int WAVE_FORMAT_PCM = 1;
    private static final int WAVE_FORMAT_IEEE_FLOAT = 3;
    private static final int WAVE_FORMAT_EXTENSIBLE = 0xFFFE;

    private static final int RIFF_TAG_OFFSET = 0;
    private static final int WAVE_TAG_OFFSET = 8;
    private static final int FMT_TAG_OFFSET = 12;
    private static final int DATA_OFFSET_FROM_DATA_TAG_OFFSET = 8;
    private static final int DATA_SIZE_OFFSET_FROM_DATA_TAG_OFFSET = 4;

    private static final int SAMPLE_CODING_OFFSET = 20;
    private static final int CHANNEL_COUNT_OFFSET = 22;
    private static final int SAMPLE_FREQ_OFFSET = 24;
    private static final int BIT_PER_SAMPLE_OFFSET = 34;

    private static final int HEADER_BLOC_SIZE = 4096;
}
