package gov.uk.ets.registry.api.notification.userinitiated.web.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class AdHocActivationDetails extends ActivationDetails {

    private boolean hasExpirationDateSection;
    private LocalTime expirationTime;

    @Override
    public Optional<LocalDateTime> getExpirationDateTime() {
        if (hasExpirationDateSection) {
            return Optional.of(LocalDateTime.of(getExpirationDate(), expirationTime));
        } else {
            return Optional.empty();
        }
    }
}
