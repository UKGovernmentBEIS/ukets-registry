package gov.uk.ets.commons.dschemas;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import com.github.fge.jsonschema.main.JsonSchema;
import gov.uk.ets.commons.dschemas.testclasses.Person;
import org.junit.jupiter.api.Test;

class JsonSchemaGeneratorTest {

    @Test
    void generateJsonSchema() {
        JsonSchemaGenerator jsonSchemaGenerator = new JsonSchemaGenerator();
        JsonSchema personSchema = jsonSchemaGenerator.generateJsonSchema(Person.class);
        assertNotNull(personSchema);
    }
}