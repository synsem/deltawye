package deltawye.lib;

/**
 * An example collection of plane graphs.
 */
public class GraphData {

    /**
     * A planar embedding of the icosahedron as an adjacency matrix.
     */
    public static final int[][] ICOSAHEDRON = { { 0, 8, 7, 11, 5, 1 },
            { 1, 0, 5, 6, 2, 8 }, { 2, 8, 1, 6, 3, 9 }, { 3, 2, 6, 4, 10, 9 },
            { 4, 6, 5, 11, 10, 3 }, { 5, 0, 11, 4, 6, 1 }, { 6, 5, 4, 3, 2, 1 },
            { 7, 0, 8, 9, 10, 11 }, { 8, 1, 2, 9, 7, 0 }, { 9, 8, 2, 3, 10, 7 },
            { 10, 4, 11, 7, 9, 3 }, { 11, 0, 7, 10, 4, 5 } };

    /**
     * A planar embedding of the dodecahedron as an adjacency matrix.
     */
    public static final int[][] DODECAHEDRON = { { 0, 1, 19, 10 }, { 1, 8, 2, 0 },
            { 2, 1, 6, 3 }, { 3, 2, 4, 19 }, { 4, 5, 17, 3 }, { 5, 15, 4, 6 },
            { 6, 7, 5, 2 }, { 7, 8, 14, 6 }, { 8, 9, 7, 1 }, { 9, 10, 13, 8 },
            { 10, 0, 11, 9 }, { 11, 18, 12, 10 }, { 12, 16, 13, 11 }, { 13, 9, 12, 14 },
            { 14, 13, 15, 7 }, { 15, 14, 16, 5 }, { 16, 17, 15, 12 }, { 17, 4, 16, 18 },
            { 18, 19, 17, 11 }, { 19, 0, 3, 18 } };

    /**
     * A planar embedding of the octahedron as an adjacency matrix.
     */
    public static final int[][] OCTAHEDRON = { { 0, 1, 3, 4, 2 }, { 1, 2, 5, 3, 0 },
            { 2, 0, 4, 5, 1 }, { 3, 0, 1, 5, 4 }, { 4, 0, 3, 5, 2 }, { 5, 1, 2, 4, 3 } };

    /**
     * A planar embedding of the cube (hexahedron) as an adjacency matrix.
     */
    public static final int[][] CUBE = { { 0, 3, 1, 4 }, { 1, 0, 2, 7 }, { 2, 3, 6, 1 },
            { 3, 5, 2, 0 }, { 4, 5, 0, 7 }, { 5, 6, 3, 4 }, { 6, 5, 7, 2 },
            { 7, 4, 1, 6 } };

    /**
     * A planar embedding of K4 (tetrahedron) as an adjacency matrix.
     */
    public static final int[][] K4 = { { 1, 3, 4, 2 }, { 2, 1, 4, 3 }, { 3, 2, 4, 1 },
            { 4, 3, 2, 1 } };

    /**
     * A planar embedding of K4 as an incidence matrix.
     */
    public static final int[][] K4_INCIDENCE = { { 1, 1, 2, 3 }, { 2, 3, 4, 5 },
            { 3, 5, 6, 1 }, { 4, 6, 4, 2 } };

    /**
     * A planar embedding of the loop graph as an incidence matrix.
     */
    public static final int[][] LOOP_INCIDENCE = { { 0, 0, 0 } };

    /**
     * A planar embedding of the double loop graph as an incidence matrix.
     */
    public static final int[][] DOUBLELOOP_INCIDENCE = { { 0, 0, 0, 1, 1 } };

}
