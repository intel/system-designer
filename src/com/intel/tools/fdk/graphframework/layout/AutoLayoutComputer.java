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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.draw2d.geometry.Point;

import com.intel.tools.fdk.graphframework.graph.impl.Leaf;
import com.intel.tools.fdk.graphframework.graph.impl.Link;
import com.intel.tools.fdk.graphframework.graph.impl.NodeContainer;

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

    /** Usable coordinates */
    private final Map<Leaf, Point> coordinates;

    /** Current coordinate */
    private int currentCoordinate = 0;

    public AutoLayoutComputer(final NodeContainer graph) {
        // Find all component which are sources
        final SortedSet<Leaf> sources = new TreeSet<>(graph.getAllLeaves().stream()
                .filter(this::isSourceInstance).collect(Collectors.toSet()));

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

        // Rotate coordinates to be usable
        this.coordinates = abscisses.keySet().stream()
                .collect(Collectors.toMap(Function.identity(), this::getCoordinate));

        // Make Y be always positive (translate)
        final int minY = this.coordinates.values().stream().mapToInt(point -> point.y).min().orElse(0);
        if (minY < 0) {
            this.coordinates.values().forEach(p -> p.y -= minY);
        }

        removeCoordinatesEmptyLines();
    }

    /**
     * Retrieve coordinates of a raw instance Coordinate returned are "raw" they symbolize the position of the instance
     * on a grid of NxN (with N the number of instances in the algorithm)
     *
     * @return a map associating a leaf node to its coordinates
     */
    public Map<Leaf, Point> getCoordinates() {
        return Collections.unmodifiableMap(coordinates);
    }

    /**
     * Rotate node coordinates and store them in a {@link Point} object
     *
     * @param node
     *            the node the get the coordinates from
     * @return rotated coordinates
     */
    private Point getCoordinate(final Leaf node) {
        final int x = ((abscisses.get(node) + ordinates.get(node)) / 2) - 1;
        final int y = ((ordinates.get(node) - abscisses.get(node)) / 2) - 1;
        return new Point(x, y);
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
            final Leaf linked = link.getInput().getLeaf();
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
                    final Leaf linked = link.getInput().getLeaf();
                    // Check that linked instance has no unvisited linked link
                    if (!hasUnvisitedLinkedInput(linked)) {
                        // Last link to the node let's check it
                        ordinates.put(linked, ++currentCoordinate);
                        rightNumbering(linked);
                    }
                });
    }

    /**
     * Modify calculated coordinates to remove empty lines.
     *
     * Depending on the number of analyzed nodes, the graph layout can be exploded a lot. Removing empty lines enhanced
     * the layout.
     */
    private void removeCoordinatesEmptyLines() {
        int line = 0;
        do {
            final int currentLine = line;
            if (!this.coordinates.values().stream().mapToInt(point -> point.y).anyMatch(y -> y == currentLine)) {
                // No node on this line, let's remove it
                this.coordinates.values().forEach(point -> {
                    if (point.y > currentLine) {
                        point.translate(0, -1);
                    }
                });
            } else {
                line++;
            }
        } while (line <= this.coordinates.values().stream().mapToInt(point -> point.y).max().orElse(-1));
    }

}
