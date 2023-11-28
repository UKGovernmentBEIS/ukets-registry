package gov.uk.ets.registry.api.allocation.type;

import java.util.Arrays;
import java.util.List;

public enum AllocationCategory {
    INSTALLATION,
    AIRCRAFT_OPERATOR;

    public List<String> getTypeNames() {
        return Arrays.stream(AllocationType.values())
            .filter(allocationType -> allocationType.getCategory() == this)
            .map(AllocationType::name)
            .toList();
    }
}
