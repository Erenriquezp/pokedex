package ec.edu.uce.pokedex.service;

import ec.edu.uce.pokedex.models.Move;
import ec.edu.uce.pokedex.models.VersionGroupDetail;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@Service
public class MoveService {
    private final WebClient webClient;

    public MoveService(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Retrieves the moves for a given Pokémon.
     *
     * @param pokemonName Name of the Pokémon.
     * @return Flux of Move objects.
     */
    public Flux<Move> getMovesForPokemon(String pokemonName) {
        return webClient.get()
                .uri("/pokemon/{name}", pokemonName)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMapMany(response -> Flux.fromIterable((List<Map<String, Object>>) response.get("moves")))
                .map(moveData -> {
                    Map<String, Object> moveInfo = (Map<String, Object>) moveData.get("move");
                    List<Map<String, Object>> versionGroupDetails = (List<Map<String, Object>>) moveData.get("version_group_details");

                    Move move = new Move();
                    move.setName((String) moveInfo.get("name"));
                    move.setUrl((String) moveInfo.get("url"));

                    move.setVersionGroupDetails(versionGroupDetails.stream().map(detail -> {
                        VersionGroupDetail versionGroupDetail = new VersionGroupDetail();
                        versionGroupDetail.setLevelLearnedAt((int) detail.get("level_learned_at"));
                        versionGroupDetail.setMoveLearnMethod((String) ((Map<String, Object>) detail.get("move_learn_method")).get("name"));
                        versionGroupDetail.setVersionGroup((String) ((Map<String, Object>) detail.get("version_group")).get("name"));
                        return versionGroupDetail;
                    }).toList());

                    return move;
                });
    }
}
