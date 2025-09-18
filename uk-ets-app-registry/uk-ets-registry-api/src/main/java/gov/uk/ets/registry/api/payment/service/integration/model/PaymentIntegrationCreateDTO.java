package gov.uk.ets.registry.api.payment.service.integration.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class PaymentIntegrationCreateDTO {
    private Integer amount;
    @JsonProperty("return_url")
    private String returnUrl;
    private String reference;
    private String description;
}
