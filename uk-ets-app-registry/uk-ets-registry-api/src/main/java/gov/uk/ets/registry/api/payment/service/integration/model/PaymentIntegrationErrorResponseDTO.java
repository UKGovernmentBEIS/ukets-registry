package gov.uk.ets.registry.api.payment.service.integration.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentIntegrationErrorResponseDTO {
    private String code;
    private String description;
}
