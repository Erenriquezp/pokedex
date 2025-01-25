package ec.edu.uce.pokedex.exception;

/**
 * Custom exception for errors occurring during the fetching of evolution chains.
 */
public class EvolutionFetchException extends RuntimeException {

    /**
     * Constructs a new EvolutionFetchException with the specified detail message.
     *
     * @param message the detail message
     */
    public EvolutionFetchException(String message) {
        super(message);
    }

    /**
     * Constructs a new EvolutionFetchException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public EvolutionFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}
