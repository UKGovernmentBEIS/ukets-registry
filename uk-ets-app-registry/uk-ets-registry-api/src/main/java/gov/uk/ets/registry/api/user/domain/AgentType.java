package gov.uk.ets.registry.api.user.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AgentType {

    YES_PUBLIC("Yes-Public"),
    YES_PRIVATE("Yes-Private"),
    NO("No")
    ;

    private final String description;

}
