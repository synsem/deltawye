package deltawye.lib;

import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A combinatorial representation of an embedded graph.
 *
 * @param <V>
 *            the type of vertices
 * @param <E>
 *            the type of edges
 * @param <F>
 *            the type of faces
 */
public abstract class AbstractPlaneGraph<V, E, F> extends AbstractGraph<V, E> {

    /**
     * Set of faces in the graph.
     *
     * @return the set of faces
     */
    public abstract Set<F> faces();

    /**
     * Number of faces in the graph.
     *
     * @return the number of faces
     */
    public int numberOfFaces() {
        return faces().size();
    }

    /**
     * Return the list of vertices that are adjacent to the specified vertex.
     *
     * The list respects the rotation order of the embedding of the plane graph.
     *
     * @param vertex
     *            the vertex whose neighbors are returned
     * @return the list of neighbors (adjacent vertices)
     * @throws NoSuchElementException
     *             if the vertex is not in the graph
     */
    public abstract RotationList<V> neighbors(V vertex);

    /**
     * Return the list of edges that are incident to the specified vertex.
     *
     * The list respects the rotation order of the embedding of the plane graph.
     *
     * @param vertex
     *            the vertex whose incident edges are returned
     * @return the list of incident edges
     * @throws NoSuchElementException
     *             if the vertex is not in the graph
     */
    public abstract RotationList<E> incidentEdges(V vertex);

    /**
     * Return the degree of the specified vertex.
     *
     * @param vertex
     *            the vertex whose degree is returned
     * @return the number of edges incident to the vertex
     * @throws NoSuchElementException
     *             if the vertex is not in the graph
     */
    public int degree(V vertex) {
        return incidentEdges(vertex).size();
    }

}
