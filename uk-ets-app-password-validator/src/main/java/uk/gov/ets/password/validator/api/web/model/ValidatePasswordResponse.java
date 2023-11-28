package uk.gov.ets.password.validator.api.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidatePasswordResponse {

	private boolean valid = false;
	private String errorCode;
    private String message;
}
