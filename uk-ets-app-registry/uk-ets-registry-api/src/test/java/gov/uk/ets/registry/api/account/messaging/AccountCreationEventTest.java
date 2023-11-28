package gov.uk.ets.registry.api.account.messaging;

import static org.assertj.core.api.Assertions.assertThat;

import gov.uk.ets.registry.api.compliance.messaging.events.ComplianceEventType;
import gov.uk.ets.registry.api.compliance.messaging.events.outgoing.ComplianceOutgoingEventBase;
import gov.uk.ets.registry.api.transaction.messaging.SurrenderEvent;
import java.io.IOException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;


@JsonTest
class AccountCreationEventTest {

    private static final Long TEST_COMPLIANT_ENTITY_ID = 1234567L;
    private static final String TEST_URID = "UK123456789";
    public static final int FIRST_YEAR_OF_VERIFIED_EMISSION = 2021;
    public static final String ACCOUNT_CREATION = "ACCOUNT_CREATION";

    @Autowired
    private JacksonTester<ComplianceOutgoingEventBase> json;

    @Test
    public void shouldSerializeAbstractComplianceEventToSpecificAccountCreationEvent() throws IOException {
        LocalDateTime dateTriggered = LocalDateTime.of(2021, 1, 1, 23, 59, 0, 123456789);
        AccountCreationEvent accountCreationEvent = AccountCreationEvent.builder()
            .compliantEntityId(TEST_COMPLIANT_ENTITY_ID)
            .dateTriggered(dateTriggered)
            .actorId(TEST_URID)
            .firstYearOfVerifiedEmissions(FIRST_YEAR_OF_VERIFIED_EMISSION)
            .build();

        JsonContent<ComplianceOutgoingEventBase> result = json.write(accountCreationEvent);

        assertThat(result).hasJsonPathNumberValue("$.compliantEntityId");
        assertThat(result).extractingJsonPathNumberValue("$.firstYearOfVerifiedEmissions")
            .isEqualTo(FIRST_YEAR_OF_VERIFIED_EMISSION);
        assertThat(result).extractingJsonPathStringValue("$.dateTriggered").isEqualTo("2021-01-01T23:59:00.123456789");
        assertThat(result).extractingJsonPathStringValue("$.type").isEqualTo(ACCOUNT_CREATION);

    }

    @Test
    public void shouldDeserializeToAbstractComplianceEvent() throws IOException {
        String content =
            "{\"compliantEntityId\":\"" + TEST_COMPLIANT_ENTITY_ID +
                "\",\"type\":\"" + ACCOUNT_CREATION + "\", \"firstYearOfVerifiedEmissions\":\"" +
                FIRST_YEAR_OF_VERIFIED_EMISSION +
                "\"}";

        ComplianceOutgoingEventBase eventBase = json.parseObject(content);
        assertThat(eventBase).isInstanceOf(AccountCreationEvent.class);
        AccountCreationEvent event = (AccountCreationEvent) eventBase;

        assertThat(event.getCompliantEntityId()).isEqualTo(TEST_COMPLIANT_ENTITY_ID);
        assertThat(event.getFirstYearOfVerifiedEmissions()).isEqualTo(FIRST_YEAR_OF_VERIFIED_EMISSION);
        assertThat(event.getType()).isEqualTo(ComplianceEventType.ACCOUNT_CREATION);

    }
}
