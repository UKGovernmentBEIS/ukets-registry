package gov.uk.ets.registry.api.note.domain;

import gov.uk.ets.registry.api.user.domain.User;
import java.util.Date;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Note {

    @Id
    @SequenceGenerator(name = "note_id_generator", sequenceName = "note_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "note_id_generator")
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String description;
    @Enumerated(EnumType.STRING)
    private NoteDomainType domainType;
    private String domainId;
    private Date creationDate;
}
