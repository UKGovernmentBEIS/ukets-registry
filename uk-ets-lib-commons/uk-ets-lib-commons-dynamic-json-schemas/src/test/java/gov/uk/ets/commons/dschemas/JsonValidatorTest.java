package gov.uk.ets.commons.dschemas;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;
import gov.uk.ets.commons.dschemas.testclasses.AnotherPerson;
import gov.uk.ets.commons.dschemas.testclasses.Person;
import org.junit.jupiter.api.Test;

public class JsonValidatorTest {

    @Test
    void validateJsonSchema() {
        JsonValidator jsonValidator = new JsonValidator();
        JsonSchemaGenerator jsonSchemaGenerator = new JsonSchemaGenerator();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        JsonSchema personSchema = jsonSchemaGenerator.generateJsonSchema(Person.class);
        try {
            assertTrue(jsonValidator.validate(objectMapper.valueToTree(Person.buildOne()), personSchema));
        } catch (ProcessingException e) {
            fail();
        }
    }

    @Test
    void validateWrongJsonSchemaIsCaught() {
        JsonValidator jsonValidator = new JsonValidator();
        JsonSchemaGenerator jsonSchemaGenerator = new JsonSchemaGenerator();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonSchema personSchema = jsonSchemaGenerator.generateJsonSchema(Person.class);
        JsonNode inputAsJsonNode = objectMapper.valueToTree(AnotherPerson.buildOne());
        try {
            assertFalse(jsonValidator.validate(inputAsJsonNode, personSchema));
        } catch (ProcessingException e) {
            fail();
        }
    }
}
