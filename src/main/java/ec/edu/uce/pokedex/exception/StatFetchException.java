package ec.edu.uce.pokedex.exception;

/**
 * Custom exception for errors occurring while fetching Pokémon stats.
 */
public class StatFetchException extends RuntimeException {

    /**
     * Constructs a new StatFetchException with the specified detail message.
     *
     * @param message the detail message.
     */
    public StatFetchException(String message) {
        super(message);
    }

    /**
     * Constructs a new StatFetchException with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause of the exception.
     */
    public StatFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}
