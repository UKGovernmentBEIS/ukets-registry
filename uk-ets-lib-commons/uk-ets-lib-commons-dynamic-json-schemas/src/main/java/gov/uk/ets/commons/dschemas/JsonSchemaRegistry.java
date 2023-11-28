package gov.uk.ets.commons.dschemas;

import com.github.fge.jsonschema.main.JsonSchema;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Used to store a schema for a class.
 */
public final class JsonSchemaRegistry {

    private final Map<Class<?>, JsonSchema> schemaRegistry = new ConcurrentHashMap<>();

    /**
     * Addition of entries.
     * @param clazz the class
     * @param jsonSchema the json schema
     */
    public void add(Class<?> clazz, JsonSchema jsonSchema) {
        schemaRegistry.put(clazz, jsonSchema);
    }

    /**
     * Retrieve the schema for a class.
     * @param clazz the class
     * @return the schema
     */
    public JsonSchema get(Class<?> clazz) {
        return schemaRegistry.get(clazz);
    }
}
