package deltawye.lib;

import java.util.*;

/**
 * A circuit is a closed walk in which no edge is repeated.
 *
 * <p>
 * Note: Since a walk uses edge tails, a circuit may contain two occurrences of
 * an undirected edge if they occur with opposite sides.
 *
 * @param <V>
 *            the type of vertices
 */
public class Circuit<V extends Vertex<V>> extends Walk<V> {

    /**
     * Create a trivial circuit at vertex {@code start} with an empty list of
     * edges.
     *
     * @param start
     *            the start vertex
     */
    public Circuit(V start) {
        super(start);
    }

    /**
     * Create a circuit from a non-empty list of edge tails.
     *
     * @param edges
     *            the list of edge tails in the circuit
     * @throws InvalidWalkException
     *             if the walk is not closed or contains repeated edges
     */
    public Circuit(List<EdgeTail<V>> edges) {
        super(normalize(edges));
        if (!isClosed()) {
            throw new InvalidWalkException("Circuit must be closed.");
        }
        if (hasRepeatedEdges()) {
            throw new InvalidWalkException("Circuit must not contain repeated edges.");
        }
    }

    /**
     * The list of vertices in the circuit.
     *
     * <p>
     * Note: This list does not include the end vertex (which is identical to
     * the start vertex).
     *
     * @see deltawye.lib.Walk#vertexList()
     */
    @Override
    public LinkedList<V> vertexList() {
        LinkedList<V> vlist = super.vertexList();
        if (vlist.size() > 1) {
            vlist.removeLast();
        }
        return vlist;
    }

    /**
     * Return the list of edge tails in the circuit as a rotation list.
     *
     * @return list of edge tails
     */
    public RotationList<EdgeTail<V>> edgeRotationList() {
        return new RotationList<>(edgeList());
    }

    /**
     * Normalize a list of edge tails.
     *
     * <p>
     * Assumption: No element occurs twice in the list. (Thus, there is a unique
     * minimal element.)
     *
     * <p>
     * The list is normalized by rotating its elements in such a way that the
     * minimum is the first element of the list.
     *
     * @param edges
     *            list of edge tails in a walk
     * @return normalized copy of the list of edges
     */
    private static <T extends Comparable<T>> List<T> normalize(List<T> edges) {
        List<T> edgeList = new ArrayList<>(edges);
        T minEdge = Collections.min(edgeList);
        int minEdgeIdx = edgeList.indexOf(minEdge);
        Collections.rotate(edgeList, -minEdgeIdx);
        return edgeList;
    }

    /**
     * Return whether the circuit is a loop (monogon).
     *
     * @return true if the circuit is a loop
     */
    public boolean isLoop() {
        return length() == 1;
    }

    /**
     * Return whether the circuit is a digon.
     *
     * @return true if the circuit is a digon
     */
    public boolean isDigon() {
        return length() == 2 && edgesUndirected().size() == 2;
    }

    /**
     * Return whether the circuit is a triangle.
     *
     * @return true if the circuit is a triangle
     */
    public boolean isTriangle() {
        return length() == 3 && edgesUndirected().size() == 3;
    }

}
