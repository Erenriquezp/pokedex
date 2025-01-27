package ec.edu.uce.pokedex.exception;

/**
 * Excepción personalizada para errores que ocurren al obtener estadísticas de Pokémon.
 */
public class StatFetchException extends RuntimeException {

    /**
     * Construye una nueva StatFetchException con el mensaje de detalle especificado.
     *
     * @param message el mensaje de detalle.
     */
    public StatFetchException(String message) {
        super(message);
    }

    /**
     * Construye una nueva StatFetchException con el mensaje detallado y la causa especificados.
     *
     * @param message el mensaje detallado.
     * @param cause la causa de la excepción.
     */
    public StatFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}
