package deltawye.app;

import java.awt.Dimension;

import javax.swing.*;

/**
 * A panel for displaying reduction sequences.
 */
public class AlgorithmOutputPanel extends JPanel {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -3214046093453979296L;

    /**
     * The result label.
     */
    private final JLabel resultLabel;

    /**
     * The output text area.
     */
    private final JTextArea resultTextArea;

    /**
     * The progress bar.
     */
    private final JProgressBar progressBar;

    /**
     * Create an algorithm output panel.
     */
    public AlgorithmOutputPanel() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Reduction Sequence"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        setAlignmentX(LEFT_ALIGNMENT);

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setAlignmentX(LEFT_ALIGNMENT);

        resultLabel = new JLabel();
        resultLabel.setAlignmentX(LEFT_ALIGNMENT);

        resultTextArea = new JTextArea(10, 20);
        resultTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultTextArea);
        scrollPane.setAlignmentX(LEFT_ALIGNMENT);
        scrollPane.setVerticalScrollBarPolicy(
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        resetDisplay();
        add(progressBar);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(resultLabel);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(scrollPane);

    }

    /**
     * Reset output areas.
     */
    void resetDisplay() {
        resultLabel.setText("Result");
        resultTextArea.setText("");
        progressBar.setValue(0);
    }

    /**
     * Return result label.
     *
     * @return reference to result label
     */
    JLabel getResultLabel() {
        return resultLabel;
    }

    /**
     * Return result text area.
     *
     * @return reference to result text area
     */
    JTextArea getResultTextArea() {
        return resultTextArea;
    }

    /**
     * Return progress bar.
     *
     * @return reference to progress bar
     */
    JProgressBar getProgressBar() {
        return progressBar;
    }

}
