package deltawye.lib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class WalkTest {

    /**
     * Example walk with repeated edges and vertices.
     */
    private static Walk<AtomicVertex> exampleWalk1 = new Walk<>(
            Arrays.asList(dl(0, 0, 1), dl(1, 1, 2), dr(1, 1, 2), dl(1, 1, 2)));

    /**
     * Example walk: p4.
     */
    private static Walk<AtomicVertex> p4 = new Walk<>(
            Arrays.asList(dl(0, 0, 1), dl(1, 1, 2), dl(2, 2, 3), dl(3, 3, 4)));

    /**
     * Example walk: c3.
     */
    private static Walk<AtomicVertex> c3 = new Walk<>(
            Arrays.asList(dl(0, 0, 1), dl(1, 1, 2), dl(2, 2, 0)));

    /**
     * Example walk: c4.
     */
    private static Walk<AtomicVertex> c4 = new Walk<>(
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
     * Shortcut for creating new DirectedEdge instances.
     */
    private static EdgeTail<AtomicVertex> dl(int i, int l, int r) {
        return e(i, l, r).leftTail();
    }

    /**
     * Shortcut for creating new DirectedEdge instances.
     */
    private static EdgeTail<AtomicVertex> dr(int i, int l, int r) {
        return e(i, l, r).rightTail();
    }

    @Test
    public void testIsClosed() {
        assertTrue(c3.isClosed());
        assertTrue(c4.isClosed());
        assertFalse(p4.isClosed());
    }

    @Test
    public void testHasRepeatedEdges() {
        assertTrue(exampleWalk1.hasRepeatedEdges());
        assertFalse(c3.hasRepeatedEdges());
        assertFalse(c4.hasRepeatedEdges());
        assertFalse(p4.hasRepeatedEdges());
    }

    @Test
    public void testHasRepeatedVertices() {
        assertTrue(exampleWalk1.hasRepeatedVertices());
        assertTrue("As a walk, C3 has repeated vertices", c3.hasRepeatedVertices());
        assertTrue("As a walk, C4 has repeated vertices", c4.hasRepeatedVertices());
        assertFalse(p4.hasRepeatedVertices());
    }

    @Test
    public void testVertexList() {
        assertEquals(Arrays.asList(v(0), v(1), v(2), v(0)), c3.vertexList());
    }

    @Test
    public void testEdgeList() {
        assertEquals(Arrays.asList(dl(0, 0, 1), dl(1, 1, 2), dl(2, 2, 0)), c3.edgeList());
    }

    @Test
    public void testEqualsObject() {
        assertFalse(c3.equals(c4));
        assertTrue(c3.equals(new Walk<>(c3.edgeList())));
    }

    @Test
    public void testToString() {
        assertEquals("(0 -> 1 -> 2 -> 1 -> 2)", exampleWalk1.toString());
        assertEquals("(0 -> 1 -> 2 -> 0)", c3.toString());
    }

}
