package gov.uk.ets.commons.dschemas;


import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;

/**
 * Simple Json validator.
 */
public final class JsonValidator {

    /**
     * Validates inputAsJson against schema.
     *
     * @param inputAsJson the input to validate
     * @param schema the schema to validate against
     * @throws ProcessingException in case of errors
     */
    public boolean validate(JsonNode inputAsJson, JsonSchema schema) throws ProcessingException {
        ProcessingReport report = schema.validate(inputAsJson);
        return report.isSuccess();
    }
}
