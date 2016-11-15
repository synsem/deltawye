package deltawye.app;

import java.awt.Dimension;

import javax.swing.*;

/**
 * A panel for displaying graph statistics.
 */
public class GraphStatisticsPanel extends JPanel {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -693171644623631009L;

    /**
     * The header label.
     */
    private JLabel headerLabel;

    /**
     * The main text label.
     */
    private JLabel mainInfoLabel;

    /**
     * Create new panel for displaying graph statistics.
     */
    public GraphStatisticsPanel() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Graph statistics"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        setAlignmentX(LEFT_ALIGNMENT);

        headerLabel = new JLabel();
        headerLabel.setAlignmentX(LEFT_ALIGNMENT);

        mainInfoLabel = new JLabel();
        mainInfoLabel.setAlignmentX(LEFT_ALIGNMENT);

        add(headerLabel);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(mainInfoLabel);
        add(Box.createHorizontalGlue());
        add(Box.createVerticalGlue());
        resetGraphInfo();
    }

    /**
     * Set maximum size based on current content dimensions.
     */
    private void fixLayout() {
        headerLabel.setMaximumSize(
                new Dimension(headerLabel.getPreferredSize().width + 20,
                        headerLabel.getPreferredSize().height + 20));
        mainInfoLabel.setMaximumSize(
                new Dimension(mainInfoLabel.getPreferredSize().width + 20,
                        mainInfoLabel.getPreferredSize().height + 20));
    }

    /**
     * Reset display.
     */
    public void resetGraphInfo() {
        headerLabel.setText("No graph loaded.");
        mainInfoLabel.setText("");
        fixLayout();
    }

    /**
     * Update display.
     *
     * @param header
     *            display heading (e.g. filename)
     * @param stats
     *            the graph statistics to display
     */
    public void updateGraphInfo(String header, GraphStatistics stats) {
        headerLabel.setText("<html><b>" + header + "</b></html>");
        mainInfoLabel.setText(stats.toHTML());
        fixLayout();
    }

}
