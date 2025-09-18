package gov.uk.ets.registry.api.document.reminder.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"requestIdentifier"})
@Table(name = "document_request_reminder")
public class DocumentRequestReminder implements Serializable {

    @Serial
    private static final long serialVersionUID = -2413995079591220889L;

    @Id
    @SequenceGenerator(name = "document_request_reminder_id_generator", sequenceName = "document_request_reminder_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "document_request_reminder_id_generator")
    private Long id;

    @Column(name = "request_identifier", unique = true)
    private Long requestIdentifier;

    @Column(name = "claimant_urid")
    private String claimantUrid;

    @Column(name = "reminder_sent_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date reminderSentAt;
}
