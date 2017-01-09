/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.tools.fdk.graphframework.graph.impl;

import java.util.Set;

import com.intel.tools.fdk.graphframework.graph.IGroup;
import com.intel.tools.fdk.graphframework.graph.INode;
import com.intel.tools.utils.IntelPalette;

/**
 * Represent a group {@link INode} which are part of a {@link Graph}.</br>
 */
public class Group extends NodeContainer implements IGroup, Comparable<Group> {

    /** Instance counter used to generate instanceId, this is required to handle Group ordered Set */
    private static int instanceCounter = 0;

    private final int id;

    private NodeContainer parent;

    public Group(final Set<Leaf> leaves, final Set<Group> groups) {
        super(leaves, groups);
        this.id = instanceCounter++;

        getStyle().setBackground(IntelPalette.GREY_1);
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

    @Override
    public int compareTo(final Group group) {
        return this.id - group.id;
    }

}
