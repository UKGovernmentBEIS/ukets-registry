package gov.uk.ets.registry.api.user.profile.recovery.service;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.notification.RecoveryEmailChangeNotification;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.profile.recovery.SmsNotification;
import gov.uk.ets.registry.api.user.profile.recovery.domain.SecurityCode;
import gov.uk.ets.registry.api.user.profile.recovery.repository.SecurityCodeRepository;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Log4j2
@Service
@RequiredArgsConstructor
public class SecurityCodeService {

    private static final int LENGTH = 6;
    private static final long TIME_TO_LIVE_MILLIS = 300000;
    public static final long TIMER_BUFFER_MILLIS = 6000; // 2% of 300000
    private static final String METADATA_CODE_NAME = "securityCode";

    private final SecurityCodeRepository repository;
    private final GroupNotificationClient groupNotificationClient;
    private final KafkaTemplate<String, Serializable> producerTemplate;

    @Value("${kafka.sms.notification.topic}")
    private String smsTopic;

    @Value("${sms.template}")
    private String smsTemplate;

    @Transactional
    public Date sendSecurityCode(String email, User user) {
        invalidateLastSecurityCode(user);
        SecurityCode securityCode = saveSecurityCode(user, email, null, null);
        GroupNotification recoveryEmailChangeNotification =
            generateRecoveryEmailChangeNotification(securityCode.getCode(), email);

        groupNotificationClient.emitGroupNotification(recoveryEmailChangeNotification);

        return securityCode.getExpiredAt();
    }

    @Transactional
    public Date sendSecurityCode(String countryCode, String phoneNumber, User user) {
        invalidateLastSecurityCode(user);
        SecurityCode securityCode = saveSecurityCode(user, null, countryCode, phoneNumber);
        producerTemplate.send(smsTopic, generateSmsNotification(securityCode));
        return securityCode.getExpiredAt();
    }

    public void validateSecurityCode(String email, User user, String securityCode) {
        validateSecurityCode(securityCode, getActiveSecurityCode(email, user));
    }

    public void validateSecurityCode(String countryCode, String phoneNumber, User user, String securityCode) {
        validateSecurityCode(securityCode, getActiveSecurityCode(countryCode, phoneNumber, user));
    }

    private void validateSecurityCode(String securityCode, Optional<SecurityCode> activeSecurityCode) {
        boolean notExists = activeSecurityCode
            .filter(code -> Objects.equals(securityCode, code.getCode()))
            .isEmpty();

        if (notExists) {
            throw new IllegalArgumentException("The security code entered is either incorrect or expired. Please try again.");
        }
    }

    private void invalidateLastSecurityCode(User user) {
        repository.findByUserAndValidIsTrue(user).ifPresent(securityCode -> securityCode.setValid(false));
    }

    @Transactional
    public void clearSecurityCodes(User user) {
        repository.deleteAllByUser(user);
    }

    public boolean hasValidSecurityCode(String email, User user) {
        return getValidSecurityCode(email, user).isPresent();
    }

    public boolean hasValidSecurityCode(String countryCode, String phoneNumber, User user) {
        return getValidSecurityCode(countryCode, phoneNumber, user).isPresent();
    }

    public Optional<SecurityCode> getActiveSecurityCode(String email, User user) {
        return getValidSecurityCode(email, user)
            .filter(code -> new Date().before(code.getExpiredAt()));
    }

    public Optional<SecurityCode> getActiveSecurityCode(String countryCode, String phoneNumber, User user) {
        return getValidSecurityCode(countryCode, phoneNumber, user)
            .filter(code -> new Date().before(code.getExpiredAt()));
    }

    private Optional<SecurityCode> getValidSecurityCode(String email, User user) {
        return repository.findByUserAndValidIsTrue(user)
            .filter(code -> Objects.equals(email, code.getEmail()));
    }

    private Optional<SecurityCode> getValidSecurityCode(String countryCode, String phoneNumber, User user) {
        return repository.findByUserAndValidIsTrue(user)
            .filter(code -> Objects.equals(countryCode, code.getCountryCode()))
            .filter(code -> Objects.equals(phoneNumber, code.getPhoneNumber()));
    }

    private SecurityCode saveSecurityCode(User user, String email, String countryCode, String phoneNumber) {
        SecurityCode securityCode = new SecurityCode();

        securityCode.setUser(user);
        securityCode.setCode(generateSecurityCode());
        securityCode.setValid(true);
        securityCode.setEmail(email);
        securityCode.setCountryCode(countryCode);
        securityCode.setPhoneNumber(phoneNumber);

        Instant now = Instant.now();
        securityCode.setCreatedAt(Date.from(now));
        securityCode.setExpiredAt(Date.from(now.plusMillis(TIME_TO_LIVE_MILLIS + TIMER_BUFFER_MILLIS)));

        repository.save(securityCode);
        return securityCode;
    }

    private String generateSecurityCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < LENGTH; i++) {
            sb.append(random.nextInt(10)); // Generates a random digit (0-9)
        }
        return sb.toString();
    }

    private SmsNotification generateSmsNotification(SecurityCode securityCode) {
        SmsNotification smsNotification = new SmsNotification();

        String code = Optional.ofNullable(securityCode.getCountryCode())
            .map(PhoneNumberUtil::normalizeDigitsOnly)
            .orElse("");

        smsNotification.setPhoneNumber("+" + code + securityCode.getPhoneNumber());
        smsNotification.setMetadata(Map.of(METADATA_CODE_NAME, securityCode.getCode()));
        smsNotification.setSmsTemplate(smsTemplate);

        return smsNotification;
    }

    private GroupNotification generateRecoveryEmailChangeNotification(String securityCode, String email) {
        RecoveryEmailChangeNotification groupNotification = new RecoveryEmailChangeNotification(securityCode);
        groupNotification.setRecipients(Set.of(email));
        return groupNotification;
    }
}
