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
package com.intel.tools.fdk.graphframework.layout;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.draw2d.geometry.Point;

import com.intel.tools.fdk.graphframework.graph.Graph;
import com.intel.tools.fdk.graphframework.graph.GraphException;
import com.intel.tools.fdk.graphframework.graph.Leaf;
import com.intel.tools.fdk.graphframework.graph.Link;

/**
 * Basic graph drawing algorithm implementation for graph
 *
 * The algorithm is adapted from the dominance drawing technique called Left/Right numbering method More info:
 * http://graphdrawing.org/literature/gd-constraints.pdf -> p54
 */
public class AutoLayoutComputer {

    /** Browsed inputs */
    private final List<Link> visitedLinks = new ArrayList<>();
    private final Map<Leaf, Integer> abscisses = new HashMap<>();
    private final Map<Leaf, Integer> ordinates = new HashMap<>();

    /** Current coordinate */
    private int currentCoordinate = 0;

    public AutoLayoutComputer(final Graph graph) {
        // Find all component which are sources
        final Set<Leaf> sources = graph.getAllLeaves().stream()
                .filter(this::isSourceInstance).collect(Collectors.toSet());

        sources.forEach(node -> {
            // Simulate the fact that all sources come from the same point
            abscisses.put(node, ++currentCoordinate);
            leftNumbering(node);
        });

        // Reset algorithm
        visitedLinks.clear();
        currentCoordinate = 0;

        sources.stream().collect(Collectors.toCollection(ArrayDeque::new))
                .descendingIterator()
                .forEachRemaining(node -> {
                    ordinates.put(node, ++currentCoordinate);
                    rightNumbering(node);
                });
    }

    /**
     * Retrieve coordinates of a raw instance Coordinate returned are "raw" they symbolize the position of the instance
     * on a grid of NxN (with N the number of instances in the algorithm)
     *
     * @param node
     *            the instance we want the coordinates
     * @return a point containing instance coordinate
     * @throws GraphException
     *             if the given node has not been computed by the algorithm
     */
    public Point getCoordinate(final Leaf node) throws GraphException {
        if (abscisses.containsKey(node) && ordinates.containsKey(node)) {
            return new Point(abscisses.get(node) + ordinates.get(node), ordinates.get(node) - abscisses.get(node));
        } else {
            throw new GraphException("Node does not have coordinates");
        }
    }

    /**
     * Check if a node is a source, i.e: if it has no input, or if none of its input is linked
     *
     * @param instance
     *            the instance to check
     * @return true if the instance is a source, else otherwise
     */
    private boolean isSourceInstance(final Leaf node) {
        // If there is no inputs, or if none is connected, then it can be a source
        return node.getLinkedInputLinks().isEmpty();
    }

    /**
     * Check if some linked input of a node has not been browsed
     *
     * @param linked
     *            the node to check
     * @return true if some inputs have not been visited, false otherwise
     */
    private boolean hasUnvisitedLinkedInput(final Leaf linked) {
        return linked.getLinkedInputLinks().stream().anyMatch(link -> !visitedLinks.contains(link));
    }

    private void leftNumbering(final Leaf origin) {
        origin.getLinkedOutputLinks().forEach(link -> {
            visitedLinks.add(link);
            final Leaf linked = link.getInputNode();
            if (!hasUnvisitedLinkedInput(linked)) {
                // Last link to the node let's check it
                abscisses.put(linked, ++currentCoordinate);
                leftNumbering(linked);
            }
        });
    }

    private void rightNumbering(final Leaf origin) {
        origin.getLinkedOutputLinks().stream()
                .collect(Collectors.toCollection(ArrayDeque::new))
                .descendingIterator()
                .forEachRemaining(link -> {
                    visitedLinks.add(link);
                    final Leaf linked = link.getInputNode();
                    // Check that linked instance has no unvisited linked link
                    if (!hasUnvisitedLinkedInput(linked)) {
                        // Last link to the node let's check it
                        ordinates.put(linked, ++currentCoordinate);
                        rightNumbering(linked);
                    }
                });
    }

}
