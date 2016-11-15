package deltawye.lib;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class MedialGraphTest {

    private static AtomicPlaneGraph createPlaneK2() {
        int[][] k2 = { { 1, 2 }, { 2, 1 } };
        return AtomicPlaneGraph.readAdjacencyList(k2);
    }

    private static AtomicPlaneGraph createPlaneK3() {
        int[][] k3 = { { 1, 2, 3 }, { 2, 1, 3 }, { 3, 1, 2 } };
        return AtomicPlaneGraph.readAdjacencyList(k3);
    }

    private static AtomicPlaneGraph createPlaneK4() {
        return AtomicPlaneGraph.readAdjacencyList(GraphData.K4);
    }

    @Test
    public void testLoopGraph() {
        AtomicPlaneGraph lg = AtomicPlaneGraph.readIncidenceList(
                GraphData.LOOP_INCIDENCE);
        assertEquals(1, lg.order());
        assertEquals(1, lg.size());
        assertEquals(2, lg.numberOfFaces());
        MedialGraph lgm = MedialGraph.fromAtomicPlaneGraph(lg);
        assertEquals(1, lgm.order());
        assertEquals(2, lgm.size());
        assertEquals(3, lgm.numberOfFaces());
    }

    @Test
    public void testK2() {
        AtomicPlaneGraph k2 = createPlaneK2();
        assertEquals(2, k2.order());
        assertEquals(1, k2.size());
        assertEquals(1, k2.numberOfFaces());
        MedialGraph k2m = MedialGraph.fromAtomicPlaneGraph(k2);
        assertEquals(1, k2m.order());
        assertEquals(2, k2m.size());
        assertEquals(3, k2m.numberOfFaces());
    }

    @Test
    public void testK3() {
        AtomicPlaneGraph k3 = createPlaneK3();
        assertEquals(3, k3.order());
        assertEquals(3, k3.size());
        assertEquals(2, k3.numberOfFaces());
        MedialGraph k3m = MedialGraph.fromAtomicPlaneGraph(k3);
        assertEquals(3, k3m.order());
        assertEquals(6, k3m.size());
        assertEquals(5, k3m.numberOfFaces());
    }

    @Test
    public void testGeodesics() {
        MedialGraph k4 = MedialGraph.fromAtomicPlaneGraph(createPlaneK4());
        assertEquals(3, k4.geodesics()
                          .size());
    }

    @Test
    public void testLenses() {
        MedialGraph k4 = MedialGraph.fromAtomicPlaneGraph(createPlaneK4());
        assertEquals(12, k4.lenses()
                           .size());
    }

}
