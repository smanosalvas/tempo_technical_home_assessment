## Assumptions for Hierarchy filter implementation

1. **Empty hierarchy**: if the input hierarchy array is empty then the filter returns an empty array.
2. **Null hierarchy or predicate**: the method fails fast with a clear `NullPointerException` message when either the
   hierarchy or predicate is null.
3. **Node IDs and depths**: node IDs are assumed to be valid positive values, and depths are assumed to be non-negative
   values.
4. **Valid hierarchy structure**: the input hierarchy is assumed to be already valid and represented in depth-first
   traversal order. The filter method does not validate malformed hierarchies
5. **Depth progression**: a child node is assumed to have a depth exactly one level greater than its parent. The input
   should not skip levels, for example `0 -> 2`.
6. **Preserves order in the result** : the filtered hierarchy preserves the original traversal order.
7. **Duplicate element evaluation**: if there are any duplicate items they are treated as separate hierarchy entries. 