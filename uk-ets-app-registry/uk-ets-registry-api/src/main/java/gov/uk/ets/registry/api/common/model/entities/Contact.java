package gov.uk.ets.registry.api.common.model.entities;

import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.AccountHolderRepresentative;
import gov.uk.ets.registry.api.common.model.services.converter.StringTrimConverter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;


/**
 * Represents a contact (physical address, email address, telephone).
 *
 */
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
public class Contact implements Serializable {

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 5702408145200119625L;

	/**
	 * The id.
	 */
	@Id
	@SequenceGenerator(name = "contact_id_generator", sequenceName = "contact_seq", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "contact_id_generator")
	private Long id;

	/**
	 * The city.
	 */
	private String city;

	/**
	 * The country.
	 */
	private String country;

	/**
	 * The email address.
	 */
	@Convert(converter = StringTrimConverter.class)
	@Column(name = "email_address")
	private String emailAddress;

	/**
	 * The line 1.
	 */
	@Convert(converter = StringTrimConverter.class)
	@Column(name = "line_1")
	private String line1;

	/**
	 * The line 2.
	 */
	@Convert(converter = StringTrimConverter.class)
	@Column(name = "line_2")
	private String line2;

	/**
	 * The line 3.
	 */
	@Convert(converter = StringTrimConverter.class)
	@Column(name = "line_3")
	private String line3;

	/**
	 * The phone number 1.
	 */
	@Convert(converter = StringTrimConverter.class)
	@Column(name = "phone_number_1")
	private String phoneNumber1;

	/**
	 * The country code of phone number 1.
	 */
	@Column(name = "phone_number_1_country")
	private String countryCode1;

	/**
	 * The phone number 2.
	 */
	@Convert(converter = StringTrimConverter.class)
	@Column(name = "phone_number_2")
	private String phoneNumber2;

	/**
	 * The country code of phone number 2.
	 */
	@Column(name = "phone_number_2_country")
	private String countryCode2;

	/**
	 * The Postal Code or ZIP.
	 */
	@Convert(converter = StringTrimConverter.class)
	@Column(name = "post_code")
	private String postCode;

	/**
	 * The position in company.
	 */
	@Convert(converter = StringTrimConverter.class)
	@Column(name = "position_in_company")
	private String positionInCompany;

	/**
	 * The state or province.
	 */
	@Convert(converter = StringTrimConverter.class)
	@Column(name = "state_or_province")
	private String stateOrProvince;

	/**
	 * The account holders.
	 */
	@OneToMany(mappedBy = "contact", fetch = FetchType.LAZY)
	private List<AccountHolder> accountHolders;

	/**
	 * The legal representatives.
	 */
	@OneToMany(mappedBy = "contact", fetch = FetchType.LAZY)
	private List<AccountHolderRepresentative> legalRepresentatives;


    /**
     * A billing email.
     */
    @Column(name = "billing_email_1")
    private String billingEmail1;

    /**
     * A second billing email.
     */
    @Column(name = "billing_email_2")
    private String billingEmail2;

	/**
	 * The contact name.
	 */
	@Column(name = "contact_name")
	private String contactName;

	/**
	 * The SOP Customer ID.
	 */
	@Column(name = "sop_customer_id")
	private String sopCustomerId;


}