package gov.uk.ets.registry.api.user.messaging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * This is an exact copy of the Keycloak Event class, which resides in keycloak-server-spi-private package.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class KeycloakEvent {

    private String id;
    
    private long time;

    private KeycloakEventType type;

    private String realmId;

    private String clientId;

    private String userId;

    private String sessionId;

    private String ipAddress;

    private String error;

    private Map<String, String> details;

    private ResourceType resourceType;

    private OperationType operationType;

    private String resourcePath;

    private String representation;
}


