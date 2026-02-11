package gov.uk.ets.registry.api.account.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;

import gov.uk.ets.registry.api.common.model.services.converter.StringTrimConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalesContact implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1165024539485196518L;

	/**
     * The sales contact email
     */
	@Convert(converter = StringTrimConverter.class)
    @Column(name = "sales_contact_email")
    private String emailAddress;

    /**
     * The sales contact email phone number
     */
	@Convert(converter = StringTrimConverter.class)
    @Column(name = "sales_contact_phone_number_country")
    private String phoneNumberCountry;

    /**
     * The sales contact email phone number country
     */
	@Convert(converter = StringTrimConverter.class)
    @Column(name = "sales_contact_phone_number")
    private String phoneNumber;

    /**
     * UK Allowances sales volumes 1 to 99
     */
    @Column(name = "sales_contact_uka_1_99")
    private boolean uka1To99;

    /**
     * UK Allowances sales volumes 100 to 999
     */
    @Column(name = "sales_contact_uka_100_999")
    private boolean uka100To999;

    /**
     * UK Allowances sales volumes 1000 plus
     */
    @Column(name = "sales_contact_uka_1000_plus")
    private boolean uka1000Plus;

}
