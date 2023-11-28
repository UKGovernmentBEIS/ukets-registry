package gov.uk.ets.registry.api.account.domain;

import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.common.model.entities.Contact;
import gov.uk.ets.registry.api.common.model.services.converter.StringTrimConverter;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents an account holder.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "identifier")
@Table(name = "account_holder")
@NamedNativeQuery(name = "AccountHolderIdentifierNextVal", query = "select nextval('account_holder_identifier_seq')")
public class AccountHolder implements Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -2014006228884276083L;

    /**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "account_holder_id_generator", sequenceName = "account_holder_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_holder_id_generator")
    private Long id;

    /**
     * The birth country.
     */
    @Column(name = "birth_country")
    private String birthCountry;

    /**
     * The date of birth.
     */
    @Column(name = "birth_date")
    @Temporal(TemporalType.DATE)
    private Date birthDate;

    /**
     * The identifier.
     */
    private Long identifier;

    /**
     * The name.
     */
    private String name;

    /**
     * The first name in case of individual account holder
     */
    private String firstName;

    /**
     * The last name in case of individual account holder
     */
    private String lastName;

    /**
     * The registration number.
     */
    @Convert(converter = StringTrimConverter.class)
    @Column(name = "registration_number")
    private String registrationNumber;

    /**
     * The noRegistrationNumberjustification.
     */
    @Convert(converter = StringTrimConverter.class)
    @Column(name = "no_reg_justification")
    private String noRegNumjustification;

    /**
     * The type.
     */
    @Enumerated(EnumType.STRING)
    private AccountHolderType type;

    /**
     * The accounts.
     */
    @OneToMany(mappedBy = "accountHolder", fetch = FetchType.LAZY)
    private List<Account> accounts;

    /**
     * The contact.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Contact contact;

    /**
     * The legal representatives.
     */
    @OneToMany(mappedBy = "accountHolder", fetch = FetchType.LAZY)
    private List<AccountHolderRepresentative> accountHolderRepresentatives;

    /**
     * Returns first name with last name concatenated if AH is INDIVIDUAL otherwise returns the name (of the ORG).
     **/
    public String actualName() {
        return this.getType() == AccountHolderType.INDIVIDUAL ? this.getFirstName() + " " + this.getLastName() :
            this.getName();
    }
}
