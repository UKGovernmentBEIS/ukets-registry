package gov.uk.ets.registry.api.notification.userinitiated.web.model;

import java.time.LocalDateTime;
import java.util.Optional;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotificationDTOValidator implements ConstraintValidator<ValidNotification, NotificationDTO> {
    @Override
    public void initialize(ValidNotification constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(NotificationDTO notification, ConstraintValidatorContext context) {
        if (isExpirationBeforeScheduled(notification.getActivationDetails())) {
            // without this we will have the default error message too which we don't need it.
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Expiration date cannot be set earlier than the scheduled date; " +
                        "can only be equal to or greater than the scheduled date.")
                .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean isExpirationBeforeScheduled(ActivationDetails activationDetails) {
        Optional<LocalDateTime> expirationDateTime = activationDetails.getExpirationDateTime();
        return expirationDateTime.isPresent() &&
            expirationDateTime.get().isBefore(activationDetails.getScheduledDateTime());
    }
}
