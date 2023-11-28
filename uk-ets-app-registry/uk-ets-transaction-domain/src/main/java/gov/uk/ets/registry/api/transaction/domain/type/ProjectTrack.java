package gov.uk.ets.registry.api.transaction.domain.type;

import java.util.Optional;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enumerates the available project tracks.
 */
@Getter
@AllArgsConstructor
public enum ProjectTrack {

    /**
     *  Track 1: A party project.
     */
    TRACK_1(1),

    /**
     * Track 2: An Article 6 Supervisory Committee project.
     */
    TRACK_2(2);

    /**
     * The track code, according to ITL DES.
     */
    private Integer code;

    /**
     * Identifies the project track based on the code.
     * @param code The code.
     * @return a project track.
     */
    public static ProjectTrack of(int code) {
        ProjectTrack result = null;
        Optional<ProjectTrack> optional = Stream.of(values())
            .filter(type -> type.getCode().equals(code))
            .findFirst();
        if (optional.isPresent()) {
            result = optional.get();
        }
        return result;
    }


}
