package deltawye.app;

import java.awt.Dimension;

import javax.swing.*;

/**
 * A panel for displaying usage information.
 */
public class WelcomePanel extends JPanel {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -3507164261457666932L;

    /**
     * The main text label.
     */
    private JLabel infoLabel;

    /**
     * Create a welcome panel.
     */
    public WelcomePanel() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Welcome"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        setAlignmentX(LEFT_ALIGNMENT);

        infoLabel = new JLabel(initText());
        add(infoLabel);
        fixLayout();

    }

    /**
     * Create initial string to be displayed by info label.
     *
     * @return welcome text
     */
    private String initText() {
        StringBuilder txt = new StringBuilder("<html>");
        txt.append("<h2>Wye-Delta-Wye Reduction</h2>");
        txt.append("<h4>Basic usage:</h4>");
        txt.append("<p><i>Step 1:</i> Open plane graph data from a file.<br>");
        txt.append("Both adjacency list and incidence list<br>");
        txt.append("formats are supported, but they must<br>");
        txt.append("encode a valid planar embedding<br>");
        txt.append("via a circular ordering of the edges.");
        txt.append("</p><br>");
        txt.append("<p><i>Step 2:</i> Click one of the <i>Reduce</i> buttons<br>");
        txt.append("to run the corresponding Wye-Delta-Wye<br>");
        txt.append("reduction algorithm.</p><br>");
        txt.append("</html>");
        return txt.toString();
    }

    /**
     * Set maximum size based on current content dimensions.
     */
    private void fixLayout() {
        infoLabel.setMaximumSize(new Dimension(infoLabel.getPreferredSize().width + 20,
                infoLabel.getPreferredSize().height + 20));
    }

}
