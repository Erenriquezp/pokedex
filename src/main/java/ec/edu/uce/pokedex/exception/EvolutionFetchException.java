package ec.edu.uce.pokedex.exception;

/**
 * Excepción personalizada para errores que ocurren durante la obtención de cadenas de evolución.
 */
public class EvolutionFetchException extends RuntimeException {

    /**
     * Construye una nueva EvolutionFetchException con el mensaje de detalle especificado.
     *
     * @param message el mensaje de detalle
     */
    public EvolutionFetchException(String message) {
        super(message);
    }

    /**
     * Construye una nueva EvolutionFetchException con el mensaje detallado y la causa especificados.
     *
     * @param message el mensaje detallado
     * @param cause la causa de la excepción
     */
    public EvolutionFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}
