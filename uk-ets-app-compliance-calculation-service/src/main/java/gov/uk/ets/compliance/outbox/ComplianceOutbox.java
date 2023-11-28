package gov.uk.ets.compliance.outbox;


import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

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

    private UUID originatingEventId;

    private Long compliantEntityId;

    @Enumerated(EnumType.STRING)
    private ComplianceOutgoingEventType type;

    @Column(columnDefinition="VARCHAR")
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
