package deltawye.app;

import java.awt.Dimension;

import javax.swing.*;

/**
 * Main GUI frame.
 */
public class MainFrame extends JFrame {

    /**
     * Serial version ID.
     */
    private static final long serialVersionUID = -6529177756802666765L;

    /**
     * The information panel.
     */
    private final JPanel welcomePanel;

    /**
     * The graph statistics panel.
     */
    private final GraphStatisticsPanel statsPanel;

    /**
     * The algorithm control panel.
     */
    private final AlgorithmControlPanel controlPanel;

    /**
     * The algorithm output panel.
     */
    private final AlgorithmOutputPanel outputPanel;

    /**
     * Create new frame.
     *
     * @param controller
     *            the GUI controller
     */
    public MainFrame(Controller controller) {

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.LINE_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        welcomePanel = new WelcomePanel();
        statsPanel = new GraphStatisticsPanel();
        infoPanel.add(welcomePanel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(statsPanel);

        JPanel algoPanel = new JPanel();
        algoPanel.setLayout(new BoxLayout(algoPanel, BoxLayout.PAGE_AXIS));
        algoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        controlPanel = new AlgorithmControlPanel(controller);
        outputPanel = new AlgorithmOutputPanel();
        algoPanel.add(controlPanel);
        algoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        algoPanel.add(outputPanel);

        mainPanel.add(infoPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        mainPanel.add(algoPanel);

        setJMenuBar(new Menubar(controller));
        setTitle("Delta-Wye");
        setContentPane(mainPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Return the panel for displaying graph statistics.
     *
     * @return reference to info panel
     */
    GraphStatisticsPanel getInfoPanel() {
        return statsPanel;
    }

    /**
     * Return the control panel.
     *
     * @return reference to control panel
     */
    AlgorithmControlPanel getControlPanel() {
        return controlPanel;
    }

    /**
     * Return the output panel.
     *
     * @return reference to output panel
     */
    AlgorithmOutputPanel getOutputPanel() {
        return outputPanel;
    }

}
