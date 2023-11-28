package gov.uk.ets.registry.api.user.profile.service;

import gov.uk.ets.registry.api.user.profile.domain.EmergencyChange;
import gov.uk.ets.registry.api.user.profile.web.EmergencyOtpChangeTaskRequest;
import gov.uk.ets.registry.api.user.profile.web.EmergencyOtpChangeTaskResponse;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class EmergencyOtpChangeService {
    private final Long otpExpiration;
    private final String otpVerificationPath;
    private final CommonEmergencyChangeService changeService;

    /**
     * Constructor injection needed for @Value properties.
     */
    public EmergencyOtpChangeService(
        @Value("${change.otp.emergency.verification.url.expiration}") Long otpExpiration,
        @Value("${change.otp.emergency.verification.path:/emergency-otp-change/email-verify?token=}")
            String otpVerificationPath, CommonEmergencyChangeService changeService) {
        this.otpExpiration = otpExpiration;
        this.otpVerificationPath = otpVerificationPath;
        this.changeService = changeService;
    }

    /**
     * Generates emergency otp request email data.
     */
    @EmitsGroupNotifications(GroupNotificationType.EMERGENCY_OTP_CHANGE_REQUESTED)
    public EmergencyChange requestChange(String email) {
        return changeService.requestEmergencyChange(email, this.otpExpiration, this.otpVerificationPath);
    }

    /**
     * Verifies token, creates task and suspends user.
     */
    @Transactional
    public EmergencyOtpChangeTaskResponse confirmChange(EmergencyOtpChangeTaskRequest request) {
        return this.changeService.confirmChange(request);
    }
}
