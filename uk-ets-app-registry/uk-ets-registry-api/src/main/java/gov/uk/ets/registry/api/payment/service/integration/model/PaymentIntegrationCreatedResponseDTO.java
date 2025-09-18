package gov.uk.ets.registry.api.payment.service.integration.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.uk.ets.registry.api.payment.domain.types.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentIntegrationCreatedResponseDTO {

    @JsonProperty("payment_id")
    private String paymentId;

    private String redirectUrl;

    private PaymentStatus status;

    @JsonProperty("_links")
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void unwrapLinks(Links links) {
        this.redirectUrl = links != null ? links.getNextUrlHref() : null;
    }

    @JsonProperty("state")
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void unwrapState(PaymentIntegrationStateDTO state) {
        this.status = state != null ? state.getStatus() : null;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Links {

        @JsonProperty("next_url")
        private UrlContainer nextUrl;

        public String getNextUrlHref() {
            return nextUrl != null ? nextUrl.getHref() : null;
        }

        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class UrlContainer {
            private String href;
        }
    }
}

