package gov.uk.ets.registry.api.user.profile.service;

import gov.uk.ets.registry.api.user.profile.domain.EmergencyChange;
import gov.uk.ets.registry.api.user.profile.web.EmergencyOtpChangeTaskRequest;
import gov.uk.ets.registry.api.user.profile.web.EmergencyOtpChangeTaskResponse;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class EmergencyPasswordOtpChangeService {
    private final Long passwordOtpExpiration;
    private final String passwordOtpVerificationPath;
    private final CommonEmergencyChangeService changeService;

    /**
     * Constructor injection needed for @Value properties.
     */
    public EmergencyPasswordOtpChangeService(
        @Value("${change.password-otp.emergency.verification.url.expiration}") Long passwordOtpExpiration,
        @Value("${change.password-otp.emergency.verification.path:/emergency-password-otp-change/email-verify?token=}")
            String passwordOtpVerificationPath,
        CommonEmergencyChangeService changeService
    ) {
        this.passwordOtpExpiration = passwordOtpExpiration;
        this.passwordOtpVerificationPath = passwordOtpVerificationPath;
        this.changeService = changeService;
    }

    /**
     * Generates emergency otp request email data.
     */
    @EmitsGroupNotifications(GroupNotificationType.EMERGENCY_PASSWORD_OTP_CHANGE_REQUESTED)
    public EmergencyChange requestChange(String email) {
        return this.changeService
            .requestEmergencyChange(email, this.passwordOtpExpiration, this.passwordOtpVerificationPath);
    }

    public EmergencyOtpChangeTaskResponse confirmChange(EmergencyOtpChangeTaskRequest request) {
        return this.changeService.confirmPasswordOtpChange(request);
    }
}
