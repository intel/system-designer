/*
 * Copyright (C) 2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.audioviz.file.ui.dialogs;

import java.nio.ByteOrder;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import com.intel.audioviz.AudioFormat;

/**
 * A dialog which allows to select an AudioFormat.
 */
public class AudioFormatDialog extends Dialog {

    /**
     * Create a Dialog to let the user to select an AudioFormat.
     * @param parentShell Parent shell of the Dialog
     * @param title The title of the Dialog
     * @param offsetSupport If true, the user can select an offset
     */
    public AudioFormatDialog(final Shell parentShell, final String title, final boolean offsetSupport) {
        super(parentShell);
        mTitle = title;
        mOffsetSupport = offsetSupport;
    }

    /**
     * Create a Dialog to let the user to select an AudioFormat.
     * @param parentShell Parent shell of the Dialog
     * @param title The title of the Dialog
     * @param offsetSupport If true, the user can select an offset
     */
    public AudioFormatDialog(final IShellProvider parentShell, final String title, final boolean offsetSupport) {
        super(parentShell);
        mTitle = title;
        mOffsetSupport = offsetSupport;
    }

    /**
     * @return The validated AudioFormat, or null if Dialog has ben canceled.
     */
    public AudioFormat getAudioFormat() {
        return mAudioFormat;
    }

    /**
     * @return The offset selected by the user. Always 0 when Dialog has been canceled or
     * if Dialog has been created without offset support.
     */
    public int getOffset() {
        return mOffset;
    }

    @Override
    protected Control createDialogArea(final Composite parent) {
        final Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new GridLayout(2, false));

        final Label encodingLabel = new Label(container, SWT.NONE);
        encodingLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
        encodingLabel.setText("Enconding:");

        mEncodingCombo = new Combo(container, SWT.READ_ONLY);
        mEncodingCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        mEncodingCombo.setItems(SUPPORTED_ENCODING_FORMAT);
        mEncodingCombo.select(DEFAULT_ENCODING_FORMAT_INDEX);

        final Label byteOrderLabel = new Label(container, SWT.NONE);
        byteOrderLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
        byteOrderLabel.setText("Byte order:");

        mByteOrderCombo = new Combo(container, SWT.READ_ONLY);
        mByteOrderCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        mByteOrderCombo.setItems(SUPPORTED_BYTE_ORDER);
        mByteOrderCombo.select(DEFAULT_BYTE_ORDER_INDEX);

        final Label channelsLabel = new Label(container, SWT.NONE);
        channelsLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
        channelsLabel.setText("Channels:");

        mChannelCombo = new Combo(container, SWT.READ_ONLY);
        mChannelCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        mChannelCombo.setItems(CHANNEL_LIST);
        mChannelCombo.select(DEFAULT_CHANNEL_INDEX);

        if (mOffsetSupport) {
            final Label offsetLabel = new Label(container, SWT.NONE);
            offsetLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
            offsetLabel.setText("Start offset (bytes):");

            mOffsetSpinner = new Spinner(container, SWT.NONE);
            mOffsetSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            mOffsetSpinner.setSelection(0);
            mOffsetSpinner.setMinimum(0);
            mOffsetSpinner.setMaximum(Integer.MAX_VALUE);
        }

        final Label sampleFrequencyLabel = new Label(container, SWT.NONE);
        sampleFrequencyLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
        sampleFrequencyLabel.setText("Sample frequency (Hz):");

        mSampleFrequencyCombo = new Combo(container, SWT.NONE);
        mSampleFrequencyCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        mSampleFrequencyCombo.setItems(STANDARD_SAMPLE_FREQUENCIES);
        mSampleFrequencyCombo.select(DEFAULT_SAMPLE_FREQUENCY_INDEX);
        mSampleFrequencyCombo.addListener(SWT.Modify, e -> {
            int frequency = -1;

            try {
                frequency = Integer.parseUnsignedInt(mSampleFrequencyCombo.getText());
            } catch (final NumberFormatException ex) {
                // Design make this unreachable since OK button is disabled when combo is not an integer
                assert false;
            }

            getButton(IDialogConstants.OK_ID).setEnabled(frequency > 0);
        });
        return container;
    }

    @Override
    protected void okPressed() {
        final int sampleFrequency = Integer.parseUnsignedInt(mSampleFrequencyCombo.getText());

        mAudioFormat = new AudioFormat(
                mChannelCombo.getSelectionIndex() + 1,
                sampleFrequency,
                SUPPORTED_ENCODING_FORMAT_BYTE_PER_SAMPLE[mEncodingCombo.getSelectionIndex()],
                SUPPORTED_ENCODING_FORMAT_IS_SIGNED[mEncodingCombo.getSelectionIndex()],
                SUPPORTED_ENCODING_FORMAT_CODING_FORMAT[mEncodingCombo.getSelectionIndex()],
                SUPPORTED_BYTE_ORDER_VALUES[mByteOrderCombo.getSelectionIndex()]
                );

        if (mOffsetSpinner != null) {
            mOffset = mOffsetSpinner.getSelection();
        }

        super.okPressed();
    }

    @Override
    protected void configureShell(final Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(mTitle);
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    private AudioFormat mAudioFormat = null;
    private int mOffset = 0;
    private final boolean mOffsetSupport;
    private final String mTitle;

    private Combo mEncodingCombo = null;
    private Combo mByteOrderCombo = null;
    private Combo mChannelCombo = null;
    private Spinner mOffsetSpinner = null;
    private Combo mSampleFrequencyCombo = null;


    private static final int DEFAULT_ENCODING_FORMAT_INDEX = 1;
    private static final String[] SUPPORTED_ENCODING_FORMAT = {
            "Signed 8-bit PCM",
            "Signed 16-bit PCM",
            "Signed 24-bit PCM",
            "Signed 32-bit PCM",
            "Unsigned 8-bit PCM",
            "Unsigned 16-bit PCM",
            "Unsigned 24-bit PCM",
            "Unsigned 32-bit PCM",
            "32-bit IEEE Float",
            "64-bit IEEE Float"
    };

    private static final boolean[] SUPPORTED_ENCODING_FORMAT_IS_SIGNED = {
            true, // Signed 8-bit PCM
            true, // Signed 16-bit PCM
            true, // Signed 24-bit PCM
            true, // Signed 32-bit PCM
            false, // Unsigned 8-bit PCM
            false, // Unsigned 16-bit PCM
            false, // Unsigned 24-bit PCM
            false, // Unsigned 32-bit PCM
            true, // 32-bit IEEE Float
            true, // 64-bit IEEE Float
    };

    private static final int[] SUPPORTED_ENCODING_FORMAT_BYTE_PER_SAMPLE = {
            1, // Signed 8-bit PCM
            2, // Signed 16-bit PCM
            3, // Signed 24-bit PCM
            4, // Signed 32-bit PCM
            1, // Unsigned 8-bit PCM
            2, // Unsigned 16-bit PCM
            3, // Unsigned 24-bit PCM
            4, // Unsigned 32-bit PCM
            4, // 32-bit IEEE Float
            8, // 64-bit IEEE Float
    };

    private static final AudioFormat.Coding[] SUPPORTED_ENCODING_FORMAT_CODING_FORMAT = {
            AudioFormat.Coding.FORMAT_PCM, // Signed 8-bit PCM
            AudioFormat.Coding.FORMAT_PCM, // Signed 16-bit PCM
            AudioFormat.Coding.FORMAT_PCM, // Signed 24-bit PCM
            AudioFormat.Coding.FORMAT_PCM, // Signed 32-bit PCM
            AudioFormat.Coding.FORMAT_PCM, // Unsigned 8-bit PCM
            AudioFormat.Coding.FORMAT_PCM, // Unsigned 16-bit PCM
            AudioFormat.Coding.FORMAT_PCM, // Unsigned 24-bit PCM
            AudioFormat.Coding.FORMAT_PCM, // Unsigned 32-bit PCM
            AudioFormat.Coding.FORMAT_IEEE_FLOAT, // 32-bit IEEE Float
            AudioFormat.Coding.FORMAT_IEEE_FLOAT, // 64-bit IEEE Float
    };

    private static final int DEFAULT_BYTE_ORDER_INDEX = 0;
    private static final String[] SUPPORTED_BYTE_ORDER = {
            "Little Endian",
            "Big Endian",
            "Native Endian ("
                    + ByteOrder.nativeOrder().toString().toLowerCase().replace('_', ' ')
                    + ")"
    };

    private static final ByteOrder[] SUPPORTED_BYTE_ORDER_VALUES = {
            ByteOrder.LITTLE_ENDIAN, // Little Endian
            ByteOrder.BIG_ENDIAN, // Big Endian
            ByteOrder.nativeOrder(), // Native Endian
    };

    private static final int DEFAULT_CHANNEL_INDEX = 1;
    private static final String[] CHANNEL_LIST = {
            "1 Channel (mono)",
            "2 Channels (stereo)",
            "3 Channels",
            "4 Channels",
            "5 Channels",
            "6 Channels",
            "7 Channels",
            "8 Channels",
            "9 Channels",
            "10 Channels",
    };

    private static final int DEFAULT_SAMPLE_FREQUENCY_INDEX = 6;
    private static final String[] STANDARD_SAMPLE_FREQUENCIES = {
            "8000",
            "11025",
            "16000",
            "22050",
            "24000",
            "32000",
            "44100",
            "48000",
            "88200",
            "96000",
            "192000"
    };
}
