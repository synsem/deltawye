package deltawye.lib;

import static org.junit.Assert.*;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class EdgeTailTest {

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
    private static EdgeTail<AtomicVertex> d(int i, int l, int r, EdgeTail.Side side) {
        return new EdgeTail<>(e(i, l, r), side);
    }

    /**
     * Shortcut for creating new EdgeTail instances.
     */
    private static EdgeTail<AtomicVertex> dl(int i, int l, int r) {
        return d(i, l, r, EdgeTail.Side.LEFT);
    }

    /**
     * Shortcut for creating new EdgeTail instances.
     */
    private static EdgeTail<AtomicVertex> dr(int i, int l, int r) {
        return d(i, l, r, EdgeTail.Side.RIGHT);
    }

    @Test
    public void testIsContinuedBy() {
        assertTrue(dl(2, 3, 4).isContinuedBy(dl(1, 4, 2)));
        assertTrue(dl(2, 4, 4).isContinuedBy(dl(1, 4, 2)));
        assertFalse(dl(2, 3, 4).isContinuedBy(dl(1, 3, 4)));
        assertTrue(dl(2, 3, 4).isContinuedBy(dr(1, 3, 4)));
        assertFalse(dl(2, 3, 4).isContinuedBy(dl(1, 3, 8)));
        assertFalse(dl(2, 3, 4).isContinuedBy(dl(1, 8, 9)));
    }

    @Test
    public void testGetEdge() {
        assertEquals(e(1, 2, 3), dl(1, 2, 3).getEdge());
        assertEquals(e(1, 2, 3), dr(1, 2, 3).getEdge());
        assertNotEquals(e(1, 2, 3), dl(2, 2, 3).getEdge());
    }

    @Test
    public void testReversed() {
        assertEquals(dl(1, 2, 3), dr(1, 2, 3).otherSide());
        assertEquals(dr(1, 2, 3), dl(1, 2, 3).otherSide());
    }

    @Test
    public void testToString() {
        assertEquals("[e1](3 -> 4)", dl(1, 3, 4).toString());
        assertEquals("[e1](9 -> 8)", dr(1, 8, 9).toString());
    }

    @Test
    public void testEqualsObject() {
        assertTrue(dl(1, 2, 3).equals(dl(1, 2, 3)));
        assertTrue(dl(1, 2, 3).equals(dl(1, 777, 999)));
        assertFalse(dl(1, 2, 3).equals(dl(2, 2, 3)));
    }

}
