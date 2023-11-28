package gov.uk.ets.registry.api.user.profile.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PasswordChangeDTO {
    private final String currentPassword;
    private final String newPassword;
}
