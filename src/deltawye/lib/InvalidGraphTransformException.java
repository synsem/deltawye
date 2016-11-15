package deltawye.lib;

/**
 * Thrown to indicate an invalid graph transformation.
 */
public class InvalidGraphTransformException extends RuntimeException {

    /**
     * Serial Version ID.
     */
    private static final long serialVersionUID = -8177992448286253136L;

    /**
     * Construct a new exception with the specified detail message.
     *
     * @param message
     *            the detail message
     */
    public InvalidGraphTransformException(String message) {
        super(message);
    }

}
