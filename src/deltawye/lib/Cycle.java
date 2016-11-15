package deltawye.lib;

import java.util.Collections;
import java.util.List;

/**
 * A cycle is a directed circuit with no repeated vertices and length at least
 * one.
 *
 * @param <V>
 *            the type of vertices
 */
public class Cycle<V extends Vertex<V>> extends Circuit<V> {

    /**
     * Create a cycle from a non-empty list of edges.
     *
     * @param edges
     *            the list of edges in the cycle
     * @throws InvalidWalkException
     *             if the edge list describes a walk that is is not closed or
     *             that contains repeated vertices
     */
    public Cycle(List<EdgeTail<V>> edges) {
        super(edges);
        if (hasRepeatedVertices()) {
            throw new InvalidWalkException("Cycle must not contain repeated vertices.");
        }
    }

    /**
     * Return a list of the vertices of this cycle in clockwise order.
     *
     * <p>
     * Note: Clockwise order is only guaranteed if this cycle was traversed in
     * clockwise order (e.g. during face detection).
     *
     * <p>
     * Note: This list does not include the end vertex (which is identical to
     * the start vertex).
     *
     * @return list of vertices
     *
     * @see deltawye.lib.Walk#vertexList()
     */
    public List<V> vertexListClockwise() {
        List<V> vlist = vertexList();
        Collections.reverse(vlist);
        return vlist;
    }

}
