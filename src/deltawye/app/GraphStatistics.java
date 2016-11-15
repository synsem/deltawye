package deltawye.app;

import deltawye.lib.AtomicPlaneGraph;
import deltawye.lib.MedialGraph;

/**
 * A collection of selected graph statistics.
 */
public class GraphStatistics {

    /**
     * Order of the graph.
     */
    private int order;

    /**
     * Size of the graph.
     */
    private int size;

    /**
     * Number of faces.
     */
    private int faces;

    /**
     * Number of loop edges.
     */
    private int loops;

    /**
     * Number of parallel edges.
     */
    private int parallels;

    /**
     * Number of loop edges in the medial graph.
     */
    private int medialLoops;

    /**
     * Number of parallel edges in the medial graph.
     */
    private int medialParallels;

    /**
     * Collect statistics for a given graph.
     *
     * @param graph
     *            the graph to describe
     */
    public GraphStatistics(AtomicPlaneGraph graph) {
        order = graph.order();
        size = graph.size();
        faces = graph.numberOfFaces();
        loops = graph.numberOfLoopEdges();
        parallels = graph.numberOfParallelEdges();
        MedialGraph medial = MedialGraph.fromAtomicPlaneGraph(graph);
        medialLoops = medial.numberOfLoopEdges();
        medialParallels = medial.numberOfParallelEdges();
    }

    /**
     * Return an HTML-formatted summary of the graph statistics.
     *
     * @return summary string in HTML
     */
    public String toHTML() {
        StringBuilder info = new StringBuilder("<html>");
        info.append(formatStatisticsLine(order, "Vertex", "Vertices"));
        info.append(formatStatisticsLine(size, "Edge"));
        info.append(formatStatisticsLine(faces, "Face"));
        info.append("<br>");
        info.append(formatStatisticsLine(loops, "Loop"));
        info.append(formatStatisticsLine(parallels, "Parallel edge"));
        info.append("<br>");
        info.append(formatStatisticsLine(medialLoops, "Medial loop"));
        info.append(formatStatisticsLine(medialParallels, "Medial parallel edge"));
        info.append("</html>");
        return info.toString();
    }

    /**
     * Return a formatted line for the graph statistics display.
     *
     * @param count
     *            integer number
     * @param name
     *            name with regular plural ("-s")
     * @return formatted string
     */
    private String formatStatisticsLine(int count, String name) {
        return formatStatisticsLine(count, name, name + "s");
    }

    /**
     * Return a formatted line for the graph statistics display.
     *
     * @param count
     *            integer number
     * @param sg
     *            singular name
     * @param pl
     *            plural name
     * @return formatted string
     */
    private String formatStatisticsLine(int count, String sg, String pl) {
        String txt;
        if (count == 1) {
            txt = sg;
        } else {
            txt = pl;
        }
        return count + " " + txt + "<br>";
    }

}
