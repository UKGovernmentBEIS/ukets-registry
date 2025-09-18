package gov.uk.ets.registry.api.notification.userinitiated.domain;

import gov.uk.ets.registry.api.notification.userinitiated.domain.types.ChannelType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class NotificationChannel {

    @Enumerated(EnumType.STRING)
    private ChannelType channelType;
}
