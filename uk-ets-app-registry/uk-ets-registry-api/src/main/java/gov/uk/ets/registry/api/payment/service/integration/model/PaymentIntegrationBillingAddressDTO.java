package gov.uk.ets.registry.api.payment.service.integration.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentIntegrationBillingAddressDTO {
    private String line1;
    private String line2;
    private String postcode;
    private String city;
    private String country;
}
