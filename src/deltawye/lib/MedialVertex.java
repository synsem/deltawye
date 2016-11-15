package deltawye.lib;

/**
 * A vertex in a medial graph.
 *
 * <p>
 * This wraps an undirected edge of the underlying original graph.
 *
 * <p>
 * The identity of a MedialVertex is determined by its integer ID value that is
 * inherited from the wrapped edge ID.
 */
public final class MedialVertex implements Vertex<MedialVertex> {

    /**
     * The integer value of this vertex.
     */
    private final int id;

    /**
     * The undirected original edge represented by this medial vertex.
     */
    private final UndirectedEdge<AtomicVertex> underlyingEdge;

    /**
     * Construct a medial vertex from a regular edge.
     *
     * @param underlyingEdge
     *            undirected edge
     */
    public MedialVertex(UndirectedEdge<AtomicVertex> underlyingEdge) {
        id = underlyingEdge.getID();
        this.underlyingEdge = underlyingEdge;
    }

    @Override
    public int getID() {
        return id;
    }

    /**
     * Return the underlying edge of the medial vertex.
     *
     * @return the underlying edge
     *
     */
    public UndirectedEdge<AtomicVertex> getUnderlyingEdge() {
        return underlyingEdge;
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
        MedialVertex other = (MedialVertex) obj;
        return id == other.id;
    }

    @Override
    public int compareTo(MedialVertex other) {
        return Integer.compare(id, other.id);
    }

    @Override
    public String toString() {
        return Integer.toString(id);
    }

}
