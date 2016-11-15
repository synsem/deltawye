package deltawye.lib;

import java.util.Set;

/**
 * An edge in a graph.
 *
 * @param <V>
 *            the type of vertices
 *
 */
public interface Edge<V extends Vertex<V>> {

    /**
     * Return the integer identifier of this edge.
     *
     * @return edge ID
     */
    public int getID();

    /**
     * Return left end vertex.
     *
     * @return vertex
     */
    public V getLeft();

    /**
     * Return right end vertex.
     *
     * @return vertex
     */
    public V getRight();

    /**
     * Return whether this edge is a loop.
     *
     * @return true if this edge is a loop
     */
    public boolean isLoop();

    /**
     * Return whether the edge connects vertices from the specified set.
     *
     * @param vertices
     *            a set of vertices
     * @return true if all end vertices of this edge are in the specified vertex
     *         set
     */
    public boolean connectsVerticesIn(Set<V> vertices);

}
