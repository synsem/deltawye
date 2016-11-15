package deltawye.lib;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An undirected edge in a graph.
 *
 * <p>
 * The identity of an edge is determined by its integer ID value. The two end
 * vertices of the edge are redundantly stored in the edge and do not contribute
 * to the edge identity.
 *
 * <p>
 * The end vertices of an undirected edge are called {@code left} and
 * {@code right}.
 *
 * <p>
 * Objects of this class are immutable if the underlying vertex type is
 * immutable.
 *
 * @param <V>
 *            the type of vertices
 *
 */
public final class UndirectedEdge<V extends Vertex<V>>
        implements Comparable<UndirectedEdge<V>>, Edge<V> {

    /**
     * The unique integer ID value of this edge.
     */
    private final int id;

    /**
     * The left end vertex of the edge.
     */
    private final V left;

    /**
     * The right end vertex of the edge.
     */
    private final V right;

    /**
     * Create an undirected edge between two vertices.
     *
     * <p>
     * The two end vertices of the edge may be identical (loop) or distinct
     * (link).
     *
     * @param id
     *            the unique ID of this edge
     * @param left
     *            one end of the edge
     * @param right
     *            the other end of the edge
     */
    public UndirectedEdge(int id, V left, V right) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("Vertices must not be null.");
        }
        this.id = id;
        this.left = left;
        this.right = right;
    }

    /**
     * Create an undirected edge between two vertices.
     *
     * <p>
     * The set {@code ends} must contain exactly one vertex (creating a loop) or
     * exactly two vertices (creating a link).
     *
     * @param id
     *            the unique ID of this edge
     * @param ends
     *            the set of end vertices
     */
    public UndirectedEdge(int id, Set<V> ends) {
        this.id = id;
        List<V> endList = new ArrayList<>(ends);
        if (endList.size() == 1) {
            left = endList.get(0);
            right = left;
        } else if (endList.size() == 2) {
            left = endList.get(0);
            right = endList.get(1);
        } else {
            throw new IllegalArgumentException("Set must contain one or two vertices.");
        }
        if (left == null || right == null) {
            throw new IllegalArgumentException("Vertices must not be null.");
        }
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public V getLeft() {
        return left;
    }

    @Override
    public V getRight() {
        return right;
    }

    /**
     * Return the other end vertex of the edge.
     *
     * @param v
     *            a vertex of the edge
     * @return the end vertex that is not v
     * @throws NonIncidenceException
     *             if vertex v is not incident to this edge
     */
    public V traverseFrom(V v) {
        if (v.equals(left)) {
            return getRight();
        } else if (v.equals(right)) {
            return getLeft();
        } else {
            throw new NonIncidenceException();
        }
    }

    /**
     * Return the two end vertices of this edge as a set.
     *
     * @return the set of end vertices
     */
    public Set<V> vertices() {
        return Stream.of(left, right)
                     .collect(Collectors.toSet());
    }

    @Override
    public boolean isLoop() {
        return left.equals(right);
    }

    /**
     * Return whether the two edges are parallel.
     *
     * @param other
     *            the other edge
     * @return true if the edges are parallel
     */
    public boolean isParallelTo(UndirectedEdge<V> other) {
        return vertices().equals(other.vertices());
    }

    /**
     * Return whether this edge is incident to the provided vertex.
     *
     * @param v
     *            the vertex to be tested for incidence
     * @return true if the vertex is incident to this edge
     */
    public boolean isIncidentTo(V v) {
        return v.equals(left) || v.equals(right);
    }

    /**
     * Return whether this edge is adjacent to the other edge.
     *
     * <p>
     * Two edges are adjacent if they share at least one vertex. In particular,
     * for the purposes of this test, an edge is adjacent to itself.
     *
     * @param other
     *            the other edge
     * @return true if the two edges are adjacent
     */
    public boolean isAdjacentTo(UndirectedEdge<V> other) {
        return sharedVertices(other).size() > 0;
    }

    /**
     * Return a list of vertices shared by two edges.
     *
     * <p>
     * The returned list may have zero, one or two elements.
     *
     * @param other
     *            the other edge
     * @return the shared vertices
     */
    public List<V> sharedVertices(UndirectedEdge<V> other) {
        return Stream.of(left, right)
                     .filter(x -> other.isIncidentTo(x))
                     .collect(Collectors.toList());
    }

    /**
     * Return the unique vertex shared by two edges.
     *
     * @param other
     *            the other edge
     * @return the shared vertex
     * @throws NonIncidenceException
     *             if the two edges do not share a unique vertex
     */
    public V getSharedVertex(UndirectedEdge<V> other) {
        List<V> shared = sharedVertices(other);
        if (shared.size() != 1) {
            throw new NonIncidenceException();
        }
        return shared.get(0);
    }

    @Override
    public boolean connectsVerticesIn(Set<V> vertices) {
        return vertices.contains(left) && vertices.contains(right);
    }

    /**
     * Return the left tail of this edge.
     *
     * @return left tail
     */
    public EdgeTail<V> leftTail() {
        return new EdgeTail<>(this, EdgeTail.Side.LEFT);
    }

    /**
     * Return the right tail of this edge.
     *
     * @return right tail
     */
    public EdgeTail<V> rightTail() {
        return new EdgeTail<>(this, EdgeTail.Side.RIGHT);
    }

    @Override
    public int hashCode() {
        // We use the same as Integer.hashCode(id);
        return id;
    }

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
        UndirectedEdge<?> other = (UndirectedEdge<?>) obj;
        return id == other.id;
    }

    @Override
    public int compareTo(UndirectedEdge<V> other) {
        return Integer.compare(id, other.id);
    }

    /**
     * Return whether this edge is smaller than the specified other edge.
     *
     * @param other
     *            the other edge
     * @return true if this edge is smaller than the other one
     */
    public boolean isSmallerThan(UndirectedEdge<V> other) {
        return compareTo(other) < 0;
    }

    @Override
    public String toString() {
        return "[e" + id + "]{" + left + ", " + right + "}";
    }

}
