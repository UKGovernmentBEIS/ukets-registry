package gov.uk.ets.registry.api.payment.service.integration.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.uk.ets.registry.api.payment.domain.types.PaymentStatus;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentIntegrationStateDTO {

    @JsonProperty("status")
    private PaymentStatus status;

    @JsonProperty("finished")
    private boolean finished;
}