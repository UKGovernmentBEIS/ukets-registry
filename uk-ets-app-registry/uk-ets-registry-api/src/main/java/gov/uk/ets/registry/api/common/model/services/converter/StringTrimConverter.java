package gov.uk.ets.registry.api.common.model.services.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class StringTrimConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return keepSingleSpace(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return keepSingleSpace(dbData);
    }

    private String keepSingleSpace(String attribute) {
        return attribute != null ? attribute.trim().replaceAll("\\s+", " ")  : null;
    }

}