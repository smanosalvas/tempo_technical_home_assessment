package com.tempo.technical.interview;


import java.util.Arrays;
import java.util.Objects;

/**
 * A node is present in the filtered hierarchy iff its node ID passes the predicate and all of its ancestors pass it as well.
 */
class HierarchyFilter {
    public static Hierarchy filter(Hierarchy hierarchy, java.util.function.IntPredicate nodeIdPredicate) {
        Objects.requireNonNull(hierarchy, "hierarchy must not be null");
        Objects.requireNonNull(nodeIdPredicate, "nodeIdPredicate must not be null");

        int hierarchySize = hierarchy.size();
        int[] filteredNodeIds = new int[hierarchySize];
        int[] filteredDepths = new int[hierarchySize];

        int currentFilterIndex = 0;
        boolean[] pathAcceptedByDepth = new boolean[hierarchySize];

        for (int nodeIndex = 0; nodeIndex < hierarchySize; nodeIndex++)
        {
            int currentNodeId = hierarchy.nodeId(nodeIndex);
            int currentDepth = hierarchy.depth(nodeIndex);

            boolean isMatch = nodeIdPredicate.test(currentNodeId);

            if ( currentDepth == 0 ) {
                pathAcceptedByDepth[currentDepth] = isMatch;
            }
            else if (!pathAcceptedByDepth[currentDepth - 1]) {
                pathAcceptedByDepth[currentDepth] = false;
            }
            else {
                pathAcceptedByDepth[currentDepth] = isMatch && pathAcceptedByDepth[ currentDepth - 1 ];
            }

            if (pathAcceptedByDepth[currentDepth]) {
                filteredNodeIds[currentFilterIndex] = currentNodeId;
                filteredDepths[currentFilterIndex] = currentDepth;

                currentFilterIndex++;
            }

        }

        return new ArrayBasedHierarchy(Arrays.copyOf(filteredNodeIds, currentFilterIndex), Arrays.copyOf(filteredDepths, currentFilterIndex));
    }
}