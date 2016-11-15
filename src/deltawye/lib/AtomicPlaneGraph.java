package deltawye.lib;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Plane graph with atomic vertices.
 */
public class AtomicPlaneGraph extends PlaneGraph<AtomicVertex> {

    /**
     * Create AtomicPlaneGraph from an incidence map representation.
     *
     * @param incidenceMap
     *            incidence map representation
     * @throws IllegalArgumentException
     *             if the incidence map representation is invalid
     */
    public AtomicPlaneGraph(
            Map<AtomicVertex, RotationList<EdgeTail<AtomicVertex>>> incidenceMap) {
        super(incidenceMap);
    }

    /**
     * Return a copy of the specified AtomicPlaneGraph.
     *
     * @param original
     *            original graph to be copied
     */
    public AtomicPlaneGraph(AtomicPlaneGraph original) {
        this(original.getIncidenceMap());
    }

    /**
     * Create AtomicPlaneGraph from an adjacency list representation.
     *
     * <p>
     * The adjacency list must be provided as a mapping from vertices to a list
     * of their neighbors in clockwise rotation order. The representation must
     * correspond to a simple graph: Loops and multiple edges are not allowed in
     * this representation.
     *
     * @param adjacencyList
     *            adjacency list representation
     * @return new AtomicPlaneGraph instance
     *
     */
    public static AtomicPlaneGraph fromAdjacencyList(
            Map<AtomicVertex, List<AtomicVertex>> adjacencyList) {
        return new AtomicPlaneGraph(convertAdjacencyListToIncidenceMap(adjacencyList));
    }

    /**
     * Create PlaneGraph from an incidence list representation.
     *
     * <p>
     * The incidence list must be provided as a mapping from vertices to a list
     * of integers, where each integer is the unique identifier of an edge and
     * the edges are listed in clockwise rotation order around the vertex.
     *
     * @param incidenceList
     *            incidence list representation
     * @return new PlaneGraph instance
     *
     */
    public static AtomicPlaneGraph fromIncidenceList(
            Map<AtomicVertex, List<Integer>> incidenceList) {
        return new AtomicPlaneGraph(convertIncidenceListToIncidenceMap(incidenceList));
    }

    /**
     * Read graph data from file.
     *
     * <p>
     * The required format of the input text file is as follows: Each line
     * starts with an integer representing a vertex in the graph, followed by a
     * space-separated list of integers representing its neighbors in rotation
     * order.
     *
     * <p>
     * The returned list of lists uses the same representation: Each list item
     * is a non-empty list whose head is a vertex of the graph and whose tail is
     * a list of neighbors.
     *
     * @param file
     *            file from which to read graph data
     * @return adjacency list representation
     * @throws IOException
     *             if input file cannot be read
     */
    public static AtomicPlaneGraph readAdjacencyList(Path file) throws IOException {
        List<List<AtomicVertex>> rawList = Files.lines(file)
                                                .map(AtomicPlaneGraph::processAdjacencyLine)
                                                .collect(Collectors.toList());
        Map<AtomicVertex, List<AtomicVertex>> adjacencyList = new HashMap<>();
        for (List<AtomicVertex> line : rawList) {
            AtomicVertex focus = line.get(0);
            line.remove(0);
            adjacencyList.put(focus, line);
        }
        return fromAdjacencyList(adjacencyList);
    }

    /**
     * Read adjacency list from integer matrix.
     *
     * <p>
     * Note: The matrix and array indices are not used. The first integer in
     * each subarray is parsed as the identifier of a vertex. All subsequent
     * integers in that subarray are parsed as identifiers of its neighbor
     * vertices in clockwise order.
     *
     * @param matrix
     *            adjacency matrix
     * @return plane graph
     */
    public static AtomicPlaneGraph readAdjacencyList(int[][] matrix) {
        Map<AtomicVertex, List<AtomicVertex>> adjacencyList = new HashMap<>();
        for (int i = 0; i < matrix.length; i++) {
            AtomicVertex vertex = new AtomicVertex(matrix[i][0]);
            List<AtomicVertex> neighbors = new ArrayList<>();
            for (int j = 1; j < matrix[i].length; j++) {
                neighbors.add(new AtomicVertex(matrix[i][j]));
            }
            adjacencyList.put(vertex, neighbors);
        }
        return fromAdjacencyList(adjacencyList);
    }

    /**
     * Read incidence list from integer matrix.
     *
     * <p>
     * Note: The matrix and array indices are not used. The first integer in
     * each subarray is parsed as the identifier of a vertex. All subsequent
     * integers in that subarray are parsed as identifiers of edges that are
     * incident to this vertex in clockwise order.
     *
     * @param matrix
     *            incidence matrix
     * @return plane graph
     */
    public static AtomicPlaneGraph readIncidenceList(int[][] matrix) {
        Map<AtomicVertex, List<Integer>> incidenceList = new HashMap<>();
        for (int i = 0; i < matrix.length; i++) {
            AtomicVertex vertex = new AtomicVertex(matrix[i][0]);
            List<Integer> edges = new ArrayList<>();
            for (int j = 1; j < matrix[i].length; j++) {
                edges.add(matrix[i][j]);
            }
            incidenceList.put(vertex, edges);
        }
        return fromIncidenceList(incidenceList);
    }

    /**
     * Read incidence list from file.
     *
     * <p>
     * The required format of the input text file is as follows: Each line
     * starts with an integer representing a vertex in the graph, followed by a
     * space-separated list of integers representing incidence edges in rotation
     * order.
     *
     * @param file
     *            file from which to read graph data
     * @return adjacency list representation
     * @throws IOException
     *             if input file cannot be read
     */
    public static AtomicPlaneGraph readIncidenceList(Path file) throws IOException {
        List<List<Integer>> intLists = Files.lines(file)
                                            .map(AtomicPlaneGraph::lineToIntList)
                                            .collect(Collectors.toList());
        Map<AtomicVertex, List<Integer>> incidenceList = new HashMap<>();
        for (List<Integer> entry : intLists) {
            AtomicVertex focus = new AtomicVertex(entry.get(0));
            entry.remove(0);
            incidenceList.put(focus, entry);
        }
        return fromIncidenceList(incidenceList);
    }

    /**
     * Process a single line of an adjacency list.
     *
     * @param s
     *            input line
     * @return parsed adjacency list entry
     */
    private static List<AtomicVertex> processAdjacencyLine(String s) {
        return Arrays.asList(s.split("\\s"))
                     .stream()
                     .map(Integer::parseInt)
                     .map(AtomicVertex::new)
                     .collect(Collectors.toList());
    }

    /**
     * Process a single line of an incidence list.
     *
     * @param s
     *            input line
     * @return parsed incidence list entry
     */
    private static List<Integer> lineToIntList(String s) {
        return Arrays.asList(s.split("\\s"))
                     .stream()
                     .map(Integer::parseInt)
                     .collect(Collectors.toList());
    }

    /**
     * Perform a Delta-Wye transformation at the specified triangular face.
     *
     * <p>
     * For the new wye vertex, an unused element is generated.
     *
     * @param delta
     *            a triangular face
     * @return the new wye vertex
     * @see PlaneGraph#reduceDeltaWye(Circuit, Vertex)
     */
    public AtomicVertex reduceDeltaWye(Circuit<AtomicVertex> delta) {
        return reduceDeltaWye(delta, new AtomicVertex(getUnusedVertexID()));
    }

    /**
     * Perform an Eta transformation.
     *
     * <p>
     * An eta transformation consists of a Delta-Wye transformation followed by
     * up to three series reductions.
     *
     * @param delta
     *            a triangular face
     * @return the new wye vertex
     * @see PlaneGraph#reduceEta(Circuit, Vertex)
     */
    public AtomicVertex reduceEta(Circuit<AtomicVertex> delta) {
        return reduceEta(delta, new AtomicVertex(getUnusedVertexID()));
    }

}
