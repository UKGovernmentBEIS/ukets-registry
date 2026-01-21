package gov.uk.ets.registry.api.account.notification;

public class MissingInvitationNotificationDataException extends RuntimeException {

    public MissingInvitationNotificationDataException(String message) {
        super(message);
    }
}
