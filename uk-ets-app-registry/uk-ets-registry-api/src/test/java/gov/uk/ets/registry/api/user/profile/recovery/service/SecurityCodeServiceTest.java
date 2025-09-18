package gov.uk.ets.registry.api.user.profile.recovery.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.profile.recovery.SmsNotification;
import gov.uk.ets.registry.api.user.profile.recovery.domain.SecurityCode;
import gov.uk.ets.registry.api.user.profile.recovery.repository.SecurityCodeRepository;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

class SecurityCodeServiceTest {

    @Mock
    private SecurityCodeRepository repository;

    @Mock
    private GroupNotificationClient groupNotificationClient;

    @Mock
    private KafkaTemplate<String, Serializable> producerTemplate;

    private SecurityCodeService service;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        service = new SecurityCodeService(repository, groupNotificationClient, producerTemplate);
    }

    @Test
    void sendSecurityCode_Email() {
        // given
        User user = new User();
        SecurityCode securityCode = new SecurityCode();

        when(repository.findByUserAndValidIsTrue(user)).thenReturn(Optional.of(securityCode));

        // when
        service.sendSecurityCode("test@mail.com", user);

        // then
        verify(repository, times(1)).save(any(SecurityCode.class));
        verify(groupNotificationClient, times(1)).emitGroupNotification(any(GroupNotification.class));
    }

    @Test
    void sendSecurityCode_PhoneNumber() {
        // given
        User user = new User();
        SecurityCode securityCode = new SecurityCode();

        when(repository.findByUserAndValidIsTrue(user)).thenReturn(Optional.of(securityCode));

        // when
        service.sendSecurityCode("GR (30)", "6987456321", user);

        // then
        verify(repository, times(1)).save(any(SecurityCode.class));
        verify(producerTemplate, times(1)).send(any(), any(SmsNotification.class));
    }

    @Test
    void validateSecurityCode_Email() {
        // given
        User user = new User();
        SecurityCode securityCode = new SecurityCode();
        securityCode.setCode("123456");
        securityCode.setEmail("test@mail.com");
        securityCode.setExpiredAt(Date.from(Instant.now().plusSeconds(100L)));

        when(repository.findByUserAndValidIsTrue(user)).thenReturn(Optional.of(securityCode));

        // when then
        assertDoesNotThrow(() -> service.validateSecurityCode("test@mail.com", user, "123456"));
    }

    @Test
    void validateSecurityCode_InvalidEmail() {
        // given
        User user = new User();
        SecurityCode securityCode = new SecurityCode();
        securityCode.setCode("123456");
        securityCode.setEmail("test@mail.com");
        securityCode.setExpiredAt(Date.from(Instant.now().plusSeconds(100L)));

        when(repository.findByUserAndValidIsTrue(user)).thenReturn(Optional.of(securityCode));

        // when then
        assertThrows(IllegalArgumentException.class,
            () -> service.validateSecurityCode("other@mail.com", user, "123456"));
    }

    @Test
    void validateSecurityCode_PhoneNumber() {
        // given
        User user = new User();
        SecurityCode securityCode = new SecurityCode();
        securityCode.setCode("123456");
        securityCode.setCountryCode("GR (30)");
        securityCode.setPhoneNumber("6987456321");
        securityCode.setExpiredAt(Date.from(Instant.now().plusSeconds(100L)));

        when(repository.findByUserAndValidIsTrue(user)).thenReturn(Optional.of(securityCode));

        // when then
        assertDoesNotThrow(() -> service.validateSecurityCode("GR (30)", "6987456321", user, "123456"));
    }

    @Test
    void validateSecurityCode_InvalidPhoneNumber() {
        // given
        User user = new User();
        SecurityCode securityCode = new SecurityCode();
        securityCode.setCode("123456");
        securityCode.setCountryCode("GR (30)");
        securityCode.setPhoneNumber("6987456321");
        securityCode.setExpiredAt(Date.from(Instant.now().plusSeconds(100L)));

        when(repository.findByUserAndValidIsTrue(user)).thenReturn(Optional.of(securityCode));

        // when then
        assertThrows(IllegalArgumentException.class,
            () -> service.validateSecurityCode("GR (30)", "1234567890", user, "123456"));
    }

    @Test
    void hasValidSecurityCode_Email() {
        // given
        User user = new User();
        SecurityCode securityCode = new SecurityCode();
        securityCode.setEmail("test@mail.com");

        when(repository.findByUserAndValidIsTrue(user)).thenReturn(Optional.of(securityCode));

        // when then
        boolean result = service.hasValidSecurityCode("test@mail.com", user);

        // then
        assertTrue(result);
    }

    @Test
    void hasValidSecurityCode_InvalidEmail() {
        // given
        User user = new User();
        SecurityCode securityCode = new SecurityCode();
        securityCode.setEmail("test@mail.com");

        when(repository.findByUserAndValidIsTrue(user)).thenReturn(Optional.of(securityCode));

        // when then
        boolean result = service.hasValidSecurityCode("other@mail.com", user);

        // then
        assertFalse(result);
    }

    @Test
    void hasValidSecurityCode_PhoneNumber() {
        // given
        User user = new User();
        SecurityCode securityCode = new SecurityCode();
        securityCode.setCountryCode("GR (30)");
        securityCode.setPhoneNumber("6987456321");

        when(repository.findByUserAndValidIsTrue(user)).thenReturn(Optional.of(securityCode));

        // when then
        boolean result = service.hasValidSecurityCode("GR (30)", "6987456321", user);

        // then
        assertTrue(result);
    }

    @Test
    void hasValidSecurityCode_InvalidPhoneNumber() {
        // given
        User user = new User();
        SecurityCode securityCode = new SecurityCode();
        securityCode.setCountryCode("GR (30)");
        securityCode.setCountryCode("6987456321");

        when(repository.findByUserAndValidIsTrue(user)).thenReturn(Optional.of(securityCode));

        // when then
        boolean result = service.hasValidSecurityCode("GR (30)", "1234567890", user);

        // then
        assertFalse(result);
    }

    @Test
    void hasActiveSecurityCode_Email() {
        // given
        User user = new User();
        SecurityCode securityCode = new SecurityCode();
        securityCode.setEmail("test@mail.com");
        securityCode.setExpiredAt(Date.from(Instant.now().plusSeconds(100)));

        when(repository.findByUserAndValidIsTrue(user)).thenReturn(Optional.of(securityCode));

        // when then
        Optional<SecurityCode> result = service.getActiveSecurityCode("test@mail.com", user);

        // then
        assertEquals(securityCode, result.get());
    }

    @Test
    void hasNoActiveSecurityCode_Email() {
        // given
        User user = new User();
        SecurityCode securityCode = new SecurityCode();
        securityCode.setEmail("test@mail.com");
        securityCode.setExpiredAt(Date.from(Instant.now().minusMillis(100)));

        when(repository.findByUserAndValidIsTrue(user)).thenReturn(Optional.of(securityCode));

        // when then
        Optional<SecurityCode> result = service.getActiveSecurityCode("test@mail.com", user);

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    void hasActiveSecurityCode_PhoneNumber() {
        // given
        User user = new User();
        SecurityCode securityCode = new SecurityCode();
        securityCode.setCountryCode("GR (30)");
        securityCode.setPhoneNumber("6987456321");
        securityCode.setExpiredAt(Date.from(Instant.now().plusSeconds(100)));

        when(repository.findByUserAndValidIsTrue(user)).thenReturn(Optional.of(securityCode));

        // when then
        Optional<SecurityCode> result = service.getActiveSecurityCode("GR (30)", "6987456321", user);

        // then
        assertEquals(securityCode, result.get());
    }

    @Test
    void hasNoActiveSecurityCode_PhoneNumber() {
        // given
        User user = new User();
        SecurityCode securityCode = new SecurityCode();
        securityCode.setCountryCode("GR (30)");
        securityCode.setPhoneNumber("6987456321");
        securityCode.setExpiredAt(Date.from(Instant.now().minusSeconds(100)));

        when(repository.findByUserAndValidIsTrue(user)).thenReturn(Optional.of(securityCode));

        // when then
        Optional<SecurityCode> result = service.getActiveSecurityCode("GR (30)", "6987456321", user);

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    void clearSecurityCodes() {
        // given
        User user = new User();

        // when
        service.clearSecurityCodes(user);

        // then
        verify(repository, times(1)).deleteAllByUser(user);
    }
}
