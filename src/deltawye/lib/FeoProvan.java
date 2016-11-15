package deltawye.lib;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Wye-Delta-Wye Reduction Algorithm based on Feo and Provan 1993.
 *
 */
public class FeoProvan implements GraphTransformationAlgorithm {

    /**
     * Algorithm parameter: Strategy for selecting the start vertex.
     */
    public enum StartVertexStrategy {
        /**
         * Select the minimum vertex (default).
         */
        MINIMUM,

        /**
         * Select the maximum vertex.
         */
        MAXIMUM,

        /**
         * Select a random vertex.
         */
        RANDOM
    }

    /**
     * Algorithm parameter: Strategy for selecting the next transformation.
     */
    public enum TransformSelectionStrategy {
        /**
         * Select a transformation with a minimum label.
         */
        MINLABEL,

        /**
         * Select a transformation with a maximum label (default).
         */
        MAXLABEL,

        /**
         * Select a transformation with a minimum degree, and prefer
         * vertex-based transformations to face-based transformations.
         *
         * <p>
         * This results in the following preference hierarchy: P2, P1, P3, P4,
         * P6, P5.
         */
        MINDEGREE,

        /**
         * Choose a transformation with a maximum degree, and prefer
         * vertex-based transformations to face-based transformations.
         *
         * <p>
         * This results in the following preference hierarchy: P6, P5, P3, P4,
         * P2, P1.
         */
        MAXDEGREE,

        /**
         * Prefer vertex-based transformations to face-based transformations,
         * and prefer transformations that involve elements with smaller
         * degrees.
         *
         * <p>
         * This results in the following preference hierarchy: P2, P3, P6, P1,
         * P4, P5.
         */
        VERTEXFIRST,

        /**
         * Prefer face-based transformations to vertex-based transformations,
         * and prefer transformations that involve elements with smaller
         * degrees.
         *
         * <p>
         * This results in the following preference hierarchy: P1, P4, P5, P2,
         * P3, P6.
         */
        FACEFIRST,

        /**
         * Select a random transformation.
         */
        RANDOM

    }

    /**
     * The current reduction state of the plane graph.
     */
    private AtomicPlaneGraph graph;

    /**
     * The active strategy for selecting the next transformation.
     */
    private final TransformSelectionStrategy tselStrategy;

    /**
     * The active strategy for selecting the start vertex.
     */
    private final StartVertexStrategy startVertexStrategy;

    /**
     * The start vertex for the edge labeling.
     */
    private AtomicVertex startVertex;

    /**
     * Edge labels for {@link FeoProvan#graph}.
     */
    private Map<UndirectedEdge<AtomicVertex>, Integer> edgeLabels;

    /**
     * The original graph.
     */
    private final AtomicPlaneGraph originalGraph;

    /**
     * Size of the original graph. Used for measuring progress.
     */
    private final int originalSize;

    /**
     * Flag for enabling or disabling the reporting of newly created edges.
     */
    private boolean reportNewEdges;

    /**
     * Random number generator.
     */
    private final Random random;

    /**
     * Initialize reduction algorithm on the specified plane graph using the
     * specified parameters.
     *
     * <p>
     * The input graph has to be connected.
     *
     * @param graph
     *            the graph to be reduced
     * @param startVertexStrategy
     *            the strategy for selecting the start vertex
     * @param tselStrategy
     *            the strategy for selecting the next transformation
     */
    public FeoProvan(AtomicPlaneGraph graph, StartVertexStrategy startVertexStrategy,
            TransformSelectionStrategy tselStrategy) {
        this.graph = new AtomicPlaneGraph(graph);
        this.startVertexStrategy = startVertexStrategy;
        this.tselStrategy = tselStrategy;
        originalGraph = graph;
        originalSize = graph.size();
        startVertex = getStartVertex(graph, startVertexStrategy);
        edgeLabels = computeEdgeLabels(graph, startVertex);
        random = new Random();
        reportNewEdges = false;
    }

    /**
     * Initialize reduction algorithm on the specified plane graph, using
     * default algorithm parameters (minimum start vertex, maxlabel
     * transformation selection).
     *
     * <p>
     * The input graph has to be connected.
     *
     * @param graph
     *            the graph to be reduced
     */
    public FeoProvan(AtomicPlaneGraph graph) {
        this(graph, StartVertexStrategy.MINIMUM, TransformSelectionStrategy.MAXLABEL);
    }

    /**
     * Return the edge labeling.
     *
     * @return edge labeling
     */
    public Map<UndirectedEdge<AtomicVertex>, Integer> getEdgeLabels() {
        return edgeLabels;
    }

    /**
     * Lookup the label of an edge tail.
     *
     * @param e
     *            edge tail to look up
     * @return label of the corresponding edge
     */
    private int lookupLabel(EdgeTail<AtomicVertex> e) {
        return edgeLabels.get(e.getEdge());
    }

    /**
     * Lookup the labels of the specified edges.
     *
     * @param edges
     *            the edges to look up
     * @return list of labels of the edges
     */
    private List<Integer> lookupLabels(Collection<UndirectedEdge<AtomicVertex>> edges) {
        return edges.stream()
                    .map(e -> edgeLabels.get(e))
                    .collect(Collectors.toList());
    }

    /**
     * Lookup the labels of the edges corresponding to the specified edge tails.
     *
     * @param edgeTails
     *            the edge tails to look up
     * @return list of labels of the corresponding edges
     */
    private List<Integer> lookupLabelsTails(
            Collection<EdgeTail<AtomicVertex>> edgeTails) {
        return edgeTails.stream()
                        .map(e -> edgeLabels.get(e.getEdge()))
                        .collect(Collectors.toList());
    }

    @Override
    public boolean hasNextStep() {
        return !graph.isK1();
    }

    @Override
    public String nextStep() {
        if (!hasNextStep()) {
            throw new InvalidGraphTransformException("K1 is not reducible.");
        }

        List<Transformation> possibleTransformations = new ArrayList<>();
        for (AtomicVertex v : graph.vertices()) {
            if (satisfiesP2(v)) {
                possibleTransformations.add(new R1V(v));
            } else if (satisfiesP3(v)) {
                possibleTransformations.add(new R2V(v));
            } else if (satisfiesP6(v)) {
                possibleTransformations.add(new R3V(v));
            }
        }
        for (Circuit<AtomicVertex> f : graph.faces()) {
            EdgeTail<AtomicVertex> e = f.edgeList()
                                        .getFirst();
            if (satisfiesP1(e)) {
                possibleTransformations.add(new R1F(e));
            } else if (satisfiesP4(e)) {
                possibleTransformations.add(new R2F(e));
            } else if (satisfiesP5(e)) {
                possibleTransformations.add(new R3F(e));
            }
        }

        if (possibleTransformations.isEmpty()) {
            System.err.println("ERROR: No transformation possible!");
            System.err.println("Dumping edge labels:");
            edgeLabels.forEach((k, v) -> System.err.println(" " + k + ": " + v));
            throw new InvalidGraphTransformException("No rule applicable!");
        }

        return selectNextTransformation(possibleTransformations).apply();
    }

    /**
     * Choose the next transformation from a list based on the active selection
     * strategy.
     *
     * @param possibleTransformations
     *            list of possible transformations
     * @return next transformation
     */
    private Transformation selectNextTransformation(
            List<Transformation> possibleTransformations) {
        Transformation nextTransformation;
        if (tselStrategy == TransformSelectionStrategy.RANDOM) {
            int idx = random.nextInt(possibleTransformations.size());
            nextTransformation = possibleTransformations.get(idx);
        } else if (tselStrategy == TransformSelectionStrategy.MINLABEL) {
            nextTransformation = Collections.min(possibleTransformations,
                    (a, b) -> Integer.compare(a.getMinLabel(), b.getMinLabel()));
        } else if (tselStrategy == TransformSelectionStrategy.MAXLABEL) {
            nextTransformation = Collections.max(possibleTransformations,
                    (a, b) -> Integer.compare(a.getMaxLabel(), b.getMaxLabel()));
        } else if (tselStrategy == TransformSelectionStrategy.MINDEGREE) {
            nextTransformation = Collections.min(possibleTransformations, (a, b) -> {
                int byDegree = Integer.compare(a.getDegree(), b.getDegree());
                if (byDegree != 0) {
                    return byDegree;
                }
                return a.getType()
                        .compareTo(b.getType());
            });
        } else if (tselStrategy == TransformSelectionStrategy.MAXDEGREE) {
            nextTransformation = Collections.max(possibleTransformations, (a, b) -> {
                int byDegree = Integer.compare(a.getDegree(), b.getDegree());
                if (byDegree != 0) {
                    return byDegree;
                }
                return b.getType()
                        .compareTo(a.getType());
            });
        } else if (tselStrategy == TransformSelectionStrategy.VERTEXFIRST) {
            nextTransformation = Collections.min(possibleTransformations, (a, b) -> {
                int byType = a.getType()
                              .compareTo(b.getType());
                if (byType != 0) {
                    return byType;
                }
                return Integer.compare(a.getDegree(), b.getDegree());
            });
        } else { // TransformSelectionStrategy.FACEFIRST
            nextTransformation = Collections.min(possibleTransformations, (a, b) -> {
                int byType = b.getType()
                              .compareTo(a.getType());
                if (byType != 0) {
                    return byType;
                }
                return Integer.compare(a.getDegree(), b.getDegree());
            });
        }
        return nextTransformation;
    }

    /**
     * Perform reduction algorithm.
     *
     * <p>
     * Return a list of descriptions of performed transformations.
     *
     * @param verbose
     *            if true report each step to stdout
     * @return list of performed transformations
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
        int targetSize = 0; // target is K1
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
        startVertex = getStartVertex(graph, startVertexStrategy);
        edgeLabels = computeEdgeLabels(graph, startVertex);
    }

    @Override
    public String toString() {
        return String.join(System.getProperty("line.separator"),
                "Wye-Delta-Wye algorithm based on Feo and Provan 1993,",
                "running on graph with " + graph.order() + " vertices and " + graph.size()
                        + " edges.",
                "Transformation selection strategy: " + tselStrategy,
                "Start vertex selection strategy: " + startVertexStrategy,
                "Start vertex: " + startVertex);
    }

    @Override
    public int normalizedLength(List<String> sequence) {
        int degreeThreeTransforms = 0;
        for (String step : sequence) {
            if (step.startsWith("P5")) {
                degreeThreeTransforms += 1;
            } else if (step.startsWith("P6")) {
                degreeThreeTransforms += 1;
            }
        }
        return degreeThreeTransforms;
    }

    @Override
    public int deltaWyeCount(List<String> sequence) {
        return (int) sequence.stream()
                             .filter(t -> t.startsWith("P5"))
                             .count();
    }

    @Override
    public int wyeDeltaCount(List<String> sequence) {
        return (int) sequence.stream()
                             .filter(t -> t.startsWith("P6"))
                             .count();
    }

    /**
     * Compute an edge labeling according to Feo and Provan 1993, using the
     * specified start vertex.
     *
     * @param g
     *            input graph
     * @param start
     *            the start vertex for the labeling
     * @return edge labeling
     */
    private static Map<UndirectedEdge<AtomicVertex>, Integer> computeEdgeLabels(
            AtomicPlaneGraph g, AtomicVertex start) {
        Map<UndirectedEdge<AtomicVertex>, Integer> edgeLabels = new HashMap<>();
        Map<AtomicVertex, Integer> vertexLabels = new HashMap<>();
        vertexLabels.put(start, 0);
        Set<AtomicVertex> boundary = Stream.of(start)
                                           .collect(Collectors.toSet());
        int level = 1;
        while (!boundary.isEmpty()) {
            Set<AtomicVertex> nextBoundary = new HashSet<>();
            for (AtomicVertex v : boundary) {
                for (EdgeTail<AtomicVertex> e : g.incidentEdgeTails(v)) {
                    edgeLabels.putIfAbsent(e.getEdge(), level);
                }
            }
            for (AtomicVertex v : boundary) {
                for (EdgeTail<AtomicVertex> e : g.incidentEdgeTails(v)) {
                    Circuit<AtomicVertex> face = g.getFace(e);
                    for (UndirectedEdge<AtomicVertex> fe : face.edgesUndirected()) {
                        edgeLabels.putIfAbsent(fe, level + 1);
                    }
                    for (AtomicVertex w : face.vertices()) {
                        if (!vertexLabels.containsKey(w)) {
                            vertexLabels.put(w, level + 1);
                            nextBoundary.add(w);
                        }
                    }
                }
            }
            boundary = nextBoundary;
            level += 2;
        }
        return edgeLabels;
    }

    /**
     * Determine the start vertex for the edge labeling.
     *
     * @param g
     *            input graph
     * @param strategy
     *            the strategy for selecting the start vertex
     * @return start vertex
     */
    private static AtomicVertex getStartVertex(AtomicPlaneGraph g,
            StartVertexStrategy strategy) {
        AtomicVertex start;
        if (strategy == StartVertexStrategy.MINIMUM) {
            start = Collections.min(g.vertices());
        } else if (strategy == StartVertexStrategy.MAXIMUM) {
            start = Collections.max(g.vertices());
        } else { // StartVertexStrategy.RANDOM
            List<AtomicVertex> vertices = new ArrayList<>(g.vertices());
            int idx = new Random().nextInt(vertices.size());
            start = vertices.get(idx);
        }
        return start;
    }

    /**
     * Return whether the positive transformation (P1) (empty loop removal) can
     * be applied at the specified edge tail (more precisely, its face).
     *
     * @param e
     *            edge tail (face) under consideration
     * @return true if P1 can be applied
     */
    private boolean satisfiesP1(EdgeTail<AtomicVertex> e) {
        return graph.isEmptyLoop(e);
    }

    /**
     * Return whether the positive transformation (P2) (leaf removal) can be
     * applied at the specified vertex.
     *
     * @param v
     *            vertex under consideration
     * @return true if P2 can be applied
     */
    private boolean satisfiesP2(AtomicVertex v) {
        return !startVertex.equals(v) && graph.degree(v) == 1;
    }

    /**
     * Return whether the positive transformation (P3) (series reduction) can be
     * applied at the specified vertex.
     *
     * @param v
     *            vertex under consideration
     * @return true if P3 can be applied
     */
    private boolean satisfiesP3(AtomicVertex v) {
        return !startVertex.equals(v) && graph.canReduceSeries(v)
                && validLabels2(graph.incidentEdges(v));
    }

    /**
     * Return whether the positive transformation (P4) (parallel reduction) can
     * be applied at the specified edge tail (more precisely, its face).
     *
     * @param e
     *            edge tail (face) under consideration
     * @return true if P4 can be applied
     */
    private boolean satisfiesP4(EdgeTail<AtomicVertex> e) {
        if (!graph.isEmptyDigon(e)) {
            return false;
        }
        return validLabels2(graph.getFace(e)
                                 .edgesUndirected());
    }

    /**
     * Return whether the positive transformation (P5) (delta-wye) can be
     * applied at the specified edge tail (more precisely, its face).
     *
     * @param e
     *            edge tail (face) under consideration
     * @return true if P5 can be applied
     */
    private boolean satisfiesP5(EdgeTail<AtomicVertex> e) {
        if (!graph.canReduceDeltaWye(e)) {
            return false;
        }
        return validLabels3(graph.getFace(e)
                                 .edgesUndirected());
    }

    /**
     * Return whether the positive transformation (P6) (wye-delta) can be
     * applied at the specified vertex.
     *
     * @param v
     *            vertex under consideration
     * @return true if P6 can be applied
     */
    private boolean satisfiesP6(AtomicVertex v) {
        if (!graph.canReduceWyeDelta(v)) {
            return false;
        }
        return !startVertex.equals(v) && validLabels3(graph.incidentEdges(v));
    }

    /**
     * Return whether the collection of edges has a correct labeling for a
     * two-element positive transformation, i.e. (P3) or (P4).
     *
     * <p>
     * Two edges have a correct labeling in this context if their integer labels
     * differ by at most one.
     *
     * @param edges
     *            collection of two edges
     * @return true if the labeling is correct
     */
    private boolean validLabels2(Collection<UndirectedEdge<AtomicVertex>> edges) {
        List<Integer> labels = lookupLabels(edges);
        int minLabel = Collections.min(labels);
        int maxLabel = Collections.max(labels);
        return maxLabel == minLabel || maxLabel == minLabel + 1;
    }

    /**
     * Return whether the collection of edges has a correct labeling for a
     * three-element positive transformation, i.e. (P5) or (P6).
     *
     * <p>
     * Three edges have a correct labeling in this context if their integer
     * labels differ by at most one and exactly one of the three edges has the
     * minimum label.
     *
     * @param edges
     *            collection of three edges
     * @return true if the labeling is correct
     */
    private boolean validLabels3(Collection<UndirectedEdge<AtomicVertex>> edges) {
        List<Integer> labels = lookupLabels(edges);
        int minLabel = Collections.min(labels);
        int maxLabel = Collections.max(labels);
        if (maxLabel != minLabel + 1) {
            return false;
        }
        // required occurrences: 1x minLabel, 2x maxLabel
        return labels.stream()
                     .filter(l -> l == maxLabel)
                     .count() == 2;
    }

    /**
     * Perform the positive transformation (P1) (empty loop removal).
     *
     * @param e
     *            edge tail (face)
     * @return description of the transformation
     */
    private String performP1(EdgeTail<AtomicVertex> e) {
        UndirectedEdge<AtomicVertex> edge = e.getEdge();
        graph.removeEdge(edge);
        edgeLabels.remove(edge);
        return "P1 at " + edge;
    }

    /**
     * Perform the positive transformation (P2) (leaf removal).
     *
     * @param v
     *            vertex
     * @return description of the transformation
     */
    private String performP2(AtomicVertex v) {
        UndirectedEdge<AtomicVertex> pendantEdge = graph.incidentEdges(v)
                                                        .getFirst();
        graph.removeVertex(v);
        edgeLabels.remove(pendantEdge);
        return "P2 at " + v;
    }

    /**
     * Perform the positive transformation (P3) (series reduction).
     *
     * @param v
     *            vertex
     * @return description of the transformation
     */
    private String performP3(AtomicVertex v) {
        String description = "P3 at " + v;
        List<UndirectedEdge<AtomicVertex>> oldEdges = graph.incidentEdges(v);
        List<Integer> labels = lookupLabels(oldEdges);
        int minLabel = Collections.min(labels);
        UndirectedEdge<AtomicVertex> newEdge = graph.reduceSeries(v);
        oldEdges.stream()
                .forEach(e -> edgeLabels.remove(e));
        edgeLabels.put(newEdge, minLabel);
        if (reportNewEdges) {
            description += " with new edge: " + newEdge;
        }
        return description;
    }

    /**
     * Perform the positive transformation (P4) (parallel reduction).
     *
     * @param e
     *            edge tail (face)
     * @return description of the transformation
     */
    private String performP4(EdgeTail<AtomicVertex> e) {
        List<EdgeTail<AtomicVertex>> oldEdges = graph.getFace(e)
                                                     .edgeList();
        List<Integer> labels = lookupLabelsTails(oldEdges);
        int maxLabel = Collections.max(labels);
        // remove the parallel edge with the larger label
        UndirectedEdge<AtomicVertex> edge = oldEdges.stream()
                                                    .filter(et -> edgeLabels.get(
                                                            et.getEdge()) == maxLabel)
                                                    .findAny()
                                                    .get()
                                                    .getEdge();
        graph.removeEdge(edge);
        edgeLabels.remove(edge);
        return "P4 at " + edge;
    }

    /**
     * Perform the positive transformation (P5) (delta-wye).
     *
     * @param e
     *            edge tail (face)
     * @return description of the transformation
     */
    private String performP5(EdgeTail<AtomicVertex> e) {
        Circuit<AtomicVertex> face = graph.getFace(e);
        String description = "P5 at " + face;
        List<EdgeTail<AtomicVertex>> oldEdges = face.edgeList();
        List<Integer> labels = lookupLabelsTails(oldEdges);
        int maxLabel = Collections.max(labels);
        // find the vertex shared by the two edges with the larger label
        Iterator<UndirectedEdge<AtomicVertex>> legs = oldEdges.stream()
                                                              .map(et -> et.getEdge())
                                                              .filter(et -> edgeLabels.get(
                                                                      et) == maxLabel)
                                                              .iterator();
        UndirectedEdge<AtomicVertex> leg0 = legs.next();
        UndirectedEdge<AtomicVertex> leg1 = legs.next();
        AtomicVertex top = leg0.getSharedVertex(leg1);
        AtomicVertex wye = graph.reduceDeltaWye(face);
        // update edge labels
        oldEdges.stream()
                .forEach(oldEdge -> edgeLabels.remove(oldEdge.getEdge()));
        for (UndirectedEdge<AtomicVertex> newEdge : graph.incidentEdges(wye)) {
            if (newEdge.isIncidentTo(top)) {
                edgeLabels.put(newEdge, maxLabel);
            } else {
                edgeLabels.put(newEdge, maxLabel - 1); // minLabel
            }
        }
        if (reportNewEdges) {
            description += " with new edges: " + graph.incidentEdges(wye);
        }
        return description;
    }

    /**
     * Perform the positive transformation (P6) (wye-delta).
     *
     * @param v
     *            vertex
     * @return description of the transformation
     */
    private String performP6(AtomicVertex v) {
        String description = "P6 at " + v;
        // find the unique edge with the minimum label
        List<UndirectedEdge<AtomicVertex>> oldEdges = graph.incidentEdges(v);
        List<Integer> labels = lookupLabels(oldEdges);
        int minLabel = Collections.min(labels);
        UndirectedEdge<AtomicVertex> minEdge = oldEdges.stream()
                                                       .filter(e -> edgeLabels.get(
                                                               e) == minLabel)
                                                       .findAny()
                                                       .get();
        AtomicVertex top = minEdge.traverseFrom(v);
        Set<UndirectedEdge<AtomicVertex>> newEdges = graph.reduceWyeDelta(v);
        // update edge labels
        oldEdges.stream()
                .forEach(oldEdge -> edgeLabels.remove(oldEdge));
        for (UndirectedEdge<AtomicVertex> newEdge : newEdges) {
            if (newEdge.isIncidentTo(top)) {
                edgeLabels.put(newEdge, minLabel);
            } else {
                edgeLabels.put(newEdge, minLabel + 1); // maxLabel
            }
        }
        if (reportNewEdges) {
            description += " with new edges: " + newEdges;
        }
        return description;
    }

    /**
     * Type of a transformation.
     */
    private enum TransformationType {

        /**
         * Vertex-based transformation.
         */
        VERTEX,

        /**
         * Face-based transformation.
         */
        FACE
    }

    /**
     * A graph transformation.
     */
    private abstract class Transformation {

        /**
         * Return the type of this transformation.
         *
         * @return transformation type
         */
        public abstract TransformationType getType();

        /**
         * Return the degree of this transformation.
         *
         * @return transformation degree
         */
        public abstract int getDegree();

        /**
         * Perform this transformation.
         *
         * @return description of the transformation
         */
        public abstract String apply();

        /**
         * Get the minimum label of any involved element.
         *
         * @return minimum label
         */
        public abstract int getMinLabel();

        /**
         * Get the maximum label of any involved element.
         *
         * @return maximum label
         */
        public abstract int getMaxLabel();
    }

    /**
     * A transformation that is applied at some vertex.
     */
    private abstract class VertexTransformation extends Transformation {

        /**
         * The identifying vertex.
         */
        private AtomicVertex vertex;

        /**
         * Create transformation at some vertex.
         *
         * @param vertex
         *            the identifying vertex
         */
        public VertexTransformation(AtomicVertex vertex) {
            this.vertex = vertex;
        }

        @Override
        public TransformationType getType() {
            return TransformationType.VERTEX;
        }

        /**
         * Return the identifying vertex.
         *
         * @return the identifying vertex
         */
        public AtomicVertex getVertex() {
            return vertex;
        }

        /**
         * Get a stream of the labels of all incident edges.
         *
         * @return labels
         */
        public IntStream getLabels() {
            return graph.incidentEdgeTails(vertex)
                        .stream()
                        .mapToInt(FeoProvan.this::lookupLabel);
        }

        @Override
        public int getMinLabel() {
            return getLabels().min()
                              .getAsInt();
        }

        @Override
        public int getMaxLabel() {
            return getLabels().max()
                              .getAsInt();
        }
    }

    /**
     * A transformation that is applied at some face.
     *
     * <p>
     * The face is identified by a contained edge tail.
     */
    private abstract class FaceTransformation extends Transformation {

        /**
         * The identifying edge tail.
         */
        private EdgeTail<AtomicVertex> edgeTail;

        /**
         * Create face transformation at specified edge tail.
         *
         * @param edgeTail
         *            identifying edge tail
         */
        public FaceTransformation(EdgeTail<AtomicVertex> edgeTail) {
            this.edgeTail = edgeTail;
        }

        /**
         * Return the identifying edge tail.
         *
         * @return the identifying edge tail
         */
        public EdgeTail<AtomicVertex> getEdgeTail() {
            return edgeTail;
        }

        @Override
        public TransformationType getType() {
            return TransformationType.FACE;
        }

        /**
         * Get a stream of the labels of all face edges.
         *
         * @return labels
         */
        public IntStream getLabels() {
            return graph.getFace(edgeTail)
                        .edgeList()
                        .stream()
                        .mapToInt(FeoProvan.this::lookupLabel);
        }

        @Override
        public int getMinLabel() {
            return getLabels().min()
                              .getAsInt();
        }

        @Override
        public int getMaxLabel() {
            return getLabels().max()
                              .getAsInt();
        }
    }

    /**
     * R1V transformation: leaf reduction.
     */
    private class R1V extends VertexTransformation {

        /**
         * Create R1V transformation at specified leaf vertex.
         *
         * @param vertex
         *            leaf vertex
         */
        public R1V(AtomicVertex vertex) {
            super(vertex);
        }

        @Override
        public int getDegree() {
            return 1;
        }

        @Override
        public String apply() {
            return performP2(getVertex());
        }
    }

    /**
     * R2V transformation: series reduction.
     */
    private class R2V extends VertexTransformation {

        /**
         * Create R2V transformation at specified series vertex.
         *
         * @param vertex
         *            series vertex
         */
        public R2V(AtomicVertex vertex) {
            super(vertex);
        }

        @Override
        public int getDegree() {
            return 2;
        }

        @Override
        public String apply() {
            return performP3(getVertex());
        }
    }

    /**
     * R3V transformation: Wye-Delta transformation.
     */
    private class R3V extends VertexTransformation {

        /**
         * Create R3V transformation at specified wye vertex.
         *
         * @param vertex
         *            wye vertex
         */
        public R3V(AtomicVertex vertex) {
            super(vertex);
        }

        @Override
        public int getDegree() {
            return 3;
        }

        @Override
        public String apply() {
            return performP6(getVertex());
        }
    }

    /**
     * R1F transformation: loop reduction.
     */
    private class R1F extends FaceTransformation {

        /**
         * Create R1F transformation at specified loop edge.
         *
         * @param edgeTail
         *            loop edge
         */
        public R1F(EdgeTail<AtomicVertex> edgeTail) {
            super(edgeTail);
        }

        @Override
        public int getDegree() {
            return 1;
        }

        @Override
        public String apply() {
            return performP1(getEdgeTail());
        }
    }

    /**
     * R2F transformation: parallel reduction.
     */
    private class R2F extends FaceTransformation {

        /**
         * Create R2F transformation at specified digon face.
         *
         * @param edgeTail
         *            digon face
         */
        public R2F(EdgeTail<AtomicVertex> edgeTail) {
            super(edgeTail);
        }

        @Override
        public int getDegree() {
            return 2;
        }

        @Override
        public String apply() {
            return performP4(getEdgeTail());
        }
    }

    /**
     * R3F transformation: Delta-Wye transformation.
     */
    private class R3F extends FaceTransformation {

        /**
         * Create R3F transformation at specified triangular face.
         *
         * @param edgeTail
         *            triangular face
         */
        public R3F(EdgeTail<AtomicVertex> edgeTail) {
            super(edgeTail);
        }

        @Override
        public int getDegree() {
            return 3;
        }

        @Override
        public String apply() {
            return performP5(getEdgeTail());
        }
    }

}
