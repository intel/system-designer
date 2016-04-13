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
package com.intel.tools.fdk.graphframework.graph.factory;

import java.util.Collections;
import java.util.Set;

import com.intel.tools.fdk.graphframework.graph.Graph;
import com.intel.tools.fdk.graphframework.graph.Group;
import com.intel.tools.fdk.graphframework.graph.Leaf;

public final class GraphFactory {

    private GraphFactory() {
    }

    public static Graph createGraph(final Set<Leaf> leaves, final Set<Group> groups) {
        return new Graph(leaves, groups);
    }

    public static Graph createGraph(final Set<Leaf> leaves) {
        return createGraph(leaves, Collections.emptySet());
    }

    public static Leaf createLeaf(final int inputNumber, final int outputNumber) {
        return new Leaf(inputNumber, outputNumber);
    }

    public static Group createGroup(final Set<Leaf> leaves, final Set<Group> groups) {
        return new Group(leaves, groups);
    }

    public static Group createGroup(final Set<Leaf> leaves) {
        return new Group(leaves, Collections.emptySet());
    }

}
