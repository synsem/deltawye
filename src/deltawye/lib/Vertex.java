package deltawye.lib;

/**
 * A vertex in a graph.
 *
 * @param <T>
 *            the underlying vertex type
 */
public interface Vertex<T> extends Comparable<T> {

    /**
     * Return the integer identifier of this vertex.
     *
     * @return vertex ID
     */
    public int getID();

}
