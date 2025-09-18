package gov.uk.ets.registry.api.common.model.services.converter;

import gov.uk.ets.registry.api.notification.userinitiated.domain.SupportedParameters;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import jakarta.persistence.Converter;

@Component
@Converter
@Log4j2
public class SupportedParametersToJsonStringConverter extends ObjectToJsonStringConverter {

    @Override
    public SupportedParameters convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, SupportedParameters.class);
        } catch (Exception ex) {
            log.debug("No SupportedParameters found", ex);
            return null;
        }
    }
}
