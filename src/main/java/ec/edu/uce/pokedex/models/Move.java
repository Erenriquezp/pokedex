package ec.edu.uce.pokedex.models;

import lombok.Data;
import java.util.List;

@Data
public class Move {
    private String name;
    private String url;
    private List<VersionGroupDetail> versionGroupDetails;
}
