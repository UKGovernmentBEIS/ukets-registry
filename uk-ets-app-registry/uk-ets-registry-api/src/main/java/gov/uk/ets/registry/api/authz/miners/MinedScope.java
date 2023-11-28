package gov.uk.ets.registry.api.authz.miners;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class MinedScope {

    public static final MinedScope NO_SCOPE = new MinedScope("NO_SCOPE");

    private final String name;

    public MinedScope(String name) {
        this.name = name;
    }
}
