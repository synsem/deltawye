package deltawye.lib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class CircuitTest {

    /**
     * Example circuit: (0 -> 1 -> 0).
     */
    private static Circuit<AtomicVertex> exampleCircuit1 = new Circuit<>(
            Arrays.asList(dl(0, 0, 1), dr(0, 0, 1)));

    /**
     * Example circuit: c3.
     */
    private static Circuit<AtomicVertex> c3 = new Circuit<>(
            Arrays.asList(dl(0, 0, 1), dl(1, 1, 2), dl(2, 2, 0)));

    /**
     * Example circuit: c3 (with non-minimal start edge).
     */
    private static Circuit<AtomicVertex> c3a = new Circuit<>(
            Arrays.asList(dl(2, 2, 0), dl(0, 0, 1), dl(1, 1, 2)));

    /**
     * Example circuit: c4.
     */
    private static Circuit<AtomicVertex> c4 = new Circuit<>(
            Arrays.asList(dl(0, 0, 1), dl(1, 1, 2), dl(2, 2, 3), dl(3, 3, 0)));

    /**
     * Shortcut for creating new AtomicVertex instances.
     */
    private static AtomicVertex v(int i) {
        return new AtomicVertex(i);
    }

    /**
     * Shortcut for creating new UndirectedEdge instances.
     */
    private static UndirectedEdge<AtomicVertex> e(int i, int l, int r) {
        return new UndirectedEdge<>(i, v(l), v(r));
    }

    /**
     * Shortcut for creating new EdgeTail instances.
     */
    private static EdgeTail<AtomicVertex> dl(int i, int l, int r) {
        return e(i, l, r).leftTail();
    }

    /**
     * Shortcut for creating new EdgeTail instances.
     */
    private static EdgeTail<AtomicVertex> dr(int i, int l, int r) {
        return e(i, l, r).rightTail();
    }

    @Test
    public void testVertexList() {
        assertEquals(Arrays.asList(v(0), v(1)), exampleCircuit1.vertexList());
        assertEquals(Arrays.asList(v(0), v(1), v(2)), c3.vertexList());
    }

    @Test
    public void testIsClosed() {
        assertTrue(c3.isClosed());
        assertTrue(c4.isClosed());
    }

    @Test
    public void testHasRepeatedEdges() {
        assertFalse(c3.hasRepeatedEdges());
        assertFalse(c4.hasRepeatedEdges());
    }

    @Test
    public void testHasRepeatedVertices() {
        assertFalse("As a circuit, C3 has repeated vertices", c3.hasRepeatedVertices());
        assertFalse("As a circuit, C4 has repeated vertices", c4.hasRepeatedVertices());
    }

    @Test
    public void testEdgeList() {
        assertEquals(Arrays.asList(dl(0, 0, 1), dl(1, 1, 2), dl(2, 2, 0)), c3.edgeList());
    }

    @Test
    public void testEqualsObject() {
        assertTrue(c3.equals(c3a));
        assertTrue(c3a.equals(c3));
        assertEquals(c3.hashCode(), c3a.hashCode());
        assertEquals(0, c3.compareTo(c3a));
        assertFalse(c3.equals(c4));
        assertEquals(-1, c3.compareTo(c4));
    }

}
