package gov.uk.ets.registry.api.compliance.messaging.outbox;

import gov.uk.ets.registry.api.compliance.messaging.events.ComplianceEventType;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import jakarta.persistence.Column;
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
import org.hibernate.Hibernate;

@Entity
@Table(name = "compliance_outbox")
@SequenceGenerator(name = "compliance_outbox_id_generator", sequenceName = "compliance_outbox_seq", allocationSize = 1)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class ComplianceOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "compliance_outbox_id_generator")
    private Long id;

    @Column(columnDefinition = "UUID")
    private UUID eventId;

    private Long compliantEntityId;

    @Enumerated(EnumType.STRING)
    private ComplianceEventType type;

    private String payload;

    private LocalDateTime generatedOn;

    @Enumerated(EnumType.STRING)
    private ComplianceOutboxStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        ComplianceOutbox that = (ComplianceOutbox) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return 1168882206;
    }
}
