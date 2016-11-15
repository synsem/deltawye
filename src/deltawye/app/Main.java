package deltawye.app;

import javax.swing.SwingUtilities;

/**
 * Main executable: Run graphical or command-line interface.
 */
public class Main {

    /**
     * Main executable: Run GUI or CLI version.
     *
     * <p>
     * GUI Version: Run this executable without arguments to start the main GUI.
     *
     * <p>
     * CLI Version: Run this executable with the argument "help" for more
     * information on the supported syntax for the command-line interface.
     *
     * @param args
     *            arguments for the command-line interface
     */
    public static void main(String[] args) {

        if (args.length > 0) {
            CLI.run(args);
        } else {
            runGUI();
        }
    }

    /**
     * Run main GUI.
     */
    private static void runGUI() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Controller();
            }
        });
    }

}
