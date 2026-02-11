package gov.uk.ets.registry.api.account.web.model;

import gov.uk.ets.registry.api.common.view.EmailAddressDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * The account sales details transfer object.
 */
@Getter
@Setter
public class SalesContactDetailsDTO implements Serializable {
    
    /**
     * The email address.
     */
    @Valid
    private EmailAddressDTO emailAddress;

	/**
     * The country code of the phone number.
     */
    @Size(max = 10, message = "Country code must not exceed 10 characters.")
    private String phoneNumberCountryCode;

    /**
     * The phone number.
     */
    @Size(max = 256, message = "Phone number must not exceed 256 characters.")
    private String phoneNumber;

    /**
     * UK Allowances sales volumes 1 to 99
     */
    private boolean uka1To99;

    /**
     * UK Allowances sales volumes 100 to 999
     */
    private boolean uka100To999;

    /**
     * UK Allowances sales volumes 1000 plus
     */
    private boolean uka1000Plus;
}
