/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
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

    /**
     * Change the default title font.
     *
     * @param font
     *            The new font to apply.
     */
    public void setTitleFont(final Font font) {
        titleFont = font;
    }

    /**
     * Change the default Sub Title font.
     *
     * @param font
     *            The new font to apply.
     */
    public void setSubTitleFont(final Font font) {
        subtitleFont = font;
    }

    private void paint(final PaintEvent e) {
        final GC gc = e.gc;

        if (titleText != null) {
            drawText(gc);
        }
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
