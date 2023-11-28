package uk.gov.ets.signing.api;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("signing")
@Getter
@Setter
public class SigningProperties {

    private Keycloak keycloak = new Keycloak();

    private Vault vault = new Vault();

    @Getter
    @Setter
    public static class Keycloak {

        private String validatorEndpoint;

        private boolean enabled = true;
    }

    @Getter
    @Setter
    public static class Vault {

        private String uri;

        private String engine;

        private String key;

        private String roleId;

        private String secretId;
    }
}
