package ec.edu.uce.pokedex.exception;

/**
 * Custom exception for errors occurring while fetching sprites.
 */
public class SpriteFetchException extends RuntimeException {

    /**
     * Constructs a new SpriteFetchException with the specified detail message.
     *
     * @param message the detail message
     */
    public SpriteFetchException(String message) {
        super(message);
    }

    /**
     * Constructs a new SpriteFetchException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public SpriteFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}
