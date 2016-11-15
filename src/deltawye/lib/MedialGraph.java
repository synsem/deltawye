package deltawye.lib;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A medial graph is a 4-regular plane graph.
 *
 * <p>
 * In order to create a medial graph from an existing atomic plane graph, use
 * {@link MedialGraph#fromAtomicPlaneGraph(AtomicPlaneGraph)}.
 */
public class MedialGraph extends PlaneGraph<MedialVertex> {

    // We color the faces (and edge tails) of a medial graph
    // black if they contain a vertex of the original graph, and
    // white otherwise. If faces are traversed in clockwise order,
    // then the left face of each edge is black and the right face is white.

    /**
     * Mapping from medial edge tails to corresponding atomic vertices in the
     * original graph.
     *
     * <p>
     * Invariant: Every EdgeTail of the medial graph is either in the black map
     * or in the white map. (In other words, the black map and the white map are
     * disjoint and their union is the set of all edge tails in the medial
     * graph. The edge tails of the medial graph are partitioned into a black
     * and a white set.)
     *
     * <p>
     * Invariant: An EdgeTail is in the black map iff the other side of the edge
     * is in the white map. (In other words, every edge has a black tail and a
     * white tail.)
     */
    private Map<EdgeTail<MedialVertex>, AtomicVertex> blackMap;

    /**
     * Mapping from medial edge tails to corresponding faces (identified by a
     * contained atomic edge tail) in the original graph.
     *
     * @see #blackMap
     */
    private Map<EdgeTail<MedialVertex>, EdgeTail<AtomicVertex>> whiteMap;

    /**
     * Construct a medial graph from an incidence map.
     *
     * @param incidenceMap
     *            incidence map representation
     * @param blackMap
     *            mapping from medial edge tails to vertices
     * @param whiteMap
     *            mapping from medial edge tails to faces
     * @throws IllegalArgumentException
     *             if the incidence map representation is invalid
     */
    private MedialGraph(
            Map<MedialVertex, RotationList<EdgeTail<MedialVertex>>> incidenceMap,
            Map<EdgeTail<MedialVertex>, AtomicVertex> blackMap,
            Map<EdgeTail<MedialVertex>, EdgeTail<AtomicVertex>> whiteMap) {
        super(incidenceMap);
        this.blackMap = blackMap;
        this.whiteMap = whiteMap;
    }

    /**
     * Construct the medial graph for the specified plane graph.
     *
     * Note: The original graph must be 3-connected in order for the medial
     * graph to be simple (i.e. have neither loops nor parallel edges).
     *
     * @param originalGraph
     *            the graph whose medial graph is constructed
     * @return a new medial graph
     */
    public static MedialGraph fromAtomicPlaneGraph(AtomicPlaneGraph originalGraph) {
        int nextEdgeID = 0; // medial edge ID
        Set<Circuit<AtomicVertex>> faces = originalGraph.faces();
        Map<MedialVertex, RotationList<EdgeTail<MedialVertex>>> incidences = new HashMap<>();
        Map<EdgeTail<MedialVertex>, AtomicVertex> blackMap = new HashMap<>();
        Map<EdgeTail<MedialVertex>, EdgeTail<AtomicVertex>> whiteMap = new HashMap<>();
        for (Circuit<AtomicVertex> face : faces) {
            // create medial vertices and edges, and build black/white maps
            RotationList<EdgeTail<AtomicVertex>> originalEdges = face.edgeRotationList();
            RotationList<UndirectedEdge<MedialVertex>> medialEdges = new RotationList<>();
            List<MedialVertex> medialVertices = new ArrayList<>();
            for (int i = 0; i < face.size(); i++) {
                MedialVertex left = new MedialVertex(originalEdges.getMod(i - 1)
                                                                  .getEdge());
                MedialVertex right = new MedialVertex(originalEdges.get(i)
                                                                   .getEdge());
                UndirectedEdge<MedialVertex> newMedialEdge = new UndirectedEdge<>(
                        nextEdgeID++, left, right);
                medialEdges.add(newMedialEdge);
                medialVertices.add(right);
                whiteMap.putIfAbsent(newMedialEdge.leftTail(), originalEdges.get(0));
                blackMap.putIfAbsent(newMedialEdge.rightTail(), originalEdges.get(i)
                                                                             .getSource());
            }
            // register incident medial edges
            for (int i = 0; i < medialVertices.size(); i++) {
                MedialVertex mv = medialVertices.get(i);
                RotationList<EdgeTail<MedialVertex>> mvIncidentEdges = incidences.getOrDefault(
                        mv, new RotationList<>());
                mvIncidentEdges.add(medialEdges.get(i)
                                               .rightTail());
                mvIncidentEdges.add(medialEdges.getMod(i + 1)
                                               .leftTail());
                incidences.putIfAbsent(mv, mvIncidentEdges);
            }
        }
        return new MedialGraph(incidences, blackMap, whiteMap);
    }

    /**
     * Return the direct extension edge tail.
     *
     * @param edge
     *            the edge tail whose direct extension should be returned
     * @return direct extension edge tail
     */
    public EdgeTail<MedialVertex> directExtension(EdgeTail<MedialVertex> edge) {
        MedialVertex focus = edge.getTarget();
        return incidentEdgeTails(focus).rotateBy(edge.otherSide(), 2);
    }

    // Note: This is similar to PlaneGraph.getFace() except that we are
    // traversing 2 steps clockwise instead of 1 step clockwise.
    /**
     * Return the geodesic circuit that contains the specified edge.
     *
     * @param start
     *            start edge of the geodesic circuit
     * @return the geodesic circuit
     */
    public Circuit<MedialVertex> geodesicFrom(EdgeTail<MedialVertex> start) {
        List<EdgeTail<MedialVertex>> edgeList = new ArrayList<>();
        edgeList.add(start);
        EdgeTail<MedialVertex> cur = directExtension(start);
        while (!cur.equals(start)) {
            edgeList.add(cur);
            cur = directExtension(cur);
        }
        return new Circuit<>(edgeList);
    }

    // Note: This should always be a trail (no repeated edges).
    /**
     * Return the geodesic walk from the specified start edge tail to the
     * specified destination vertex.
     *
     * @param startEdge
     *            the start edge tail
     * @param destVertex
     *            the destination vertex
     * @throws InvalidWalkException
     *             if there is no geodesic walk from the start edge to the
     *             destination vertex
     * @return the geodesic walk
     */
    private Walk<MedialVertex> geodesicWalkBetween(EdgeTail<MedialVertex> startEdge,
            MedialVertex destVertex) {
        if (startEdge.getSource()
                     .equals(destVertex)) { // trivial walk
            return new Circuit<>(destVertex);
        }
        List<EdgeTail<MedialVertex>> edgeList = new ArrayList<>();
        edgeList.add(startEdge);
        EdgeTail<MedialVertex> cur = directExtension(startEdge);
        while (!cur.equals(startEdge) && !cur.getSource()
                                             .equals(destVertex)) {
            edgeList.add(cur);
            cur = directExtension(cur);
        }
        if (!cur.getSource()
                .equals(destVertex)) {
            // destVertex is not contained in geodesic circuit of startEdge
            throw new InvalidWalkException(
                    "No geodesic walk from " + startEdge + " to " + destVertex);
        }
        return new Walk<>(edgeList);
    }

    /**
     * Return the set of all geodesic circuits in the medial graph.
     *
     * <p>
     * Note: Only one tail of each edge is traversed. (The opposite tail would
     * give rise to the same circuit in opposite direction.)
     *
     * @return the set of geodesic circuits
     */
    public Set<Circuit<MedialVertex>> geodesics() {
        Set<Circuit<MedialVertex>> geodesics = new HashSet<>();
        Set<UndirectedEdge<MedialVertex>> visited = new HashSet<>();
        for (UndirectedEdge<MedialVertex> edge : edges()) {
            if (!visited.contains(edge)) {
                Circuit<MedialVertex> geodesic = geodesicFrom(edge.leftTail());
                geodesics.add(geodesic);
                visited.addAll(geodesic.edges()
                                       .stream()
                                       .map(EdgeTail::getEdge)
                                       .collect(Collectors.toSet()));
            }
        }
        return geodesics;
    }

    /**
     * Partition all edges of the medial graph into geodesic classes.
     *
     * <p>
     * Geodesic classes are identified by an integer. If two edges are mapped to
     * the same integer, then they are in the same geodesic class, meaning that
     * there is a geodesic path between these two edges.
     *
     * @param geodesics
     *            a list of geodesic circuits (as computed by
     *            {@link MedialGraph#geodesics()})
     * @return mapping from edges to their geodesic class
     */
    private static Map<UndirectedEdge<MedialVertex>, Integer> geoClassMap(
            Set<Circuit<MedialVertex>> geodesics) {
        Map<UndirectedEdge<MedialVertex>, Integer> geoClassMap = new HashMap<>();
        int classID = 0;
        for (Circuit<MedialVertex> geodesic : geodesics) {
            for (EdgeTail<MedialVertex> edge : geodesic.edges()) {
                geoClassMap.put(edge.getEdge(), classID);
            }
            classID++;
        }
        return geoClassMap;
    }

    /**
     * Return a set of the geodesic classes of a vertex.
     *
     * <p>
     * The geodesic classes of a vertex is the union of the geodesic classes of
     * all edges that are incident to that vertex.
     *
     * @param geoClassMap
     *            a mapping from edges to their geodesic class (as returned by
     *            {@link #geoClassMap(Set)})
     * @param vertex
     *            the vertex to look up
     * @return the set of geodesic classes of the vertex
     */
    private Set<Integer> vertexGeoClasses(
            Map<UndirectedEdge<MedialVertex>, Integer> geoClassMap, MedialVertex vertex) {
        return incidentEdges(vertex).stream()
                                    .map(e -> geoClassMap.get(e))
                                    .collect(Collectors.toSet());
    }

    /**
     * Return the set of lenses in this medial graph.
     *
     * <p>
     * More precisely, this method only returns the 2-lenses of the medial
     * graph, i.e. those with two distinct poles and boundaries.
     *
     * @return the set of lenses
     */
    public TreeSet<Lens> lenses() {
        TreeSet<Lens> lenses = new TreeSet<>();
        Map<UndirectedEdge<MedialVertex>, Integer> geoClassMap = geoClassMap(geodesics());
        for (MedialVertex southPole : vertices()) {
            for (MedialVertex northPole : vertices()) {
                if (southPole.compareTo(northPole) > 0) { // unique pairings
                    if (vertexGeoClasses(geoClassMap, southPole).equals(
                            vertexGeoClasses(geoClassMap, northPole))) {
                        lenses.addAll(lensesBetweenPoles(southPole, northPole));
                    }
                }
            }
        }
        return lenses;
    }

    /**
     * Return the set of lenses between the specified poles.
     *
     * @param southPole
     *            fixed south pole of the lens
     * @param northPole
     *            fixed north pole of the lens
     * @return the set of lenses between the poles
     */
    private TreeSet<Lens> lensesBetweenPoles(MedialVertex southPole,
            MedialVertex northPole) {
        TreeSet<Lens> lenses = new TreeSet<>();
        Set<MedialVertex> poles = Stream.of(southPole, northPole)
                                        .collect(Collectors.toSet());
        RotationList<EdgeTail<MedialVertex>> southEdges = incidentEdgeTails(southPole);
        for (EdgeTail<MedialVertex> southLeft : southEdges) {
            EdgeTail<MedialVertex> southRight = southEdges.nextAfter(southLeft);
            Walk<MedialVertex> leftLensBoundary = geodesicWalkBetween(southLeft,
                    northPole);
            Walk<MedialVertex> rightLensBoundary = geodesicWalkBetween(southRight,
                    northPole);
            if (leftLensBoundary.intersectVertices(rightLensBoundary)
                                .equals(poles)) {
                EdgeTail<MedialVertex> northLeft = leftLensBoundary.edgeList()
                                                                   .getLast();
                EdgeTail<MedialVertex> northRight = rightLensBoundary.edgeList()
                                                                     .getLast();
                if (nextEdge(northRight, RotationDirection.CLOCKWISE).equals(
                        northLeft.otherSide())) {
                    lenses.add(lensFromBoundaries(leftLensBoundary, rightLensBoundary));
                }
            }
        }
        return lenses;
    }

    /**
     * Return a lens subgraph based on the specified lens boundaries.
     *
     * <p>
     * No validation is performed whether the specified boundaries describe an
     * actual lens in this graph.
     *
     * <p>
     * By convention, both the left and the right boundaries of a lens are
     * directed walks from the south pole to the north pole.
     *
     * @param leftBoundary
     *            left boundary of the lens
     * @param rightBoundary
     *            right boundary of the lens
     * @return the lens subgraph
     */
    private Lens lensFromBoundaries(Walk<MedialVertex> leftBoundary,
            Walk<MedialVertex> rightBoundary) {

        // Determine whether the lens contains inner vertices
        Set<MedialVertex> innerVertices = new HashSet<>();
        TreeSet<MedialVertex> boundaryAdjacentInnerVertices = new TreeSet<>();
        MedialVertex northPole = rightBoundary.getEndVertex();
        for (EdgeTail<MedialVertex> leftEdge : leftBoundary.edgeList()) {
            if (!leftEdge.getTarget()
                         .equals(northPole)) {
                MedialVertex next = prevVertex(leftEdge.getTarget(),
                        leftEdge.getSource());
                if (!rightBoundary.contains(next) && !leftBoundary.contains(next)) {
                    boundaryAdjacentInnerVertices.add(next);
                }
            }
        }
        for (EdgeTail<MedialVertex> rightEdge : rightBoundary.edgeList()) {
            if (!rightEdge.getTarget()
                          .equals(northPole)) {
                MedialVertex next = nextVertex(rightEdge.getTarget(),
                        rightEdge.getSource());
                if (!leftBoundary.contains(next) && !rightBoundary.contains(next)) {
                    boundaryAdjacentInnerVertices.add(next);
                }
            }
        }
        if (!boundaryAdjacentInnerVertices.isEmpty()) {
            MedialVertex innerVertex = boundaryAdjacentInnerVertices.first();
            Set<MedialVertex> boundaryVertices = new HashSet<>();
            boundaryVertices.addAll(leftBoundary.vertices());
            boundaryVertices.addAll(rightBoundary.vertices());
            innerVertices = connectedVertices(innerVertex, boundaryVertices);
        }

        // Collect all vertices contained in the lens subgraph
        Set<MedialVertex> lensVertices = new HashSet<>();
        lensVertices.addAll(leftBoundary.vertices());
        lensVertices.addAll(rightBoundary.vertices());
        lensVertices.addAll(innerVertices);

        return new Lens(vertexInducedSubgraphAsIncidenceMap(lensVertices), leftBoundary,
                rightBoundary);
    }

    /**
     * Return all vertices in the component of the {@code start} vertex in the
     * graph that results from removing all {@code boundary} vertices.
     *
     * @param start
     *            start vertex
     * @param boundary
     *            a set of forbidden vertices
     * @return set of connected vertices
     */
    private Set<MedialVertex> connectedVertices(MedialVertex start,
            Set<MedialVertex> boundary) {
        Set<MedialVertex> visited = new HashSet<>();
        TreeSet<MedialVertex> queued = new TreeSet<>();
        if (!boundary.contains(start)) {
            queued.add(start);
        }
        while (!queued.isEmpty()) {
            MedialVertex next = queued.pollFirst();
            visited.add(next);
            neighbors(next).stream()
                           .filter(v -> !boundary.contains(v))
                           .filter(v -> !visited.contains(v))
                           .forEach(v -> queued.add(v));
        }
        return visited;
    }

    /**
     * Return whether the specified medial face corresponds to a vertex in the
     * underlying graph.
     *
     * <p>
     * Every medial face corresponds to either a vertex or a face in the
     * underlying graph.
     *
     * @param medialFace
     *            the face in the medial graph
     * @return true if the medial face corresponds to an underlying vertex
     */
    public boolean isUnmedialVertex(Circuit<MedialVertex> medialFace) {
        LinkedList<EdgeTail<MedialVertex>> faceEdges = medialFace.edgeList();
        if (faceEdges.isEmpty()) {
            throw new IllegalArgumentException(
                    "Medial face must contain at least one edge.");
        }
        return blackMap.containsKey(faceEdges.getFirst());
    }

    /**
     * Return the vertex in the underlying graph that corresponds to the medial
     * face.
     *
     * @param medialFace
     *            the face in the medial graph
     * @return the vertex in the original graph
     * @throws IllegalArgumentException
     *             if the medial face does not correspond to a single vertex
     */
    public AtomicVertex toUnmedialVertex(Circuit<MedialVertex> medialFace) {
        LinkedList<EdgeTail<MedialVertex>> faceEdges = medialFace.edgeList();
        if (faceEdges.isEmpty()) {
            throw new IllegalArgumentException(
                    "Medial face must contain at least one edge.");
        }
        EdgeTail<MedialVertex> faceEdge = faceEdges.getFirst();
        if (!blackMap.containsKey(faceEdge)) {
            throw new IllegalArgumentException(
                    "Medial face does not correspond to underlying vertex.");
        }
        return blackMap.get(faceEdge);
    }

    /**
     * Return an edge tail that identifies the face in the underlying graph that
     * corresponds to the specified medial face.
     *
     * @param medialFace
     *            the face in the medial graph
     * @return an edge tail in the corresponding face in the original graph
     * @throws IllegalArgumentException
     *             if the medial face does not correspond to a face
     */
    public EdgeTail<AtomicVertex> toUnmedialFace(Circuit<MedialVertex> medialFace) {
        LinkedList<EdgeTail<MedialVertex>> faceEdges = medialFace.edgeList();
        if (faceEdges.isEmpty()) {
            throw new IllegalArgumentException(
                    "Medial face must contain at least one edge.");
        }
        EdgeTail<MedialVertex> faceEdge = faceEdges.getFirst();
        if (!whiteMap.containsKey(faceEdge)) {
            throw new IllegalArgumentException(
                    "Medial face does not correspond to underlying face.");
        }
        return whiteMap.get(faceEdge);
    }

}
