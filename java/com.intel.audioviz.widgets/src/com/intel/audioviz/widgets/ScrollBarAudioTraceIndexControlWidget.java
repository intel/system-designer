/*
 * Copyright (C) 2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */

package com.intel.audioviz.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;

import com.intel.audioviz.trace.AudioTraceController;

/**
 * ScrollBarAudioTraceIndexControlWidget implements an AudioTraceIndexControlWidget using a simple horizontal scroll
 * bar.
 */
public class ScrollBarAudioTraceIndexControlWidget
extends AudioTraceIndexControlWidget
implements Listener {

    public ScrollBarAudioTraceIndexControlWidget(
            final Composite parent, final int style) {
        super(parent, style);

        mProportionalShift = 0;
        mChannelScrollBar = null;

        final GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        super.setLayout(gridLayout);

        final Canvas scrollBarCanvas = new Canvas(this, SWT.H_SCROLL);
        final GridData scrollBarCanvasGridData = new GridData();
        scrollBarCanvasGridData.horizontalAlignment = SWT.FILL;
        scrollBarCanvasGridData.grabExcessHorizontalSpace = true;
        /* In order consume minimum space, we shall set heightHint to 0, but doing this
         * makes the scroll bar to disappear !
         */
        scrollBarCanvasGridData.heightHint = 1;
        scrollBarCanvas.setLayoutData(scrollBarCanvasGridData);
        mChannelScrollBar = scrollBarCanvas.getHorizontalBar();
        mChannelScrollBar.setEnabled(false);
        mChannelScrollBar.addListener(SWT.Selection, this);
    }

    @Override
    public void setLayout(final Layout layout) {
        // Internally control the layout of widget required to render the channel.
    }

    @Override
    public void handleEvent(final Event event) {
        getAudioTraceController().setAudioSampleIndex(
                mChannelScrollBar.getSelection() << mProportionalShift);
    }

    @Override
    public void update() {
        if (mChannelScrollBar != null) {
            if (getAudioTraceController().getUnit() != AudioTraceController.INVALID_UNIT) {
                final double audioSamplePerTraceSample = getAudioTraceController().getUnit();

                final long currentTraceAudioSampleCount =
                        (long) (getAudioTraceController().getMinimumVisibleTraceSampleCount()
                                * audioSamplePerTraceSample);
                final long currentTraceAudioSampleIndex = getAudioTraceController().getAudioSampleIndex();
                final long maximumTraceAudioSampleIndex = Math.max(
                        getAudioTraceController().getMaximumAudioSampleIndex(),
                        currentTraceAudioSampleIndex + currentTraceAudioSampleCount);


                final long traceAudioSampleCountIncrement = (long) Math.max(audioSamplePerTraceSample, 1f);
                final long traceAudioSampleCountPageIncrement = currentTraceAudioSampleCount / PAGE_FACTOR;

                /* All computed ScrollBar values are long and may not be castable to int. Need to
                 * reduce them since maximumTraceSampleIndex may be greater than Integer.MAX_VALUE
                 */
                if (maximumTraceAudioSampleIndex > Integer.MAX_VALUE) {
                    /* Find the smallest required shift to have each value lesser than Integer.MAX_VALUE
                     * but still meaningful */
                    mProportionalShift = Math.min(
                            Long.numberOfTrailingZeros(maximumTraceAudioSampleIndex), Integer.SIZE);
                } else {
                    mProportionalShift = 0;
                }
                final int selection = doProportion(currentTraceAudioSampleIndex);
                final int maximum = doProportion(maximumTraceAudioSampleIndex);
                final int thumb = doProportion(currentTraceAudioSampleCount);
                final int increment = Math.max(1, doProportion(traceAudioSampleCountIncrement));
                final int pageIncrement =  Math.max(1, doProportion(traceAudioSampleCountPageIncrement));

                if (currentTraceAudioSampleCount > 0) {
                    mChannelScrollBar.setValues(
                            selection,
                            0,
                            maximum,
                            thumb,
                            increment,
                            pageIncrement);
                    mChannelScrollBar.setEnabled(true);
                    return;
                }
            }
            mChannelScrollBar.setEnabled(false);
        }
    }

    private int doProportion(final long value) {
        return (int) (value >> mProportionalShift);
    }

    private ScrollBar mChannelScrollBar;
    private int mProportionalShift;
    private static final int PAGE_FACTOR = 2;
}
