package ec.edu.uce.pokedex.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EvolutionService {

    private final WebClient webClient;

    public EvolutionService(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Obtiene la cadena evolutiva de un Pokémon por su nombre de especie.
     *
     * @param speciesName Nombre de la especie del Pokémon.
     * @return Mono con una lista de mapas que representan los detalles evolutivos.
     */
    public Mono<List<Map<String, Object>>> getEvolutionChain(String speciesName) {
        return fetchSpeciesData(speciesName)
                .flatMap(this::fetchEvolutionChain)
                .map(this::extractEvolutionDetails);
    }

    /**
     * Realiza una solicitud para obtener datos de una especie.
     */
    private Mono<Map<String, Object>> fetchSpeciesData(String speciesName) {
        return webClient.get()
                .uri("/pokemon-species/{name}", speciesName)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }

    /**
     * Realiza una solicitud para obtener la cadena evolutiva usando la URL proporcionada.
     */
    private Mono<Map<String, Object>> fetchEvolutionChain(Map<String, Object> speciesData) {
        String evolutionChainUrl = (String) ((Map<String, Object>) speciesData.get("evolution_chain")).get("url");
        return webClient.get()
                .uri(evolutionChainUrl)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }

    /**
     * Extrae los detalles de evolución de la cadena evolutiva.
     */
    private List<Map<String, Object>> extractEvolutionDetails(Map<String, Object> chainData) {
        return flattenEvolutionChain((Map<String, Object>) chainData.get("chain"));
    }

    /**
     * Convierte la estructura anidada de la cadena evolutiva en una lista plana.
     */
    private List<Map<String, Object>> flattenEvolutionChain(Map<String, Object> chain) {
        List<Map<String, Object>> evolutions = new ArrayList<>();
        traverseEvolutionChain(chain, evolutions);
        return evolutions;
    }

    /**
     * Recorre recursivamente la cadena evolutiva y acumula los datos.
     */
    private void traverseEvolutionChain(Map<String, Object> chain, List<Map<String, Object>> evolutions) {
        evolutions.add(Map.of(
                "species", chain.get("species"),
                "evolution_details", chain.get("evolution_details")
        ));

        List<Map<String, Object>> evolvesTo = (List<Map<String, Object>>) chain.get("evolves_to");
        if (evolvesTo != null) {
            evolvesTo.forEach(nextEvolution -> traverseEvolutionChain(nextEvolution, evolutions));
        }
    }
}
