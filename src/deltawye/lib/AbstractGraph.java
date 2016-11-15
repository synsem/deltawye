package deltawye.lib;

import java.util.Set;

/**
 * A graph is a pair of a set of vertices and a set of edges.
 *
 * @param <V>
 *            the type of vertices
 * @param <E>
 *            the type of edges
 *
 */
public abstract class AbstractGraph<V, E> {

    /**
     * Set of vertices in the graph.
     *
     * @return the set of vertices
     */
    public abstract Set<V> vertices();

    /**
     * Set of edges in the graph.
     *
     * @return the set of edges
     */
    public abstract Set<E> edges();

    /**
     * Number of vertices in the graph.
     *
     * @return the number of vertices
     */
    public int order() {
        return vertices().size();
    }

    /**
     * Number of edges in the graph.
     *
     * @return the number of edges
     */
    public int size() {
        return edges().size();
    }

}
