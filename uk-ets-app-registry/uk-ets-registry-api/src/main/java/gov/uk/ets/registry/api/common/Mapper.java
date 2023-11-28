package gov.uk.ets.registry.api.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class Mapper {

    private final ObjectMapper jacksonMapper;

    public <T> String convertToJson(T o) {
        try {
            return jacksonMapper.writeValueAsString(o);
        } catch (Exception e) {
            log.error("Exception during JSON serialization:", e);
            throw new JsonMappingException("Exception during JSON serialization");
        }
    }

    public <T> T convertToPojo(String dbData, Class<T> clazz) {
        try {
            return jacksonMapper.readValue(dbData, clazz);
        } catch (Exception e) {
            log.error("Exception during JSON deserialization:", e);
            throw new JsonMappingException("Exception during JSON deserialization");
        }
    }

    public<T> T convertToPojoWithUnknownEnumValuesAndUnknownProperties(String dbData, Class<T> clazz) {
        try {
            jacksonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            jacksonMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
            return jacksonMapper.readValue(dbData, clazz);
        } catch (Exception e) {
            log.error("Exception during JSON deserialization:", e);
            throw new JsonMappingException("Exception during JSON deserialization");
        }
    }

    public JsonNode convertToJsonNode(String dbData) {
        try {
            return jacksonMapper.readTree(dbData);
        } catch (Exception e) {
            log.error("Exception during JSON deserialization:", e);
            throw new JsonMappingException("Exception during JSON deserialization");
        }
    }

    public <T> T convertResToPojo(Resource res, Class<T> clazz) {
        try {
            return jacksonMapper.readValue(res.getInputStream(), clazz);
        } catch (Exception e) {
            log.error("Exception during JSON deserialization:", e);
            throw new JsonMappingException("Exception during JSON deserialization");
        }
    }

    public <T> T convertResToPojo(Resource res, TypeReference<T> clazz) {
        try {
            return jacksonMapper.readValue(res.getInputStream(), clazz);
        } catch (Exception e) {
            log.error("Exception during JSON deserialization:", e);
            throw new JsonMappingException("Exception during JSON deserialization");
        }
    }

}
