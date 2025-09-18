package gov.uk.ets.registry.api.payment.service.integration.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.uk.ets.registry.api.payment.domain.types.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentIntegrationStatusResponseDTO {

    @JsonProperty("amount")
    private Integer amount;

    private PaymentStatus status;

    @JsonProperty("settlement_summary.captured_date")
    private LocalDate paidOn;

    private String email;

    @JsonProperty("card_details")
    private PaymentIntegrationCardDetailsDTO cardDetails;

    @JsonProperty("state")
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void unwrapState(PaymentIntegrationStateDTO state) {
        this.status = state != null ? state.getStatus() : null;
    }

    @JsonProperty("settlement_summary")
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void unwrapSettlementSummary(SettlementSummary settlementSummary) {
        this.paidOn = settlementSummary != null ? settlementSummary.getCapturedDate() : null;
    }

    @Getter
    public static class SettlementSummary {
        @JsonProperty("captured_date")
        private LocalDate capturedDate;
    }
}

