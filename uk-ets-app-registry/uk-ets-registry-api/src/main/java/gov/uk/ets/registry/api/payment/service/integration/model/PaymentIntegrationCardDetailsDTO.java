package gov.uk.ets.registry.api.payment.service.integration.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentIntegrationCardDetailsDTO {

    @JsonProperty("cardholder_name")
    private String cardholderName;

    @JsonProperty("billing_address")
    private PaymentIntegrationBillingAddressDTO billingAddress;
}
