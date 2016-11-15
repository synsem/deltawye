package deltawye.lib;

/**
 * Thrown to indicate an invalid walk.
 *
 * <p>
 * For example, a walk is invalid if subsequent edges in its edge list are not
 * adjacent.
 */
public class InvalidWalkException extends RuntimeException {

    /**
     * Serial Version ID.
     */
    private static final long serialVersionUID = 5841951763497625217L;

    /**
     * Construct a new exception.
     */
    public InvalidWalkException() {
        super();
    }

    /**
     * Construct a new exception with the specified detail message.
     *
     * @param message
     *            the detail message
     */
    public InvalidWalkException(String message) {
        super(message);
    }

}
