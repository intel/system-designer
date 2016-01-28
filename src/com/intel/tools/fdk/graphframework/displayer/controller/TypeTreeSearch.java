/* ============================================================================
 * INTEL CONFIDENTIAL
 *
 * Copyright 2015-2016 Intel Corporation All Rights Reserved.
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
package com.intel.tools.fdk.graphframework.displayer.controller;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.TreeSearch;

import com.intel.tools.fdk.graphframework.figure.IGraphFigure;

/** Allows to search all figures of a given type */
public class TypeTreeSearch implements TreeSearch {

    private final Class<? extends IGraphFigure> type;

    /** @param type class object of the searched type */
    public TypeTreeSearch(final Class<? extends IGraphFigure> type) {
        this.type = type;
    }

    @Override
    public boolean accept(final IFigure figure) {
        return type.isInstance(figure);
    }

    @Override
    public boolean prune(final IFigure figure) {
        return false;
    }

}
