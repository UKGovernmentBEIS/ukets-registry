package gov.uk.ets.registry.api.notification.userinitiated;

import static org.assertj.core.api.Assertions.assertThat;

import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationType;
import gov.uk.ets.registry.api.notification.userinitiated.web.model.AdHocActivationDetails;
import gov.uk.ets.registry.api.notification.userinitiated.web.model.ComplianceActivationDetails;
import gov.uk.ets.registry.api.notification.userinitiated.web.model.ContentDetails;
import gov.uk.ets.registry.api.notification.userinitiated.web.model.NotificationDTO;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import org.assertj.core.util.introspection.IntrospectionError;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

@JsonTest
class NotificationDTOSerializationTest {

    private static final String TEST_SUBJECT = "test-subject";
    private static final String TEST_CONTENT = "test-content";
    @Autowired
    private JacksonTester<NotificationDTO> json;

    @Test
    void shouldDeserializeAdHoc() throws IOException {

        String content = "{\n" +
            "  \"type\": \"AD_HOC\",\n" +
            "  \"activationDetails\": {\n" +
            "    \"scheduledDate\": \"2021-01-01\",\n" +
            "    \"scheduledTime\": \"10:11\",\n" +
            "    \"hasExpiredDateSection\": true,\n" +
            "    \"expirationDate\": \"2021-01-02\",\n" +
            "    \"expirationTime\": \"12:10\"\n" +
            "  }\n" +
            "}";

        NotificationDTO notificationDTO = json.parseObject(content);

        assertThat(notificationDTO.getActivationDetails()).isInstanceOf(AdHocActivationDetails.class);
    }

    @Test
    void shouldDeserializeCompliance() throws IOException {

        String content = "{\n" +
            "  \"type\": \"EMISSIONS_MISSING_FOR_OHA\",\n" +
            "  \"activationDetails\": {\n" +
            "    \"scheduledDate\": \"2021-01-01\",\n" +
            "    \"scheduledTime\": \"10:11\",\n" +
            "    \"hasRecurrence\": true,\n" +
            "    \"expirationDate\": \"2021-01-02\",\n" +
            "    \"recurrenceDays\": \"2\"\n" +
            "  }\n" +
            "}";

        NotificationDTO notificationDTO = json.parseObject(content);

        assertThat(notificationDTO.getActivationDetails()).isInstanceOf(ComplianceActivationDetails.class);
    }

    @Test
    void shouldSerializeAdHocWithoutDerivedFields() throws IOException {
        NotificationDTO notificationDTO = NotificationDTO.builder()
            .type(NotificationType.AD_HOC)
            .contentDetails(ContentDetails.builder()
                .subject(TEST_SUBJECT)
                .content(TEST_CONTENT)
                .build())
            .activationDetails(AdHocActivationDetails.builder()
                .scheduledDate(LocalDate.now())
                .scheduledTime(LocalTime.now())
                .hasExpirationDateSection(true)
                .expirationDate(LocalDate.of(2030, 1, 1))
                .expirationTime(LocalTime.now())
                .build())
            .build();

        JsonContent<NotificationDTO> result = json.write(notificationDTO);

        assertThat(result).extractingJsonPathStringValue("$.type").isEqualTo(NotificationType.AD_HOC.toString());
        assertThat(result).extractingJsonPathValue("$.activationDetails").extracting("expirationDate")
            .isEqualTo("2030-01-01");
        assertThat(result).extractingJsonPathValue("$.activationDetails").extracting("hasExpirationDateSection")
            .isEqualTo(true);
        // check non-existence of derived fields:
        try {        	
          assertThat(result).extractingJsonPathValue("$.activationDetails").extracting("scheduledDateTime").isNull();        	
        } catch (IntrospectionError e) {
        	assertThat(e).hasCauseInstanceOf(IntrospectionError.class);    
        	assertThat(e).hasMessageContaining("Can't find any field or property with name 'scheduledDateTime'.");
        }
        try {
          assertThat(result).extractingJsonPathValue("$.activationDetails").extracting("expirationDateTime").isNull(); 	
          } catch (IntrospectionError e) {
          	assertThat(e).hasCauseInstanceOf(IntrospectionError.class);    
          	assertThat(e).hasMessageContaining("Can't find any field or property with name 'expirationDateTime'.");
          }

    }
}
