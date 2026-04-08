package gov.uk.ets.registry.api.common.reporting.metrics.domain;

import gov.uk.ets.registry.api.common.reporting.metrics.messaging.events.AbstractReportingMetricsEvent;
import gov.uk.ets.registry.api.common.reporting.metrics.types.OutboxStatus;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "reporting_metrics_outbox")
@SequenceGenerator(name = "reporting_metrics_outbox_id_generator", sequenceName = "reporting_metrics_outbox_seq", allocationSize = 1)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class ReportingMetricsOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reporting_metrics_outbox_id_generator")
    private Long id;
    
    @Column(columnDefinition = "UUID")
    private UUID eventId;
    
    /**
     * Contains a JSON representation of the payload.
     */
    @Type(JsonBinaryType.class)
    @Column(name = "payload", columnDefinition = "jsonb")
    private AbstractReportingMetricsEvent payload;

    private LocalDateTime generatedOn;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;
}
