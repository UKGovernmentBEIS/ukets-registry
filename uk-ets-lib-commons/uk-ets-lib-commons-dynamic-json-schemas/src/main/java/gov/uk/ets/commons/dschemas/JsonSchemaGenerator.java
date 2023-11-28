package gov.uk.ets.commons.dschemas;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.victools.jsonschema.generator.Option;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;
import lombok.SneakyThrows;

/**
 * Generates a json schema from a class.
 */
public final class JsonSchemaGenerator {

    /**
     * Generates a json schema from a class.
     * @param clazz the class
     * @return the schema
     */
    @SneakyThrows
    public JsonSchema generateJsonSchema(Class<?> clazz) {
        SchemaGeneratorConfigBuilder
            configBuilder = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2019_09, OptionPreset.PLAIN_JSON);
        // forbid extra properties coming in
        configBuilder.with(Option.FORBIDDEN_ADDITIONAL_PROPERTIES_BY_DEFAULT);
        SchemaGeneratorConfig config = configBuilder.build();
        SchemaGenerator generator = new SchemaGenerator(config);
        JsonNode jsonSchemaNode = generator.generateSchema(clazz);
        return JsonSchemaFactory.byDefault().getJsonSchema(jsonSchemaNode);
    }
}
