package gov.uk.ets.registry.api.account.web.model;

import java.io.Serializable;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import gov.uk.ets.registry.api.common.view.EmailAddressDTO;
import lombok.Getter;
import lombok.Setter;

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
}
