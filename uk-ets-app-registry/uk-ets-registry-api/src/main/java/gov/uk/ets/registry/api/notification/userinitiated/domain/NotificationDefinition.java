package gov.uk.ets.registry.api.notification.userinitiated.domain;

import gov.uk.ets.registry.api.common.model.services.converter.SelectionCriteriaToJsonStringConverter;
import gov.uk.ets.registry.api.common.model.services.converter.SupportedParametersToJsonStringConverter;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationType;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "notification_definition")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDefinition {

    @Id
    @SequenceGenerator(name = "notification_definition_id_generator", sequenceName = "notification_definition_seq",
        allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_definition_id_generator")
    private Long id;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String shortText;

    private String longText;

    @Embedded
    private NotificationChannel channel;

    @Convert(converter = SelectionCriteriaToJsonStringConverter.class)
    private SelectionCriteria selectionCriteria;

    @Convert(converter = SupportedParametersToJsonStringConverter.class)
    private SupportedParameters supportedParameters;

    private Integer typeId;
}
