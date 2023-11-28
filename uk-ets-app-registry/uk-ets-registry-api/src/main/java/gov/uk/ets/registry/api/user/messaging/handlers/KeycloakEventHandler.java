package gov.uk.ets.registry.api.user.messaging.handlers;

import gov.uk.ets.registry.api.user.messaging.KeycloakEvent;

/**
 * All keycloak event handlers must implement this interface.
 */
public interface KeycloakEventHandler {

    /**
     * Returns true if the handler can handle/process the specific event.
     */
    boolean canHandle(KeycloakEvent event);

    /**
     * Contains the main processing logic of the handler.
     */
    void handle(KeycloakEvent event);

}
