package uk.gov.ets.password.validator.api.web;

import gov.uk.ets.registry.api.common.validators.PasswordStrength;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ets.password.validator.api.service.PasswordValidatorService;
import uk.gov.ets.password.validator.api.web.model.PasswordStrengthRequest;
import uk.gov.ets.password.validator.api.web.model.PasswordStrengthResponse;
import uk.gov.ets.password.validator.api.web.model.ValidatePasswordResponse;

@RestController
@CrossOrigin(origins = {"${application.url}"})
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/api-password-validate", produces = MediaType.APPLICATION_JSON_VALUE)
@Log4j2
public class PasswordValidatorServiceController {

    private final PasswordValidatorService passwordValidatorService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ValidatePasswordResponse> validatePassword(
        @RequestBody(required = true) @NotNull @PasswordStrength String password) {

        if (!passwordValidatorService.compliesWithPasswordPolicies(password)) {
            log.warn("Password does not conform to password policies.");
            return new ResponseEntity<>(new ValidatePasswordResponse(
                false,
                "password_policy_violation",
                "Password does not conform to password policies."),
                                        HttpStatus.OK);
        }

        if (passwordValidatorService.isPwned(password)) {
            log.warn("Password has been pwned.");
            return new ResponseEntity<>(new ValidatePasswordResponse(
                false,
                "pwned",
                "Password has been pwned."),
                HttpStatus.OK);
        }

        return new ResponseEntity<>(new ValidatePasswordResponse(
            true,
            null,
            null),
            HttpStatus.OK);
    }

    @PostMapping(path = "strength.calculate", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PasswordStrengthResponse> passwordStrength(
        @RequestBody(required = true) @Valid PasswordStrengthRequest passwordStrengthRequest) {

        int score = passwordValidatorService.passwordStrength(passwordStrengthRequest.getPassword());
        log.info(String.format("Requested password strength score is: %d", score));

        return new ResponseEntity<>(new PasswordStrengthResponse(score), HttpStatus.OK);
    }
}
