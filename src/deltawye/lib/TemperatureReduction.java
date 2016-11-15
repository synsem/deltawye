package deltawye.lib;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Wye-Delta-Wye Reduction Algorithm using temperature-controlled randomized
 * search.
 *
 * <p>
 * This algorithm utilizes the omega and eta transformations familiar from
 * {@link SteinitzGruenbaum}, but uses a randomized search to find the next step
 * in the transformation sequence. In order to avoid cyclic transformation
 * subsequences, a concept of vertex and edge hotness (temperature) is used.
 */
public class TemperatureReduction implements GraphTransformationAlgorithm {

    /**
     * Strategy for choosing among edge-reducing transformations.
     */
    public enum Strategy {
        /**
         * Choose a random transformation.
         */
        RANDOM,
        /**
         * Prefer transformations that maximize edge reduction, typically
         * leading to shorter reduction sequences.
         */
        SHORT,
        /**
         * Prefer transformations that minimize (positive) edge reduction,
         * typically leading to longer reduction sequences.
         */
        LONG
    }

    /**
     * The current reduction state of the plane graph.
     */
    private AtomicPlaneGraph graph;

    /**
     * The original graph.
     */
    private final AtomicPlaneGraph originalGraph;

    /**
     * Size of the original graph. Used for measuring progress.
     */
    private final int originalSize;

    /**
     * Hotness of vertices.
     */
    private Map<AtomicVertex, Integer> vertexHotness;

    /**
     * Hotness of edges.
     */
    private Map<UndirectedEdge<AtomicVertex>, Integer> edgeHotness;

    /**
     * The active strategy for choosing among edge-reducing transformations.
     */
    private final Strategy strategy;

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
     * Initialize reduction algorithm on the specified plane graph.
     *
     * @param graph
     *            the graph to be reduced
     * @param strategy
     *            the strategy for choosing among edge-reducing transformations
     */
    public TemperatureReduction(AtomicPlaneGraph graph, Strategy strategy) {
        this.strategy = strategy;
        this.graph = new AtomicPlaneGraph(graph);
        originalGraph = graph;
        originalSize = graph.size();
        vertexHotness = new HashMap<>();
        edgeHotness = new HashMap<>();
        random = new Random();
    }

    /**
     * Initialize reduction algorithm on the specified plane graph with default
     * strategy (prefer short sequences).
     *
     * @param graph
     *            the graph to be reduced
     */
    public TemperatureReduction(AtomicPlaneGraph graph) {
        this(graph, Strategy.SHORT);
    }

    @Override
    public boolean hasNextStep() {
        return !graph.isK4();
    }

    @Override
    public String nextStep() {
        String transformation;
        List<Transformation> bestTransformations;
        // find possible transformations
        List<Transformation> positiveTransformations = new ArrayList<>();
        List<Transformation> neutralTransformations = new ArrayList<>();
        for (AtomicVertex v : graph.vertices()) {
            if (graph.canReduceWyeDelta(v)) {
                int reductionValue = (int) graph.incidentEdgeTails(v)
                                                .stream()
                                                .filter(e -> graph.isEmptyTriangle(e))
                                                .count();
                Transformation omega = new OmegaTransformation(v, reductionValue);
                if (reductionValue > 0) {
                    positiveTransformations.add(omega);
                } else {
                    neutralTransformations.add(omega);
                }
            }
        }
        for (Circuit<AtomicVertex> f : graph.faces()) {
            if (graph.canReduceDeltaWye(f)) {
                int reductionValue = (int) f.vertices()
                                            .stream()
                                            .filter(v -> graph.isWye(v))
                                            .count();
                Transformation eta = new EtaTransformation(f, reductionValue);
                if (reductionValue > 0) {
                    positiveTransformations.add(eta);
                } else {
                    neutralTransformations.add(eta);
                }
            }
        }

        // first look for positive eta or omega transform
        if (!positiveTransformations.isEmpty()) {
            Map<Integer, List<Transformation>> byValue;
            byValue = positiveTransformations.stream()
                                             .collect(Collectors.groupingBy(
                                                     t -> t.getReductionValue()));
            if (strategy == Strategy.RANDOM) {
                bestTransformations = positiveTransformations;
            } else {
                int key;
                if (strategy == Strategy.SHORT) {
                    key = Collections.max(byValue.keySet());
                } else { // LONG
                    key = Collections.min(byValue.keySet());
                }
                bestTransformations = byValue.get(key);
            }

        } else if (!neutralTransformations.isEmpty()) {
            // apply random neutral eta or omega transform to cold element
            Map<Integer, List<Transformation>> byTemperature;
            byTemperature = neutralTransformations.stream()
                                                  .collect(Collectors.groupingBy(
                                                          t -> t.getTemperature()));
            int key = Collections.min(byTemperature.keySet());
            bestTransformations = byTemperature.get(key);

        } else {
            throw new InvalidGraphTransformException("No rule applicable!");
        }

        // choose a random transformation among the best ones
        int idx = random.nextInt(bestTransformations.size());
        transformation = bestTransformations.get(idx)
                                            .apply();
        return transformation;
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
    public String toString() {
        return String.join(System.getProperty("line.separator"),
                "Randomized Wye-Delta-Wye algorithm,", "running on graph with "
                        + graph.order() + " vertices and " + graph.size() + " edges.",
                "Selection Strategy: " + strategy + ".");
    }

    @Override
    public void reset() {
        graph = new AtomicPlaneGraph(originalGraph);
        vertexHotness = new HashMap<>();
        edgeHotness = new HashMap<>();
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

    /**
     * A graph transformation.
     */
    private abstract class Transformation {

        /**
         * The reduction value of this transformation.
         *
         * <p>
         * This integer corresponds to the value of the index 'i' of the omega
         * and eta reductions of Steinitz 1922 or Grünbaum 1967.
         */
        private int reductionValue;

        /**
         * Create a transformation.
         *
         * @param reductionValue
         *            the reduction value
         */
        public Transformation(int reductionValue) {
            this.reductionValue = reductionValue;
        }

        /**
         * Return the reduction value of this transformation.
         *
         * <p>
         * The reduction value is the number of edge reductions its application
         * causes (via series or parallel reductions).
         *
         * @return reduction value
         */
        public int getReductionValue() {
            return reductionValue;
        }

        /**
         * Return the temperature of this transformation.
         *
         * <p>
         * Intuitively, vertices and edges get hotter if they are involved in a
         * transformation. This methods reports the current temperature of the
         * vertices and edges that would be involved in this transformation.
         *
         * @return temperature
         */
        public abstract int getTemperature();

        /**
         * Perform this transformation.
         *
         * @return description of the transformation
         */
        public abstract String apply();
    }

    /**
     * An omega transformation that is applied at some vertex.
     */
    private class OmegaTransformation extends Transformation {

        /**
         * The identifying vertex.
         */
        private AtomicVertex vertex;

        /**
         * Create transformation at some vertex.
         *
         * @param vertex
         *            the identifying vertex
         * @param reductionValue
         *            the reduction value
         */
        public OmegaTransformation(AtomicVertex vertex, int reductionValue) {
            super(reductionValue);
            this.vertex = vertex;
        }

        @Override
        public String apply() {
            Set<UndirectedEdge<AtomicVertex>> newEdges = graph.reduceOmega(vertex);
            int temperature = getTemperature();
            for (UndirectedEdge<AtomicVertex> edge : newEdges) {
                edgeHotness.put(edge, temperature + 1);
            }
            return OMEGA + " at " + vertex;
        }

        @Override
        public int getTemperature() {
            return vertexHotness.getOrDefault(vertex, 0);
        }
    }

    /**
     * An eta transformation that is applied at some delta face.
     */
    private class EtaTransformation extends Transformation {

        /**
         * The identifying vertex.
         */
        private Circuit<AtomicVertex> delta;

        /**
         * Create eta transformation at some delta face.
         *
         * @param delta
         *            the identifying delta face
         * @param reductionValue
         *            the reduction value
         */
        public EtaTransformation(Circuit<AtomicVertex> delta, int reductionValue) {
            super(reductionValue);
            this.delta = delta;
        }

        @Override
        public String apply() {
            AtomicVertex wye = graph.reduceEta(delta);
            int temperature = getTemperature();
            vertexHotness.put(wye, temperature + 1);
            return ETA + " at " + delta;
        }

        @Override
        public int getTemperature() {
            int temperature = 0;
            for (UndirectedEdge<AtomicVertex> edge : delta.edgesUndirected()) {
                int edgeTemperature = edgeHotness.getOrDefault(edge, 0);
                if (edgeTemperature > temperature) {
                    temperature = edgeTemperature;
                }
            }
            return temperature;
        }
    }

}
