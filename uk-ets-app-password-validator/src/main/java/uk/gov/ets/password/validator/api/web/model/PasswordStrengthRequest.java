package uk.gov.ets.password.validator.api.web.model;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordStrengthRequest {

    @NotNull
    String password;
}
