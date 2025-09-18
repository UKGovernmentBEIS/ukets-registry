package gov.uk.ets.registry.api.notification;

import gov.uk.ets.registry.api.notification.integration.AccountOpeningSuccessOutcomeNotificationProperties;
import gov.uk.ets.registry.api.notification.integration.IntegrationErrorOutcomeNotificationProperties;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

/**
 * Groups all notification properties.
 * {@link Validated} will not work with public fields because it proxies the class thus it needs to go through getters!
 * https://github.com/spring-projects/spring-boot/issues/19638
 */
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "mail")
public class NotificationProperties {

    @NestedConfigurationProperty
    @Valid
    private AccountManagementNotificationProperties accountManagement;

    @NestedConfigurationProperty
    @Valid
    private TrustedAccountDescriptionNotificationProperties trustedAccountUpdateDescription;

    @NestedConfigurationProperty
    @Valid
    private TransactionNotificationProperties transaction;

    @NestedConfigurationProperty
    @Valid
    private AccountOpeningNotificationProperties accountOpening;

    @NestedConfigurationProperty
    @Valid
    private DocumentRequestNotificationProperties requestDocument;

    @NestedConfigurationProperty
    @Valid
    private DocumentRequestReminderNotificationProperties documentRequestReminder;

    @NestedConfigurationProperty
    @Valid
    private DocumentSubmitNotificationProperties submitDocument;

    @NestedConfigurationProperty
    @Valid
    private EmailChangeNotificationProperties emailChange;

    @NestedConfigurationProperty
    @Valid
    private RequestResetPasswordNotificationProperties resetPassword;

    @NestedConfigurationProperty
    @Valid
    private TokenChangeNotificationProperties tokenChange;

    @NestedConfigurationProperty
    @Valid
    private EmergencyChangeNotificationProperties emergencyOtpChange;

    @NestedConfigurationProperty
    @Valid
    private ResetPasswordSuccessNotificationProperties resetPasswordSuccess;

    @NestedConfigurationProperty
    @Valid
    private EmergencyChangeNotificationProperties emergencyOtpChangeTaskApproved;

    @NestedConfigurationProperty
    @Valid
    private EmergencyChangeNotificationProperties emergencyPasswordOtpChange;

    @NestedConfigurationProperty
    @Valid
    private PasswordChangeNotificationProperties passwordChange;

    @NestedConfigurationProperty
    @Valid
    private LoginErrorNotificationProperties loginError;

    @NestedConfigurationProperty
    @Valid
    private OtpSetNotificationProperties otpSet;
    
    @NestedConfigurationProperty
    @Valid
    private UserDetailsUpdateRequestNotificationProperties userDetailsUpdate;
    
    @NestedConfigurationProperty
    @Valid
    private UserDetailsUpdateCompletedNotificationProperties userDetailsUpdateCompleted;

    @NestedConfigurationProperty
    @Valid
    private UserDeactivationRequestNotificationProperties userDeactivationRequest;
    
    @NestedConfigurationProperty
    @Valid
    private UserDeactivationCompletedNotificationProperties userDeactivationCompleted;

    @NestedConfigurationProperty
    @Valid
    private AccountClosureNotificationProperties accountClosure;
    
    @NestedConfigurationProperty
    @Valid
    private UploadAllocationTableNotificationsProperties uploadAllocationTable;

    @NestedConfigurationProperty
    @Valid
    private RequestDeadlineNotificationProperties requestDeadline;

    @NestedConfigurationProperty
    @Valid
    private RecoveryEmailChangeNotificationProperties recoveryEmailChange;

    @NestedConfigurationProperty
    @Valid
    private IntegrationErrorOutcomeNotificationProperties integrationErrorOutcome;

    @NestedConfigurationProperty
    @Valid
    private AccountOpeningSuccessOutcomeNotificationProperties integrationAccountOpening;
    
    @NestedConfigurationProperty
    @Valid
    private PaymentRequestGroupNotificationProperties requestPayment;
}
