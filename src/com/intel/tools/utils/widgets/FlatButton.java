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

import javax.swing.text.StyleContext.SmallAttributeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.intel.tools.utils.IntelPalette;

public class FlatButton extends Canvas {
    public enum FlatButtonStyle {
        LARGE_BUTTON,
        SMALL_BUTTON
    }

    private static final int DEFAULT_PADDING = 4;

    private Image image;
    private Image disabledImage;

    private String text;
    private final FlatButtonStyle style;

    private Color backgroundColor = IntelPalette.WHITE;
    private Color selectedColor = IntelPalette.MEDIUM_BLUE;
    private Color selectedTextColor = IntelPalette.WHITE;
    private Color mouseOverColor = IntelPalette.PALE_BLUE;

    private boolean clicked;
    private boolean mouseIn;

    private int arrowPos;

    private final List<SelectionListener> listeners;

    private boolean arrowVisible = false;

    public FlatButton(final Composite parent, final FlatButtonStyle style) {
        super(parent, SWT.NONE);

        this.style = style;
        listeners = new ArrayList<SelectionListener>();
        disabledImage = null;

        addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(final DisposeEvent e) {
                if (disabledImage != null) {
                    disabledImage.dispose();
                }
            }
        });
        addPaintListener(new PaintListener() {
            @Override
            public void paintControl(final PaintEvent pe) {
                paint(pe);
            }
        });

        addListener(SWT.MouseEnter, new Listener() {
            @Override
            public void handleEvent(final Event event) {
                mouseIn = true;
                redraw();
            }
        });

        addListener(SWT.MouseExit, new Listener() {
            @Override
            public void handleEvent(final Event event) {
                mouseIn = false;
                redraw();
            }
        });

        addListener(SWT.MouseUp, new Listener() {
            @Override
            public void handleEvent(final Event event) {
                clicked = false;
                mouseIn = false;
                redraw();

                for (final SelectionListener listener : FlatButton.this.listeners) {
                    listener.widgetSelected(new SelectionEvent(event));
                }
            }

        });

        addListener(SWT.MouseDown, new Listener() {
            @Override
            public void handleEvent(final Event event) {
                clicked = true;
                redraw();
            }
        });
    }

    private void paint(final PaintEvent e) {
        final GC gc = e.gc;

        drawBackground(gc);

        if (image != null) {
            drawImage(gc);
        }
        if (text != null) {
            drawText(gc);
        }
        if (arrowVisible) {
            drawArrow(gc);
        }
    }

    private void drawArrow(final GC gc) {
        final Rectangle rect = getClientArea();

        gc.setBackground(IntelPalette.BLACK);

        if (style == FlatButtonStyle.SMALL_BUTTON) {
            final int y = (rect.height - 8) / 2;
            final int[] rightArrow = new int[] {arrowPos, y, arrowPos + 4, y + 4, arrowPos, y + 8};
            gc.fillPolygon(rightArrow);
        } else {
            final int x = (rect.width - 8) / 2;
            final int[] downArrow = new int[] {x, arrowPos, x + 8, arrowPos, x + 4, arrowPos + 4};
            gc.fillPolygon(downArrow);
        }

    }

    private void drawBackground(final GC gc) {
        Color color;

        if (this.clicked) {
            color = selectedColor;
        } else if (this.mouseIn) {
            color = mouseOverColor;
        } else {
            color = backgroundColor;
        }
        gc.setBackground(color);
        gc.fillRectangle(getClientArea());

    }

    private void drawImage(final GC gc) {
        final Rectangle rect = getClientArea();
        final Point imageSize = new Point(image.getBounds().width, image.getBounds().height);

        int x;
        switch (style) {
        case SMALL_BUTTON:
            x = DEFAULT_PADDING;
            break;
        case LARGE_BUTTON:
            x = (rect.width - imageSize.x) / 2;
            break;
        default:
            x = (rect.width - imageSize.x) / 2;
            break;
        }

        if (isEnabled()) {
            gc.drawImage(image, x, DEFAULT_PADDING);
        } else {
            gc.drawImage(disabledImage, x, DEFAULT_PADDING);
        }
    }

    private void drawText(final GC gc) {
        final Rectangle rect = getClientArea();
        Point imageSize = null;

        if (image != null) {
            imageSize = new Point(image.getBounds().width, image.getBounds().height);
        }

        if (isEnabled()) {
            if (clicked) {
                gc.setForeground(selectedTextColor);
            } else {
                gc.setForeground(getForeground());
            }
        } else {
            gc.setForeground(IntelPalette.GREY);
        }

        gc.setFont(getFont());
        final Point textSize = gc.textExtent(text);
        int x, y;

        switch (style) {
        case LARGE_BUTTON:
            x = (rect.width - textSize.x) / 2;
            break;
        case SMALL_BUTTON:
            x = DEFAULT_PADDING;
            if (imageSize != null) {
                x += imageSize.x + DEFAULT_PADDING;
            }
            break;
        default:
            x = (rect.width - textSize.x) / 2;
            break;

        }

        if (image == null) {
            y = DEFAULT_PADDING;
        } else {
            if (style != FlatButtonStyle.SMALL_BUTTON) {
                y = 2 * DEFAULT_PADDING + image.getBounds().height;
            } else {
                y = (rect.height - textSize.y) / 2;
            }
        }
        gc.drawText(text, x, y, true);
    }

    public void addSelectionListener(final SelectionListener listener) {
        checkWidget();
        listeners.add(listener);
    }

    @Override
    public Point computeSize(final int wHint, final int hHint, final boolean changed) {

        int width, height;
        Rectangle imageBounds = null;
        if (image != null) {
            imageBounds = image.getBounds();
        }
        Point textSize = null;
        if (this.text != null) {
            final GC gc = new GC(this);
            textSize = gc.textExtent(this.text);
            gc.dispose();
        }

        if (style == FlatButtonStyle.SMALL_BUTTON) {
            width = DEFAULT_PADDING;
            height = 2 * DEFAULT_PADDING;

            if (image != null) {
                height += imageBounds.height;
                width += imageBounds.width;
            }
            if (text != null) {
                width += textSize.x + DEFAULT_PADDING;
                height = Math.max(height, textSize.y + 2 * DEFAULT_PADDING);
            }
            if (arrowVisible) {
                width += DEFAULT_PADDING;
                arrowPos = width;
                width += 4;
                height = Math.max(height, 8 + 2 * DEFAULT_PADDING);
            }
            width += DEFAULT_PADDING;
        } else {
            height = DEFAULT_PADDING;
            width = 2 * DEFAULT_PADDING;

            if (image != null) {
                height += imageBounds.height;
                width += imageBounds.width;
            }
            if (text != null) {
                height += textSize.y + DEFAULT_PADDING;
                width = Math.max(width, textSize.x + 2 * DEFAULT_PADDING);
            }
            if (arrowVisible) {
                height += DEFAULT_PADDING;
                arrowPos = height;
                height += 4;
                width = Math.max(width, 8 + 2 * DEFAULT_PADDING);
            }
            height += DEFAULT_PADDING;
        }

        return new Point(Math.max(width, wHint), Math.max(height, hHint));
    }

    public Color getBackgroundColor() {
        checkWidget();
        return backgroundColor;
    }

    public Image getImage() {
        checkWidget();
        return image;
    }

    public Color getMouseOverColor() {
        checkWidget();
        return mouseOverColor;
    }

    public Color getSelectedColor() {
        checkWidget();
        return selectedColor;
    }

    public Color getSelectedTextColor() {
        return selectedTextColor;
    }

    public String getText() {
        checkWidget();
        return text;
    }

    public void removeSelectionListener(final SelectionListener listener) {
        checkWidget();
        listeners.remove(listener);
    }

    public void setBackgroundColor(final Color backgroundColor) {
        checkWidget();
        this.backgroundColor = backgroundColor;
    }

    public void setImage(final Image image) {
        checkWidget();
        this.image = image;
        if (this.disabledImage != null) {
            this.disabledImage.dispose();
        }
        this.disabledImage  = new Image(getDisplay(), this.image, SWT.IMAGE_DISABLE);
        redraw();
    }

    public void setMouseOverColor(final Color mouseOverColor) {
        checkWidget();
        this.mouseOverColor = mouseOverColor;
    }

    public void setSelectedColor(final Color selectedColor) {
        checkWidget();
        this.selectedColor = selectedColor;
    }

    public void setSelectedTextColor(final Color selectedTextColor) {
        this.selectedTextColor = selectedTextColor;
    }

    public void setText(final String text) {
        checkWidget();
        this.text = text;
        redraw();
    }

    @Override
    public void setEnabled(final boolean state) {
        super.setEnabled(state);
        redraw();
    }

    public void setArrowVisible(final boolean b) {
        checkWidget();
        arrowVisible = b;
        redraw();
    }
}
