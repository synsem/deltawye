package deltawye.app;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import deltawye.lib.FeoProvan;
import deltawye.lib.SteinitzGruenbaum;
import deltawye.lib.TemperatureReduction;

/**
 * A panel for controlling graph algorithms.
 */
public class AlgorithmControlPanel extends JPanel implements ActionListener {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -1534426876975182793L;

    /**
     * Action command for calling Temperature strategy.
     */
    private static final String temperatureReduceAction = "Temperature:Reduce";

    /**
     * Action command for calling Steinitz strategy.
     */
    private static final String steinitzReduceAction = "Steinitz:Reduce";

    /**
     * Action command for calling Feo and Provan strategy.
     */
    private static final String fpReduceAction = "FeoProvan:Reduce";

    /**
     * Action command for setting Steinitz strategy.
     */
    private static final String steinitzStrategyRandom = "Steinitz:Random";

    /**
     * Action command for setting Steinitz strategy.
     */
    private static final String steinitzStrategyMinLenses = "Steinitz:MinLenses";

    /**
     * Action command for setting Steinitz strategy.
     */
    private static final String steinitzStrategyMaxLenses = "Steinitz:MaxLenses";

    /**
     * Action command for setting Steinitz strategy.
     */
    private static final String steinitzStrategyYesPole = "Steinitz:PreferPoles";

    /**
     * Action command for setting Steinitz strategy.
     */
    private static final String steinitzStrategyNoPole = "Steinitz:PreferNonPoles";

    /**
     * Action command for setting Temperature strategy.
     */
    private static final String temperatureStrategyRandom = "Temperature:Random";

    /**
     * Action command for setting Temperature strategy.
     */
    private static final String temperatureStrategyShort = "Temperature:Short";

    /**
     * Action command for setting Temperature strategy.
     */
    private static final String temperatureStrategyLong = "Temperature:Long";

    /**
     * Action command for setting Feo and Provan start vertex strategy.
     */
    private static final String fpStartMin = "Start:Minimum";

    /**
     * Action command for setting Feo and Provan start vertex strategy.
     */
    private static final String fpStartMax = "Start:Maximum";

    /**
     * Action command for setting Feo and Provan start vertex strategy.
     */
    private static final String fpStartRnd = "Start:Random";

    /**
     * Action command for setting Feo and Provan transformation selection
     * strategy.
     */
    private static final String fpTselMinLabel = "Tsel:MinLabel";

    /**
     * Action command for setting Feo and Provan transformation selection
     * strategy.
     */
    private static final String fpTselMaxLabel = "Tsel:MaxLabel";

    /**
     * Action command for setting Feo and Provan transformation selection
     * strategy.
     */
    private static final String fpTselMinDegree = "Tsel:MinDegree";

    /**
     * Action command for setting Feo and Provan transformation selection
     * strategy.
     */
    private static final String fpTselMaxDegree = "Tsel:MaxDegree";

    /**
     * Action command for setting Feo and Provan transformation selection
     * strategy.
     */
    private static final String fpTselRandom = "Tsel:Random";

    /**
     * The GUI controller.
     */
    private final Controller controller;

    /**
     * Button for initiating Temperature reduction.
     */
    private final JButton temperatureBtnReduce;

    /**
     * Button for canceling Temperature reduction.
     */
    private final JButton temperatureBtnCancel;

    /**
     * Button for initiating Steinitz reduction.
     */
    private final JButton steinitzBtnReduce;

    /**
     * Button for canceling Steinitz reduction.
     */
    private final JButton steinitzBtnCancel;

    /**
     * Button for initiating Feo and Provan reduction.
     */
    private final JButton feoProvanBtnReduce;

    /**
     * Button for canceling Feo and Provan reduction.
     */
    private final JButton feoProvanBtnCancel;

    /**
     * Button for resetting the display.
     */
    private final JButton resetBtn;

    /**
     * Create an algorithm control panel.
     *
     * @param controller
     *            the GUI controller
     */
    public AlgorithmControlPanel(Controller controller) {
        this.controller = controller;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Reduction Algorithms"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        setAlignmentX(LEFT_ALIGNMENT);

        // Temperature reduction
        JPanel temperature = new JPanel();
        temperature.setLayout(new BoxLayout(temperature, BoxLayout.X_AXIS));
        temperature.setAlignmentX(LEFT_ALIGNMENT);
        temperatureBtnReduce = new JButton("Reduce");
        temperatureBtnReduce.setActionCommand(temperatureReduceAction);
        temperatureBtnReduce.addActionListener(this);
        temperatureBtnReduce.setEnabled(false);
        temperatureBtnCancel = new JButton("Cancel");
        temperatureBtnCancel.setEnabled(false);

        temperature.add(new JLabel("<html><b>Temperature-based:</b></html>"));
        temperature.add(Box.createHorizontalGlue());
        temperature.add(temperatureBtnReduce);
        temperature.add(Box.createRigidArea(new Dimension(5, 0)));
        temperature.add(temperatureBtnCancel);

        // Temperature parameters
        JPanel temperatureOpts = new JPanel();
        temperatureOpts.setLayout(new BoxLayout(temperatureOpts, BoxLayout.X_AXIS));
        temperatureOpts.setAlignmentX(LEFT_ALIGNMENT);

        JLabel temperatureOptsLabel = new JLabel("Selection Strategy:");
        temperatureOptsLabel.setAlignmentY(TOP_ALIGNMENT);

        JRadioButton temperatureOptsShort = new JRadioButton("Short");
        temperatureOptsShort.setActionCommand(temperatureStrategyShort);
        temperatureOptsShort.addActionListener(this);
        JRadioButton temperatureOptsLong = new JRadioButton("Long");
        temperatureOptsLong.setActionCommand(temperatureStrategyLong);
        temperatureOptsLong.addActionListener(this);
        JRadioButton temperatureOptsRandom = new JRadioButton("Random");
        temperatureOptsRandom.setActionCommand(temperatureStrategyRandom);
        temperatureOptsRandom.addActionListener(this);
        ButtonGroup temperatureOptsGroup = new ButtonGroup();
        temperatureOptsGroup.add(temperatureOptsShort);
        temperatureOptsGroup.add(temperatureOptsLong);
        temperatureOptsGroup.add(temperatureOptsRandom);
        temperatureOptsShort.setSelected(true);

        JPanel temperatureOptsGrid = new JPanel();
        temperatureOptsGrid.setLayout(new GridLayout(2, 2));
        temperatureOptsGrid.setAlignmentY(TOP_ALIGNMENT);
        temperatureOptsGrid.add(temperatureOptsShort);
        temperatureOptsGrid.add(temperatureOptsLong);
        temperatureOptsGrid.add(temperatureOptsRandom);

        temperatureOpts.add(temperatureOptsLabel);
        temperatureOpts.add(Box.createRigidArea(new Dimension(10, 0)));
        temperatureOpts.add(Box.createHorizontalGlue());
        temperatureOpts.add(temperatureOptsGrid);

        // Steinitz reduction
        JPanel steinitz = new JPanel();
        steinitz.setLayout(new BoxLayout(steinitz, BoxLayout.X_AXIS));
        steinitz.setAlignmentX(LEFT_ALIGNMENT);
        steinitzBtnReduce = new JButton("Reduce");
        steinitzBtnReduce.setActionCommand(steinitzReduceAction);
        steinitzBtnReduce.addActionListener(this);
        steinitzBtnReduce.setEnabled(false);
        steinitzBtnCancel = new JButton("Cancel");
        steinitzBtnCancel.setEnabled(false);

        steinitz.add(new JLabel("<html><b>Steinitz:</b></html>"));
        steinitz.add(Box.createHorizontalGlue());
        steinitz.add(steinitzBtnReduce);
        steinitz.add(Box.createRigidArea(new Dimension(5, 0)));
        steinitz.add(steinitzBtnCancel);

        // Steinitz parameters: triangle selection
        JPanel steinitzOpts = new JPanel();
        steinitzOpts.setLayout(new BoxLayout(steinitzOpts, BoxLayout.X_AXIS));
        steinitzOpts.setAlignmentX(LEFT_ALIGNMENT);

        JLabel steinitzOptsLabel = new JLabel("Selection Strategy:");
        steinitzOptsLabel.setAlignmentY(TOP_ALIGNMENT);

        JRadioButton steinitzOptsMinLenses = new JRadioButton("Min Lenses");
        steinitzOptsMinLenses.setActionCommand(steinitzStrategyMinLenses);
        steinitzOptsMinLenses.addActionListener(this);
        JRadioButton steinitzOptsMaxLenses = new JRadioButton("Max Lenses");
        steinitzOptsMaxLenses.setActionCommand(steinitzStrategyMaxLenses);
        steinitzOptsMaxLenses.addActionListener(this);
        JRadioButton steinitzOptsYesPole = new JRadioButton("Prefer Polar");
        steinitzOptsYesPole.setActionCommand(steinitzStrategyYesPole);
        steinitzOptsYesPole.addActionListener(this);
        JRadioButton steinitzOptsNoPole = new JRadioButton("Prefer Nonpolar");
        steinitzOptsNoPole.setActionCommand(steinitzStrategyNoPole);
        steinitzOptsNoPole.addActionListener(this);
        JRadioButton steinitzOptsRandom = new JRadioButton("Random");
        steinitzOptsRandom.setActionCommand(steinitzStrategyRandom);
        steinitzOptsRandom.addActionListener(this);
        ButtonGroup steinitzOptsGroup = new ButtonGroup();
        steinitzOptsGroup.add(steinitzOptsMinLenses);
        steinitzOptsGroup.add(steinitzOptsMaxLenses);
        steinitzOptsGroup.add(steinitzOptsYesPole);
        steinitzOptsGroup.add(steinitzOptsNoPole);
        steinitzOptsGroup.add(steinitzOptsRandom);
        steinitzOptsMaxLenses.setSelected(true);

        JPanel steinitzOptsGrid = new JPanel();
        steinitzOptsGrid.setLayout(new GridLayout(3, 2));
        steinitzOptsGrid.setAlignmentY(TOP_ALIGNMENT);
        steinitzOptsGrid.add(steinitzOptsMinLenses);
        steinitzOptsGrid.add(steinitzOptsMaxLenses);
        steinitzOptsGrid.add(steinitzOptsYesPole);
        steinitzOptsGrid.add(steinitzOptsNoPole);
        steinitzOptsGrid.add(steinitzOptsRandom);

        steinitzOpts.add(steinitzOptsLabel);
        steinitzOpts.add(Box.createRigidArea(new Dimension(10, 0)));
        steinitzOpts.add(Box.createHorizontalGlue());
        steinitzOpts.add(steinitzOptsGrid);

        // Feo and Provan reduction
        JPanel feoProvan = new JPanel();
        feoProvan.setLayout(new BoxLayout(feoProvan, BoxLayout.X_AXIS));
        feoProvan.setAlignmentX(LEFT_ALIGNMENT);
        feoProvanBtnReduce = new JButton("Reduce");
        feoProvanBtnReduce.setActionCommand(fpReduceAction);
        feoProvanBtnReduce.addActionListener(this);
        feoProvanBtnReduce.setEnabled(false);
        feoProvanBtnCancel = new JButton("Cancel");
        feoProvanBtnCancel.setEnabled(false);

        feoProvan.add(new JLabel("<html><b>Feo and Provan:</b></html>"));
        feoProvan.add(Box.createRigidArea(new Dimension(10, 0)));
        feoProvan.add(Box.createHorizontalGlue());
        feoProvan.add(feoProvanBtnReduce);
        feoProvan.add(Box.createRigidArea(new Dimension(5, 0)));
        feoProvan.add(feoProvanBtnCancel);

        // Feo and Provan parameters: start vertex
        JPanel feoProvanOptsStart = new JPanel();
        feoProvanOptsStart.setLayout(new BoxLayout(feoProvanOptsStart, BoxLayout.X_AXIS));
        feoProvanOptsStart.setAlignmentX(LEFT_ALIGNMENT);

        JLabel feoProvanOptsStartLabel = new JLabel("Start vertex:");
        feoProvanOptsStartLabel.setAlignmentY(TOP_ALIGNMENT);

        JRadioButton fpOptStartMin = new JRadioButton("Minimum");
        fpOptStartMin.setActionCommand(fpStartMin);
        fpOptStartMin.addActionListener(this);
        JRadioButton fpOptStartMax = new JRadioButton("Maximum");
        fpOptStartMax.setActionCommand(fpStartMax);
        fpOptStartMax.addActionListener(this);
        JRadioButton fpOptStartRnd = new JRadioButton("Random");
        fpOptStartRnd.setActionCommand(fpStartRnd);
        fpOptStartRnd.addActionListener(this);
        ButtonGroup fpOptStartGroup = new ButtonGroup();
        fpOptStartGroup.add(fpOptStartMin);
        fpOptStartGroup.add(fpOptStartMax);
        fpOptStartGroup.add(fpOptStartRnd);
        fpOptStartRnd.setSelected(true);

        JPanel feoProvanOptsStartGrid = new JPanel();
        feoProvanOptsStartGrid.setLayout(new GridLayout(2, 2));
        feoProvanOptsStartGrid.setAlignmentY(TOP_ALIGNMENT);
        feoProvanOptsStartGrid.add(fpOptStartMin);
        feoProvanOptsStartGrid.add(fpOptStartMax);
        feoProvanOptsStartGrid.add(fpOptStartRnd);

        feoProvanOptsStart.add(feoProvanOptsStartLabel);
        feoProvanOptsStart.add(Box.createRigidArea(new Dimension(10, 0)));
        feoProvanOptsStart.add(Box.createHorizontalGlue());
        feoProvanOptsStart.add(feoProvanOptsStartGrid);

        // Feo and Provan parameters: transformation selection
        JPanel feoProvanOptsTsel = new JPanel();
        feoProvanOptsTsel.setLayout(new BoxLayout(feoProvanOptsTsel, BoxLayout.X_AXIS));
        feoProvanOptsTsel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel feoProvanOptsTselLabel = new JLabel("Selection Strategy:");
        feoProvanOptsTselLabel.setAlignmentY(TOP_ALIGNMENT);

        JRadioButton fpOptTselMinLabel = new JRadioButton("Min Label");
        fpOptTselMinLabel.setActionCommand(fpTselMinLabel);
        fpOptTselMinLabel.addActionListener(this);
        JRadioButton fpOptTselMaxLabel = new JRadioButton("Max Label");
        fpOptTselMaxLabel.setActionCommand(fpTselMaxLabel);
        fpOptTselMaxLabel.addActionListener(this);
        JRadioButton fpOptTselMinDegree = new JRadioButton("Min Degree");
        fpOptTselMinDegree.setActionCommand(fpTselMinDegree);
        fpOptTselMinDegree.addActionListener(this);
        JRadioButton fpOptTselMaxDegree = new JRadioButton("Max Degree");
        fpOptTselMaxDegree.setActionCommand(fpTselMaxDegree);
        fpOptTselMaxDegree.addActionListener(this);
        JRadioButton fpOptTselRandom = new JRadioButton("Random");
        fpOptTselRandom.setActionCommand(fpTselRandom);
        fpOptTselRandom.addActionListener(this);
        ButtonGroup fpOptTselGroup = new ButtonGroup();
        fpOptTselGroup.add(fpOptTselMinLabel);
        fpOptTselGroup.add(fpOptTselMaxLabel);
        fpOptTselGroup.add(fpOptTselMinDegree);
        fpOptTselGroup.add(fpOptTselMaxDegree);
        fpOptTselGroup.add(fpOptTselRandom);
        fpOptTselMaxLabel.setSelected(true);

        JPanel feoProvanOptsTselGrid = new JPanel();
        feoProvanOptsTselGrid.setLayout(new GridLayout(3, 2));
        feoProvanOptsTselGrid.setAlignmentY(TOP_ALIGNMENT);
        feoProvanOptsTselGrid.add(fpOptTselMinLabel);
        feoProvanOptsTselGrid.add(fpOptTselMaxLabel);
        feoProvanOptsTselGrid.add(fpOptTselMinDegree);
        feoProvanOptsTselGrid.add(fpOptTselMaxDegree);
        feoProvanOptsTselGrid.add(fpOptTselRandom);

        feoProvanOptsTsel.add(feoProvanOptsTselLabel);
        feoProvanOptsTsel.add(Box.createRigidArea(new Dimension(10, 0)));
        feoProvanOptsTsel.add(Box.createHorizontalGlue());
        feoProvanOptsTsel.add(feoProvanOptsTselGrid);

        // General controls: reset button
        JPanel general = new JPanel();
        general.setLayout(new BoxLayout(general, BoxLayout.X_AXIS));
        general.setAlignmentX(LEFT_ALIGNMENT);
        resetBtn = new JButton("Reset");
        resetBtn.addActionListener(this);
        resetBtn.setEnabled(false);
        general.add(Box.createHorizontalGlue());
        general.add(resetBtn);

        // Populate panel
        add(temperature);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(temperatureOpts);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(steinitz);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(steinitzOpts);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(feoProvan);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(feoProvanOptsStart);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(feoProvanOptsTsel);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(general);

    }

    /**
     * Enable the reset button.
     */
    void enableResetButton() {
        resetBtn.setEnabled(true);
    }

    /**
     * Enable the buttons for running reduction algorithms.
     */
    void enableReduceButtons() {
        temperatureBtnReduce.setEnabled(true);
        steinitzBtnReduce.setEnabled(true);
        feoProvanBtnReduce.setEnabled(true);
    }

    /**
     * Disable the buttons for running reduction algorithms.
     */
    void disableReduceButtons() {
        temperatureBtnReduce.setEnabled(false);
        steinitzBtnReduce.setEnabled(false);
        feoProvanBtnReduce.setEnabled(false);
        resetBtn.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if (steinitzReduceAction.equals(actionCommand)) {
            controller.runReductionSteinitz(steinitzBtnCancel);
        } else if (fpReduceAction.equals(actionCommand)) {
            controller.runReductionFeoProvan(feoProvanBtnCancel);
        } else if (temperatureReduceAction.equals(actionCommand)) {
            controller.runReductionTemperature(temperatureBtnCancel);
        } else if ("Reset".equals(actionCommand)) {
            resetBtn.setEnabled(false);
            controller.resetOutput();
        } else if (temperatureStrategyRandom.equals(actionCommand)) {
            controller.setTemperatureStrategy(TemperatureReduction.Strategy.RANDOM);
        } else if (temperatureStrategyShort.equals(actionCommand)) {
            controller.setTemperatureStrategy(TemperatureReduction.Strategy.SHORT);
        } else if (temperatureStrategyLong.equals(actionCommand)) {
            controller.setTemperatureStrategy(TemperatureReduction.Strategy.LONG);
        } else if (steinitzStrategyRandom.equals(actionCommand)) {
            controller.setSteinitzStrategy(
                    SteinitzGruenbaum.LensTriangleSelectionStrategy.RANDOM);
        } else if (steinitzStrategyMinLenses.equals(actionCommand)) {
            controller.setSteinitzStrategy(
                    SteinitzGruenbaum.LensTriangleSelectionStrategy.MINLENSES);
        } else if (steinitzStrategyMaxLenses.equals(actionCommand)) {
            controller.setSteinitzStrategy(
                    SteinitzGruenbaum.LensTriangleSelectionStrategy.MAXLENSES);
        } else if (steinitzStrategyYesPole.equals(actionCommand)) {
            controller.setSteinitzStrategy(
                    SteinitzGruenbaum.LensTriangleSelectionStrategy.PREFER_POLE);
        } else if (steinitzStrategyNoPole.equals(actionCommand)) {
            controller.setSteinitzStrategy(
                    SteinitzGruenbaum.LensTriangleSelectionStrategy.PREFER_NONPOLE);
        } else if (fpStartMin.equals(actionCommand)) {
            controller.setFeoProvanStartStrategy(FeoProvan.StartVertexStrategy.MINIMUM);
        } else if (fpStartMax.equals(actionCommand)) {
            controller.setFeoProvanStartStrategy(FeoProvan.StartVertexStrategy.MAXIMUM);
        } else if (fpStartRnd.equals(actionCommand)) {
            controller.setFeoProvanStartStrategy(FeoProvan.StartVertexStrategy.RANDOM);
        } else if (fpTselMinLabel.equals(actionCommand)) {
            controller.setFeoProvanTselStrategy(
                    FeoProvan.TransformSelectionStrategy.MINLABEL);
        } else if (fpTselMaxLabel.equals(actionCommand)) {
            controller.setFeoProvanTselStrategy(
                    FeoProvan.TransformSelectionStrategy.MAXLABEL);
        } else if (fpTselMinDegree.equals(actionCommand)) {
            controller.setFeoProvanTselStrategy(
                    FeoProvan.TransformSelectionStrategy.MINDEGREE);
        } else if (fpTselMaxDegree.equals(actionCommand)) {
            controller.setFeoProvanTselStrategy(
                    FeoProvan.TransformSelectionStrategy.MAXDEGREE);
        } else if (fpTselRandom.equals(actionCommand)) {
            controller.setFeoProvanTselStrategy(
                    FeoProvan.TransformSelectionStrategy.RANDOM);
        }
    }
}
