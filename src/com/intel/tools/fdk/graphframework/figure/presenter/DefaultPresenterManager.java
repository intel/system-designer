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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.intel.tools.fdk.graphframework.graph.GraphHelper;
import com.intel.tools.fdk.graphframework.graph.IGroup;
import com.intel.tools.fdk.graphframework.graph.ILeaf;
import com.intel.tools.fdk.graphframework.graph.INode;

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
        if (groupToPresenterMap.containsKey(group)) {
            return groupToPresenterMap.get(group);
        } else {
            final List<Presenter<? extends INode>> presenterList = GraphHelper.getAllLeaves(group).stream()
                    .map(this::getPresenter).collect(Collectors.toList());

            groupToPresenterMap.put(group, new GroupPresenter(group, presenterList));
            return groupToPresenterMap.get(group);
        }
    }

}
