package gov.uk.ets.registry.api.user.profile.web;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class PasswordValidationRequest {
    private final String password;
}
