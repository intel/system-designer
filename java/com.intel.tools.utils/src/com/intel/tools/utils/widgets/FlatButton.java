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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.window.DefaultToolTip;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.intel.tools.utils.IntelPalette;

/**
 * Flat button
 */
public class FlatButton extends Canvas {
    /**
     * Enum with allowed styles:
     * <ul>
     * <li>LARGE_BUTTON</li>
     * <li>SMALL_BUTTON</li/
     * <ul>
     */
    public enum FlatButtonStyle {
        LARGE_BUTTON,
        SMALL_BUTTON
    }

    /**
     * Inner class for custom tooltip
     */
    class TooltipEx extends DefaultToolTip {

        /**
         * Default Constructor
         */
        public TooltipEx(final Control control) {
            super(control);
            this.setBackgroundColor(IntelPalette.WHITE);
        }

        /**
         * @return the text of the tooltip
         */
        public String getText() {
            return getText(null);
        }

    }

    private static final int DEFAULT_PADDING = 4;

    private Image image = null;
    private Image disabledImage = null;

    private String text;
    private final FlatButtonStyle style;

    private Color backgroundColor = IntelPalette.WHITE;
    private Color selectedColor = IntelPalette.MEDIUM_BLUE;
    private Color selectedTextColor = IntelPalette.WHITE;
    private Color mouseOverColor = IntelPalette.PALE_BLUE;

    private final List<SelectionListener> listeners;

    private int arrowPos;
    private boolean arrowVisible = false;
    private boolean mouseIn;
    private boolean clicked;

    protected boolean isCheckable = false;
    protected boolean isChecked = false;

    private TooltipEx tooltip = null;

    /**
     * Default constructor
     *
     * @param parent
     *            the parent composite
     * @param style
     *            the {@link FlatButtonStyle} style to apply
     */
    public FlatButton(final Composite parent, final FlatButtonStyle style) {
        super(parent, SWT.NONE);

        this.style = style;
        listeners = new ArrayList<SelectionListener>();

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
                if (isCheckable) {
                    isChecked = !isChecked;
                    clicked = isChecked;
                } else {
                    clicked = false;
                }
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

    /**
     * Paint the button
     *
     * @param e
     *            the paint event
     */
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
                y = (2 * DEFAULT_PADDING) + image.getBounds().height;
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
                height = Math.max(height, textSize.y + (2 * DEFAULT_PADDING));
            }
            if (arrowVisible) {
                width += DEFAULT_PADDING;
                arrowPos = width;
                width += 4;
                height = Math.max(height, 8 + (2 * DEFAULT_PADDING));
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
                width = Math.max(width, textSize.x + (2 * DEFAULT_PADDING));
            }
            if (arrowVisible) {
                height += DEFAULT_PADDING;
                arrowPos = height;
                height += 4;
                width = Math.max(width, 8 + (2 * DEFAULT_PADDING));
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

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.swt.widgets.Control#getToolTipText()
     */
    @Override
    public String getToolTipText() {
        if (tooltip == null) {
            return "";
        }

        return tooltip.getText();
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
        this.disabledImage = new Image(getDisplay(), this.image, SWT.IMAGE_DISABLE);
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

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.swt.widgets.Control#setToolTipText(java.lang.String)
     */
    @Override
    public void setToolTipText(final String string) {
        // NOTE: Discard the call to the super class because of an random issue on repainting the background
        // while a tooltip text is defined...

        tooltip = new TooltipEx(this);
        tooltip.setText(string);
        if (image != null) {
            final Rectangle bounds = image.getBounds();
            if (bounds != null) {
                tooltip.setShift(new Point(bounds.width, bounds.height));
            }
        }
    }

}
