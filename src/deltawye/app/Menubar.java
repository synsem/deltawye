package deltawye.app;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.*;

/**
 * A Swing menubar for the main GUI frame.
 */
public class Menubar extends JMenuBar implements ActionListener {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 2548866895853961159L;

    /**
     * The GUI controller.
     */
    private final Controller controller;

    /**
     * Create a new Menubar.
     *
     * @param controller
     *            the GUI controller
     */
    public Menubar(Controller controller) {
        this.controller = controller;

        JMenu menu;
        JMenu submenu;
        JMenuItem menuItem;

        // File menu

        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        add(menu);

        submenu = new JMenu("Open");
        submenu.setMnemonic(KeyEvent.VK_O);
        menuItem = new JMenuItem("Adjacency list...");
        menuItem.setActionCommand("OpenAdjacency");
        menuItem.addActionListener(this);
        menuItem.setMnemonic(KeyEvent.VK_A);
        menuItem.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        submenu.add(menuItem);
        menuItem = new JMenuItem("Incidence list...");
        menuItem.setActionCommand("OpenIncidence");
        menuItem.addActionListener(this);
        menuItem.setMnemonic(KeyEvent.VK_I);
        submenu.add(menuItem);
        menu.add(submenu);

        menuItem = new JMenuItem("Exit");
        menuItem.addActionListener(this);
        menuItem.setMnemonic(KeyEvent.VK_E);
        menuItem.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
        menu.add(menuItem);

        // Help menu

        menu = new JMenu("Help");
        menu.setMnemonic(KeyEvent.VK_H);
        add(menu);

        menuItem = new JMenuItem("About");
        menuItem.addActionListener(this);
        menuItem.setMnemonic(KeyEvent.VK_A);
        menu.add(menuItem);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if ("OpenAdjacency".equals(actionCommand)) {
            controller.openFileAdjacency();
        } else if ("OpenIncidence".equals(actionCommand)) {
            controller.openFileIncidence();
        } else if ("Exit".equals(actionCommand)) {
            controller.exit();
        } else if ("About".equals(actionCommand)) {
            controller.showAbout();
        } else {
            System.err.println("GUI error in menubar: Received unknown action: '"
                    + actionCommand + "'");
        }
    }

}
