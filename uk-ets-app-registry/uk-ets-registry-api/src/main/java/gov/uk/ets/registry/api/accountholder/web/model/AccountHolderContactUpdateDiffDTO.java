package gov.uk.ets.registry.api.accountholder.web.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import gov.uk.ets.registry.api.account.web.model.LegalRepresentativeDetailsDTO;
import gov.uk.ets.registry.api.common.view.AddressDTO;
import gov.uk.ets.registry.api.common.view.EmailAddressDTO;
import gov.uk.ets.registry.api.common.view.PhoneNumberDTO;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@EqualsAndHashCode
@Getter
@Setter
public class AccountHolderContactUpdateDiffDTO implements Serializable {

    private static final long serialVersionUID = 8566305492755688070L;

    private String positionInCompany;
    private LegalRepresentativeDetailsDTO legalRepresentativeDetails;
    private LegalRepresentativeDetailsDTO details;
    private AddressDTO address;
    private PhoneNumberDTO phoneNumber;
    private EmailAddressDTO emailAddress;
}
