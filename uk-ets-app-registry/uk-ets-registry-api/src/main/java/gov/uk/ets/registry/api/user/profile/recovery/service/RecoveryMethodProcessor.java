package gov.uk.ets.registry.api.user.profile.recovery.service;

import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.profile.recovery.domain.SecurityCode;
import gov.uk.ets.registry.api.user.profile.recovery.web.RecoveryMethodRemoveRequest;
import gov.uk.ets.registry.api.user.profile.recovery.web.RecoveryMethodUpdateRequest;
import gov.uk.ets.registry.api.user.profile.recovery.web.RecoveryMethodUpdateResponse;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static gov.uk.ets.registry.api.user.profile.recovery.service.SecurityCodeService.TIMER_BUFFER_MILLIS;

@Service
@RequiredArgsConstructor
public class RecoveryMethodProcessor {

    private final SecurityCodeService securityCodeService;
    private final UserService userService;
    private final EventService eventService;

    @Transactional
    public RecoveryMethodUpdateResponse generateSecurityCode(RecoveryMethodUpdateRequest request) {

        User currentUser = userService.getCurrentUser();
        RecoveryMethodUpdateResponse response = new RecoveryMethodUpdateResponse();
        Date expiredAt;
        if (request.getEmail() != null) {
            checkEmail(request.getEmail(), currentUser);
            expiredAt = getOrSendSecurityCode(request.getEmail(), currentUser);
            response.setEmail(request.getEmail());
        } else {
            expiredAt = getOrSendSecurityCode(request.getCountryCode(), request.getPhoneNumber(), currentUser);
            response.setCountryCode(request.getCountryCode());
            response.setPhoneNumber(request.getPhoneNumber());
        }
        response.setExpiresInMillis(calculateRemainingMillis(expiredAt));

        return response;
    }

    @Transactional
    public RecoveryMethodUpdateResponse resendSecurityCode(RecoveryMethodUpdateRequest request) {

        User currentUser = userService.getCurrentUser();
        RecoveryMethodUpdateResponse response = new RecoveryMethodUpdateResponse();
        Date expiredAt;
        if (request.getEmail() != null
            && securityCodeService.hasValidSecurityCode(request.getEmail(), currentUser)) {
            expiredAt = getOrSendSecurityCode(request.getEmail(), currentUser);
            response.setEmail(request.getEmail());
        } else if (securityCodeService.hasValidSecurityCode(request.getCountryCode(), request.getPhoneNumber(), currentUser)) {
            expiredAt = getOrSendSecurityCode(request.getCountryCode(), request.getPhoneNumber(), currentUser);
            response.setCountryCode(request.getCountryCode());
            response.setPhoneNumber(request.getPhoneNumber());
        } else {
            throw new IllegalArgumentException("You are not allowed to request new security code.");
        }
        response.setExpiresInMillis(calculateRemainingMillis(expiredAt));
        return response;
    }

    private Long calculateRemainingMillis(Date expiredAt) {
        return expiredAt == null ? null :
            Math.max((long)((expiredAt.getTime() - System.currentTimeMillis()) - TIMER_BUFFER_MILLIS), 0);
    }

    private Date getOrSendSecurityCode(String email, User user) {
        return securityCodeService.getActiveSecurityCode(email, user)
            .map(SecurityCode::getExpiredAt)
            .orElseGet(() -> securityCodeService.sendSecurityCode(email, user));
    }

    private Date getOrSendSecurityCode(String countryCode, String phoneNumber, User user) {
        return securityCodeService.getActiveSecurityCode(countryCode, phoneNumber, user)
            .map(SecurityCode::getExpiredAt)
            .orElseGet(() -> securityCodeService.sendSecurityCode(countryCode, phoneNumber, user));
    }

    @Transactional
    public void updateRecoveryMethod(RecoveryMethodUpdateRequest recoveryMethodUpdateRequest) {
        User currentUser = userService.getCurrentUser();
        String urid = currentUser.getUrid();
        String securityCode = recoveryMethodUpdateRequest.getSecurityCode();
        if (recoveryMethodUpdateRequest.getEmail() != null) {
            checkEmail(recoveryMethodUpdateRequest.getEmail(), currentUser);
            securityCodeService.validateSecurityCode(recoveryMethodUpdateRequest.getEmail(), currentUser, securityCode);
            eventService.createAndPublishEvent(urid, urid, "", EventType.USER_RECOVERY_METHOD_UPDATED, "Update recovery email");
        } else {
            securityCodeService.validateSecurityCode(recoveryMethodUpdateRequest.getCountryCode(),
                recoveryMethodUpdateRequest.getPhoneNumber(), currentUser, securityCode);
            eventService.createAndPublishEvent(urid, urid, "", EventType.USER_RECOVERY_METHOD_UPDATED, "Update recovery phone");
        }
        userService.updateUserRecoveryMethods(recoveryMethodUpdateRequest);
        securityCodeService.clearSecurityCodes(currentUser);
    }

    private void checkEmail(String newRecoveryEmail, User user) {
        if (Objects.equals(user.getEmail(), newRecoveryEmail)) {
            throw new IllegalArgumentException("User email address cannot be the same as user recovery email address.");
        }
    }

    @Transactional
    public void removeRecoveryMethod(RecoveryMethodRemoveRequest recoveryMethodRemoveRequest) {

        RecoveryMethodUpdateRequest recoveryMethodUpdateRequest = new RecoveryMethodUpdateRequest();
        String urid = userService.getCurrentUser().getUrid();

        if (Boolean.TRUE.equals(recoveryMethodRemoveRequest.getRemoveEmail())) {
            recoveryMethodUpdateRequest.setEmail("");
            eventService.createAndPublishEvent(urid, urid, "", EventType.USER_RECOVERY_METHOD_DELETED, "Remove recovery email");
        }

        if (Boolean.TRUE.equals(recoveryMethodRemoveRequest.getRemovePhoneNumber())) {
            recoveryMethodUpdateRequest.setCountryCode("");
            recoveryMethodUpdateRequest.setPhoneNumber("");
            eventService.createAndPublishEvent(urid, urid, "", EventType.USER_RECOVERY_METHOD_DELETED, "Remove recovery phone");
        }

        userService.updateUserRecoveryMethods(recoveryMethodUpdateRequest);
    }

    public void hideRecoveryMethodsNotification() {
        userService.setHideRecoveryMethodsNotification();
    }
}
