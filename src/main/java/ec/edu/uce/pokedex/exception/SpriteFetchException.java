package ec.edu.uce.pokedex.exception;

/**
 * Excepción personalizada para errores que ocurren al obtener sprites.
 */
public class SpriteFetchException extends RuntimeException {

    /**
     * Construye una nueva SpriteFetchException con el mensaje de detalle especificado.
     *
     * @param message el mensaje de detalle
     */
    public SpriteFetchException(String message) {
        super(message);
    }

    /**
     * Construye una nueva SpriteFetchException con el mensaje detallado y la causa especificados.
     *
     * @param message el mensaje detallado
     * @param cause la causa de la excepción
     */
    public SpriteFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}
