package gov.uk.ets.registry.api.integration.service.metscontacts;

import gov.uk.ets.registry.api.account.web.model.accountcontact.MetsAccountContactType;
import gov.uk.ets.registry.api.account.web.model.accountcontact.MetsContactDTO;
import gov.uk.ets.registry.api.account.web.model.accountcontact.OperatorType;
import gov.uk.ets.registry.api.common.view.PhoneNumberDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.netz.integration.model.metscontacts.MetsContactsEvent;
import uk.gov.netz.integration.model.metscontacts.MetsContactsMessage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MetsContactMapperTest {

    private MetsContactMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new MetsContactMapper();
    }

    private MetsContactsMessage buildMessage() {
        MetsContactsMessage msg = new MetsContactsMessage();
        msg.setFirstName("John");
        msg.setLastName("Smith");
        msg.setEmail("john.smith@example.com");
        msg.setTelephoneCountryCode("+44");
        msg.setTelephoneNumber("2079460056");
        msg.setMobilePhoneCountryCode("+44");
        msg.setMobileNumber("7700900123");
        msg.setContactTypes(List.of("PRIMARY"));
        msg.setUserType(OperatorType.OPERATOR_ADMIN.name());

        return msg;
    }

    @Test
    void testConvertSingleMessage() {
        MetsContactsMessage message = buildMessage(); // πρέπει να έχει telephoneCountryCode "+44" ή "44"

        MetsContactDTO dto = mapper.convert(new MetsContactsEvent() {{
            setDetails(List.of(message));
        }}).get(0);

        assertNotNull(dto);
        assertEquals("John Smith", dto.getFullName());
        assertEquals("john.smith@example.com", dto.getEmail());
        assertEquals(OperatorType.OPERATOR_ADMIN, dto.getOperatorType());
        assertTrue(dto.getContactTypes().contains(MetsAccountContactType.PRIMARY));

        PhoneNumberDTO phone = dto.getPhoneNumber();
        assertNotNull(phone);
        assertEquals("2079460056", phone.getPhoneNumber1());
        assertEquals("UK (44)", phone.getCountryCode1());
        assertEquals("7700900123", phone.getPhoneNumber2());
        assertEquals("UK (44)", phone.getCountryCode2());
    }

    @Test
    void testConvertMultipleMessages() {
        MetsContactsMessage msg1 = buildMessage();

        MetsContactsMessage msg2 = new MetsContactsMessage();
        msg2.setFirstName("Alice");
        msg2.setLastName("Brown");
        msg2.setEmail("alice.brown@example.com");
        msg2.setTelephoneCountryCode("+33");
        msg2.setTelephoneNumber("111222333");
        msg2.setMobilePhoneCountryCode("+33");
        msg2.setMobileNumber("999888777");
        msg2.setContactTypes(List.of("SECONDARY"));
        msg2.setUserType(OperatorType.OPERATOR.name());

        MetsContactsEvent event = new MetsContactsEvent();
        event.setDetails(List.of(msg1, msg2));

        List<MetsContactDTO> result = mapper.convert(event);

        assertEquals(2, result.size());
        MetsContactDTO dto1 = result.get(0);
        assertEquals("John Smith", dto1.getFullName());
        assertEquals(OperatorType.OPERATOR_ADMIN, dto1.getOperatorType());
        assertTrue(dto1.getContactTypes().contains(MetsAccountContactType.PRIMARY));
        MetsContactDTO dto2 = result.get(1);
        assertEquals("Alice Brown", dto2.getFullName());
        assertEquals(OperatorType.OPERATOR, dto2.getOperatorType());
        assertTrue(dto2.getContactTypes().contains(MetsAccountContactType.SECONDARY));
    }
}
