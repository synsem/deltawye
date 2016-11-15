package deltawye.lib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class PlaneGraphTest {

    private static PlaneGraph<AtomicVertex> createPlaneC3() {
        int[][] c3 = { { 0, 1, 2 }, { 1, 0, 2 }, { 2, 0, 1 } };
        return AtomicPlaneGraph.readAdjacencyList(c3);
    }

    private static PlaneGraph<AtomicVertex> createPlaneP4() {
        int[][] p4 = { { 0, 1 }, { 1, 0, 2 }, { 2, 1, 3 }, { 3, 2 } };
        return AtomicPlaneGraph.readAdjacencyList(p4);
    }

    private static PlaneGraph<AtomicVertex> createPlaneK4() {
        return AtomicPlaneGraph.readAdjacencyList(GraphData.K4);
    }

    private static PlaneGraph<AtomicVertex> createPlaneCube() {
        return AtomicPlaneGraph.readAdjacencyList(GraphData.CUBE);
    }

    @Test
    public void testCreateCube() {
        PlaneGraph<AtomicVertex> cube = createPlaneCube();
        assertEquals(8, cube.order());
        assertEquals(12, cube.size());
        assertEquals(6, cube.numberOfFaces());
        assertTrue(cube.isSimple());
    }

    @Test
    public void testVertexInducedSubgraph() {
        PlaneGraph<AtomicVertex> c4 = createPlaneCube().vertexInducedSubgraph(
                Stream.of(0, 1, 2, 3)
                      .map(AtomicVertex::new)
                      .collect(Collectors.toSet()));
        assertEquals(4, c4.order());
        assertEquals(4, c4.size());
        assertEquals(2, c4.numberOfFaces());
        assertTrue(c4.isSimple());
    }

    @Test
    public void testFaces() {
        assertEquals(6, createPlaneCube().numberOfFaces());
        assertEquals(2, createPlaneC3().numberOfFaces());
    }

    @Test
    public void testAreNeighbors() {
        PlaneGraph<AtomicVertex> cube = createPlaneCube();
        assertTrue(cube.areNeighbors(new AtomicVertex(3), new AtomicVertex(5)));
        assertFalse(cube.areNeighbors(new AtomicVertex(1), new AtomicVertex(5)));
    }

    @Test
    public void testReduceSeries() {
        PlaneGraph<AtomicVertex> p4 = createPlaneP4();
        assertEquals(3, p4.size());
        assertFalse(p4.areNeighbors(new AtomicVertex(0), new AtomicVertex(2)));
        p4.reduceSeries(new AtomicVertex(1));
        assertEquals(2, p4.size());
        assertTrue(p4.areNeighbors(new AtomicVertex(0), new AtomicVertex(2)));
    }

    @Test
    public void testReduceWyeDelta() {
        PlaneGraph<AtomicVertex> k4 = createPlaneK4();
        assertTrue(k4.isK4());
        assertEquals(4, k4.order());
        assertEquals(6, k4.size());
        k4.reduceWyeDelta(new AtomicVertex(4));
        assertFalse(k4.isK4());
        assertEquals(3, k4.order());
        assertEquals(6, k4.size());
    }

    @Test
    public void testReduceOmega() {
        PlaneGraph<AtomicVertex> k4 = createPlaneK4();
        k4.reduceOmega(new AtomicVertex(4));
        assertFalse(k4.isK4());
        assertEquals(3, k4.order());
        assertEquals(3, k4.size());
    }

    @Test
    public void testReduceDeltaWye() {
        PlaneGraph<AtomicVertex> k4 = createPlaneK4();
        Circuit<AtomicVertex> face = new ArrayList<>(k4.faces()).get(0);
        k4.reduceDeltaWye(face, new AtomicVertex(k4.getUnusedVertexID()));
        assertFalse(k4.isK4());
        assertEquals(5, k4.order());
        assertEquals(6, k4.size());
        assertEquals(3, k4.numberOfFaces());
    }

    @Test
    public void testReduceEta() {
        PlaneGraph<AtomicVertex> k4 = createPlaneK4();
        Circuit<AtomicVertex> face = new ArrayList<>(k4.faces()).get(0);
        k4.reduceEta(face, new AtomicVertex(k4.getUnusedVertexID()));
        assertFalse(k4.isK4());
        assertEquals(2, k4.order());
        assertEquals(3, k4.size());
        assertEquals(3, k4.numberOfFaces());
    }

}
