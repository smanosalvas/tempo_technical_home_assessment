package com.tempo.technical.interview;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HierarchyTest
{
        @Test
        void testFilter() {
            Hierarchy unfiltered = new ArrayBasedHierarchy(
                    new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11},
                    new int[]{0, 1, 2, 3, 1, 0, 1, 0, 1, 1, 2}
            );
            Hierarchy filteredActual = HierarchyFilter.filter(unfiltered, nodeId -> nodeId % 3 != 0);
            Hierarchy filteredExpected = new ArrayBasedHierarchy(
                    new int[]{1, 2, 5, 8, 10, 11},
                    new int[]{0, 1, 1, 0, 1, 2}
            );
            assertEquals(filteredExpected.formatString(), filteredActual.formatString());
        }

    @Test
    void testFilterForEvenNodeIds () {
        Hierarchy unfiltered = new ArrayBasedHierarchy(
                new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11},
                new int[]{0, 1, 2, 3, 1, 0, 1, 0, 1, 1, 2}
        );
        Hierarchy filteredActual = HierarchyFilter.filter(unfiltered, nodeId -> nodeId % 2 == 0);
        Hierarchy filteredExpected = new ArrayBasedHierarchy(
                new int[]{6, 8, 10},
                new int[]{0, 0, 1}
        );
        assertEquals(filteredExpected.formatString(), filteredActual.formatString());
    }

    @Test
    void testFilterWhenHierarchyIsEmpty () {
        Hierarchy unfiltered = new ArrayBasedHierarchy(
                new int[]{},
                new int[]{}
        );
        Hierarchy filteredActual = HierarchyFilter.filter(unfiltered, nodeId -> nodeId % 3 != 0);
        Hierarchy filteredExpected = new ArrayBasedHierarchy(
                new int[]{},
                new int[]{}
        );
        assertEquals(filteredExpected.formatString(), filteredActual.formatString());
    }

    @Test
    void testFilterAllNodesPasses() {
        Hierarchy unfiltered = new ArrayBasedHierarchy(
                new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11},
                new int[]{0, 1, 2, 3, 1, 0, 1, 0, 1, 1, 2}
        );
        Hierarchy filteredActual = HierarchyFilter.filter(unfiltered, nodeId -> nodeId % 1 == 0);
        Hierarchy filteredExpected = new ArrayBasedHierarchy(
                new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11},
                new int[]{0, 1, 2, 3, 1, 0, 1, 0, 1, 1, 2}
        );
        assertEquals(filteredExpected.formatString(), filteredActual.formatString());
    }

    @Test
    void testFilterWhenRootFails () {
        Hierarchy unfiltered = new ArrayBasedHierarchy(
                new int[]{1, 2, 4},
                new int[]{0, 1, 1}
        );
        Hierarchy filteredActual = HierarchyFilter.filter(unfiltered, nodeId -> nodeId % 2 == 0);
        Hierarchy filteredExpected = new ArrayBasedHierarchy(
                new int[]{},
                new int[]{}
        );
        assertEquals(filteredExpected.formatString(), filteredActual.formatString());
    }

    @Test
    void testFilterWhenParentFailsButGrandChildPasses () {
        Hierarchy unfiltered = new ArrayBasedHierarchy(
                new int[]{2, 3, 4, 6, 5, 8, 10, 11, 12, 14},
                new int[]{0, 1, 2, 3, 1, 2, 0, 1, 2, 3}
        );
        Hierarchy filteredActual = HierarchyFilter.filter(unfiltered, nodeId -> nodeId % 2 == 0);
        Hierarchy filteredExpected = new ArrayBasedHierarchy(
                new int[]{2, 10},
                new int[]{0, 0}
        );
        assertEquals(filteredExpected.formatString(), filteredActual.formatString());
    }

    @Test
    void testFilterWhenSiblingSurvivesAfterFailedBranch () {
        Hierarchy unfiltered = new ArrayBasedHierarchy(
                new int[]{2, 3, 4, 6, 8, 10, 12, 5, 14, 16},
                new int[]{0, 1, 2, 3, 1, 2, 2, 1, 2, 0}
        );
        Hierarchy filteredActual = HierarchyFilter.filter(unfiltered, nodeId -> nodeId % 2 == 0);
        Hierarchy filteredExpected = new ArrayBasedHierarchy(
                new int[]{2, 8, 10, 12, 16},
                new int[]{0, 1, 2, 2, 0}
        );
        assertEquals(filteredExpected.formatString(), filteredActual.formatString());
    }

    @Test
    void testFilterWhenMultipleRoots () {
        Hierarchy unfiltered = new ArrayBasedHierarchy(
                new int[]{1, 10, 11, 12, 20, 21, 9, 30, 31, 32},
                new int[]{0, 1, 2, 1, 0, 1, 0, 1, 2, 0}
        );
        Hierarchy filteredActual = HierarchyFilter.filter(unfiltered, nodeId -> nodeId >= 10 );
        Hierarchy filteredExpected = new ArrayBasedHierarchy(
                new int[]{20, 21, 32},
                new int[]{0, 1, 0}
        );
        assertEquals(filteredExpected.formatString(), filteredActual.formatString());
    }

    @Test
    void testFilterWhenOneMiddleNodeFails () {
        Hierarchy unfiltered = new ArrayBasedHierarchy(
                new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}
        );
        Hierarchy filteredActual = HierarchyFilter.filter(unfiltered, nodeId -> nodeId != 5 );
        Hierarchy filteredExpected = new ArrayBasedHierarchy(
                new int[]{1, 2, 3, 4},
                new int[]{0, 1, 2, 3}
        );
        assertEquals(filteredExpected.formatString(), filteredActual.formatString());
    }

    @Test
    void testFilterWhenAllNodesFail () {
        Hierarchy unfiltered = new ArrayBasedHierarchy(
                new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}
        );
        Hierarchy filteredActual = HierarchyFilter.filter(unfiltered, nodeId -> nodeId == 0 );
        Hierarchy filteredExpected = new ArrayBasedHierarchy(
                new int[]{},
                new int[]{}
        );
        assertEquals(filteredExpected.formatString(), filteredActual.formatString());
    }

    @Test
    void testFilterForSingleNodeHierarchyAndFails () {
        Hierarchy unfiltered = new ArrayBasedHierarchy(
                new int[]{1},
                new int[]{0}
        );
        Hierarchy filteredActual = HierarchyFilter.filter(unfiltered, nodeId -> nodeId != 1 );
        Hierarchy filteredExpected = new ArrayBasedHierarchy(
                new int[]{},
                new int[]{}
        );
        assertEquals(filteredExpected.formatString(), filteredActual.formatString());
    }

    @Test
    void testFilterForSingleNodeHierarchyAndPasses () {
        Hierarchy unfiltered = new ArrayBasedHierarchy(
                new int[]{1},
                new int[]{0}
        );
        Hierarchy filteredActual = HierarchyFilter.filter(unfiltered, nodeId -> nodeId == 1 );
        Hierarchy filteredExpected = new ArrayBasedHierarchy(
                new int[]{1},
                new int[]{0}
        );
        assertEquals(filteredExpected.formatString(), filteredActual.formatString());
    }

    @Test
    void testFilterThrowsNullExceptionWhenHierarchyIsNull () {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> HierarchyFilter.filter(null, nodeId -> true)
        );

        assertEquals("hierarchy must not be null", exception.getMessage());
    }

    @Test
    void testFilterThrowsNullExceptionWhenPredicateIsNull () {
        Hierarchy unfiltered = new ArrayBasedHierarchy(
                new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}
        );
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> HierarchyFilter.filter(unfiltered, null)
        );

        assertEquals("nodeIdPredicate must not be null", exception.getMessage());
    }

    @Test
    void shouldHandleVeryDeepHierarchyWithoutStackOverflow() {
        int size = 100_000;
        int[] nodeIds = new int[size];
        int[] depths = new int[size];

        for (int i = 0; i < size; i++) {
            nodeIds[i] = i + 1;
            depths[i] = i;
        }

        Hierarchy hierarchy = new ArrayBasedHierarchy(nodeIds, depths);

        Hierarchy result = HierarchyFilter.filter(hierarchy, nodeId -> true);

        assertEquals(size, result.size());
        assertEquals(1, result.nodeId(0));
        assertEquals(0, result.depth(0));
        assertEquals(size, result.nodeId(size - 1));
        assertEquals(size - 1, result.depth(size - 1));
    }
}
