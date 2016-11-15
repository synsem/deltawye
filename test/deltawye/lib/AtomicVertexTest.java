package deltawye.lib;

import static org.junit.Assert.*;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class AtomicVertexTest {

    /**
     * Shortcut for creating new AtomicVertex instances.
     */
    private static AtomicVertex v(int i) {
        return new AtomicVertex(i);
    }

    /**
     * Vertices behave as expected in collections.
     */
    @Test
    public void testVerticesInCollection() {
        Set<AtomicVertex> s1 = Stream.of(2, 3, 4)
                                     .map(AtomicVertex::new)
                                     .collect(Collectors.toSet());
        s1.add(v(2));
        s1.add(v(5));
        s1.remove(v(3));
        Set<AtomicVertex> s2 = Stream.of(5, 2, 4)
                                     .map(AtomicVertex::new)
                                     .collect(Collectors.toSet());
        assertEquals(s2, s1);
    }

    @Test
    public void testGetID() {
        assertEquals(22, v(22).getID());
    }

    @Test
    public void testEqualsObject() {
        assertTrue(v(4).equals(v(4)));
        assertFalse(v(4).equals(v(2)));
    }

    @Test
    public void testCompareTo() {
        assertEquals(-1, v(2).compareTo(v(4)));
        assertEquals(0, v(4).compareTo(v(4)));
        assertEquals(1, v(4).compareTo(v(1)));
    }

    @Test
    public void testIsSmallerThan() {
        assertTrue(v(2).isSmallerThan(v(33)));
        assertFalse(v(2).isSmallerThan(v(1)));
    }

    @Test
    public void testToString() {
        assertEquals("432", v(432).toString());
    }

    @Test
    public void testHashCode() {
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 25; j += 2) {
                AtomicVertex vi = v(i);
                AtomicVertex vj = v(j);
                if (vi.equals(vj)) {
                    assertEquals("Equal vertices must have same hash code", vi.hashCode(),
                            vj.hashCode());
                } else {
                    assertNotEquals("Nonequal vertices should have distinct hash code",
                            vi.hashCode(), vj.hashCode());
                }
            }
        }
    }

}
