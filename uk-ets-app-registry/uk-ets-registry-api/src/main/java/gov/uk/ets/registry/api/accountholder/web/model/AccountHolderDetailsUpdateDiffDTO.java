package gov.uk.ets.registry.api.accountholder.web.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import gov.uk.ets.registry.api.account.web.model.DetailsDTO;
import gov.uk.ets.registry.api.common.view.AddressDTO;
import gov.uk.ets.registry.api.common.view.EmailAddressDTO;
import gov.uk.ets.registry.api.common.view.PhoneNumberDTO;
import java.io.Serializable;
import javax.validation.Valid;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@EqualsAndHashCode
@Getter
@Setter
public class AccountHolderDetailsUpdateDiffDTO implements Serializable {

    private static final long serialVersionUID = 8566306592755688070L;

    @Valid
    private EmailAddressDTO emailAddress;
    @Valid
    private AddressDTO address;
    @Valid
    private PhoneNumberDTO phoneNumber;
    @Valid
    private DetailsDTO details;

}
