package deltawye.lib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class AtomicPlaneGraphTest {

    @Test
    public void testK4ReadAdjacencyListIntArrayArray() {
        AtomicPlaneGraph k4 = AtomicPlaneGraph.readAdjacencyList(GraphData.K4);
        assertTrue(k4.isK4());
        assertEquals(4, k4.getDeltaFaces()
                          .count());
        assertEquals(4, k4.getWyeVertices()
                          .count());
    }

    @Test
    public void testK4ReadIncidenceListIntArrayArray() {
        AtomicPlaneGraph k4 = AtomicPlaneGraph.readIncidenceList(GraphData.K4_INCIDENCE);
        assertTrue(k4.isK4());
        assertTrue(k4.isSimple());
        assertEquals(4, k4.getDeltaFaces()
                          .count());
        assertEquals(4, k4.getWyeVertices()
                          .count());
    }

    @Test
    public void testLoopGraph() {
        AtomicPlaneGraph loopGraph = AtomicPlaneGraph.readIncidenceList(
                GraphData.LOOP_INCIDENCE);
        assertEquals(1, loopGraph.order());
        assertEquals(1, loopGraph.size());
        assertEquals(2, loopGraph.numberOfFaces());
        assertTrue(loopGraph.hasLoopEdges());
        assertFalse(loopGraph.hasParallelEdges());
        assertEquals(0, loopGraph.getDeltaFaces()
                                 .count());
        assertEquals(0, loopGraph.getWyeVertices()
                                 .count());
    }

    @Test
    public void testDoubleLoopGraph() {
        AtomicPlaneGraph dlg = AtomicPlaneGraph.readIncidenceList(
                GraphData.DOUBLELOOP_INCIDENCE);
        assertEquals(1, dlg.order());
        assertEquals(2, dlg.size());
        assertEquals(3, dlg.numberOfFaces());
        assertTrue(dlg.hasLoopEdges());
        assertTrue(dlg.hasParallelEdges());
        assertFalse(dlg.isSimple());
    }

    @Test
    public void testManuallyReduceK4ToK1() {
        AtomicPlaneGraph g = AtomicPlaneGraph.readAdjacencyList(GraphData.K4);
        assertTrue(g.isWye(new AtomicVertex(3)));
        g.reduceOmega(new AtomicVertex(3));
        g.reduceSeries(new AtomicVertex(4));
        // remove parallel edge from double loop graph
        g.removeEdge(g.edges()
                      .iterator()
                      .next());
        // remove leaf vertex
        g.removeVertex(new AtomicVertex(2));
        assertTrue(g.isK1());
        assertEquals(1, g.order());
        assertEquals(0, g.size());
        assertEquals(1, g.numberOfFaces());
    }

}
