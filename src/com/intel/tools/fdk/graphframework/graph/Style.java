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

import java.util.Optional;

import org.eclipse.swt.graphics.Image;

/**
 * Object representing a style of an object
 *
 * This object carry data provided by client which allows to customize the representation of a given
 * {@link IGraphElement}
 *
 * @Todo add foreground and background colors and the alpha the representation
 */
public class Style {

    private Optional<String> label = Optional.empty();
    private Optional<Image> icon = Optional.empty();

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
    }

}
