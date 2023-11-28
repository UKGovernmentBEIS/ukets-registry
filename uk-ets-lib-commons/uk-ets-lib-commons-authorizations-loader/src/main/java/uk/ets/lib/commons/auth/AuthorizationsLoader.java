package uk.ets.lib.commons.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ResourceServerRepresentation;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Log4j2
@RequiredArgsConstructor
@Component
public class AuthorizationsLoader {

    private final ObjectMapper mapper;

    public void doLoad(Configuration config, Resource apiAuthzConfig) {
        Keycloak keycloak =
            Keycloak.getInstance(
                config.getAuthServerUrl(),
                config.getRealm(),
                config.getResource(),
                AuthzClient.create(config).obtainAccessToken().getToken());
        try {
            ResourceServerRepresentation resourceServerRep =
                mapper.readValue(apiAuthzConfig.getInputStream(), ResourceServerRepresentation.class);
            List<ClientRepresentation> clients =
                keycloak
                    .realm(config.getRealm())
                    .clients()
                    .findByClientId(config.getResource());
            if (clients.size() != 1) {
                log.debug("No client was found in keycloak");
                return;
            }
            ClientRepresentation ukEtsRegistryApiClientRep = clients.get(0);
            log.info("Deleting old authorization data");
            ukEtsRegistryApiClientRep.setAuthorizationServicesEnabled(Boolean.FALSE);
            keycloak
                .realm(config.getRealm())
                .clients()
                .get(ukEtsRegistryApiClientRep.getId())
                .update(ukEtsRegistryApiClientRep);
            log.info("Deleting old authorization data success");
            log.info("Re-enabling authorization services");
            ukEtsRegistryApiClientRep.setAuthorizationServicesEnabled(Boolean.TRUE);
            keycloak
                .realm(config.getRealm())
                .clients()
                .get(ukEtsRegistryApiClientRep.getId())
                .update(ukEtsRegistryApiClientRep);

            removeDefaultResource(config, keycloak, ukEtsRegistryApiClientRep);

            log.info("Re-enabling  authorization services success");
            log.info("Loading authorization data");
            keycloak
                .realm(config.getRealm())
                .clients()
                .get(ukEtsRegistryApiClientRep.getId())
                .authorization()
                .importSettings(resourceServerRep);
            log.info("Authorization data loaded successfully");
        } catch (Exception e) {
            throw new IllegalStateException("Could not perform operation", e);
        }
    }

    /**
     * When authz settings are enabled for a realm a default resource is created.This Default
     * Resource must be removed.
     */
    private void removeDefaultResource(
        Configuration config,
        Keycloak keycloak,
        ClientRepresentation ukEtsRegistryApiClientRep) {
        List<ResourceRepresentation> resources =
            keycloak
                .realm(config.getRealm())
                .clients()
                .get(ukEtsRegistryApiClientRep.getId())
                .authorization()
                .resources()
                .resources();

        resources.forEach(
            r -> {
                keycloak
                    .realm(config.getRealm())
                    .clients()
                    .get(ukEtsRegistryApiClientRep.getId())
                    .authorization()
                    .resources()
                    .resource(r.getId())
                    .remove();
            });
    }
}
