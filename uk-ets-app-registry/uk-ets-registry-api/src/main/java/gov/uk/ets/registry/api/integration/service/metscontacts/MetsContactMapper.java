package gov.uk.ets.registry.api.integration.service.metscontacts;

import gov.uk.ets.registry.api.account.web.model.accountcontact.MetsAccountContactType;
import gov.uk.ets.registry.api.account.web.model.accountcontact.MetsContactDTO;
import gov.uk.ets.registry.api.account.web.model.accountcontact.OperatorType;
import gov.uk.ets.registry.api.common.view.PhoneNumberDTO;
import org.springframework.stereotype.Component;
import uk.gov.netz.integration.model.metscontacts.MetsContactsEvent;
import uk.gov.netz.integration.model.metscontacts.MetsContactsMessage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
public class MetsContactMapper {

    public List<MetsContactDTO> convert(MetsContactsEvent event) {
        List<MetsContactDTO> contacts = new ArrayList<>();
        event.getDetails().forEach(detail -> {
            contacts.add(convert(detail));
        });
        return contacts;
    }

    private MetsContactDTO convert(MetsContactsMessage message) {
        MetsContactDTO metsContactDTO = new MetsContactDTO();
        metsContactDTO.setEmail(message.getEmail());
        metsContactDTO.setFullName(fullName(message));
        metsContactDTO.setPhoneNumber(phoneNumber(message));
        if(!Objects.isNull(message.getContactTypes())) {
            metsContactDTO.setContactTypes(convert(message.getContactTypes()));
        }
        metsContactDTO.setOperatorType(
                OperatorType.valueOf(message.getUserType()));
        return metsContactDTO;
    }

    private Set<MetsAccountContactType> convert(List<String> contactTypes) {
        Set<MetsAccountContactType> metsAccountContactTypes = new HashSet<>();
        for(String contactType : contactTypes) {
            metsAccountContactTypes.add(MetsAccountContactType.valueOf(contactType));
        }
        return metsAccountContactTypes;
    }

    private String fullName(MetsContactsMessage message) {
        return message.getFirstName() + " " + message.getLastName();
    }

    private PhoneNumberDTO phoneNumber(MetsContactsMessage message) {
        PhoneNumberDTO phoneNumberDTO = new PhoneNumberDTO();
        phoneNumberDTO.setPhoneNumber1(message.getTelephoneNumber());
        phoneNumberDTO.setCountryCode1(message.getTelephoneCountryCode());
        phoneNumberDTO.setPhoneNumber2(message.getMobileNumber());
        phoneNumberDTO.setCountryCode2(message.getMobilePhoneCountryCode());
        return phoneNumberDTO;
    }
}
