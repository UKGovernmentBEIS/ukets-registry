package gov.uk.ets.registry.api.user.forgot.passwd;

import gov.uk.ets.registry.api.user.forgot.passwd.service.ForgotPasswordService;
import gov.uk.ets.registry.api.user.forgot.passwd.web.ResetPasswordRequest;
import gov.uk.ets.registry.api.user.forgot.passwd.web.ResetPasswordResponse;
import gov.uk.ets.registry.api.user.forgot.passwd.web.ValidateTokenResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for forgot password.
 */
@RestController
@RequestMapping(path = "/api-registry", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@RequiredArgsConstructor
public class ForgotPasswordController {

    private final ForgotPasswordService forgotPasswordService;

    /**
     * @param email the user email(matches the username)
     * @return an empty response regardless the user's email exists in the registry database
     */
    @PostMapping(path = "forgot-password.request.link")
    public ResponseEntity<Void> requestResetPasswordLink(@RequestParam @Valid @Email String email) {
        forgotPasswordService.requesResetPasswordEmail(email);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "forgot-password.validate.token")
    public ValidateTokenResponse validateToken(@RequestParam @Valid @NotEmpty String token) {
        return forgotPasswordService.validateToken(token);
    }

    @PostMapping(path = "forgot-password.reset.password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResetPasswordResponse resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        return forgotPasswordService.resetPassword(request);
    }
}
