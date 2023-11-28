package gov.uk.ets.registry.api.user.messaging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * The mapper object for the representation property of Keycloak Admin Events.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class AdminEventRepresentation {

    private String id;

    private String name;

    private String clientRole;

    private String containerId;
}


