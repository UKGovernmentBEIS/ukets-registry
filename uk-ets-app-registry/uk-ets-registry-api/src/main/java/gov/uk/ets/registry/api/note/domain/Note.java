package gov.uk.ets.registry.api.note.domain;

import gov.uk.ets.registry.api.user.domain.User;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
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
