# Tempo technical home assessment

This repo contains the proposed solution for the exercise in this Gist https://gist.github.com/hdamoo/d2a6c4026ebe0deaf7b4ceaa3bc5c6c1#submission 

## Project structure

```textmate
docs/
    -> Hierarchy.md => Contains the assumptions related to the hierarchy filter implementation. 
    -> SimpleCache.md => Contains the tasks 2 output which is the code review of the SimpleCache class. 

src/
    main/
        java/
            com.tempo.technical.interview
                ArrayBasedHierarchy.java -> original implementation from the Gist file. 
                Hierarchy.java -> Original code of the hierarchy interface. 
                HierarchyFilter -> proposed solution for the given exercise. 
    test/
        java/
            com.tempo.technical.interview
                HierarchyTest -> Contains multiple tests and edge cases to test the filter implementation. 

TECHNICAL_SOURCE.md -> Took from the original Gist and contains the general instructions for the exercise. 

```

## Execute this project 

### Pre-requirements 
1. Java 21
2. Maven 

### Execution

1. Download the project by `git clone git@github.com:smanosalvas/tempo_technical_home_assessment.git`
2. Run tests `mvn clean test` 
3. All tests must pass. 

## Trade-offs 

1. Uses primitive arrays and trims at the end for performance and simplicity, accepting temporary over-allocation. The current algorithm uses O(n) time and O(n) additional memory.
2. Uses an iterative scan instead of recursion because the input is flat and DFS-ordered.
3. Null inputs are rejected explicitly using `Objects.requireNonNull(...)` to fail fast with clear error messages.