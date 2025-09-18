package gov.uk.ets.registry.usernotifications;

/**
 * What kind of {@link GroupNotification} are available.
 */
public enum GroupNotificationType {
    /**
     * This notification type if invoked when a transaction is finalised, which means that it was approved.
     */
    TRANSACTION_FINALISATION,
    TRANSACTION_INBOUND,
    TRANSACTION_INBOUND_COMPLETION,
    TRANSACTION_PROPOSAL,
    TRANSACTION_MANUALLY_CANCELLED,
    TRANSACTION_COMPLETION,
    TRANSACTION_RECEIVED,

    ACCOUNT_PROPOSAL,
    ACCOUNT_UPDATE_PROPOSAL,

    TRUSTED_ACCOUNT_UPDATE_DESCRIPTION,
    /**
     * All tasks are completed from TaskService's complete method so we need a generic Notification Type here
     * (e.g. tasks about {@link GroupNotificationType#ACCOUNT_UPDATE_PROPOSAL})
     */
    TASK_COMPLETE_OUTCOME,

    ACCOUNT_OPENING_FINALISATION,
    DEADLINE_REMINDER,
    DEADLINE_UPDATE,
    DOCUMENT_REQUEST,
    DOCUMENT_REQUEST_FINALISATION,
    DOCUMENT_REQUEST_REMINDER,
    /**
     * Notification type that is invoked when user requests an email request.
     */
    EMAIL_CHANGE_REQUESTED,
    /**
     * Notification type that is invoked when user requests an email request.
     */
    EMAIL_CHANGE_CONFIRMATION,
    /**
     * Notification type that is mapped to EMAIL_CHANGE_APPROVED or EMAIL_CHANGE_REJECTED depending on the task
     * outcome.
     */
    EMAIL_CHANGE_TASK_COMPLETED,
    /**
     * Notification type that is invoked when the change email request task is approved.
     */
    EMAIL_CHANGE_APPROVED,
    /**
     * Notification type that is invoked when the change email request task is rejected.
     */
    EMAIL_CHANGE_REJECTED,

    /**
     * Notification type that is invoked when the user requests an email with reset passwd link.
     */
    REQUEST_RESET_PASSWORD_LINK,

    /**
     * Notification when a user requests to change his 2FA token.
     */
    TOKEN_CHANGE_REQUEST,

    /**
     * Notification when a user sets the OTP
     */
    OTP_SET,
    /**
     * Notification type that is invoked when user requests an emergency OTP change request.
     */
    EMERGENCY_OTP_CHANGE_REQUESTED,
    /**
     * Notification type that is invoked when the user resets the passwd.
     */
    RESET_PASSWORD_SUCCESS,
    /**
     * Notification type that is invoked when emergency OTP change task is completed.
     */
    EMERGENCY_OTP_CHANGE_COMPLETE,
    /**
     * Notification type that is invoked when user requests an emergency Password and OTP change request.
     */
    EMERGENCY_PASSWORD_OTP_CHANGE_REQUESTED,
    /**
     * Notification type that is invoked when emergency Password and OTP change task is completed.
     */
    EMERGENCY_PASSWORD_OTP_CHANGE_COMPLETE,
    /**
     * Notification type that is invoked when the user changes his email through My Profile page.
     */
    PASSWORD_CHANGE_SUCCESS,
    /**
     * Notification type that is invoked when an invalid login attempt was performed.
     */
    LOGIN_ERROR,
    /**
     * Notification type that is invoked when a user status change is performed
     */
    EMAIL_CHANGE_STATUS,
    /**
     * Notification type that is invoked when a user details update is requested
     */
    USER_DETAILS_UPDATE_REQUEST,
    /**
     * Notification type that is invoked when a user details update task is completed
     */
    USER_DETAILS_UPDATE_COMPLETED,
    /**
     * Notification type that is invoked when a user deactivation is requested
     * @deprecated remove when sure that it is not needed
     */
    @Deprecated
    USER_DEACTIVATION_REQUEST,
    /**
     * Notification type that is invoked when a user deactivation task is completed
     *  @deprecated remove when sure that it is not needed
     */
    @Deprecated
    USER_DEACTIVATION_COMPLETED,
    /**
     * Notification type that is invoked when an account closure request is completed
     */
    ACCOUNT_CLOSURE_COMPLETED,
    
    /**
     * Notification type that is invoked when user uploads an allocation table.
     */
    UPLOAD_ALLOCATION_TABLE_REQUESTED,
    /**
     * Notification type that is invoked when the upload allocation table request task is completed.
     */
    UPLOAD_ALLOCATION_TABLE_COMPLETED,
    /**
     * Notification type that is invoked when the upload allocation table request task is approved.
     */
    UPLOAD_ALLOCATION_TABLE_APPROVED,
    /**
     * Notification type that is invoked when the upload allocation table request request task is rejected.
     */
    UPLOAD_ALLOCATION_TABLE_REJECTED,
    /**
     * Notification type that is invoked when user request to update recovery email.
     */
    RECOVERY_EMAIL_CHANGE_REQUEST,
    /**
     * Notification type that is invoked when a request via integration points was not processed correctly.
     */
    INTEGRATION_ERROR_OUTCOME,
    /**
     * Notification type that is invoked when a request via account opening integration point was processed correctly.
     */
    INTEGRATION_ACCOUNT_OPENING_SUCCESS_OUTCOME,
    
    //Payments Related
    PAYMENT_REQUEST,
    
}
