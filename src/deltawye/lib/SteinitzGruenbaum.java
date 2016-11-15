package deltawye.lib;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Algorithm for reducing a 3-connected plane graph, based on Steinitz 1922 and
 * Grünbaum 1967.
 */
public class SteinitzGruenbaum implements GraphTransformationAlgorithm {

    /**
     * Algorithm parameter: Strategy for selecting the boundary triangle of a
     * minimal lens.
     */
    public enum LensTriangleSelectionStrategy {
        /**
         * Select a random boundary triangle.
         */
        RANDOM,

        /**
         * Select a boundary triangle that is part of a minimum number of
         * lenses.
         */
        MINLENSES,

        /**
         * Select a boundary triangle that is part of a maximal number of
         * lenses.
         */
        MAXLENSES,

        /**
         * Prefer boundary triangles that are adjacent to a lens pole.
         */
        PREFER_POLE,

        /**
         * Prefer boundary triangles that are not adjacent to a lens pole.
         */
        PREFER_NONPOLE
    }

    /**
     * The current reduction state of the plane graph.
     */
    private AtomicPlaneGraph graph;

    /**
     * The active strategy for selecting the boundary triangle.
     */
    private final LensTriangleSelectionStrategy strategy;

    /**
     * The original graph.
     */
    private final AtomicPlaneGraph originalGraph;

    /**
     * Size of the original graph. Used for measuring progress.
     */
    private final int originalSize;

    /**
     * Random number generator.
     */
    private final Random random;

    /**
     * String representation of an omega transformation.
     */
    private static final String OMEGA = "Omega";

    /**
     * String representation of an eta transformation.
     */
    private static final String ETA = "Eta";

    /**
     * Initialize reduction algorithm on the specified plane graph with the
     * specified strategy for boundary triangle selection.
     *
     * <p>
     * The input graph has to be 3-connected.
     *
     * @param graph
     *            the graph to be reduced
     * @param strategy
     *            the strategy for selecting the boundary triangle
     */
    public SteinitzGruenbaum(AtomicPlaneGraph graph,
            LensTriangleSelectionStrategy strategy) {
        this.graph = new AtomicPlaneGraph(graph);
        this.strategy = strategy;
        originalGraph = graph;
        originalSize = graph.size();
        random = new Random();
    }

    /**
     * Initialize reduction algorithm on the specified plane graph with default
     * parameter settings (random triangle selection).
     *
     * <p>
     * The input graph has to be 3-connected.
     *
     * @param graph
     *            the graph to be reduced
     */
    public SteinitzGruenbaum(AtomicPlaneGraph graph) {
        this(graph, LensTriangleSelectionStrategy.RANDOM);
    }

    @Override
    public boolean hasNextStep() {
        return !graph.isK4();
    }

    /**
     * Perform a single reduction step on the input graph using the algorithm by
     * Steinitz 1922 and Grünbaum 1967.
     *
     * @throws InvalidGraphTransformException
     *             if the graph is already K4
     * @return a description of the performed transformation
     */
    @Override
    public String nextStep() {
        final String transformation;
        if (graph.isK4()) {
            throw new InvalidGraphTransformException(
                    "K4 is not reducible using this algorithm.");
        }

        // Construct medial graph
        MedialGraph m = MedialGraph.fromAtomicPlaneGraph(graph);

        // Find minimal lenses
        TreeSet<Lens> lenses = m.lenses();
        int numberOfFacesInMinimalLens = lenses.first()
                                               .numberOfInnerFaces();
        Set<Lens> minimalLenses = lenses.stream()
                                        .filter(l -> l.numberOfInnerFaces() == numberOfFacesInMinimalLens)
                                        .collect(Collectors.toSet());

        // Select a boundary triangle in a minimal lens
        Circuit<MedialVertex> boundaryTriFace = selectBoundaryTriangle(minimalLenses);

        // Perform corresponding transformation in original graph
        if (m.isUnmedialVertex(boundaryTriFace)) {
            AtomicVertex v3 = m.toUnmedialVertex(boundaryTriFace);
            graph.reduceOmega(v3);
            transformation = OMEGA + " at " + v3;
        } else {
            Circuit<AtomicVertex> f3 = graph.getFace(m.toUnmedialFace(boundaryTriFace));
            graph.reduceEta(f3);
            transformation = ETA + " at " + f3;
        }
        return transformation;
    }

    /**
     * Return a boundary triangle selected from one of the specified lenses.
     *
     * @param lenses
     *            set of lenses
     * @return boundary triangle
     */
    private Circuit<MedialVertex> selectBoundaryTriangle(Set<Lens> lenses) {
        Circuit<MedialVertex> nextBoundaryTriangle;
        if (strategy == LensTriangleSelectionStrategy.RANDOM) {
            List<Circuit<MedialVertex>> allBoundaryTrianglesList = new ArrayList<>(
                    getAllBoundaryTriangles(lenses));
            int idx = random.nextInt(allBoundaryTrianglesList.size());
            nextBoundaryTriangle = allBoundaryTrianglesList.get(idx);
        } else if (strategy == LensTriangleSelectionStrategy.MAXLENSES
                || strategy == LensTriangleSelectionStrategy.MINLENSES) {
            // build map keyed by the number of containing lenses
            Map<Long, List<Circuit<MedialVertex>>> triangleMap;
            Stream<Circuit<MedialVertex>> triangleStream = new ArrayList<>(
                    getAllBoundaryTriangles(lenses)).stream();
            triangleMap = triangleStream.collect(
                    Collectors.groupingBy(f -> lenses.stream()
                                                     .filter(l -> l.boundaryFaces()
                                                                   .contains(f))
                                                     .count()));
            long key;
            if (strategy == LensTriangleSelectionStrategy.MAXLENSES) {
                key = Collections.max(triangleMap.keySet());
            } else { // MINLENSES
                key = Collections.min(triangleMap.keySet());
            }
            List<Circuit<MedialVertex>> relevantTriangles = triangleMap.get(key);
            // choose a random triangle among the fitting ones
            int idx = random.nextInt(relevantTriangles.size());
            nextBoundaryTriangle = relevantTriangles.get(idx);
        } else if (strategy == LensTriangleSelectionStrategy.PREFER_POLE
                || strategy == LensTriangleSelectionStrategy.PREFER_NONPOLE) {
            List<Circuit<MedialVertex>> polarTriangles = new ArrayList<>();
            List<Circuit<MedialVertex>> nonPolarTriangles = new ArrayList<>();
            List<Circuit<MedialVertex>> relevantTriangles = new ArrayList<>();
            for (Lens l : lenses) {
                for (Circuit<MedialVertex> f : l.boundaryFaces()) {
                    if (f.isTriangle()) {
                        if (l.isPolar(f)) {
                            polarTriangles.add(f);
                        } else {
                            nonPolarTriangles.add(f);
                        }
                    }
                }
            }
            if (strategy == LensTriangleSelectionStrategy.PREFER_POLE
                    && !polarTriangles.isEmpty()) {
                relevantTriangles = polarTriangles;
            } else if (!nonPolarTriangles.isEmpty()) {
                relevantTriangles = nonPolarTriangles;
            } else {
                relevantTriangles = polarTriangles;
            }
            // choose a random triangle among the fitting ones
            int idx = random.nextInt(relevantTriangles.size());
            nextBoundaryTriangle = relevantTriangles.get(idx);
        } else {
            throw new InvalidGraphTransformException(
                    "Unknown triangle selection strategy: " + strategy);
        }
        return nextBoundaryTriangle;
    }

    /**
     * Return the set of all boundary triangles of the specified lenses.
     *
     * @param lenses
     *            a set of lenses
     * @return the set of all boundary triangles
     */
    private Set<Circuit<MedialVertex>> getAllBoundaryTriangles(Set<Lens> lenses) {
        Set<Circuit<MedialVertex>> allBoundaryTriangles = new HashSet<>();
        for (Lens l : lenses) {
            for (Circuit<MedialVertex> f : l.boundaryFaces()) {
                if (f.isTriangle()) {
                    allBoundaryTriangles.add(f);
                }
            }
        }
        return allBoundaryTriangles;
    }

    /**
     * Perform a full reduction of the input graph to K4 using the algorithm by
     * Steinitz 1922 and Grünbaum 1967.
     *
     * @param verbose
     *            if true report each step to stdout
     * @return a description of the transformation sequence
     */
    @Override
    public List<String> run(boolean verbose) {
        String step;
        Stream.Builder<String> transformations = Stream.builder();
        while (hasNextStep()) {
            step = nextStep();
            transformations.add(step);
            if (verbose) {
                System.out.println(step);
            }
        }
        return transformations.build()
                              .collect(Collectors.toList());
    }

    @Override
    public int getProgress() {
        int targetSize = 6; // target is K4
        int currentSize = graph.size();
        if (originalSize <= targetSize || currentSize == targetSize) {
            return 100;
        } else {
            return (int) (100 - 100 * (currentSize - targetSize)
                    / (double) (originalSize - targetSize));
        }
    }

    @Override
    public void reset() {
        graph = new AtomicPlaneGraph(originalGraph);
    }

    @Override
    public String toString() {
        return String.join(System.getProperty("line.separator"),
                "Wye-Delta-Wye algorithm based on Steinitz 1922 and Grünbaum 1967,",
                "running on graph with " + graph.order() + " vertices and " + graph.size()
                        + " edges.",
                "Lens triangle selection strategy: " + strategy + ".");
    }

    @Override
    public int normalizedLength(List<String> sequence) {
        // All steps are degree 3 transformations.
        // Add one step for the final transition from K4 to K1.
        return sequence.size() + 1;
    }

    @Override
    public int deltaWyeCount(List<String> sequence) {
        return (int) sequence.stream()
                             .filter(t -> t.startsWith(ETA))
                             .count();
    }

    @Override
    public int wyeDeltaCount(List<String> sequence) {
        return (int) sequence.stream()
                             .filter(t -> t.startsWith(OMEGA))
                             .count();
    }

}
