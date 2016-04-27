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
