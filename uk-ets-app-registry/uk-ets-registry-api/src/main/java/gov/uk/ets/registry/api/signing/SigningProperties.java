package gov.uk.ets.registry.api.signing;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("signing")
@Getter
@Setter
public class SigningProperties {

    private String verifySignatureEndpoint = "http://localhost:8585/api-signing/verify";

    private boolean enabled = false;
}
