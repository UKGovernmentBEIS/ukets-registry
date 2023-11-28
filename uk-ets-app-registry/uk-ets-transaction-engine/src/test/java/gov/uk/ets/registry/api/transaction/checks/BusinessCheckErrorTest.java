package gov.uk.ets.registry.api.transaction.checks;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;


class BusinessCheckErrorTest {

    @Test
    void shouldBeSerializedAndDeserialized() {
        ObjectMapper mapper = new ObjectMapper();
        BusinessCheckError error = new BusinessCheckError(1, "a message");
        assertDoesNotThrow(() -> mapper.writeValueAsString(error));
        String json = "{\"code\": 1, \"message\": \"a message\"}";
        assertDoesNotThrow(() -> mapper.readValue(json, BusinessCheckError.class));
    }
}