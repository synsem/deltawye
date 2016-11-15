package deltawye.lib;

/**
 * A vertex in a graph.
 *
 * <p>
 * This class wraps an {@code int} value, somewhat like {@code Integer} but with
 * restricted functionality.
 *
 * <p>
 * Two vertices are considered equal if their integer values are equal. Objects
 * of this class are immutable.
 *
 */
public final class AtomicVertex implements Vertex<AtomicVertex> {

    /**
     * The integer value of this vertex.
     */
    private final int id;

    /**
     * Create the vertex corresponding to the specified integer value.
     *
     * @param id
     *            vertex identifier
     */
    public AtomicVertex(int id) {
        this.id = id;
    }

    @Override
    public int getID() {
        return id;
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
        AtomicVertex other = (AtomicVertex) obj;
        return id == other.id;
    }

    @Override
    public int compareTo(AtomicVertex other) {
        return Integer.compare(id, other.id);
    }

    /**
     * Return whether this vertex is smaller than the specified other vertex.
     *
     * @param other
     *            the other vertex
     * @return true if this vertex is smaller than the other
     */
    public boolean isSmallerThan(AtomicVertex other) {
        return compareTo(other) < 0;
    }

    @Override
    public String toString() {
        return Integer.toString(id);
    }

}
