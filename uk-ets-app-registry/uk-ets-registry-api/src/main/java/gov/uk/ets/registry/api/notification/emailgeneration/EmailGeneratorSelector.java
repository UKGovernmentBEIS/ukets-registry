package gov.uk.ets.registry.api.notification.emailgeneration;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.file.upload.allocationtable.notification.UploadAllocationTableEmailNotification;
import gov.uk.ets.registry.api.notification.*;
import gov.uk.ets.registry.api.notification.integration.AccountOpeningSuccessOutcomeNotification;
import gov.uk.ets.registry.api.notification.integration.IntegrationDisabledNotification;
import gov.uk.ets.registry.api.notification.integration.IntegrationErrorOutcomeNotification;
import gov.uk.ets.registry.api.user.profile.domain.EmergencyOtpChangeRequestedNotification;
import gov.uk.ets.registry.api.user.profile.domain.EmergencyOtpChangeTaskApprovedNotification;
import gov.uk.ets.registry.api.user.profile.domain.EmergencyPasswordOtpChangeRequestedNotification;
import gov.uk.ets.registry.api.user.profile.domain.PasswordChangeSuccessNotification;
import gov.uk.ets.registry.api.user.profile.notification.EmailChangeApprovedNotification;
import gov.uk.ets.registry.api.user.profile.notification.EmailChangeConfirmationNotification;
import gov.uk.ets.registry.api.user.profile.notification.EmailChangeRejectedNotification;
import gov.uk.ets.registry.api.user.profile.notification.EmailChangeRequestedNotification;
import gov.uk.ets.registry.usernotifications.GroupNotification;

/**
 * Selects the correct email generator.
 */
public class EmailGeneratorSelector {

    /**
     * Private constructor.
     */
    private EmailGeneratorSelector() {
        // constructor is hidden
    }

    /**
     * Select the correct generator.
     *
     * @param groupNotification      The notification data that will be sent
     * @param notificationProperties actual message templates for the transaction notification
     * @return the email generator
     */
    public static EmailGenerator select(GroupNotification groupNotification,
                                                   NotificationProperties notificationProperties,
                                                   Configuration freemarkerConfiguration,
                                                   MailConfiguration mailConfiguration) {
        switch (groupNotification.type()) {
            case TRANSACTION_FINALISATION:
                return new TransactionFinalisationEmailGenerator(notificationProperties,
                    (TransactionRelatedGroupNotification) groupNotification, freemarkerConfiguration,
                    mailConfiguration);
            case TRANSACTION_COMPLETION:
            case TRANSACTION_INBOUND_COMPLETION:
                return new TransactionCompletionEmailGenerator(notificationProperties,
                    (TransactionRelatedGroupNotification) groupNotification, freemarkerConfiguration,
                    mailConfiguration);
            case TRANSACTION_RECEIVED:
            case TRANSACTION_INBOUND:
                return new InboundTransactionEmailGenerator(notificationProperties,
                    (TransactionRelatedGroupNotification) groupNotification, freemarkerConfiguration,
                    mailConfiguration);
            case TRANSACTION_PROPOSAL:
                return new TransactionProposalEmailGenerator(notificationProperties,
                    (TransactionRelatedGroupNotification) groupNotification, freemarkerConfiguration,
                    mailConfiguration);
            case TRANSACTION_MANUALLY_CANCELLED:
                return new TransactionManuallyCancelledEmailGenerator(notificationProperties,
                    (TransactionRelatedGroupNotification) groupNotification, freemarkerConfiguration,
                    mailConfiguration);
            case ACCOUNT_PROPOSAL:
                return new AccountProposalEmailGenerator(notificationProperties,
                    (AccountProposalGroupNotification) groupNotification, freemarkerConfiguration, mailConfiguration);
            case ACCOUNT_CLOSURE_COMPLETED:
                return new AccountClosureEmailGenerator(notificationProperties,
                    (AccountClosureGroupNotification) groupNotification, freemarkerConfiguration, mailConfiguration);
            case ACCOUNT_UPDATE_PROPOSAL:
            case TASK_COMPLETE_OUTCOME:
                return new AccountUpdateEmailGenerator(notificationProperties,
                    (AccountUpdateGroupNotification) groupNotification, freemarkerConfiguration, mailConfiguration);
            case TRUSTED_ACCOUNT_UPDATE_DESCRIPTION:
                return new TrustedAccountUpdateDescriptionEmailGenerator(notificationProperties,
                    (TrustedAccountUpdateDescriptionGroupNotification) groupNotification, freemarkerConfiguration,
                    mailConfiguration);
            case ACCOUNT_OPENING_FINALISATION:
                return new AccountOpeningEmailGenerator(notificationProperties,
                    (AccountOpeningGroupNotification) groupNotification, freemarkerConfiguration, mailConfiguration);
            // TODO: could be renamed to USER_STATUS_CHANGE
            case EMAIL_CHANGE_STATUS:
                // Email for account opening approval with ARs status change from REGISTERED to VALIDATED
                return new ARAccountOpeningEmailGenerator(notificationProperties,
                    (EmailChangeUserStatusNotification) groupNotification, freemarkerConfiguration, mailConfiguration);
            case DEADLINE_UPDATE:
            case DEADLINE_REMINDER:
                return new DeadlineEmailGenerator(notificationProperties.getRequestDeadline(),
                    (RequestDeadlineNotification) groupNotification, freemarkerConfiguration, mailConfiguration);
            case DOCUMENT_REQUEST:
            case DOCUMENT_REQUEST_FINALISATION:
                return new DocumentRequestEmailGenerator(notificationProperties,
                    (DocumentRequestGroupNotification) groupNotification, freemarkerConfiguration, mailConfiguration);
            case DOCUMENT_REQUEST_REMINDER:
                return new DocumentRequestReminderEmailGenerator(notificationProperties.getDocumentRequestReminder(),
                        (DocumentRequestReminderGroupNotification) groupNotification, freemarkerConfiguration, mailConfiguration);
            case EMAIL_CHANGE_REQUESTED:
                return new EmailChangeRequestedEmailGenerator(notificationProperties.getEmailChange(),
                        (EmailChangeRequestedNotification) groupNotification, freemarkerConfiguration, mailConfiguration);
            case EMAIL_CHANGE_CONFIRMATION:
                return new EmailChangeConfirmationEmailGenerator(
                    (EmailChangeConfirmationNotification) groupNotification, notificationProperties.getEmailChange(),
                    freemarkerConfiguration, mailConfiguration);
            case EMAIL_CHANGE_APPROVED:
                return new EmailChangeApprovedEmailGenerator(notificationProperties.getEmailChange(),
                        (EmailChangeApprovedNotification) groupNotification, freemarkerConfiguration, mailConfiguration);
            case EMAIL_CHANGE_REJECTED:
                return new EmailChangeRejectedEmailGenerator((EmailChangeRejectedNotification) groupNotification,
                    notificationProperties.getEmailChange(), freemarkerConfiguration, mailConfiguration);
            case REQUEST_RESET_PASSWORD_LINK:
                return new RequestResetPasswordEmailGenerator((RequestResetPasswordLinkNotification) groupNotification,
                    notificationProperties.getResetPassword(), freemarkerConfiguration, mailConfiguration);
            case TOKEN_CHANGE_REQUEST:
                return new TokenChangeEmailGenerator((TokenChangeNotification) groupNotification,
                    notificationProperties.getTokenChange(), freemarkerConfiguration, mailConfiguration);
            case LOGIN_ERROR:
                return new LoginErrorEmailGenerator((LoginErrorNotification) groupNotification,
                    notificationProperties.getLoginError(), freemarkerConfiguration, mailConfiguration);
            case OTP_SET:
                return new OtpSetEmailGenerator(notificationProperties.getOtpSet(),(OtpSetNotification) groupNotification,
                        freemarkerConfiguration, mailConfiguration);
            case EMERGENCY_OTP_CHANGE_REQUESTED:
                return new EmergencyOtpChangeRequestEmailGenerator(
                    (EmergencyOtpChangeRequestedNotification) groupNotification, notificationProperties,
                    freemarkerConfiguration, mailConfiguration);
            case EMERGENCY_OTP_CHANGE_COMPLETE:
                return new EmergencyOtpChangeTaskCompletedEmailGenerator(
                    (EmergencyOtpChangeTaskApprovedNotification) groupNotification, notificationProperties,
                    freemarkerConfiguration, mailConfiguration);
            case RESET_PASSWORD_SUCCESS:
                return new ResetPasswordSuccessEmailGenerator(
                    notificationProperties.getResetPasswordSuccess(), (ResetPasswordSuccessNotification) groupNotification,
                    freemarkerConfiguration, mailConfiguration);
            case EMERGENCY_PASSWORD_OTP_CHANGE_REQUESTED:
                return new EmergencyPasswordOtpChangeRequestEmailGenerator(
                    (EmergencyPasswordOtpChangeRequestedNotification) groupNotification, notificationProperties,
                    freemarkerConfiguration, mailConfiguration);
            case PASSWORD_CHANGE_SUCCESS:
                return new PasswordChangeSuccessEmailGenerator(notificationProperties,
                        (PasswordChangeSuccessNotification) groupNotification, freemarkerConfiguration, mailConfiguration);
            case RECOVERY_EMAIL_CHANGE_REQUEST:
                return new RecoveryEmailChangeEmailGenerator(notificationProperties,
                    (RecoveryEmailChangeNotification) groupNotification, freemarkerConfiguration, mailConfiguration);
            case USER_DETAILS_UPDATE_REQUEST:
                return new UserDetailsUpdateRequestEmailGenerator((UserDetailsUpdateNotification) groupNotification,
                    notificationProperties, freemarkerConfiguration, mailConfiguration);
            case USER_DETAILS_UPDATE_COMPLETED:
                return new UserDetailsUpdateCompletedEmailGenerator((UserDetailsUpdateNotification) groupNotification,
                    notificationProperties, freemarkerConfiguration, mailConfiguration);
            case USER_DEACTIVATION_REQUEST:
                return new UserDeactivationRequestEmailGenerator((UserDeactivationNotification) groupNotification,
                notificationProperties, freemarkerConfiguration, mailConfiguration);
            case USER_DEACTIVATION_COMPLETED:
                return new UserDeactivationCompletedEmailGenerator((UserDeactivationNotification) groupNotification,
                    notificationProperties, freemarkerConfiguration, mailConfiguration);               
            case UPLOAD_ALLOCATION_TABLE_REQUESTED:
                return new UploadAllocationTableRequestEmailGenerator((UploadAllocationTableEmailNotification) 
                    groupNotification, notificationProperties.getUploadAllocationTable(), 
                    freemarkerConfiguration, mailConfiguration);
            case UPLOAD_ALLOCATION_TABLE_APPROVED:
                return new UploadAllocationTableApprovedEmailGenerator((UploadAllocationTableEmailNotification) 
                    groupNotification, notificationProperties.getUploadAllocationTable(), 
                    freemarkerConfiguration, mailConfiguration);
            case UPLOAD_ALLOCATION_TABLE_REJECTED:
                return new UploadAllocationTableRejectedEmailGenerator((UploadAllocationTableEmailNotification) 
                    groupNotification, notificationProperties.getUploadAllocationTable(), 
                    freemarkerConfiguration, mailConfiguration);
            case INTEGRATION_ERROR_OUTCOME:
                return new IntegrationErrorOutcomeEmailGenerator(notificationProperties.getIntegrationErrorOutcome(),
                    (IntegrationErrorOutcomeNotification) groupNotification, freemarkerConfiguration, mailConfiguration);
            case INTEGRATION_ACCOUNT_OPENING_SUCCESS_OUTCOME:
                return new IntegrationAccountOpeningSuccessOutcomeEmailGenerator(notificationProperties.getIntegrationAccountOpening(),
                    (AccountOpeningSuccessOutcomeNotification) groupNotification, freemarkerConfiguration, mailConfiguration);
            case INTEGRATION_POINT_DISABLED:
                return new IntegrationDisabledEmailGenerator(
                    (IntegrationDisabledNotification) groupNotification, freemarkerConfiguration, mailConfiguration);
            case PAYMENT_REQUEST:
                return new PaymentRequestEmailGenerator(notificationProperties.getRequestPayment(),
                    (PaymentRequestGroupNotification) groupNotification, freemarkerConfiguration, mailConfiguration); 
            case PAYMENT_REMINDER:
                return new PaymentReminderEmailGenerator(
                    (PaymentReminderNotification) groupNotification, freemarkerConfiguration, mailConfiguration);                 
            default:
                return new DoNothingEmailGenerator();
        }
    }
}
