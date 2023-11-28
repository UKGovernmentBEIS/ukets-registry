package gov.uk.ets.registry.api.authz.miners;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class MinedRole {
    private final String id;

    public MinedRole(String id) {
        this.id = id;
    }

    public MinedRole not() {
        return new MinedRole("not " + id);
    }
}
