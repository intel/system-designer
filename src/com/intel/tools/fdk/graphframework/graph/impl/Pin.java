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
package com.intel.tools.fdk.graphframework.graph.impl;

import java.util.Optional;

import com.intel.tools.fdk.graphframework.graph.IPin;
import com.intel.tools.fdk.graphframework.graph.Style;
import com.intel.tools.utils.IntelPalette;

/**
 * Represent a connection point of a {@link Leaf}
 */
public abstract class Pin implements IPin {

    private final Style style = new Style();

    private final int id;
    private final Leaf leaf;
    private Optional<Link> link = Optional.empty();

    public Pin(final int id, final Leaf leaf) {
        this.id = id;
        this.leaf = leaf;

        getStyle().setForeground(IntelPalette.GREY);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Leaf getLeaf() {
        return leaf;
    }

    @Override
    public Optional<Link> getLink() {
        return link;
    }

    /**
     * Connect this pin to a link.</br>
     * This method is used at {@link Link} creation and thus is not exposed everywhere
     *
     * @param link
     *            to connect
     */
    void connect(final Link link) {
        this.link = Optional.of(link);
    }

    /**
     * Disonnect this pin from a link.</br>
     * This method is used at {@link Link} deletion and thus is not exposed everywhere
     */
    void disconnect() {
        this.link = Optional.empty();
    }

    @Override
    public Style getStyle() {
        return style;
    }

}
