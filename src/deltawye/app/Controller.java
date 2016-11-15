package deltawye.app;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.*;

import deltawye.lib.*;

/**
 * Main controller for the Swing GUI.
 */
public class Controller {

    /**
     * Main GUI frame.
     */
    private MainFrame gui;

    /**
     * The current graph (model).
     */
    private AtomicPlaneGraph graph;

    /**
     * Most recently chosen directory for loading graph data files.
     */
    private File lastUsedGraphDataDir;

    /**
     * Active strategy for Steinitz algorithm.
     */
    private SteinitzGruenbaum.LensTriangleSelectionStrategy steinitzStrategy;

    /**
     * Active strategy for Temperature algorithm.
     */
    private TemperatureReduction.Strategy temperatureStrategy;

    /**
     * Active start vertex strategy for Feo and Provan algorithm.
     */
    private FeoProvan.StartVertexStrategy fpStartStrategy;

    /**
     * Active transformation selection strategy for Feo and Provan algorithm.
     */
    private FeoProvan.TransformSelectionStrategy fpTselStrategy;

    /**
     * Create GUI Controller.
     */
    public Controller() {
        lastUsedGraphDataDir = new File(System.getProperty("user.dir"));
        steinitzStrategy = SteinitzGruenbaum.LensTriangleSelectionStrategy.MAXLENSES;
        temperatureStrategy = TemperatureReduction.Strategy.SHORT;
        fpStartStrategy = FeoProvan.StartVertexStrategy.RANDOM;
        fpTselStrategy = FeoProvan.TransformSelectionStrategy.MAXLABEL;
        // no bold default font
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        gui = new MainFrame(this);
    }

    /**
     * Exit the program.
     */
    public void exit() {
        System.exit(0);
    }

    /**
     * Show modal error dialog.
     *
     * @param msg
     *            error message
     */
    public void showErrorMsg(String msg) {
        JOptionPane.showMessageDialog(gui, msg, "Error", JOptionPane.ERROR_MESSAGE);

    }

    /**
     * Show the About window.
     */
    public void showAbout() {
        StringBuilder msg = new StringBuilder("<html><center>");
        msg.append("<b>DeltaWye</b> Version " + Meta.getVersionString() + "<br><br>");
        msg.append("Copyright 2016 Mathias Schenner<br><br>");
        msg.append("This program is free software<br>under the MIT License.<br><br>");
        msg.append("</center></html>");
        JLabel info = new JLabel(msg.toString());
        JOptionPane.showMessageDialog(gui, info, "About",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Show file chooser dialog and open graph from adjacency list file.
     */
    public void openFileAdjacency() {
        openFile(true);
    }

    /**
     * Show file chooser dialog and open graph from incidence list file.
     */
    public void openFileIncidence() {
        openFile(false);
    }

    /**
     * Show file chooser dialog and open graph from file (adjacency list or
     * incidence list format).
     *
     * @param isAdjacency
     *            true if input is in adjacency list format
     */
    private void openFile(boolean isAdjacency) {
        final JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(lastUsedGraphDataDir);
        int retVal = fc.showOpenDialog(gui);
        if (retVal == JFileChooser.APPROVE_OPTION) {
            lastUsedGraphDataDir = fc.getCurrentDirectory();
            File file = fc.getSelectedFile();
            new OpenGraphFromFile(file.toPath(), isAdjacency).execute();
        }
    }

    /**
     * Reset the output area.
     */
    public void resetOutput() {
        gui.getOutputPanel()
           .resetDisplay();
    }

    /**
     * Set strategy parameter for Steinitz algorithm.
     *
     * @param strategy
     *            the chosen strategy
     */
    public void setSteinitzStrategy(
            SteinitzGruenbaum.LensTriangleSelectionStrategy strategy) {
        steinitzStrategy = strategy;
    }

    /**
     * Set strategy parameter for Temperature algorithm.
     *
     * @param strategy
     *            the chosen strategy
     */
    public void setTemperatureStrategy(TemperatureReduction.Strategy strategy) {
        temperatureStrategy = strategy;
    }

    /**
     * Set start vertex strategy parameter for Feo and Provan algorithm.
     *
     * @param strategy
     *            the chosen strategy
     */
    public void setFeoProvanStartStrategy(FeoProvan.StartVertexStrategy strategy) {
        fpStartStrategy = strategy;
    }

    /**
     * Set transformation selection strategy parameter for Feo and Provan
     * algorithm.
     *
     * @param strategy
     *            the chosen strategy
     */
    public void setFeoProvanTselStrategy(FeoProvan.TransformSelectionStrategy strategy) {
        fpTselStrategy = strategy;
    }

    /**
     * Run Temperature reduction algorithm and show result in GUI.
     *
     * @param cancelBtn
     *            button for canceling the execution
     */
    public void runReductionTemperature(JButton cancelBtn) {
        runReductionAlgorithm(new TemperatureReduction(graph, temperatureStrategy),
                cancelBtn);
    }

    /**
     * Run Steinitz reduction algorithm and show result in GUI.
     *
     * @param cancelBtn
     *            button for canceling the execution
     */
    public void runReductionSteinitz(JButton cancelBtn) {
        runReductionAlgorithm(new SteinitzGruenbaum(graph, steinitzStrategy), cancelBtn);
    }

    /**
     * Run Feo and Provan reduction algorithm and show result in GUI.
     *
     * @param cancelBtn
     *            button for canceling the execution
     */
    public void runReductionFeoProvan(JButton cancelBtn) {
        runReductionAlgorithm(new FeoProvan(graph, fpStartStrategy, fpTselStrategy),
                cancelBtn);
    }

    /**
     * Run a graph reduction algorithm and show result in GUI.
     *
     * @param algo
     *            the reduction algorithm to run
     * @param cancelBtn
     *            button for canceling the execution
     */
    public void runReductionAlgorithm(GraphTransformationAlgorithm algo,
            JButton cancelBtn) {
        gui.getControlPanel()
           .disableReduceButtons();
        resetOutput();
        RunGraphTransformationAlgorithm task = new RunGraphTransformationAlgorithm(algo,
                cancelBtn);
        JProgressBar progressBar = gui.getOutputPanel()
                                      .getProgressBar();
        task.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("progress".equals(evt.getPropertyName())) {
                    progressBar.setValue((Integer) evt.getNewValue());
                }
            }
        });
        task.execute();
    }

    /**
     * A SwingWorker for running a graph transformation algorithm in a
     * background thread.
     */
    private class RunGraphTransformationAlgorithm
            extends SwingWorker<List<String>, String> implements ActionListener {

        /**
         * A button to cancel the execution of this thread.
         */
        private final JButton cancelBtn;

        /**
         * The graph transformation algorithm to be executed by this thread.
         */
        private final GraphTransformationAlgorithm algo;

        /**
         * The text area for printing the algorithm output.
         */
        private final JTextArea resultArea;

        /**
         * Create a thread for running the specified algorithm.
         *
         * @param algo
         *            the algorithm to run
         * @param cancelBtn
         *            a button that can be used to cancel the execution of this
         *            thread
         */
        RunGraphTransformationAlgorithm(GraphTransformationAlgorithm algo,
                JButton cancelBtn) {
            this.algo = algo;
            this.cancelBtn = cancelBtn;
            resultArea = gui.getOutputPanel()
                            .getResultTextArea();
            cancelBtn.addActionListener(this);
            cancelBtn.setEnabled(true);
        }

        @Override
        protected List<String> doInBackground() throws Exception {
            List<String> transformations = new ArrayList<>();
            setProgress(algo.getProgress());
            while (!isCancelled() && algo.hasNextStep()) {
                String transformation = algo.nextStep();
                publish(transformation);
                setProgress(algo.getProgress());
                transformations.add(transformation);
            }
            return transformations;
        }

        @Override
        protected void process(List<String> steps) {
            for (String step : steps) {
                if (!isCancelled()) {
                    resultArea.append(step + "\n");
                }
            }
        }

        @Override
        protected void done() {
            try {
                JLabel resultLabel = gui.getOutputPanel()
                                        .getResultLabel();
                if (!isCancelled()) {
                    List<String> steps = get();
                    resultLabel.setText("Result: Reduction in " + steps.size()
                            + " steps (normalized: " + algo.normalizedLength(steps)
                            + ").");
                } else {
                    String cancelMsg = "Reduction algorithm was canceled.";
                    resultArea.append(cancelMsg + "\n");
                    resultLabel.setText(cancelMsg);
                }
                gui.getControlPanel()
                   .enableResetButton();
            } catch (InterruptedException e) {
                // ignore
            } catch (ExecutionException e) {
                System.err.println(e);
                showErrorMsg("Encountered error during reduction algorithm.");
            } finally {
                gui.getControlPanel()
                   .enableReduceButtons();
                cancelBtn.removeActionListener(this);
                cancelBtn.setEnabled(false);
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            cancelBtn.removeActionListener(this);
            cancelBtn.setEnabled(false);
            cancel(false);
        }
    }

    /**
     * A SwingWorker for opening a graph from a file in a background thread.
     */
    private class OpenGraphFromFile extends SwingWorker<AtomicPlaneGraph, Void> {

        /**
         * Graph filename.
         */
        private final Path filename;

        /**
         * A flag that indicates whether the graph data is stored in adjacency
         * list format (true) or incidence list format (false).
         */
        private final boolean isAdjacency;

        /**
         * Create a background thread for opening a graph encoded in the
         * specified file.
         *
         * @param filename
         *            path to the graph data
         * @param isAdjacency
         *            true if the graph data is in adjacency list format
         */
        OpenGraphFromFile(Path filename, boolean isAdjacency) {
            this.filename = filename;
            this.isAdjacency = isAdjacency;
        }

        @Override
        protected AtomicPlaneGraph doInBackground() throws Exception {
            if (isAdjacency) {
                return AtomicPlaneGraph.readAdjacencyList(filename);
            } else {
                return AtomicPlaneGraph.readIncidenceList(filename);
            }
        }

        @Override
        protected void done() {
            try {
                graph = get();
                gui.getInfoPanel()
                   .updateGraphInfo(filename.getFileName()
                                            .toString(),
                           new GraphStatistics(graph));
                gui.getControlPanel()
                   .enableReduceButtons();
                gui.getOutputPanel()
                   .resetDisplay();
                gui.pack();
            } catch (InterruptedException e) {
                // ignore
            } catch (ExecutionException e) {
                String why = null;
                Throwable cause = e.getCause();
                if (cause != null) {
                    why = cause.getMessage();
                } else {
                    why = e.getMessage();
                }
                System.err.println("Error retrieving file: " + filename);
                System.err.println(why);
                showErrorMsg("Cannot open file: " + filename);
            }
        }
    }

}
