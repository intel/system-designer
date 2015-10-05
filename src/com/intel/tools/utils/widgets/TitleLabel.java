/* ============================================================================
 * INTEL CONFIDENTIAL
 *
 * Copyright 2013 - 2014 Intel Corporation All Rights Reserved.
 *
 * The source code contained or described herein and all documents related to
 * the source code ("Material") are owned by Intel Corporation or its suppliers
 * or licensors. Title to the Material remains with Intel Corporation or its
 * suppliers and licensors. The Material contains trade secrets and proprietary
 * and confidential information of Intel or its suppliers and licensors. The
 * Material is protected by worldwide copyright and trade secret laws and
 * treaty provisions. No part of the Material may be used, copied, reproduced,
 * modified, published, uploaded, posted, transmitted, distributed, or
 * disclosed in any way without Intel's prior express written permission.
 *
 * No license under any patent, copyright, trade secret or other intellectual
 * property right is granted to or conferred upon you by disclosure or delivery
 * of the Materials, either expressly, by implication, inducement, estoppel or
 * otherwise. Any license under such intellectual property rights must be
 * express and approved by Intel in writing.
 * ============================================================================
 */

package com.intel.tools.utils.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.SWTResourceManager;

import com.intel.tools.utils.IntelPalette;

public class TitleLabel extends Canvas {

    private static final int DEFAULT_PADDING = 6;

    private Font titleFont;
    private Font subtitleFont;

    private String titleText;
    private String subTitleText;

    public TitleLabel(final Composite parent) {
        super(parent, SWT.NONE);

        titleFont = SWTResourceManager.getFont("Intel Clear", 14, SWT.NORMAL);
        if (!titleFont.getFontData()[0].getName().equals("Intel Clear")) {
            titleFont = SWTResourceManager.getFont("Segoe UI", 14, SWT.NORMAL);
        }
        subtitleFont = SWTResourceManager.getFont("Intel Clear", 8, SWT.NORMAL);
        if (!subtitleFont.getFontData()[0].getName().equals("Intel Clear")) {
            subtitleFont = SWTResourceManager.getFont("Segoe UI", 8, SWT.NORMAL);
        }

        addPaintListener(new PaintListener() {
            @Override
            public void paintControl(final PaintEvent pe) {
                paint(pe);
            }
        });
    }

    private void paint(final PaintEvent e) {
        final GC gc = e.gc;

        drawBackground(gc);

        if (titleText != null) {
            drawText(gc);
        }
    }

    private void drawBackground(final GC gc) {
        // Color color;
        //
        // color = IntelPalette.GREEN;
        // gc.setBackground(color);
        // gc.fillRectangle(getClientArea());

    }

    private void drawText(final GC gc) {
        final Rectangle rect = getClientArea();
        int x, y;

        gc.setForeground(getForeground());
        gc.setTextAntialias(SWT.ON);

        gc.setFont(titleFont);
        final Point textSize = gc.textExtent(titleText);
        y = (rect.height - textSize.y) / 2;
        x = 0;
        gc.drawText(titleText, x, y, true);

        if (subTitleText != null) {
            gc.setForeground(IntelPalette.GREY_4);
            gc.drawLine(textSize.x + DEFAULT_PADDING, (int) (textSize.y * 0.2 + y), textSize.x + DEFAULT_PADDING,
                    (int) (textSize.y * 0.8 + y));

            gc.setForeground(getForeground());
            gc.setFont(subtitleFont);
            final Point subTitleTextSize = gc.textExtent(subTitleText);
            y = (rect.height - subTitleTextSize.y) / 2 - 1;

            gc.drawText(subTitleText, textSize.x + DEFAULT_PADDING * 2, y);
        }

    }

    @Override
    public Point computeSize(final int wHint, final int hHint, final boolean changed) {

        int width = 157, height = 30;

        final GC gc = new GC(this);
        gc.setFont(titleFont);
        final Point textSize = gc.textExtent(titleText);
        width = textSize.x;
        height = textSize.y;

        if (subTitleText != null) {

            gc.setFont(subtitleFont);
            final Point subTitleTextSize = gc.textExtent(subTitleText);
            width += subTitleTextSize.x;
            width += DEFAULT_PADDING * 2;
        }

        return new Point(Math.max(width, wHint), Math.max(height, hHint));
    }

    public String getTitleText() {
        checkWidget();
        return titleText;
    }

    public void setTitleText(final String text) {
        checkWidget();
        this.titleText = text;
        redraw();
    }

    @Override
    public void setEnabled(final boolean state) {
        super.setEnabled(state);
        redraw();
    }

    public String getSubTitleText() {
        return subTitleText;
    }

    public void setSubTitleText(final String subTitleText) {
        this.subTitleText = subTitleText;
    }
}
