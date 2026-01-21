package gov.uk.ets.registry.api.helper.integration;

import cucumber.api.java.sr_latn.I;
import gov.uk.ets.registry.api.account.web.model.accountcontact.MetsAccountContactType;
import gov.uk.ets.registry.api.account.web.model.accountcontact.OperatorType;
import org.springframework.stereotype.Component;
import uk.gov.netz.integration.model.metscontacts.MetsContactsEvent;
import uk.gov.netz.integration.model.metscontacts.MetsContactsMessage;

import java.util.List;

@Component
public class MetsContactsTestHelper {

    public MetsContactsEvent aircraftMetsContactsEvent(String fullIdentifier) {
        MetsContactsEvent event = new MetsContactsEvent();
        event.setOperatorId(fullIdentifier);
        event.setDetails(List.of(
                secondary(),
                primary()
        ));
        return event;
    }

    public MetsContactsEvent aircraftMetsContactsEvent_invalid(String fullIdentifier) {
        MetsContactsEvent event = new MetsContactsEvent();
        event.setOperatorId(fullIdentifier);
        event.setDetails(List.of(
                primary_invalid()
        ));
        return event;
    }

    public MetsContactsEvent installationMetsContactsEvent(String fullIdentifier) {
        MetsContactsEvent event = new MetsContactsEvent();
        event.setOperatorId(fullIdentifier);
        event.setDetails(List.of(
                secondary(),
                primary()
        ));
        return event;
    }

    public MetsContactsEvent installationMetsContactsEvent_invalid(String fullIdentifier) {
        MetsContactsEvent event = new MetsContactsEvent();
        event.setOperatorId(fullIdentifier);
        event.setDetails(List.of(
               primary_invalid()
        ));
        return event;
    }

    public MetsContactsEvent maritimeMetsContactsEvent(String fullIdentifier) {
        MetsContactsEvent event = new MetsContactsEvent();
        event.setOperatorId(fullIdentifier);
        event.setDetails(List.of(
                secondary(),
                primary()
        ));
        return event;
    }

    public MetsContactsEvent maritimeMetsContactsEvent_invalid(String fullIdentifier) {
        MetsContactsEvent event = new MetsContactsEvent();
        event.setOperatorId(fullIdentifier);
        event.setDetails(List.of(
                primary_invalid()
        ));
        return event;
    }

    private MetsContactsMessage primary() {
        MetsContactsMessage message = metsContactsMessage(1);
        message.setContactTypes(List.of(MetsAccountContactType.PRIMARY.name()));
        message.setUserType(OperatorType.OPERATOR_ADMIN.name());
        return message;
    }

    private MetsContactsMessage primary_invalid() {
        MetsContactsMessage message = metsContactsMessageInvalid(4);
        message.setContactTypes(List.of("Invalid_type"));
        message.setUserType("Invalid_user_type");
        return message;
    }

    private MetsContactsMessage secondary() {
        MetsContactsMessage message = metsContactsMessage(2);
        message.setContactTypes(List.of(MetsAccountContactType.SECONDARY.name()));
        message.setUserType(OperatorType.OPERATOR.name());
        return message;
    }

    MetsContactsMessage metsContactsMessage(int i) {
        return MetsContactsMessage.builder()
                .firstName("firstName"+i)
                .lastName("lastName"+i)
                .email("email"+i+"@mail.com")
                .mobilePhoneCountryCode("UK")
                .mobileNumber("697777897"+i)
                .telephoneCountryCode("UK")
                .telephoneNumber("212111114"+i)
                .build();
    }

    MetsContactsMessage metsContactsMessageInvalid(int i) {
        return MetsContactsMessage.builder()
                .firstName(null)
                .lastName(null)
                .email("test")
                .mobilePhoneCountryCode("OO")
                .mobileNumber("INVALID"+i)
                .telephoneCountryCode("II")
                .telephoneNumber(null)
                .build();
    }



}
