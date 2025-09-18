package gov.uk.ets.registry.api.account.web.model;

import java.io.Serializable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * The account billing details transfer object.
 */
@Getter
@Setter
public class BillingContactDetailsDTO implements Serializable {

    /**
     * The billing contact name.
     */
    @Size(max = 256, message = "Account name must not exceed 256 characters.")
    private String contactName;

    /**
     * The country code of the phone number.
     */
    @Size(max = 10, message = "Country code must not exceed 10 characters.")
    private String phoneNumberCountryCode;

    /**
     * The billing phone number.
     */
    @Size(max = 256, message = "Phone number must not exceed 256 characters.")
    private String phoneNumber;

    /**
     * The billing email.
     */
    @Email
    @Size(max = 256, message = "Email must not exceed 256 characters.")
    private String email;

    /**
     * The SOP Customer ID.
     */
    @Size(max = 256, message = "SOP Customer Id must not exceed 256 characters.")
    private String sopCustomerId;
}
