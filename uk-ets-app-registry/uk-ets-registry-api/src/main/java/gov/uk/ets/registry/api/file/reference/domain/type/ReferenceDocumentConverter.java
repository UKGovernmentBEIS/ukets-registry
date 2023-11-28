package gov.uk.ets.registry.api.file.reference.domain.type;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class ReferenceDocumentConverter implements AttributeConverter<ReferenceDocument, String> {

    @Override
    public String convertToDatabaseColumn(ReferenceDocument document) {
        if (document == null) {
            return null;
        }
        return document.getDocumentName();
    }

    @Override
    public ReferenceDocument convertToEntityAttribute(String dbName) {
        if (dbName == null) {
            return null;
        }

        return java.util.stream.Stream.of(ReferenceDocument.values())
          .filter(c -> c.getDocumentName().equals(dbName))
          .findFirst()
          .orElseThrow(IllegalArgumentException::new);
    }

}
