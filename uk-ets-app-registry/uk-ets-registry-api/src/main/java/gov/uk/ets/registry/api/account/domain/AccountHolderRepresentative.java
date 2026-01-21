package gov.uk.ets.registry.api.account.domain;

import gov.uk.ets.registry.api.account.domain.types.AccountContactType;
import gov.uk.ets.registry.api.common.model.entities.Contact;
import gov.uk.ets.registry.api.common.model.services.converter.StringTrimConverter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/** Represents a legal representative. */
@Getter
@Setter
@EqualsAndHashCode(of = { "firstName", "lastName", "accountHolder" })
@Entity
@Table(name = "account_holder_representative")
public class AccountHolderRepresentative implements Serializable {

    /** Serialization version. */
    private static final long serialVersionUID = 1634205081077080253L;

    /** The id. */
    @Id
    @SequenceGenerator(name = "legal_representative_id_generator", 
        sequenceName = "legal_representative_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "legal_representative_id_generator")
    private Long id;

    /** The also known as. */
    @Convert(converter = StringTrimConverter.class)
    @Column(name = "also_known_as")
    private String alsoKnownAs;

    /** The date of birth of LR. */
    @Column(name = "birth_date")
    @Temporal(TemporalType.DATE)
    private Date birthDate;

    /** The first name. */
    @Convert(converter = StringTrimConverter.class)
    @Column(name = "first_name")
    private String firstName;

    /** The last name. */
    @Convert(converter = StringTrimConverter.class)
    @Column(name = "last_name")
    private String lastName;

    /** The account holder. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_holder_id")
    private AccountHolder accountHolder;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_contact_type")
    private AccountContactType accountContactType;

    /** The contact. */
    @ManyToOne(fetch = FetchType.LAZY)
    private Contact contact;

    @Column(name = "invited_date")
    private LocalDateTime invitedOn;
}
