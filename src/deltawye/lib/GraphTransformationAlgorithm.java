package deltawye.lib;

import java.util.List;

/**
 * An interface for graph transformation algorithms.
 */
public interface GraphTransformationAlgorithm {

    /**
     * Return whether the algorithm will perform another transformation.
     *
     * @return true if there is another transformation
     */
    public boolean hasNextStep();

    /**
     * Perform the next transformation step and return a description of the
     * transformation.
     *
     * @return description of the transformation
     */
    public String nextStep();

    /**
     * Report the current progress of the transformation as an integer between 0
     * and 100.
     *
     * @return progress indication
     */
    public int getProgress();

    /**
     * Perform reduction algorithm.
     *
     * @param verbose
     *            if true report each step to stdout
     * @return a description of the transformation sequence
     */
    public List<String> run(boolean verbose);

    /**
     * Restore the original graph.
     */
    public void reset();

    /**
     * Return the normalized length of the specified reduction sequence.
     *
     * <p>
     * The normalized length is the number of Delta-Wye or Wye-Delta
     * transformations required for reduction to K1.
     *
     * @param sequence
     *            the reduction sequence to normalize
     * @return normalized length
     */
    public int normalizedLength(List<String> sequence);

    /**
     * Return the number of Delta-Wye transformations contained in the specified
     * reduction sequence.
     *
     * @param sequence
     *            the reduction sequence to analyze
     * @return number of delta-wye transformations
     */
    public int deltaWyeCount(List<String> sequence);

    /**
     * Return the number of Wye-Delta transformations contained in the specified
     * reduction sequence.
     *
     * @param sequence
     *            the reduction sequence to analyze
     * @return number of wye-delta transformations
     */
    public int wyeDeltaCount(List<String> sequence);

}
