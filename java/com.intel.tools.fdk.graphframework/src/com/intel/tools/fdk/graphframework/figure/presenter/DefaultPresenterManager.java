/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.tools.fdk.graphframework.figure.presenter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.intel.tools.fdk.graphframework.graph.IGroup;
import com.intel.tools.fdk.graphframework.graph.ILeaf;
import com.intel.tools.fdk.graphframework.graph.INode;
import com.intel.tools.fdk.graphframework.graph.INodeContainer.INodeContainerListener;

/**
 * This is the default implementation for a presenter manager. It is used by the LayoutGenerator if none is passed for
 * construction.
 */
public class DefaultPresenterManager implements IPresenterManager {

    private final Map<ILeaf, LeafPresenter> leafToPresenterMap = new HashMap<>();
    private final Map<IGroup, GroupPresenter> groupToPresenterMap = new HashMap<>();

    @Override
    public LeafPresenter getPresenter(final ILeaf leaf) {
        return leafToPresenterMap.computeIfAbsent(leaf, leaf2 -> new LeafPresenter(leaf2));
    }

    @Override
    public GroupPresenter getPresenter(final IGroup group) {
        return groupToPresenterMap.computeIfAbsent(group, this::setupGroupPresenter);
    }

    private GroupPresenter setupGroupPresenter(final IGroup group) {
        final Set<Presenter<? extends INode>> presenterList = new HashSet<>();
        presenterList.addAll(group.getLeaves().stream().map(this::getPresenter).collect(Collectors.toSet()));
        presenterList.addAll(group.getGroups().stream().map(this::getPresenter).collect(Collectors.toSet()));

        final GroupPresenter presenter = new GroupPresenter(group, presenterList);
        group.addListener(new INodeContainerListener() {

            @Override
            public void leafAdded(final ILeaf addedLeaf) {
                presenter.add(getPresenter(addedLeaf));
            }

            @Override
            public void groupAdded(final IGroup addedGroup) {
                presenter.add(getPresenter(addedGroup));
            }

            @Override
            public void leafRemoved(final ILeaf removedLeaf) {
                presenter.remove(getPresenter(removedLeaf));
            }

            @Override
            public void groupRemoved(final IGroup removedGroup) {
                presenter.remove(getPresenter(removedGroup));
            }

        });
        return presenter;
    }

}
