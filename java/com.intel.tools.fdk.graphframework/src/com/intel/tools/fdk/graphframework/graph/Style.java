/* ============================================================================
 * INTEL CONFIDENTIAL
 *
 * Copyright 2016 Intel Corporation All Rights Reserved.
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
