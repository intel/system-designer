/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.tools.fdk.graphframework.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import com.intel.tools.utils.IntelPalette;

/**
 * Object representing a style of an object
 *
 * This object carry data provided by client which allows to customize the representation of a given
 * {@link IGraphElement}
 *
 * @Todo add the alpha of the representation
 */
public class Style {

    /**
     * Class allowing to listen for style update
     */
    public interface IStyleListener {
        default void labelUpdated(final Optional<String> label) {
        }

        default void iconUpdated(final Optional<Image> label) {
        }

        default void foregroundUpdated(final Color color) {
        }

        default void backgroundUpdated(final Color color) {
        }
    }

    private final List<IStyleListener> listeners = new ArrayList<>();

    private Optional<String> label = Optional.empty();
    private Optional<Image> icon = Optional.empty();
    private Color foreground = IntelPalette.INTEL_BLUE;
    private Color background = IntelPalette.INTEL_BLUE;

    /**
     * @return an optional object potentially carrying a label which should be displayed
     */
    public Optional<String> getLabel() {
        return label;
    }

    /**
     * @param label
     *            the label which should be displayed or null if none should be displayed
     */
    public void setLabel(final String label) {
        this.label = label == null ? Optional.empty() : Optional.of(label);
        listeners.forEach(l -> l.labelUpdated(this.label));
    }

    /**
     * @return an optional object potentially carrying an icon which should be displayed
     */
    public Optional<Image> getIcon() {
        return icon;
    }

    /**
     * @param icon
     *            the icon which should be displayed or null if none should be displayed
     */
    public void setIcon(final Image icon) {
        this.icon = icon == null ? Optional.empty() : Optional.of(icon);
        listeners.forEach(l -> l.iconUpdated(this.icon));
    }

    /**
     * @return the foreground color to be used by the element representation
     */
    public Color getForeground() {
        return foreground;
    }

    /**
     * @param foreground
     *            set a new foreground color to be used by the element representation
     */
    public void setForeground(final Color foreground) {
        assert foreground != null : "Foreground color must not be null.";
        this.foreground = foreground;
        listeners.forEach(l -> l.foregroundUpdated(this.foreground));
    }

    /**
     * @return the background color to be used by the element representation
     */
    public Color getBackground() {
        return background;
    }

    /**
     * @param background
     *            set a new background color to be used by the element representation
     */
    public void setBackground(final Color background) {
        assert background != null : "Background color must not be null.";
        this.background = background;
        listeners.forEach(l -> l.backgroundUpdated(this.background));
    }

    public void addListener(final IStyleListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(final IStyleListener listener) {
        this.listeners.remove(listener);
    }

}
