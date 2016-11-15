package deltawye.app;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import deltawye.lib.*;
import deltawye.lib.SteinitzGruenbaum.LensTriangleSelectionStrategy;

/**
 * Main command-line interface.
 */
public class CLI {

    /**
     * A string describing the command-line interface of this program.
     */
    private static final String usage = String.join(System.getProperty("line.separator"),
            "DeltaWye version " + Meta.getVersionString(), "", "Usage:", "",
            "  $ java -jar deltawye.jar <algorithm> <filename>",
            "  $ java -jar deltawye.jar batch <count> <algorithm> <filename>", "",
            "where:", "", "  <algorithm>   = steinitz <sg-strategy>",
            "                | feoprovan <fp-start> <fp-strategy>",
            "                | temperature <tr-strategy>",
            "  <sg-strategy> = random | min | max | pole | nonpole",
            "  <fp-start>    = random | min | max",
            "  <fp-strategy> = random | minlabel | maxlabel | mindegree | maxdegree",
            "  <tr-strategy> = random | short | long",
            "  <count>       = <number of requested runs of the algorithm as integer>",
            "  <filename>    = <path to graph data textfile in adjacency list format>",
            "", "Example:", "",
            "  $ java -jar deltawye.jar steinitz random data/icosahedron.txt", "");

    /**
     * Abort execution: Print usage information on standard error and exit.
     */
    public static void abort() {
        System.err.println(usage);
        System.exit(1);
    }

    /**
     * Report usage information on standard output.
     */
    public static void printUsage() {
        System.out.println(usage);
    }

    /**
     * Parse strategy parameter for Steinitz reduction algorithm.
     *
     * <p>
     * Exits the program if argument cannot be parsed.
     *
     * @param input
     *            command-line argument string
     * @return strategy
     */
    private static LensTriangleSelectionStrategy parseSgStrategy(String input) {
        LensTriangleSelectionStrategy strategy = null;
        if ("random".equals(input)) {
            strategy = LensTriangleSelectionStrategy.RANDOM;
        } else if ("min".equals(input)) {
            strategy = LensTriangleSelectionStrategy.MINLENSES;
        } else if ("max".equals(input)) {
            strategy = LensTriangleSelectionStrategy.MAXLENSES;
        } else if ("pole".equals(input)) {
            strategy = LensTriangleSelectionStrategy.PREFER_POLE;
        } else if ("nonpole".equals(input)) {
            strategy = LensTriangleSelectionStrategy.PREFER_NONPOLE;
        } else {
            System.err.println("Error: Cannot parse Steinitz strategy: " + input);
            abort();
        }
        return strategy;
    }

    /**
     * Parse start vertex selection strategy parameter for Feo and Provan
     * reduction algorithm.
     *
     * <p>
     * Exits the program if argument cannot be parsed.
     *
     * @param input
     *            command-line argument string
     * @return strategy
     */
    private static FeoProvan.StartVertexStrategy parseFpStart(String input) {
        FeoProvan.StartVertexStrategy strategy = null;
        if ("random".equals(input)) {
            strategy = FeoProvan.StartVertexStrategy.RANDOM;
        } else if ("min".equals(input)) {
            strategy = FeoProvan.StartVertexStrategy.MINIMUM;
        } else if ("max".equals(input)) {
            strategy = FeoProvan.StartVertexStrategy.MAXIMUM;
        } else {
            System.err.println(
                    "Error: Cannot parse Feo and Provan start vertex strategy: " + input);
            abort();
        }
        return strategy;
    }

    /**
     * Parse transformation selection strategy parameter for Feo and Provan
     * reduction algorithm.
     *
     * <p>
     * Exits the program if argument cannot be parsed.
     *
     * @param input
     *            command-line argument string
     * @return strategy
     */
    private static FeoProvan.TransformSelectionStrategy parseFpStrategy(String input) {
        FeoProvan.TransformSelectionStrategy strategy = null;
        if ("random".equals(input)) {
            strategy = FeoProvan.TransformSelectionStrategy.RANDOM;
        } else if ("minlabel".equals(input)) {
            strategy = FeoProvan.TransformSelectionStrategy.MINLABEL;
        } else if ("maxlabel".equals(input)) {
            strategy = FeoProvan.TransformSelectionStrategy.MAXLABEL;
        } else if ("mindegree".equals(input)) {
            strategy = FeoProvan.TransformSelectionStrategy.MINDEGREE;
        } else if ("maxdegree".equals(input)) {
            strategy = FeoProvan.TransformSelectionStrategy.MAXDEGREE;
        } else {
            System.err.println(
                    "Error: Cannot parse Feo and Provan selection strategy: " + input);
            abort();
        }
        return strategy;
    }

    /**
     * Parse transformation selection strategy parameter for temperature-based
     * random reduction algorithm.
     *
     * <p>
     * Exits the program if argument cannot be parsed.
     *
     * @param input
     *            command-line argument string
     * @return strategy
     */
    private static TemperatureReduction.Strategy parseTemperatureStrategy(String input) {
        TemperatureReduction.Strategy strategy = null;
        if ("random".equals(input)) {
            strategy = TemperatureReduction.Strategy.RANDOM;
        } else if ("short".equals(input)) {
            strategy = TemperatureReduction.Strategy.SHORT;
        } else if ("long".equals(input)) {
            strategy = TemperatureReduction.Strategy.LONG;
        } else {
            System.err.println(
                    "Error: Cannot parse temperature selection strategy: " + input);
            abort();
        }
        return strategy;
    }

    /**
     * Main command-line interface.
     *
     * <p>
     * Parse command-line arguments and run the requested algorithm. Results are
     * reported on standard output.
     *
     * <p>
     * For information on the supported syntax, execute this program with the
     * single argument "help".
     *
     * @param args
     *            command-line arguments
     */
    public static void run(String[] args) {
        if (args.length > 0) {
            String action = args[0];
            if ("help".equals(action)) {
                printUsage();
            } else if ("steinitz".equals(action) && args.length == 3) {
                singleRun(parseSteinitz(args[1], args[2]));
            } else if ("feoprovan".equals(action) && args.length == 4) {
                singleRun(parseFeoProvan(args[1], args[2], args[3]));
            } else if ("temperature".equals(action) && args.length == 3) {
                singleRun(parseTemperature(args[1], args[2]));
            } else if ("batch".equals(action) && args.length > 4) {
                int count = 0;
                try {
                    count = Integer.parseInt(args[1]);
                    if (count < 1) {
                        throw new NumberFormatException("Count must be positive.");
                    }
                } catch (NumberFormatException e) {
                    System.err.println(e);
                    abort();
                }
                action = args[2];
                if ("steinitz".equals(action) && args.length == 5) {
                    batchRun(parseSteinitz(args[3], args[4]), count);
                } else if ("feoprovan".equals(action) && args.length == 6) {
                    batchRun(parseFeoProvan(args[3], args[4], args[5]), count);
                } else if ("temperature".equals(action) && args.length == 5) {
                    batchRun(parseTemperature(args[3], args[4]), count);
                } else {
                    System.err.println("Invalid arguments.");
                    abort();
                }
            } else {
                System.err.println("Invalid arguments.");
                abort();
            }
        } else {
            abort();
        }
        System.exit(0);
    }

    /**
     * Run the specified graph transformation algorithm once. Report the details
     * of the found reduction sequence.
     *
     * @param algorithm
     *            the graph transformation algorithm to run
     */
    private static void singleRun(GraphTransformationAlgorithm algorithm) {
        System.out.println(algorithm);
        List<String> sequence = algorithm.run(true);
        long steps = sequence.size();
        int normalized = algorithm.normalizedLength(sequence);
        System.out.println("Reduction completed after " + steps + " steps.");
        System.out.println("Normalized reduction length: " + normalized);
        System.out.println("Number of Delta-Wye Transformations: "
                + algorithm.deltaWyeCount(sequence));
        System.out.println("Number of Wye-Delta Transformations: "
                + algorithm.wyeDeltaCount(sequence));
    }

    /**
     * Run the specified graph reduction algorithm the specified number of
     * times. Report the length of each found reduction sequence and a short
     * summary statistics.
     *
     * @param algorithm
     *            the algorithm to run
     * @param count
     *            the number of times the algorithm should be executed
     */
    private static void batchRun(GraphTransformationAlgorithm algorithm, int count) {
        System.out.println(algorithm);
        System.out.println("Batch run: " + count + " iterations.");
        List<Integer> nativeResults = new ArrayList<>();
        List<Integer> normalizedResults = new ArrayList<>();
        long nativeTotal = 0;
        long normalizedTotal = 0;
        for (int i = 1; i <= count; i++) {
            algorithm.reset();
            List<String> sequence = algorithm.run(false);
            int nativeSteps = sequence.size();
            int normalizedSteps = algorithm.normalizedLength(sequence);
            System.out.println("Run " + i + " completed after " + nativeSteps
                    + " steps with " + algorithm.deltaWyeCount(sequence)
                    + " Delta-Wye and " + algorithm.wyeDeltaCount(sequence)
                    + " Wye-Delta transformations (normalized length: " + normalizedSteps
                    + ").");
            nativeResults.add(nativeSteps);
            nativeTotal += nativeSteps;
            normalizedResults.add(normalizedSteps);
            normalizedTotal += normalizedSteps;
        }
        System.out.println("");
        System.out.println("RESULTS:");
        System.out.println("  Shortest sequence: " + Collections.min(nativeResults));
        System.out.println("  Longest sequence: " + Collections.max(nativeResults));
        System.out.printf("  Average length: %.2f%n%n", (double) nativeTotal / count);
        System.out.println("NORMALIZED RESULTS:");
        System.out.println("  Shortest sequence: " + Collections.min(normalizedResults));
        System.out.println("  Longest sequence: " + Collections.max(normalizedResults));
        System.out.printf("  Average length: %.2f%n%n", (double) normalizedTotal / count);
    }

    /**
     * Parse the arguments of a call to the Steinitz algorithm and return the
     * corresponding graph transformation algorithm.
     *
     * @param strategyString
     *            string representation of the strategy to use
     * @param filename
     *            path to the graph data
     * @return graph transformation algorithm instance
     */
    private static GraphTransformationAlgorithm parseSteinitz(String strategyString,
            String filename) {
        LensTriangleSelectionStrategy strategy = parseSgStrategy(strategyString);
        AtomicPlaneGraph graph = readGraph(filename);
        return new SteinitzGruenbaum(graph, strategy);
    }

    /**
     * Parse the arguments of a call to the Feo and Provan algorithm and return
     * the corresponding graph transformation algorithm.
     *
     * @param startString
     *            string representation of the start vertex selection strategy
     * @param strategyString
     *            string representation of the transformation selection strategy
     * @param filename
     *            path to the graph data
     * @return graph transformation algorithm instance
     */
    private static GraphTransformationAlgorithm parseFeoProvan(String startString,
            String strategyString, String filename) {
        FeoProvan.StartVertexStrategy start = parseFpStart(startString);
        FeoProvan.TransformSelectionStrategy strategy = parseFpStrategy(strategyString);
        AtomicPlaneGraph graph = readGraph(filename);
        return new FeoProvan(graph, start, strategy);
    }

    /**
     * Parse the arguments of a call to the random reduction algorithm and
     * return the corresponding graph transformation algorithm.
     *
     * @param strategyString
     *            string representation of the transformation selection strategy
     * @param filename
     *            path to the graph data
     * @return graph transformation algorithm instance
     */
    private static GraphTransformationAlgorithm parseTemperature(String strategyString,
            String filename) {
        TemperatureReduction.Strategy strategy = parseTemperatureStrategy(strategyString);
        AtomicPlaneGraph graph = readGraph(filename);
        return new TemperatureReduction(graph, strategy);
    }

    /**
     * Read graph data in adjacency list format from textfile.
     *
     * @param filename
     *            path to the graph data
     * @return graph
     */
    private static AtomicPlaneGraph readGraph(String filename) {
        AtomicPlaneGraph graph = null;
        try {
            Path file = Paths.get(filename);
            graph = AtomicPlaneGraph.readAdjacencyList(file);
        } catch (IOException e) {
            System.err.println(
                    "Error: Could not read graph data from '" + filename + "'.");
            abort();
        }
        return graph;
    }

}
