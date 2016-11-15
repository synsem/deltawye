package deltawye.lib;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Representation of a plane graph.
 *
 * @param <V>
 *            the type of vertices in the graph
 */
public class PlaneGraph<V extends Vertex<V>>
        extends AbstractPlaneGraph<V, UndirectedEdge<V>, Circuit<V>> {

    /**
     * A representation of a plane graph in the form of a mapping from vertices
     * to a list of their incident edges in rotation order.
     */
    private final Map<V, RotationList<EdgeTail<V>>> incidenceMap;

    /**
     * An unused vertex ID that can be used for new elements.
     */
    private int nextUnusedVertexID;

    /**
     * An unused edge ID that can be used for new elements.
     */
    private int nextUnusedEdgeID;

    /**
     * Create PlaneGraph from an incidence map representation.
     *
     * @param incidenceMap
     *            incidence map representation
     * @throws IllegalArgumentException
     *             if the incidence map representation is invalid
     */
    public PlaneGraph(Map<V, RotationList<EdgeTail<V>>> incidenceMap) {
        this.incidenceMap = incidenceMap;
        if (!isValid()) {
            throw new IllegalArgumentException("Invalid graph representation.");
        }
        nextUnusedVertexID = 1 + maxVertexID();
        nextUnusedEdgeID = 1 + maxEdgeID();
    }

    /**
     * Convert an adjacency list representation to an incidence map.
     *
     * @param adjacencyList
     *            adjacency list representation
     * @param <V>
     *            the type of vertices in the graph
     * @return incidence map
     */
    public static <V extends Vertex<V>> Map<V, RotationList<EdgeTail<V>>> convertAdjacencyListToIncidenceMap(
            Map<V, List<V>> adjacencyList) {
        Map<Set<V>, Integer> edgeToID = new HashMap<>();
        Map<V, List<Integer>> incidenceMapID = new HashMap<>();
        int edgeID = 0;
        for (Map.Entry<V, List<V>> entry : adjacencyList.entrySet()) {
            V v = entry.getKey();
            List<Integer> vEdges = new ArrayList<>();
            incidenceMapID.put(v, vEdges);
            for (V w : entry.getValue()) {
                if (v.equals(w)) {
                    throw new IllegalArgumentException(
                            "No loops allowed in adjacency list format: " + v);
                }
                Set<V> edge = Stream.of(v, w)
                                    .collect(Collectors.toSet());
                if (edgeToID.containsKey(edge)) {
                    vEdges.add(edgeToID.get(edge));
                } else { // register new edge
                    edgeToID.put(edge, edgeID);
                    vEdges.add(edgeID);
                    edgeID++;
                }
            }
        }
        return convertIncidenceListToIncidenceMap(incidenceMapID);
    }

    /**
     * Create PlaneGraph from an adjacency list representation.
     *
     * <p>
     * The adjacency list must be provided as a mapping from vertices to a list
     * of their neighbors in clockwise rotation order. The representation must
     * correspond to a simple graph: Loops and multiple edges are not allowed in
     * this representation.
     *
     * @param adjacencyList
     *            adjacency list representation
     * @param <V>
     *            the type of vertices in the graph
     * @return new PlaneGraph instance
     *
     */
    public static <V extends Vertex<V>> PlaneGraph<V> fromAdjacencyListPG(
            Map<V, List<V>> adjacencyList) {
        return new PlaneGraph<>(convertAdjacencyListToIncidenceMap(adjacencyList));
    }

    /**
     * Convert an incidence list representation to an incidence map.
     *
     * <p>
     * The incidence list must be provided as a mapping from vertices to a list
     * of integers, where each integer is the unique identifier of an edge and
     * the edges are listed in clockwise rotation order around the vertex.
     *
     * @param incidenceList
     *            incidence list representation
     * @param <V>
     *            the type of vertices in the graph
     * @return incidence map
     */
    public static <V extends Vertex<V>> Map<V, RotationList<EdgeTail<V>>> convertIncidenceListToIncidenceMap(
            Map<V, List<Integer>> incidenceList) {
        // Build mapping from edge ID to edge end vertices
        Map<Integer, V> edgeMapLeft = new HashMap<>();
        Map<Integer, V> edgeMapRight = new HashMap<>();
        for (Map.Entry<V, List<Integer>> entry : incidenceList.entrySet()) {
            V v = entry.getKey();
            for (int eid : entry.getValue()) { // edge ID
                if (!edgeMapLeft.containsKey(eid)) {
                    edgeMapLeft.put(eid, v);
                } else if (!edgeMapRight.containsKey(eid)) {
                    edgeMapRight.put(eid, v);
                } else {
                    throw new IllegalArgumentException(
                            "Edge must not have more than two ends.");
                }
            }
        }
        Map<V, RotationList<EdgeTail<V>>> incidenceMap = new HashMap<>();
        Set<Integer> rootedLeft = new HashSet<>();
        incidenceList.forEach((v, vEdgeIDs) -> {
            RotationList<EdgeTail<V>> vEdges = new RotationList<>();
            incidenceMap.put(v, vEdges);
            for (int eid : vEdgeIDs) {
                UndirectedEdge<V> edge = new UndirectedEdge<>(eid, edgeMapLeft.get(eid),
                        edgeMapRight.get(eid));
                EdgeTail<V> edgeTail;
                if (rootedLeft.contains(eid)) {
                    edgeTail = new EdgeTail<>(edge, EdgeTail.Side.RIGHT);
                } else {
                    rootedLeft.add(eid);
                    edgeTail = new EdgeTail<>(edge, EdgeTail.Side.LEFT);
                }
                vEdges.add(edgeTail);
            }
        });
        return incidenceMap;
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
     * @param <V>
     *            the type of vertices in the graph
     * @return new PlaneGraph instance
     *
     */
    public static <V extends Vertex<V>> PlaneGraph<V> fromIncidenceListPG(
            Map<V, List<Integer>> incidenceList) {
        return new PlaneGraph<>(convertIncidenceListToIncidenceMap(incidenceList));
    }

    /**
     * Return a copy of the incidence map of this graph.
     *
     * @return incidence map
     */
    public Map<V, RotationList<EdgeTail<V>>> getIncidenceMap() {
        Map<V, RotationList<EdgeTail<V>>> newIncidenceMap = new HashMap<>();
        incidenceMap.forEach((k, v) -> newIncidenceMap.put(k, new RotationList<>(v)));
        return newIncidenceMap;
    }

    /**
     * Return the integer ID of the largest vertex in this graph.
     *
     * If the graph is empty, return 0.
     *
     * @return max vertex ID in use
     */
    private int maxVertexID() {
        Set<V> vertexSet = vertices();
        if (vertexSet.isEmpty()) {
            return 0;
        } else {
            return Collections.max(vertexSet)
                              .getID();
        }
    }

    /**
     * Return the integer ID of the largest edge in this graph.
     *
     * If the graph has no edges, return 0.
     *
     * @return max edge ID in use
     */
    private int maxEdgeID() {
        Set<UndirectedEdge<V>> edgeSet = edges();
        if (edgeSet.isEmpty()) {
            return 0;
        } else {
            return Collections.max(edgeSet)
                              .getID();
        }
    }

    /**
     * Return a fresh vertex ID.
     *
     * @return unused vertex ID
     */
    public int getUnusedVertexID() {
        return nextUnusedVertexID++;
    }

    /**
     * Return a fresh edge ID.
     *
     * @return unused edge ID
     */
    public int getUnusedEdgeID() {
        return nextUnusedEdgeID++;
    }

    /**
     * Return whether the internal graph representation is valid.
     *
     * <p>
     * This method checks several invariants that the graph representation must
     * satisfy. For example, there must be exactly two occurrences of every edge
     * ID in the value set of the incidence map. Every edge must connect exactly
     * the two vertices which are mapped to this edge. Every edge with the same
     * ID must have the same end vertices.
     *
     * @return true if the graph representation is valid
     */
    public boolean isValid() {
        Map<UndirectedEdge<V>, Integer> edgeCount = new HashMap<>();
        Map<UndirectedEdge<V>, Set<V>> edgeEnds = new HashMap<>();
        for (Map.Entry<V, RotationList<EdgeTail<V>>> entry : incidenceMap.entrySet()) {
            V v = entry.getKey();
            for (EdgeTail<V> et : entry.getValue()) {
                UndirectedEdge<V> e = et.getEdge();
                if (!e.isIncidentTo(v)) {
                    // Edge is not incident to key vertex.
                    return false;
                }
                edgeCount.put(e, edgeCount.getOrDefault(e, 0) + 1);
                if (edgeEnds.containsKey(e)) {
                    if (!e.vertices()
                          .equals(edgeEnds.get(e))) {
                        // Same edge ID used for different vertex sets.
                        return false;
                    }
                } else {
                    edgeEnds.put(e, e.vertices());
                }
            }
        }
        Set<Integer> edgeCounts = new HashSet<>(edgeCount.values());
        if (!edgeCounts.isEmpty()) { // allow graph without edges
            if (!edgeCounts.equals(Collections.singleton(2))) {
                // There are edges that do not connect exactly 2 vertices.
                return false;
            }
        }
        return true;
    }

    /**
     * Return whether the graph is simple.
     *
     * <p>
     * A graph is simple if it has no loops and no parallel edges.
     *
     * @return true if the graph is simple
     */
    public boolean isSimple() {
        return (!hasLoopEdges() && !hasParallelEdges());
    }

    /**
     * Return whether the graph has loop edges.
     *
     * @return true if the graph has loops
     */
    public boolean hasLoopEdges() {
        return edges().stream()
                      .anyMatch(UndirectedEdge::isLoop);
    }

    /**
     * Return the stream of loop edges.
     *
     * @return loop edges
     */
    public Stream<UndirectedEdge<V>> getLoopEdges() {
        return edges().stream()
                      .filter(UndirectedEdge::isLoop);
    }

    /**
     * Return the number of loop edges.
     *
     * @return number of loop edges
     */
    public int numberOfLoopEdges() {
        return (int) getLoopEdges().count();
    }

    /**
     * Return whether the graph has parallel edges.
     *
     * @return true if the graph has parallel edges
     *
     */
    public boolean hasParallelEdges() {
        return createConnectionMap().values()
                                    .stream()
                                    .anyMatch(es -> es.size() > 1);
    }

    /**
     * Return the number of parallel edges.
     *
     * @return number of parallel edges
     *
     */
    public int numberOfParallelEdges() {
        return createConnectionMap().values()
                                    .stream()
                                    .filter(es -> es.size() > 1)
                                    .mapToInt(es -> es.size() - 1)
                                    .sum();
    }

    /**
     * Create a map from sets of vertices to sets of edges that connect these
     * vertices. This map can be used to look up all edges that connect the
     * specified set of edges.
     *
     * <p>
     * All vertex sets that form the keys of this map have exactly 1 (loop
     * edges) or 2 (link edges) elements.
     *
     * @return connection map
     */
    private Map<Set<V>, Set<UndirectedEdge<V>>> createConnectionMap() {
        return edges().stream()
                      .collect(Collectors.groupingBy(UndirectedEdge::vertices,
                              Collectors.toSet()));
    }

    /**
     * Return the incidence map of the subgraph induced by the specified vertex
     * set.
     *
     * @param vertices
     *            vertex set of the subgraph
     * @return the incidence map of the induced subgraph
     */
    public Map<V, RotationList<EdgeTail<V>>> vertexInducedSubgraphAsIncidenceMap(
            Set<V> vertices) {
        Map<V, RotationList<EdgeTail<V>>> subgraphMap = new HashMap<>();
        for (V v : vertices) {
            RotationList<EdgeTail<V>> vEdges = new RotationList<>();
            incidentEdgeTails(v).stream()
                                .filter(e -> e.connectsVerticesIn(vertices))
                                .forEach(e -> vEdges.add(e));
            subgraphMap.put(v, vEdges);
        }
        return subgraphMap;
    }

    /**
     * Return the subgraph induced by the specified vertex set.
     *
     * @param vertices
     *            vertex set of the subgraph
     * @return the induced subgraph
     */
    public PlaneGraph<V> vertexInducedSubgraph(Set<V> vertices) {
        return new PlaneGraph<>(vertexInducedSubgraphAsIncidenceMap(vertices));
    }

    @Override
    public String toString() {
        return "Plane graph with " + order() + " vertices, " + size() + " edges and "
                + numberOfFaces() + " faces.";
    }

    @Override
    public Set<V> vertices() {
        return incidenceMap.keySet();
    }

    @Override
    public Set<UndirectedEdge<V>> edges() {
        return edgeTails().stream()
                          .map(EdgeTail::getEdge)
                          .collect(Collectors.toSet());
    }

    /**
     * Return the set of edge tails in the graph.
     *
     * @return set of edge tails
     */
    public Set<EdgeTail<V>> edgeTails() {
        Set<EdgeTail<V>> edgeSet = new HashSet<>();
        incidenceMap.values()
                    .forEach(es -> edgeSet.addAll(es));
        return edgeSet;
    }

    @Override
    public Set<Circuit<V>> faces() {
        Set<EdgeTail<V>> edgeTails = edgeTails();
        if (edgeTails.isEmpty()) {
            if (vertices().size() > 1) {
                throw new InvalidWalkException("Graph must be connected.");
            }
            return vertices().stream()
                             .map(v -> new Circuit<>(v))
                             .collect(Collectors.toSet());
        }
        Set<Circuit<V>> faces = new HashSet<>();
        Set<EdgeTail<V>> visited = new HashSet<>();
        // Detect all faces by traversing all edge tails once.
        for (EdgeTail<V> e : edgeTails) {
            if (!visited.contains(e)) {
                Circuit<V> f = getFace(e);
                faces.add(f);
                visited.addAll(f.edgeList());
            }
        }
        return faces;
    }

    /**
     * Return the face that is defined by the circuit that contains the edge
     * tail {@code start} and traverses adjacent edges in clockwise direction.
     *
     * @param start
     *            an edge tail in the face
     * @return the containing face circuit
     */
    public Circuit<V> getFace(EdgeTail<V> start) {
        List<EdgeTail<V>> faceEdges = new ArrayList<>();
        faceEdges.add(start);
        EdgeTail<V> cur = nextEdge(start, RotationDirection.CLOCKWISE);
        while (!cur.equals(start)) {
            faceEdges.add(cur);
            cur = nextEdge(cur, RotationDirection.CLOCKWISE);
        }
        return new Circuit<>(faceEdges);
    }

    @Override
    public RotationList<V> neighbors(V vertex) {
        return new RotationList<>(incidenceMap.get(vertex)
                                              .stream()
                                              .map(e -> e.traverseFrom(vertex))
                                              .collect(Collectors.toList()));
    }

    @Override
    public RotationList<UndirectedEdge<V>> incidentEdges(V vertex) {
        return new RotationList<>(incidenceMap.get(vertex)
                                              .stream()
                                              .map(EdgeTail::getEdge)
                                              .collect(Collectors.toList()));
    }

    /**
     * Return edge tails incident to the specified vertex.
     *
     * @param vertex
     *            the vertex
     * @return list of edge tails
     */
    public RotationList<EdgeTail<V>> incidentEdgeTails(V vertex) {
        return new RotationList<>(incidenceMap.get(vertex));
    }

    /**
     * Return whether the two specified vertices are neighbors in this graph.
     *
     * @param u
     *            first vertex
     * @param v
     *            second vertex
     * @return true if there is an edge between the two vertices
     */
    public boolean areNeighbors(V u, V v) {
        return neighbors(u).contains(v);
    }

    /**
     * Return the edge tail that comes next after the edge tail e in the
     * specified rotation order at the target vertex of e.
     *
     * @param edge
     *            the edge relative to which we are operating
     * @param dir
     *            the direction in which to rotate
     * @return the next edge in the specified direction
     */
    public EdgeTail<V> nextEdge(EdgeTail<V> edge, RotationDirection dir) {
        V focus = edge.getTarget();
        return incidenceMap.get(focus)
                           .rotateAt(edge.otherSide(), dir);
    }

    /**
     * Return the vertex that comes clockwise next after the vertex {@code prev}
     * in the rotation scheme at vertex {@code focus}.
     *
     * @param focus
     *            the vertex whose neighbors are considered
     * @param prev
     *            the neighbor relative to which we are operating
     * @return the vertex that is clockwise next after {@code prev}
     */
    public V nextVertex(V focus, V prev) {
        return neighbors(focus).nextAfter(prev);
    }

    /**
     * Return the vertex that comes clockwise before the vertex {@code next} in
     * the rotation scheme at vertex {@code focus}.
     *
     * @param focus
     *            the vertex whose neighbors are considered
     * @param next
     *            the neighbor relative to which we are operating
     * @return the vertex that is clockwise before {@code prev}
     */
    public V prevVertex(V focus, V next) {
        return neighbors(focus).prevBefore(next);
    }

    /**
     * Return whether a series reduction can be performed at the specified
     * vertex.
     *
     * @param v
     *            the vertex to be removed
     * @return true if series reduction can be performed
     */
    public boolean canReduceSeries(V v) {
        RotationList<EdgeTail<V>> vEdges = incidentEdgeTails(v);
        // Series reduction requires vertex of degree 2.
        if (vEdges.size() != 2) {
            return false;
        }
        EdgeTail<V> e1 = vEdges.getFirst();
        EdgeTail<V> e2 = vEdges.getLast();
        // Series reduction requires non-parallel edges.
        if (e1.isParallelTo(e2)) {
            return false;
        }
        // Series reduction requires non-loop edges.
        if (e1.isLoop() || e2.isLoop()) {
            return false;
        }
        return true;
    }

    /**
     * Perform a series reduction at the specified vertex.
     *
     * <p>
     * Contract the two edges of a vertex of degree 2. The two edges must not
     * form a loop. This implementation removes both old edges and adds a new
     * edge which is also returned from this method.
     *
     * <p>
     * Note that this method will create a parallel edge if the outer ends of
     * the contracted edges were already connected.
     *
     * @param v
     *            the vertex to be removed
     * @return the new edge
     * @throws InvalidGraphTransformException
     *             if the vertex does not have degree 2 or if the incident edges
     *             form a loop
     */
    public UndirectedEdge<V> reduceSeries(V v) {
        if (!canReduceSeries(v)) {
            throw new InvalidGraphTransformException(
                    "Series reduction not possible at vertex " + v);
        }
        RotationList<EdgeTail<V>> vEdges = incidentEdgeTails(v);
        EdgeTail<V> e1 = vEdges.getFirst();
        EdgeTail<V> e2 = vEdges.getLast();
        UndirectedEdge<V> newEdge = closeTriangle(e1, e2);
        removeVertex(v);
        return newEdge;
    }

    /**
     * Return whether a Wye-Delta transformation can be performed at the
     * specified vertex.
     *
     * @param wye
     *            the wye vertex for the transformation
     * @return true if a Wye-Delta transformation can be performed
     */
    public boolean canReduceWyeDelta(V wye) {
        RotationList<EdgeTail<V>> edgeList = incidentEdgeTails(wye);
        // Wye-Delta transformation requires vertex of degree 3.
        if (edgeList.size() != 3) {
            return false;
        }
        // Wye-Delta transformation requires three distinct neighbors.
        if (new HashSet<>(neighbors(wye)).size() != 3) {
            return false;
        }
        // Wye vertex must not have loop edges.
        for (EdgeTail<V> e : edgeList) {
            if (e.isLoop()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Perform a Wye-Delta transformation at the specified tristar vertex.
     *
     * <p>
     * The incident edges must not be loops.
     *
     * @param wye
     *            a tristar vertex
     * @param suppressParallelEdges
     *            if true do not add parallel edges
     * @return the set of new edges
     */
    private Set<UndirectedEdge<V>> reduceWyeDelta(V wye, boolean suppressParallelEdges) {
        if (!canReduceWyeDelta(wye)) {
            throw new InvalidGraphTransformException(
                    "Wye-Delta reduction not possible at vertex " + wye);
        }
        RotationList<EdgeTail<V>> edgeList = incidentEdgeTails(wye);
        EdgeTail<V> e0 = edgeList.getLast();
        EdgeTail<V> e1 = edgeList.prevBefore(e0);
        EdgeTail<V> e2 = edgeList.prevBefore(e1);
        Set<UndirectedEdge<V>> newEdges = new HashSet<>();
        if (!suppressParallelEdges || !areNeighbors(e0.getTarget(), e1.getTarget())) {
            // Note: closeTriangle requires CCW order: (leftLeg, rightLeg)
            newEdges.add(closeTriangle(e0, e1));
        }
        if (!suppressParallelEdges || !areNeighbors(e1.getTarget(), e2.getTarget())) {
            newEdges.add(closeTriangle(e1, e2));
        }
        if (!suppressParallelEdges || !areNeighbors(e2.getTarget(), e0.getTarget())) {
            newEdges.add(closeTriangle(e2, e0));
        }
        removeVertex(wye);
        return newEdges;
    }

    /**
     * Perform a Wye-Delta transformation at the specified tristar vertex.
     *
     * <p>
     * The incident edges must not be loops.
     *
     * @param wye
     *            a tristar vertex
     * @return the set of new edges
     */
    public Set<UndirectedEdge<V>> reduceWyeDelta(V wye) {
        return reduceWyeDelta(wye, false);
    }

    /**
     * Perform an Omega transformation.
     *
     * <p>
     * An omega transformation consists of a Wye-Delta transformation followed
     * by up to three parallel reductions.
     *
     * @param wye
     *            a tristar vertex
     * @return the set of new edges
     */
    public Set<UndirectedEdge<V>> reduceOmega(V wye) {
        return reduceWyeDelta(wye, true);
    }

    /**
     * Return whether a Delta-Wye transformation can be performed at the face
     * identified by the specified edge tail.
     *
     * @param e
     *            edge tail of a face
     * @return true if a Delta-Wye transformation can be performed
     */
    public boolean canReduceDeltaWye(EdgeTail<V> e) {
        return canReduceDeltaWye(getFace(e));
    }

    /**
     * Return whether a Delta-Wye transformation can be performed at the
     * specified circuit.
     *
     * @param delta
     *            the delta circuit
     * @return true if a Delta-Wye transformation can be performed
     */
    public boolean canReduceDeltaWye(Circuit<V> delta) {
        // Delta circuit must have exactly 3 different vertices.
        if (delta.order() != 3) {
            return false;
        }
        // Delta circuit must be a cycle.
        if (!delta.isCycle()) {
            return false;
        }
        // Delta circuit must be an empty triangular face.
        EdgeTail<V> e = delta.edgeList()
                             .getFirst();
        return getFace(e).equals(delta) && isEmptyTriangle(e);
    }

    /**
     * Perform a Delta-Wye transformation at the specified triangular face.
     *
     * @param delta
     *            a triangular face
     * @param center
     *            a new vertex
     * @param performSeriesReductions
     *            if true perform series reductions at former face vertices
     *            after delta-wye transformation
     * @return the new wye vertex (center)
     */
    private V reduceDeltaWye(Circuit<V> delta, V center,
            boolean performSeriesReductions) {
        if (!canReduceDeltaWye(delta)) {
            throw new InvalidGraphTransformException(
                    "Delta-Wye transformation not possible at circuit " + delta);
        }
        List<V> deltaVertices = delta.vertexList();
        Cycle<V> deltaCycle;
        try {
            deltaCycle = delta.toCycle();
        } catch (InvalidWalkException ex) {
            throw new InvalidGraphTransformException("Delta face must be a cycle.");
        }
        addStarVertexInCycle(deltaCycle, center);
        for (UndirectedEdge<V> e : deltaCycle.edgesUndirected()) {
            removeEdge(e);
        }
        if (performSeriesReductions) {
            for (V x : deltaVertices) {
                if (canReduceSeries(x)) {
                    reduceSeries(x);
                }
            }
        }
        return center;
    }

    /**
     * Perform a Delta-Wye transformation at the specified triangular face.
     *
     * @param delta
     *            a triangular face
     * @param center
     *            a new vertex
     * @return the new wye vertex (center)
     */
    public V reduceDeltaWye(Circuit<V> delta, V center) {
        return reduceDeltaWye(delta, center, false);
    }

    /**
     * Perform an Eta transformation.
     *
     * <p>
     * An eta transformation consists of a Delta-Wye transformation followed by
     * up to three series reductions.
     *
     * <p>
     * Note: An eta transformation on a simple graph may result in a multigraph.
     * For example, an eta transformation on any vertex of K4 will produce a
     * multigraph consisting of two vertices and three parallel edges between
     * them.
     *
     * @param delta
     *            a triangular face
     * @param center
     *            a new vertex
     * @return the new wye vertex (center)
     */
    public V reduceEta(Circuit<V> delta, V center) {
        return reduceDeltaWye(delta, center, true);
    }

    /**
     * Return whether the graph is complete.
     *
     * @return true if the graph is complete
     */
    public boolean isComplete() {
        for (V v : vertices()) {
            for (V w : vertices()) {
                if (!v.equals(w) && !areNeighbors(v, w)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Return whether this graph is (isomorphic to) the graph K4.
     *
     * @return true if this is K4
     */
    public boolean isK4() {
        return (order() == 4 && isComplete());
    }

    /**
     * Return whether this graph is (isomorphic to) the graph K1.
     *
     * @return true if this is K1
     */
    public boolean isK1() {
        return (order() == 1 && size() == 0);
    }

    /**
     * Return whether the edge tail is part of an empty loop.
     *
     * @param e
     *            edge tail
     * @return true if the edge is an empty loop
     */
    public boolean isEmptyLoop(EdgeTail<V> e) {
        // We are looking for a face with one edge.
        // nextEdge(e, RotationDirection.CLOCKWISE).equals(e);
        // getFace(e).length() == 1;
        return getFace(e).isLoop();
    }

    /**
     * Return whether the edge tail is part of an empty digon.
     *
     * @param e
     *            edge tail
     * @return true if the edge belongs to an empty digon
     */
    public boolean isEmptyDigon(EdgeTail<V> e) {
        return getFace(e).isDigon();
    }

    /**
     * Return whether the edge tail is part of an empty triangle.
     *
     * @param e
     *            edge tail
     * @return true if the edge belongs to an empty triangle
     */
    public boolean isEmptyTriangle(EdgeTail<V> e) {
        return getFace(e).isTriangle();
    }

    /**
     * Return whether the specified vertex is a wye (tristar).
     *
     * @param v
     *            the vertex to examine
     * @return true if the vertex is a wye
     */
    public boolean isWye(V v) {
        return degree(v) == 3;
    }

    /**
     * Return a stream of all wye (tristar) vertices in the graph.
     *
     * @return stream of wye vertices
     */
    public Stream<V> getWyeVertices() {
        return vertices().stream()
                         .filter(v -> isWye(v));
    }

    /**
     * Return a stream of all delta (triangular) faces in the graph.
     *
     * @return stream of delta faces
     */
    public Stream<Circuit<V>> getDeltaFaces() {
        return faces().stream()
                      .filter(Circuit::isTriangle);
    }

    /**
     * Add a new vertex to the graph that is placed in the middle of the
     * specified face and connected to all of the vertices in the face.
     *
     * @param cycle
     *            the cycle in which to place the new vertex
     * @param center
     *            a new vertex
     * @return the new vertex (center)
     * @throws InvalidGraphTransformException
     *             if the provided vertex is not fresh
     */
    public V addStarVertexInCycle(Cycle<V> cycle, V center) {
        if (incidenceMap.containsKey(center)) {
            throw new InvalidGraphTransformException("Center vertex must be new.");
        }
        if (cycle.size() < 3) {
            throw new InvalidGraphTransformException(
                    "Cycle must contain at least three edges.");
        }
        // add center vertex with outwards pointing halfedges
        Map<V, EdgeTail<V>> newOuterEdgeTails = new HashMap<>();
        RotationList<EdgeTail<V>> newInnerEdgeTails = new RotationList<>();
        for (V v : cycle.vertexListClockwise()) {
            UndirectedEdge<V> e = new UndirectedEdge<>(getUnusedEdgeID(), center, v);
            newInnerEdgeTails.add(e.leftTail());
            newOuterEdgeTails.put(v, e.rightTail());
        }
        incidenceMap.put(center, newInnerEdgeTails);
        // add inwards pointing halfedges to cycle vertices
        for (EdgeTail<V> cycleEdge : cycle.edgeList()) {
            V v = cycleEdge.getSource();
            EdgeTail<V> newEdge = newOuterEdgeTails.get(v);
            unsafeAddHalfEdge(newEdge, cycleEdge, RotationDirection.COUNTERCLOCKWISE);
        }
        return center;
    }

    /**
     * Add a triangle edge between the target vertices of two non-loop edge
     * tails originating at the same vertex.
     *
     * @param leftLeg
     *            edge clockwise from {@code rightLeg}
     * @param rightLeg
     *            edge counterclockwise from {@code leftLeg}
     * @return new edge
     * @throws InvalidGraphTransformException
     *             if {@code leftLeg} and {@code rightLeg} do not share their
     *             source vertex or are not in clockwise order at that vertex
     */
    public UndirectedEdge<V> closeTriangle(EdgeTail<V> leftLeg, EdgeTail<V> rightLeg) {
        if (!leftLeg.getSource()
                    .equals(rightLeg.getSource())) {
            throw new InvalidGraphTransformException("Legs do not share source vertex.");
        }
        if (leftLeg.isLoop() || rightLeg.isLoop()) {
            throw new InvalidGraphTransformException("Loop edges are not allowed here.");
        }
        if (!nextEdge(rightLeg.otherSide(), RotationDirection.CLOCKWISE).equals(
                leftLeg)) {
            throw new InvalidGraphTransformException(
                    "Legs are not in correct rotation order.");
        }
        V l = leftLeg.getTarget();
        V r = rightLeg.getTarget();
        UndirectedEdge<V> newEdge = new UndirectedEdge<>(getUnusedEdgeID(), l, r);
        unsafeAddHalfEdge(newEdge.leftTail(), leftLeg.otherSide(),
                RotationDirection.CLOCKWISE);
        unsafeAddHalfEdge(newEdge.rightTail(), rightLeg.otherSide(),
                RotationDirection.COUNTERCLOCKWISE);
        return newEdge;
    }

    /**
     * Remove the specified edge from the graph.
     *
     * @param edge
     *            the edge to remove
     * @throws NoSuchElementException
     *             if the end vertices of the edge are not in the graph
     */
    public void removeEdge(UndirectedEdge<V> edge) {
        incidenceMap.get(edge.getLeft())
                    .remove(edge.leftTail());
        incidenceMap.get(edge.getRight())
                    .remove(edge.rightTail());
    }

    /**
     * Remove the specified vertex from the graph.
     *
     * This also removes all edges incident to the vertex.
     *
     * @param v
     *            the vertex to remove
     * @throws NoSuchElementException
     *             if the vertex is not in the graph
     */
    public void removeVertex(V v) {
        for (UndirectedEdge<V> e : incidentEdges(v)) {
            removeEdge(e);
        }
        incidenceMap.remove(v);
    }

    /**
     * Add the edge tail {@code newEdge} to the list of incident edges of its
     * source vertex, right after its existing reference edge tail
     * {@code refEdge} in the rotation direction {@code dir}.
     *
     * <p>
     * Warning: This is an unsafe subatomic graph transformation that may result
     * in an inconsistent graph state. Use with care.
     *
     * @param newEdge
     *            the new edge tail to be inserted in the incidence list of its
     *            source vertex
     * @param refEdge
     *            an edge tail already incident to the source vertex of
     *            {@code newEdge} relative to which the new edge will be
     *            inserted
     * @param dir
     *            insert the new edge one step in this direction after
     *            {@code refEdge}
     * @throws NonIncidenceException
     *             if {@code newEdge} and {@code refEdge} do not share their
     *             source vertex
     */
    private void unsafeAddHalfEdge(EdgeTail<V> newEdge, EdgeTail<V> refEdge,
            RotationDirection dir) {
        RotationList<EdgeTail<V>> edgeList = incidenceMap.get(newEdge.getSource());
        int targetIdx = edgeList.indexOf(refEdge);
        if (targetIdx == -1) {
            throw new NonIncidenceException();
        }
        if (dir == RotationDirection.CLOCKWISE) {
            targetIdx = targetIdx + 1;
        }
        edgeList.add(targetIdx, newEdge);
    }

}
