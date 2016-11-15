package deltawye.lib;

import static org.junit.Assert.*;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class UndirectedEdgeTest {

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

    @Test
    public void testTraverseFrom() {
        assertEquals(v(4), e(800, 22, 4).traverseFrom(v(22)));
        try {
            e(2, 4, 4).traverseFrom(v(1));
            fail("Expected exception was not thrown.");
        } catch (NonIncidenceException ex) { // RuntimeException
            assertEquals(NonIncidenceException.class, ex.getClass());
        }
    }

    @Test
    public void testVertices() {
        assertEquals(2, e(20, 3, 2).vertices()
                                   .size());
        assertEquals(1, e(20, 4, 4).vertices()
                                   .size());
    }

    @Test
    public void testIsIncidentTo() {
        assertTrue(e(1, 2, 3).isIncidentTo(v(3)));
        assertTrue(e(1, 2, 2).isIncidentTo(v(2)));
        assertFalse(e(1, 2, 2).isIncidentTo(v(1)));
    }

    @Test
    public void testIsAdjacentTo() {
        assertTrue(e(1, 2, 3).isAdjacentTo(e(2, 3, 4)));
        assertTrue(e(1, 2, 3).isAdjacentTo(e(2, 3, 3)));
        assertFalse(e(1, 2, 2).isAdjacentTo(e(2, 3, 4)));
    }

    @Test
    public void testIsParallelTo() {
        assertTrue(e(1, 2, 3).isParallelTo(e(2, 2, 3)));
        assertTrue(e(1, 2, 3).isParallelTo(e(2, 3, 2)));
        assertFalse(e(1, 2, 3).isParallelTo(e(2, 2, 4)));
    }

    @Test
    public void testSharedVertices() {
        assertEquals(2, e(1, 22, 1).sharedVertices(e(99, 1, 22))
                                   .size());
        assertEquals(1, e(1, 22, 3).sharedVertices(e(99, 1, 22))
                                   .size());
        assertEquals(0, e(1, 22, 3).sharedVertices(e(99, 4, 4))
                                   .size());
    }

    /**
     * Two edges are equal iff they have the same ID.
     */
    @Test
    public void testEquals() {
        assertTrue(e(4, 1, 2).equals(e(4, 1, 2)));
        assertFalse(e(4, 1, 2).equals(e(5, 1, 2)));
        // Two edges with same ID should never differ in end vertices:
        // They are treated as identical.
        assertTrue(e(4, 1, 2).equals(e(4, 99, 99)));
        assertFalse(e(4, 1, 2).equals(e(5, 99, 99)));
    }

    @Test
    public void testCompareTo() {
        assertEquals(-1, e(4, 200, 200).compareTo(e(20, 1, 1)));
        assertEquals(0, e(4, 200, 200).compareTo(e(4, 99, 98)));
        assertEquals(1, e(200, 200, 200).compareTo(e(20, 1, 1)));
    }

    @Test
    public void testToString() {
        assertEquals("[e99]{2, 3}", e(99, 2, 3).toString());
    }

}
