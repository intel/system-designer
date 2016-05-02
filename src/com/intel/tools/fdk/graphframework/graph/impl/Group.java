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

import java.util.Set;

import com.intel.tools.fdk.graphframework.graph.IGroup;
import com.intel.tools.fdk.graphframework.graph.INode;

/**
 * Represent a group {@link INode} which are part of a {@link Graph}.</br>
 */
public class Group extends NodeContainer implements IGroup {

    private NodeContainer parent;

    public Group(final Set<Leaf> leaves, final Set<Group> groups) {
        super(leaves, groups);
    }

    @Override
    public NodeContainer getParent() {
        return parent;
    }

    protected void setParent(final NodeContainer parent) {
        assert parent != this : "A node cannot be its own parent";
        if (this.parent != null) {
            this.parent.remove(this);
        }
        this.parent = parent;
    }

    @Override
    public void delete() {
        getExternalLinks().forEach(Link::delete);
        if (this.parent != null) {
            this.parent.remove(this);
        }
    }

}
