package gov.uk.ets.registry.api.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserActionError {
    /**
     * Error code for the case that the provided enrolment key is invalid.
     */
    ENROLMENT_KEY_INVALID("", ""),

    /**
     * Error code for the case that the provided enrolment key is expired.
     */
    ENROLMENT_KEY_EXPIRED("", ""),

    /**
     * Error code for the case that the previous user status is empty when UNSUSPENDING.
     */
    PREVIOUS_USER_STATUS_UNDEFINED("", ""),

    /**
     * Error code for the case that the current & new UserState are not compatible.
     */
    INVALID_USER_STATUS_TRANSITION("", ""),

    /**
     * Error code for the case that the provided URID for the user to be registered is invalid.
     */
    INVALID_URID("", ""),

    /**
     * Error code for the case that a user requests a new registration code while there is already a pending task.
     */
    ENROLMENT_KEY_TASK_PENDING("", ""),

    LOST_KEY_TASK_PENDING("There is already an emergency access request pending approval", ""),

    USER_NOT_ACTIVE("Your account is not active. Please contact...", ""),

    INVALID_PASSWORD("Old password is incorrect. Please try again.", ""),
    INVALID_OTP("Invalid OTP code.", "otpCode");

    private final String message;
    private final String componentId;
}
