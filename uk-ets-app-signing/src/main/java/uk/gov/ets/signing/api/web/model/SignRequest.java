package uk.gov.ets.signing.api.web.model;

import lombok.Data;

@Data
public class SignRequest {
    private String data;
    private String otpCode;
}
