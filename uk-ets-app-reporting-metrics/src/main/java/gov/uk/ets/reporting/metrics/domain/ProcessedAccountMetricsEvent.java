package gov.uk.ets.reporting.metrics.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.Type;

import gov.uk.ets.reporting.metrics.messaging.events.AbstractReportingMetricsEvent;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "processed_account_metrics_event", schema = "reporting_metrics")
@SequenceGenerator(name = "processed_account_metrics_event_id_generator", schema = "reporting_metrics", sequenceName = "processed_account_metrics_event_seq", allocationSize = 1)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class ProcessedAccountMetricsEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "processed_account_metrics_event_id_generator")
    private Long id;
    
    @Column(columnDefinition = "UUID")
    private UUID eventId;
    
    @Type(JsonBinaryType.class)
    @Column(name = "payload", columnDefinition = "jsonb")
    private AbstractReportingMetricsEvent payload;

    private LocalDateTime processedOn;

}
