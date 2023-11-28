package uk.gov.ets.signing.api.web.model;

import lombok.Data;

@Data
public class VerificationRequest {
    private String data;
    private String signature;
}
