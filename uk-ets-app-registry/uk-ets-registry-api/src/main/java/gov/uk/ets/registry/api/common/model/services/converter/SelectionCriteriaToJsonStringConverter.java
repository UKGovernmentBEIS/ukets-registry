package gov.uk.ets.registry.api.common.model.services.converter;

import gov.uk.ets.registry.api.notification.userinitiated.domain.SelectionCriteria;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.persistence.Converter;

@Component
@Converter
@Log4j2
public class SelectionCriteriaToJsonStringConverter extends ObjectToJsonStringConverter {

    @Override
    public SelectionCriteria convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, SelectionCriteria.class);
        } catch (Exception ex) {
            log.warn("No SelectionCriteria found", ex);
            return null;
        }
    }
}
