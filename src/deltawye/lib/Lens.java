package deltawye.lib;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * A lens is a plane subgraph of a medial graph.
 *
 * <p>
 * This class models 2-lenses which have exactly two boundaries.
 *
 */
public class Lens extends PlaneGraph<MedialVertex> implements Comparable<Lens> {

    /**
     * The left boundary of the lens.
     */
    private final Walk<MedialVertex> leftBoundary;

    /**
     * The right boundary of the lens.
     */
    private final Walk<MedialVertex> rightBoundary;

    /**
     * Construct a Lens from an incidence map.
     *
     * @param incidenceMap
     *            the incidence map of the lens graph
     * @param leftBoundary
     *            left boundary of the lens
     * @param rightBoundary
     *            right boundary of the lens
     */
    public Lens(Map<MedialVertex, RotationList<EdgeTail<MedialVertex>>> incidenceMap,
            Walk<MedialVertex> leftBoundary, Walk<MedialVertex> rightBoundary) {
        super(incidenceMap);
        this.leftBoundary = leftBoundary;
        this.rightBoundary = rightBoundary;
    }

    /**
     * Return the set of inner edges.
     *
     * <p>
     * The inner edges are all edges of the lens that are not part of the
     * boundary.
     *
     * @return the set of inner edges
     */
    public Set<UndirectedEdge<MedialVertex>> innerEdges() {
        Set<UndirectedEdge<MedialVertex>> innerEdges = edges();
        innerEdges.removeAll(leftBoundary.edgesUndirected());
        innerEdges.removeAll(rightBoundary.edgesUndirected());
        return innerEdges;
    }

    /**
     * Return the set of inner edge tails.
     *
     * <p>
     * The inner edge tails are all edge tails of the lens that are not part of
     * the boundary.
     *
     * @return the set of inner edge tails
     */
    public Set<EdgeTail<MedialVertex>> innerEdgeTails() {
        Set<EdgeTail<MedialVertex>> innerEdges = edgeTails();
        innerEdges.removeAll(leftBoundary.edges());
        innerEdges.removeAll(rightBoundary.edges());
        return innerEdges;
    }

    /**
     * Return whether the specified vertex is part of the lens boundary.
     *
     * @param v
     *            the vertex to test
     * @return true if the vertex is part of the lens boundary
     */
    public boolean isBoundaryVertex(MedialVertex v) {
        return leftBoundary.contains(v) || rightBoundary.contains(v);
    }

    /**
     * Return the set of inner faces of the lens.
     *
     * <p>
     * Like {@link #faces()} but without the outer face.
     *
     * @return the set of inner faces
     */
    public Set<Circuit<MedialVertex>> innerFaces() {
        Circuit<MedialVertex> outerFace = getFace(leftBoundary.edgeList()
                                                              .getFirst());
        Set<Circuit<MedialVertex>> faces = faces();
        faces.remove(outerFace);
        return faces;
    }

    /**
     * Return the number of inner faces of a lens.
     *
     * <p>
     * The outer face of a lens graph is usually not counted as a face of the
     * lens.
     *
     * @return the number of inner faces
     */
    public int numberOfInnerFaces() {
        return innerFaces().size();
    }

    /**
     * Return the set of faces that are incident to a boundary edge of the lens.
     *
     * <p>
     * This method returns a TreeSet in order to allow direct access to the
     * smallest and largest boundary faces.
     *
     * @return the set of boundary faces
     */
    public TreeSet<Circuit<MedialVertex>> boundaryFaces() {
        TreeSet<Circuit<MedialVertex>> boundaryFaces = new TreeSet<>();
        for (Circuit<MedialVertex> f : innerFaces()) {
            if (f.sharesEdgesWith(leftBoundary) || f.sharesEdgesWith(rightBoundary)) {
                boundaryFaces.add(f);
            }
        }
        return boundaryFaces;
    }

    /**
     * Return whether the specified circuit contains one of the poles of this
     * lens.
     *
     * @param circuit
     *            some medial circuit
     * @return true if the circuit contains one of the lens poles
     */
    public boolean isPolar(Circuit<MedialVertex> circuit) {
        MedialVertex southPole = leftBoundary.getStartVertex();
        MedialVertex northPole = leftBoundary.getEndVertex();
        return circuit.contains(southPole) || circuit.contains(northPole);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((leftBoundary == null) ? 0 : leftBoundary.hashCode());
        result = prime * result
                + ((rightBoundary == null) ? 0 : rightBoundary.hashCode());
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
        Lens other = (Lens) obj;
        if (leftBoundary == null) {
            if (other.leftBoundary != null) {
                return false;
            }
        } else if (!leftBoundary.equals(other.leftBoundary)) {
            return false;
        }
        if (rightBoundary == null) {
            if (other.rightBoundary != null) {
                return false;
            }
        } else if (!rightBoundary.equals(other.rightBoundary)) {
            return false;
        }
        return true;
    }

    /**
     * Two lenses are compared by (1) number of inner faces, (2) left boundary
     * walk, (3) right boundary walk.
     */
    @Override
    public int compareTo(Lens o) {
        int byInnerFaceCount = Integer.compare(numberOfInnerFaces(),
                o.numberOfInnerFaces());
        if (byInnerFaceCount != 0) {
            return byInnerFaceCount;
        }
        int byLeftBoundary = leftBoundary.compareTo(o.leftBoundary);
        if (byLeftBoundary != 0) {
            return byLeftBoundary;
        }
        return rightBoundary.compareTo(o.rightBoundary);
    }

}
