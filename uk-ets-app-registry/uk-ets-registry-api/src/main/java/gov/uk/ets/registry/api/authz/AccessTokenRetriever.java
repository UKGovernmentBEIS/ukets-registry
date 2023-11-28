package gov.uk.ets.registry.api.authz;

import org.keycloak.representations.AccessTokenResponse;

/**
 * Responsible for retrieving an access token.
 */
public interface AccessTokenRetriever {
    /**
     * Obtain an access token for the service account role.
     * @return the access token
     */
    AccessTokenResponse obtainAccessToken();
}
