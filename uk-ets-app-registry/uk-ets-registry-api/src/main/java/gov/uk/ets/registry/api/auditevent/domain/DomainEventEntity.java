package gov.uk.ets.registry.api.auditevent.domain;

import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "domain_event")
public class DomainEventEntity implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = -503333143003655395L;

    @Id
    @SequenceGenerator(name = "domain_event_id_generator", sequenceName = "domain_event_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "domain_event_id_generator")
    private Long id;
    private String domainType; // what
    private String domainId; // request id
    private String domainAction;
    private String description; // comment in the user stories
    private String creator; // who
    private String creatorType; // 'user' or 'system'

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date creationDate; // when


}
