package gov.uk.ets.registry.api.common.model.services.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Simple JPA converter that converts between a class and its JSON representation when saving in the database.
 * Converts from String/JSON back to the class when reading from database.
 */
@Component
@Converter
@RequiredArgsConstructor
public abstract class ObjectToJsonStringConverter implements AttributeConverter<Object, String> {

    // TODO Maybe revisit this because I could not inject ObjectMapper here because several @DataJpa tests fail.
    static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Object meta) {
        try {
            return objectMapper.writeValueAsString(meta);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }

}