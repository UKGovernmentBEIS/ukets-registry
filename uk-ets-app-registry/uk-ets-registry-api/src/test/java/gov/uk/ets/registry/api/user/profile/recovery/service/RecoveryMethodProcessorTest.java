package gov.uk.ets.registry.api.user.profile.recovery.service;

import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.profile.recovery.domain.SecurityCode;
import gov.uk.ets.registry.api.user.profile.recovery.web.RecoveryMethodRemoveRequest;
import gov.uk.ets.registry.api.user.profile.recovery.web.RecoveryMethodUpdateRequest;
import gov.uk.ets.registry.api.user.profile.recovery.web.RecoveryMethodUpdateResponse;
import gov.uk.ets.registry.api.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RecoveryMethodProcessorTest {

    @Mock
    private SecurityCodeService securityCodeService;

    @Mock
    private UserService userService;

    @Mock
    private EventService eventService;

    private RecoveryMethodProcessor processor;

    private static final long TIME_TO_LIVE_MILLIS = 300000;
    public static final long TIMER_BUFFER_MILLIS = 6000; // 2% of 300000

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        processor = new RecoveryMethodProcessor(securityCodeService, userService, eventService);
    }

    @Test
    void generateSecurityCode_Email() {
        // given
        User user = new User();
        Date expiredAt = Date.from(Instant.now().plusMillis((TIME_TO_LIVE_MILLIS + TIMER_BUFFER_MILLIS)));
        RecoveryMethodUpdateRequest request = new RecoveryMethodUpdateRequest();
        request.setEmail("test@mail.com");

        when(userService.getCurrentUser()).thenReturn(user);
        when(securityCodeService.getActiveSecurityCode("test@mail.com", user)).thenReturn(Optional.empty());
        when(securityCodeService.sendSecurityCode("test@mail.com", user)).thenReturn(expiredAt);

        // when
        RecoveryMethodUpdateResponse result = processor.generateSecurityCode(request);

        assertTrue(result.getExpiresInMillis() > 0);
        assertTrue(result.getExpiresInMillis() <= TIME_TO_LIVE_MILLIS);
    }

    @Test
    void generateSecurityCode_EmailSameAsUserEmail() {
        // given
        User user = new User();
        user.setEmail("test@mail.com");
        Date expiredAt = Date.from(Instant.now().plusMillis((TIME_TO_LIVE_MILLIS + TIMER_BUFFER_MILLIS)));
        RecoveryMethodUpdateRequest request = new RecoveryMethodUpdateRequest();
        request.setEmail("test@mail.com");

        when(userService.getCurrentUser()).thenReturn(user);

        // when
        assertThrows(IllegalArgumentException.class, () -> processor.generateSecurityCode(request));
    }

    @Test
    void generateSecurityCode_Email_CodeExists() {
        // given
        User user = new User();
        Date expiredAt = Date.from(Instant.now().plusMillis((TIME_TO_LIVE_MILLIS + TIMER_BUFFER_MILLIS)));
        SecurityCode securityCode = new SecurityCode();
        securityCode.setExpiredAt(expiredAt);
        RecoveryMethodUpdateRequest request = new RecoveryMethodUpdateRequest();
        request.setEmail("test@mail.com");

        when(userService.getCurrentUser()).thenReturn(user);
        when(securityCodeService.getActiveSecurityCode("test@mail.com", user))
            .thenReturn(Optional.of(securityCode));

        // when
        RecoveryMethodUpdateResponse result = processor.generateSecurityCode(request);

        // then
        assertTrue(result.getExpiresInMillis() > 0);
        assertTrue(result.getExpiresInMillis() <= TIME_TO_LIVE_MILLIS);
    }

    @Test
    void generateSecurityCode_PhoneNumber() {
        // given
        User user = new User();
        Date expiredAt = Date.from(Instant.now().plusMillis((TIME_TO_LIVE_MILLIS + TIMER_BUFFER_MILLIS)));
        RecoveryMethodUpdateRequest request = new RecoveryMethodUpdateRequest();
        request.setCountryCode("GR (30)");
        request.setPhoneNumber("6987456321");

        when(userService.getCurrentUser()).thenReturn(user);
        when(securityCodeService.getActiveSecurityCode("GR (30)", "6987456321", user)).thenReturn(Optional.empty());
        when(securityCodeService.sendSecurityCode("GR (30)", "6987456321", user)).thenReturn(expiredAt);

        // when
        RecoveryMethodUpdateResponse result = processor.generateSecurityCode(request);

        // then
        assertTrue(result.getExpiresInMillis() > 0);
        assertTrue(result.getExpiresInMillis() <= TIME_TO_LIVE_MILLIS);
    }

    @Test
    void generateSecurityCode_PhoneNumber_CodeExists() {
        // given
        User user = new User();
        Date expiredAt = Date.from(Instant.now().plusMillis((TIME_TO_LIVE_MILLIS + TIMER_BUFFER_MILLIS)));
        SecurityCode securityCode = new SecurityCode();
        securityCode.setExpiredAt(expiredAt);
        RecoveryMethodUpdateRequest request = new RecoveryMethodUpdateRequest();
        request.setCountryCode("GR (30)");
        request.setPhoneNumber("6987456321");

        when(userService.getCurrentUser()).thenReturn(user);
        when(securityCodeService.getActiveSecurityCode("GR (30)", "6987456321", user))
            .thenReturn(Optional.of(securityCode));

        // when
        RecoveryMethodUpdateResponse result = processor.generateSecurityCode(request);

        // then
        assertTrue(result.getExpiresInMillis() > 0);
        assertTrue(result.getExpiresInMillis() <= TIME_TO_LIVE_MILLIS);
    }

    @Test
    void resendSecurityCode_Email() {
        // given
        User user = new User();
        Date expiredAt = Date.from(Instant.now().plusMillis((TIME_TO_LIVE_MILLIS + TIMER_BUFFER_MILLIS)));
        RecoveryMethodUpdateRequest request = new RecoveryMethodUpdateRequest();
        request.setEmail("test@mail.com");

        when(userService.getCurrentUser()).thenReturn(user);
        when(securityCodeService.hasValidSecurityCode("test@mail.com", user)).thenReturn(true);
        when(securityCodeService.getActiveSecurityCode("test@mail.com", user)).thenReturn(Optional.empty());
        when(securityCodeService.sendSecurityCode("test@mail.com", user)).thenReturn(expiredAt);

        // when
        RecoveryMethodUpdateResponse result = processor.resendSecurityCode(request);

        // then
        assertTrue(result.getExpiresInMillis() > 0);
        assertTrue(result.getExpiresInMillis() <= TIME_TO_LIVE_MILLIS);
    }

    @Test
    void resendSecurityCode_Email_CodeExists() {
        // given
        User user = new User();
        Date expiredAt = Date.from(Instant.now().plusMillis((TIME_TO_LIVE_MILLIS + TIMER_BUFFER_MILLIS)));
        SecurityCode securityCode = new SecurityCode();
        securityCode.setExpiredAt(expiredAt);
        RecoveryMethodUpdateRequest request = new RecoveryMethodUpdateRequest();
        request.setEmail("test@mail.com");

        when(userService.getCurrentUser()).thenReturn(user);
        when(securityCodeService.hasValidSecurityCode("test@mail.com", user)).thenReturn(true);
        when(securityCodeService.getActiveSecurityCode("test@mail.com", user))
            .thenReturn(Optional.of(securityCode));

        // when
        RecoveryMethodUpdateResponse result = processor.resendSecurityCode(request);

        // then
        assertTrue(result.getExpiresInMillis() > 0);
        assertTrue(result.getExpiresInMillis() <= TIME_TO_LIVE_MILLIS);
    }

    @Test
    void resendSecurityCode_PhoneNumber() {
        // given
        User user = new User();
        Date expiredAt = Date.from(Instant.now().plusMillis((TIME_TO_LIVE_MILLIS + TIMER_BUFFER_MILLIS)));
        RecoveryMethodUpdateRequest request = new RecoveryMethodUpdateRequest();
        request.setCountryCode("GR (30)");
        request.setPhoneNumber("6987456321");

        when(userService.getCurrentUser()).thenReturn(user);
        when(securityCodeService.hasValidSecurityCode("GR (30)", "6987456321", user)).thenReturn(true);
        when(securityCodeService.getActiveSecurityCode("GR (30)", "6987456321", user)).thenReturn(Optional.empty());
        when(securityCodeService.sendSecurityCode("GR (30)", "6987456321", user)).thenReturn(expiredAt);

        // when
        RecoveryMethodUpdateResponse result = processor.resendSecurityCode(request);

        // then
        assertTrue(result.getExpiresInMillis() > 0);
        assertTrue(result.getExpiresInMillis() <= TIME_TO_LIVE_MILLIS);
    }

    @Test
    void resendSecurityCode_PhoneNumber_CodeExists() {
        // given
        User user = new User();
        Date expiredAt = Date.from(Instant.now().plusMillis((TIME_TO_LIVE_MILLIS + TIMER_BUFFER_MILLIS)));
        SecurityCode securityCode = new SecurityCode();
        securityCode.setExpiredAt(expiredAt);
        RecoveryMethodUpdateRequest request = new RecoveryMethodUpdateRequest();
        request.setCountryCode("GR (30)");
        request.setPhoneNumber("6987456321");

        when(userService.getCurrentUser()).thenReturn(user);
        when(securityCodeService.hasValidSecurityCode("GR (30)", "6987456321", user)).thenReturn(true);
        when(securityCodeService.getActiveSecurityCode("GR (30)", "6987456321", user))
            .thenReturn(Optional.of(securityCode));

        // when
        RecoveryMethodUpdateResponse result = processor.resendSecurityCode(request);

        // then
        assertTrue(result.getExpiresInMillis() > 0);
        assertTrue(result.getExpiresInMillis() <= TIME_TO_LIVE_MILLIS);
    }

    @Test
    void resendSecurityCode_InvalidPhoneNumber() {
        // given
        User user = new User();
        RecoveryMethodUpdateRequest request = new RecoveryMethodUpdateRequest();
        request.setCountryCode("GR (30)");
        request.setPhoneNumber("6987456321");

        when(userService.getCurrentUser()).thenReturn(user);
        when(securityCodeService.hasValidSecurityCode("GR (30)", "6987456321", user)).thenReturn(false);

        // when then
        assertThrows(IllegalArgumentException.class, () -> processor.resendSecurityCode(request));
    }

    @Test
    void updateRecoveryMethod_Email() {
        // given
        User user = new User();
        user.setUrid("urid");
        RecoveryMethodUpdateRequest request = new RecoveryMethodUpdateRequest();
        request.setEmail("test@mail.com");
        request.setSecurityCode("123456");

        when(userService.getCurrentUser()).thenReturn(user);

        // when
        processor.updateRecoveryMethod(request);

        // then
        verify(securityCodeService, times(1)).validateSecurityCode("test@mail.com", user, "123456");
        verify(userService, times(1)).updateUserRecoveryMethods(request);
        verify(securityCodeService, times(1)).clearSecurityCodes(user);
        verify(eventService, times(1))
            .createAndPublishEvent("urid", "urid", "", EventType.USER_RECOVERY_METHOD_UPDATED, "Update recovery email");
    }

    @Test
    void updateRecoveryMethod_EmailSameAsUserEmail() {
        // given
        User user = new User();
        user.setUrid("urid");
        user.setEmail("test@mail.com");
        RecoveryMethodUpdateRequest request = new RecoveryMethodUpdateRequest();
        request.setEmail("test@mail.com");
        request.setSecurityCode("123456");

        when(userService.getCurrentUser()).thenReturn(user);

        // when
        assertThrows(IllegalArgumentException.class, () -> processor.updateRecoveryMethod(request));
    }


    @Test
    void updateRecoveryMethod_PhoneNumber() {
        // given
        User user = new User();
        user.setUrid("urid");
        RecoveryMethodUpdateRequest request = new RecoveryMethodUpdateRequest();
        request.setCountryCode("GR (30)");
        request.setPhoneNumber("6987456321");
        request.setSecurityCode("123456");

        when(userService.getCurrentUser()).thenReturn(user);

        // when
        processor.updateRecoveryMethod(request);

        // then
        verify(securityCodeService, times(1)).validateSecurityCode("GR (30)", "6987456321", user, "123456");
        verify(userService, times(1)).updateUserRecoveryMethods(request);
        verify(securityCodeService, times(1)).clearSecurityCodes(user);
        verify(eventService, times(1))
            .createAndPublishEvent("urid", "urid", "", EventType.USER_RECOVERY_METHOD_UPDATED, "Update recovery phone");
    }

    @Test
    void removeRecoveryMethod_Email() {
        // given
        RecoveryMethodRemoveRequest request = new RecoveryMethodRemoveRequest();
        request.setRemoveEmail(true);

        User user = new User();
        user.setUrid("urid");
        when(userService.getCurrentUser()).thenReturn(user);

        // when
        processor.removeRecoveryMethod(request);

        // then
        verify(userService, times(1)).updateUserRecoveryMethods(any(RecoveryMethodUpdateRequest.class));
        verify(eventService, times(1))
            .createAndPublishEvent("urid", "urid", "", EventType.USER_RECOVERY_METHOD_DELETED, "Remove recovery email");
    }

    @Test
    void removeRecoveryMethod_PhoneNumber() {
        // given
        RecoveryMethodRemoveRequest request = new RecoveryMethodRemoveRequest();
        request.setRemovePhoneNumber(true);

        User user = new User();
        user.setUrid("urid");
        when(userService.getCurrentUser()).thenReturn(user);

        // when
        processor.removeRecoveryMethod(request);

        // then
        verify(userService, times(1)).updateUserRecoveryMethods(any(RecoveryMethodUpdateRequest.class));
        verify(eventService, times(1))
            .createAndPublishEvent("urid", "urid", "", EventType.USER_RECOVERY_METHOD_DELETED, "Remove recovery phone");
    }
}
