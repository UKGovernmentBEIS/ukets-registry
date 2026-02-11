package gov.uk.ets.compliance.events;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import gov.uk.ets.compliance.domain.DynamicCompliance;
import gov.uk.ets.compliance.domain.events.ChangeYearEvent;

public class ChangeYearEventTest {

	private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    
	@Test
	void testSerializeAndDeserialize() throws Exception {
		// Load JSON from test resources
		ClassPathResource resource = new ClassPathResource("dynamic_compliance_entity_01.json");
		String json = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

		// Deserialize
		DynamicCompliance compliance = objectMapper.readValue(json, DynamicCompliance.class);
		assertNotNull(compliance);
		ChangeYearEvent evt = ChangeYearEvent.
				builder().
				actorId("system").
				compliantEntityId(1003164L).
				dateRequested(LocalDateTime.now().minusMinutes(2)).
				dateTriggered(null).
				eventId(UUID.fromString("7abcce0e-66bf-4f33-be51-2700ec793c89")).
				newYear(2026).
				build();
		
		compliance.processDynamicComplianceUpdateEvent(evt);
		
		// Serialize again
		String serialized = objectMapper.writeValueAsString(compliance);
		assertNotNull(serialized);

		// Optional: print for debugging
		System.out.println("Serialized JSON: " + serialized);
	}

}
