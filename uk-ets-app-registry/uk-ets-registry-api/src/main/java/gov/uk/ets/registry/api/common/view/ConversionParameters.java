package gov.uk.ets.registry.api.common.view;

import gov.uk.ets.registry.api.account.web.model.BillingContactDetailsDTO;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConversionParameters {

    private EmailAddressDTO email;
    private AddressDTO address;
    private PhoneNumberDTO phone;
    private String positionInCompany;
    private BillingContactDetailsDTO billingContactDetails;
    // todo: these values are deprecated and should be removed.
    private String billingEmail1;
    private String billingEmail2;
}
