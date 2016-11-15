package deltawye.lib;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A walk in a graph.
 *
 * <p>
 * A walk consists of a start vertex and a (possibly empty) list of edges
 * (formally edge tails), such that the source vertex of the first edge is the
 * start vertex of the walk and the source vertices of all other edges in the
 * list are identical to the target vertex of the edge that precedes them in the
 * list.
 *
 * @param <V>
 *            the type of vertices
 */
public class Walk<V extends Vertex<V>> extends AbstractGraph<V, EdgeTail<V>>
        implements Comparable<Walk<V>> {

    /**
     * The start vertex of the walk.
     */
    private V start;

    /**
     * The end vertex of the walk.
     */
    private V end;

    /**
     * The list of edge tails in the walk that connect the start vertex to the
     * end vertex.
     */
    private LinkedList<EdgeTail<V>> edgeTails;

    /**
     * Create a trivial walk at vertex {@code start} with an empty list of
     * edges.
     *
     * @param start
     *            the start vertex
     */
    public Walk(V start) {
        if (start == null) {
            throw new InvalidWalkException("Start vertex must not be null.");
        }
        this.start = start;
        end = start;
        edgeTails = new LinkedList<>();
    }

    /**
     * Create a non-trivial walk from a non-empty list of edges.
     *
     * @param edges
     *            the list of edges in the walk
     */
    public Walk(List<EdgeTail<V>> edges) {
        if (edges == null) {
            throw new InvalidWalkException("Edge list must not be null.");
        }
        if (edges.isEmpty()) {
            throw new InvalidWalkException("Edge list must not be empty.");
        }
        for (int i = 0; i < edges.size() - 1; i++) {
            if (!edges.get(i)
                      .isContinuedBy(edges.get(i + 1))) {
                throw new InvalidWalkException(
                        "Consecutive edges in a walk must be adjacent.");
            }
        }
        this.edgeTails = new LinkedList<>(edges);
        start = this.edgeTails.getFirst()
                              .getSource();
        end = this.edgeTails.getLast()
                            .getTarget();
    }

    @Override
    public Set<V> vertices() {
        return new HashSet<>(vertexList());
    }

    @Override
    public Set<EdgeTail<V>> edges() {
        return new HashSet<>(edgeTails);
    }

    /**
     * Return the length of this walk, i.e. the number of edges.
     *
     * <p>
     * Note: Since a walk may contain the same edge multiple times, the length
     * of a walk need not be equal to its size (i.e. the number of distinct
     * edges in the walk).
     *
     * @return length
     */
    public int length() {
        return edgeTails.size();
    }

    /**
     * Return the set of edges, but each edge converted to an undirected edge.
     *
     * @return the set of undirected edges in this walk
     */
    public Set<UndirectedEdge<V>> edgesUndirected() {
        return edgeTails.stream()
                        .map(EdgeTail::getEdge)
                        .collect(Collectors.toSet());
    }

    /**
     * Return the start vertex of the walk.
     *
     * @return the start vertex
     */
    public V getStartVertex() {
        return start;
    }

    /**
     * Return the end vertex of the walk.
     *
     * @return the end vertex
     */
    public V getEndVertex() {
        return end;
    }

    /**
     * Return whether this walk contains the specified vertex.
     *
     * @param vertex
     *            some vertex
     * @return true if this walk contains that vertex
     */
    public boolean contains(V vertex) {
        return vertices().contains(vertex);
    }

    /**
     * Return the set of edges that are shared between two walks, with all edges
     * being treated as undirected.
     *
     * @param other
     *            the other walk
     * @return the set of shared edges
     */
    public Set<UndirectedEdge<V>> intersectEdges(Walk<V> other) {
        Set<UndirectedEdge<V>> sharedEdges = edgesUndirected();
        sharedEdges.retainAll(other.edgesUndirected());
        return sharedEdges;
    }

    /**
     * Return whether the two walks share at least one edge, with all edges
     * being treated as undirected.
     *
     * @param other
     *            the other walk
     * @return true if the walks share at least one edge
     */
    public boolean sharesEdgesWith(Walk<V> other) {
        return !intersectEdges(other).isEmpty();
    }

    /**
     * Return whether this walk is closed.
     *
     * <p>
     * A walk is closed if its start vertex is identical to its end vertex.
     *
     * @return true if this walk is closed
     */
    public boolean isClosed() {
        return start.equals(end);
    }

    /**
     * Return whether the walk contains repeated edges.
     *
     * <p>
     * More precisely: Return whether the walk contains repeated edge tails. Two
     * edge tails of the same edge are not considered a repeated edge.
     *
     * @return true if the walk has repeated edges
     */
    public boolean hasRepeatedEdges() {
        return size() != length();
    }

    /**
     * Return whether the walk contains repeated vertices.
     *
     * @return true if the walk has repeated vertices
     */
    public boolean hasRepeatedVertices() {
        return vertices().size() != vertexList().size();
    }

    /**
     * Return whether this walk contains repeated inner vertices.
     *
     * <p>
     * In other words, the method will return true if this walk contains a
     * repeated vertex except for the end vertex which may be identical to the
     * start vertex.
     *
     * @return true if this walk has repeated inner vertices
     */
    private boolean hasRepeatedInnerVertices() {
        Set<V> visited = new HashSet<>(); // without start vertex
        List<V> vertices = vertexList();
        // traverse vertices without end vertex
        for (int i = 1; i < vertices.size() - 1; i++) {
            V v = vertices.get(i);
            if (v.equals(start) || visited.contains(v)) {
                return true;
            }
            visited.add(v);
        }
        // finally check end vertex
        return visited.contains(end);
    }

    /**
     * Return whether this walk is a trail.
     *
     * <p>
     * A trail is a walk with no edge repeated.
     *
     * @return true if the walk is a trail
     */
    public boolean isTrail() {
        return !hasRepeatedEdges();
    }

    /**
     * Return whether this walk is a path.
     *
     * <p>
     * A path is a walk with no vertex repeated.
     *
     * @return true if the walk is a path
     */
    public boolean isPath() {
        return !hasRepeatedVertices();
    }

    /**
     * Return whether this walk is a circuit.
     *
     * <p>
     * A circuit is a closed trail.
     *
     * @return true if this walk is a circuit
     */
    public boolean isCircuit() {
        return isClosed() && isTrail();
    }

    /**
     * Convert this walk to a circuit.
     *
     * @throws InvalidWalkException
     *             if this walk is no circuit (see {@link #isCircuit()}).
     * @return this walk as a circuit object
     */
    public Circuit<V> toCircuit() {
        if (length() == 0) {
            return new Circuit<>(start);
        }
        return new Circuit<>(edgeTails);
    }

    /**
     * Return whether this walk is a cycle.
     *
     * <p>
     * A cycle is a circuit of length at least one that repeats no vertex,
     * except for the end vertex which must be identical to the start vertex.
     *
     * @return true if the walk is a cycle
     */
    public boolean isCycle() {
        return isCircuit() && !hasRepeatedInnerVertices();
    }

    /**
     * Convert this walk to a cycle.
     *
     * @throws InvalidWalkException
     *             if this walk is no cycle (see {@link #isCycle()}).
     * @return this walk as a cycle object
     */
    public Cycle<V> toCycle() {
        return new Cycle<>(edgeTails);
    }

    /**
     * Return the set of vertices that are shared between two walks.
     *
     * @param other
     *            the other walk
     * @return the set of shared vertices
     */
    public Set<V> intersectVertices(Walk<V> other) {
        Set<V> sharedVertices = vertices();
        sharedVertices.retainAll(other.vertices());
        return sharedVertices;
    }

    /**
     * Return the list of vertices in the walk.
     *
     * <p>
     * Note: Subclasses for closed walks may override this method to exclude the
     * end vertex. This also affects the result of
     * {@link #hasRepeatedVertices()}.
     *
     * @return the list of vertices
     */
    public LinkedList<V> vertexList() {
        LinkedList<V> vertices = new LinkedList<>();
        vertices.add(start);
        for (EdgeTail<V> e : edgeTails) {
            vertices.add(e.getTarget());
        }
        return vertices;
    }

    /**
     * Return the list of edge tails in the walk.
     *
     * @return the list of edges
     */
    public LinkedList<EdgeTail<V>> edgeList() {
        return new LinkedList<>(edgeTails);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((start == null) ? 0 : start.hashCode());
        result = prime * result + ((edgeTails == null) ? 0 : edgeTails.hashCode());
        return result;
    }

    /**
     * Two walks are equal iff they have the same start vertex and the same list
     * of edges.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Walk<?> other = (Walk<?>) obj;
        if (start == null) {
            if (other.start != null) {
                return false;
            }
        } else if (!start.equals(other.start)) {
            return false;
        }
        if (edgeTails == null) {
            if (other.edgeTails != null) {
                return false;
            }
        } else if (!edgeTails.equals(other.edgeTails)) {
            return false;
        }
        return true;
    }

    /**
     * Two walks are compared by (1) length, (2) start vertex, (3) edge tails.
     */
    @Override
    public int compareTo(Walk<V> other) {
        int byLength = Integer.compare(length(), other.length());
        if (byLength != 0) {
            return byLength;
        }
        int byStart = getStartVertex().compareTo(other.getStartVertex());
        if (byStart != 0) {
            return byStart;
        }
        for (int i = 0; i < length(); i++) {
            int byEdge = edgeTails.get(i)
                                  .compareTo(other.edgeTails.get(i));
            if (byEdge != 0) {
                return byEdge;
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder desc = new StringBuilder("(" + start);
        for (EdgeTail<V> e : edgeList()) {
            desc.append(" -> " + e.getTarget());
        }
        desc.append(")");
        return desc.toString();

    }

}
