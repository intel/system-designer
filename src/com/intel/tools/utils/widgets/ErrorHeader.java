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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.wb.swt.SWTResourceManager;

import com.intel.tools.utils.IntelPalette;

/**
 * This class presents a large Orange or Red label to show errors in editors. This widged changes from visible to
 * invisible depending on the error message.
 */
public class ErrorHeader extends Canvas {
    public enum ErrorLevel {
        INFO, WARNING, ERROR
    }

    private static final int DEFAULT_PADDING = 4;

    private String errorText;
    private final List<Control> buttonList = new ArrayList<>();

    private boolean isMouseOver;

    private final Cursor handCursor;
    private final Cursor defaultCursor;

    private Action action;

    public ErrorHeader(final Composite parent) {
        super(parent, SWT.NONE);

        handCursor = new org.eclipse.swt.graphics.Cursor(parent.getDisplay(), SWT.CURSOR_HAND);
        defaultCursor = this.getCursor();

        Font font = SWTResourceManager.getFont("Intel Clear", 10, SWT.NORMAL);
        if (!font.getFontData()[0].getName().equals("Intel Clear")) {
            font = SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL);
        }
        setFont(font);

        setBackground(IntelPalette.ORANGE);
        setForeground(IntelPalette.WHITE);

        addPaintListener(new PaintListener() {
            @Override
            public void paintControl(final PaintEvent pe) {
                paint(pe);
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(final MouseEvent e) {
                if (action != null) {
                    action.run();
                }
            }
        });
        addMouseTrackListener(new MouseTrackListener() {

            @Override
            public void mouseHover(final MouseEvent e) {
            }

            @Override
            public void mouseExit(final MouseEvent e) {
                isMouseOver = false;
                redraw();
            }

            @Override
            public void mouseEnter(final MouseEvent e) {
                isMouseOver = true;
                redraw();
            }
        });
    }

    private void paint(final PaintEvent e) {
        final GC gc = e.gc;

        paintBackground(gc);

        if (errorText != null) {
            drawText(gc);
        }
    }

    private void paintBackground(final GC gc) {
        final Rectangle rect = getClientArea();
        gc.setForeground(getBackground());
        gc.fillRectangle(rect);
    }

    private void drawText(final GC gc) {
        final Rectangle rect = getClientArea();
        int x, y;

        if (isMouseOver && action != null) {
            gc.setForeground(IntelPalette.LIGHT_BLUE);
        } else {
            gc.setForeground(getForeground());
        }
        gc.setTextAntialias(SWT.ON);

        gc.setFont(getFont());
        final Point textSize = gc.textExtent(errorText);
        y = (int) Math.floor((rect.height - textSize.y) / 2) - 1;
        x = DEFAULT_PADDING;
        gc.drawText(errorText, x, y, true);

        int buttonIndex = 0;
        final int buttonSize = (int) (rect.height * 0.6);
        final int buttonMargin = (rect.height - buttonSize) / 2;
        for (final Control button : buttonList) {
            button.setBounds(rect.width - ((buttonSize + buttonMargin) * (buttonIndex + 1)),
                    (rect.height - buttonSize) / 2, buttonSize,
                    buttonSize);
            buttonIndex++;
        }

    }

    @Override
    public Point computeSize(final int wHint, final int hHint, final boolean changed) {

        int width, height;
        if (errorText != null) {

            final GC gc = new GC(this);
            gc.setFont(getFont());
            final Point textSize = gc.textExtent(errorText);
            width = textSize.x;
            height = textSize.y + DEFAULT_PADDING * 2;

            width = Math.max(width, wHint);
            height = Math.max(height, hHint);
        } else {
            // If we don't have an error to display the width is as set by parent and the height is 0
            return new Point(wHint, 0);
        }
        return new Point(width, height);
    }

    public void addButton(final Control control) {
        buttonList.add(control);
    }

    public String getErrorText() {
        checkWidget();
        return errorText;
    }

    /**
     * Set The error level of the error message.
     *
     * @param level
     *            Le error level to set.
     */
    public void setErrorLevel(final ErrorLevel level) {
        switch (level) {
            case ERROR:
                setBackground(IntelPalette.RED);
                break;
            case WARNING:
                setBackground(IntelPalette.ORANGE);
                break;
            case INFO:
                setBackground(IntelPalette.INTEL_BLUE);
                break;
        }
    }

    public void resetErrorMessage() {
        setErrorText(null);
    }

    public void setErrorText(final String text) {
        checkWidget();
        this.errorText = text;
        getParent().layout(true);
        redraw();
    }

    @Override
    public void setEnabled(final boolean state) {
        super.setEnabled(state);
        redraw();
    }

    public void setAction(final Action action) {
        if (action != null) {
            this.setCursor(handCursor);
        } else {
            this.setCursor(defaultCursor);
        }
        this.action = action;
    }

    @Override
    public void dispose() {
        handCursor.dispose();
        super.dispose();
    }
}
