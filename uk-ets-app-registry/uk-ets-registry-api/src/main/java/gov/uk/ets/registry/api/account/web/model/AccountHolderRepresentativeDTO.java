package gov.uk.ets.registry.api.account.web.model;

import gov.uk.ets.registry.api.common.view.AddressDTO;
import gov.uk.ets.registry.api.common.view.EmailAddressDTO;
import gov.uk.ets.registry.api.common.view.PhoneNumberDTO;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * The legal representative transfer object.
 */
@Getter
@Setter
public class AccountHolderRepresentativeDTO implements Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 6254041228244195602L;

    /**
     * The id.
     */
    private Long id;

    /**
     * The details.
     */
    private LegalRepresentativeDetailsDTO details;

    /**
     * Position in the company
     */
    @Size(max = 256, message = "Position must not exceed 256 characters.")
    private String positionInCompany;

    /**
     * The address.
     */
    @Valid
    private AddressDTO address;

    /**
     * The phone number.
     */
    @Valid
    private PhoneNumberDTO phoneNumber;

    /**
     * The email address.
     */
    @Valid
    private EmailAddressDTO emailAddress;

}
