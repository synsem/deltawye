package deltawye.lib;

import java.util.Set;

/**
 * A tail of an undirected edge.
 *
 * <p>
 * Every edge has two tails (half edges, darts), called {@code LEFT} and
 * {@code RIGHT}.
 *
 * @param <V>
 *            the type of vertices
 *
 */
public final class EdgeTail<V extends Vertex<V>>
        implements Edge<V>, Comparable<EdgeTail<V>> {

    /**
     * The possible sides of an edge tail.
     */
    public enum Side {
        /**
         * The left side.
         */
        LEFT,

        /**
         * The right side.
         */
        RIGHT;

        /**
         * Return the other side.
         *
         * @param side
         *            the side to reverse
         * @return the other side
         */
        public static Side reverse(Side side) {
            if (side == LEFT) {
                return RIGHT;
            } else {
                return LEFT;
            }

        }

    }

    /**
     * The edge to which this tail belongs.
     */
    private final UndirectedEdge<V> edge;

    /**
     * The side of the tail.
     */
    private final Side side;

    /**
     * Create an edge tail.
     *
     * @param edge
     *            the edge
     * @param side
     *            the side of the tail
     */
    public EdgeTail(UndirectedEdge<V> edge, Side side) {
        this.edge = edge;
        this.side = side;
    }

    /**
     * Return the side of the tail.
     *
     * @return the side
     */
    public Side getSide() {
        return side;
    }

    /**
     * Return the other tail of the edge.
     *
     * @return the tail at the other side
     */
    public EdgeTail<V> otherSide() {
        return new EdgeTail<>(edge, Side.reverse(side));
    }

    /**
     * Return the full edge.
     *
     * @return the edge
     */
    public UndirectedEdge<V> getEdge() {
        return edge;
    }

    @Override
    public int getID() {
        return edge.getID();
    }

    @Override
    public V getLeft() {
        return edge.getLeft();
    }

    @Override
    public V getRight() {
        return edge.getRight();
    }

    @Override
    public boolean isLoop() {
        return edge.isLoop();
    }

    /**
     * Return whether the two edges are parallel.
     *
     * @param other
     *            the other edge
     * @return true if the edges are parallel
     */
    public boolean isParallelTo(EdgeTail<V> other) {
        return edge.isParallelTo(other.edge);
    }

    /**
     * Return the other end vertex of the edge.
     *
     * @param v
     *            a vertex of the edge
     * @return the end vertex that is not v
     * @throws NonIncidenceException
     *             if vertex v is not incident to this edge
     * @see UndirectedEdge#traverseFrom(Vertex)
     */
    public V traverseFrom(V v) {
        return edge.traverseFrom(v);
    }

    @Override
    public boolean connectsVerticesIn(Set<V> vertices) {
        return edge.connectsVerticesIn(vertices);
    }

    /**
     * Return the source vertex.
     *
     * @return the source
     */
    public V getSource() {
        if (side == Side.LEFT) {
            return edge.getLeft();
        } else {
            return edge.getRight();
        }
    }

    /**
     * Return the target vertex.
     *
     * @return the target vertex
     */
    public V getTarget() {
        if (side == Side.LEFT) {
            return edge.getRight();
        } else {
            return edge.getLeft();
        }
    }

    /**
     * Return whether the source vertex of the other edge tail is the target
     * vertex of this edge tail. The other direction is not allowed.
     *
     * @param other
     *            the other edge
     * @return true if the other edge is adjacent to this edge
     */
    public boolean isContinuedBy(EdgeTail<V> other) {
        return getTarget().equals(other.getSource());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((edge == null) ? 0 : edge.hashCode());
        result = prime * result + ((side == null) ? 0 : side.hashCode());
        return result;
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
        EdgeTail<?> other = (EdgeTail<?>) obj;
        if (edge == null) {
            if (other.edge != null) {
                return false;
            }
        } else if (!edge.equals(other.edge)) {
            return false;
        }
        return side == other.side;
    }

    @Override
    public int compareTo(EdgeTail<V> other) {
        int byEdge = edge.compareTo(other.edge);
        if (byEdge != 0) {
            return byEdge;
        }
        return side.compareTo(other.side);
    }

    @Override
    public String toString() {
        return "[e" + getID() + "](" + getSource() + " -> " + getTarget() + ")";
    }

}
