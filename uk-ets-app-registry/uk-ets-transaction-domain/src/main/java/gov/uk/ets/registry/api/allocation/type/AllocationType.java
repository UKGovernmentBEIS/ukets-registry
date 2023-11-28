package gov.uk.ets.registry.api.allocation.type;

import static gov.uk.ets.registry.api.allocation.type.AllocationCategory.AIRCRAFT_OPERATOR;
import static gov.uk.ets.registry.api.allocation.type.AllocationCategory.INSTALLATION;

import java.util.Optional;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enumerates the various allocation types.
 */
@Getter
@AllArgsConstructor
public enum AllocationType {

    /**
     * National allocation table for installations.
     */
    NAT(INSTALLATION),

    /**
     * National allocation table for aircraft operators.
     */
    NAVAT(AIRCRAFT_OPERATOR),

    /**
     * National allocation table for new entrants.
     */
    NER(INSTALLATION);

    private final AllocationCategory category;

    /**
     * Checks whether this allocation type is related with an installation.
     * @return false/true.
     */
    public boolean isRelatedWithInstallation() {
        return category == INSTALLATION;
    }

    /**
     * Checks whether this allocation type is related with an aircraft operator.
     * @return false/true.
     */
    public boolean isRelatedWithAircraftOperator() {
        return category == AIRCRAFT_OPERATOR;
    }

    /**
     * Parses the provided input string.
     * @param input The input string.
     * @return an allocation type.
     */
    public static AllocationType parse(String input) {
        AllocationType result = null;
        Optional<AllocationType> optional = Stream.of(values())
            .filter(type -> type.name().equals(input))
            .findFirst();
        if (optional.isPresent()) {
            result = optional.get();
        }
        return result;
    }

}
