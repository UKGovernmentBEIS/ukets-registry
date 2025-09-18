package gov.uk.ets.registry.api.payment.service.integration.utils;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "payment.integration")
@Validated
public class UkGovPaymentProperties {

    @NotBlank
    private String apiUrl;

    @NotBlank
    private String apiKey;

    @NotBlank
    private String returnUrl;
    
    private PaymentWebLinkProperties weblink;

    @PostConstruct
    public void validate() {
        if (!returnUrl.contains("%s")) {
            throw new IllegalArgumentException("payment.integration.returnUrl must contain '%s' for reference identifier.");
        }
    }
}
