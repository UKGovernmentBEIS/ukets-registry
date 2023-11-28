package uk.gov.ets.registration.user.validation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidatePasswordResponse {
    private boolean valid = false;
    private String errorCode;
    private String message;
}
