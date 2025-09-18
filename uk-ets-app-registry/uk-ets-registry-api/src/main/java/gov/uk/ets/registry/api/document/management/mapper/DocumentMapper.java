package gov.uk.ets.registry.api.document.management.mapper;

import gov.uk.ets.registry.api.document.management.domain.Document;
import gov.uk.ets.registry.api.document.management.domain.DocumentCategory;
import gov.uk.ets.registry.api.document.management.web.model.DocumentCategoryDTO;
import gov.uk.ets.registry.api.document.management.web.model.DocumentDTO;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DocumentMapper {

    public DocumentDTO toDto(Document domain) {
        DocumentDTO dto = new DocumentDTO();
        dto.setId(domain.getId());
        dto.setTitle(domain.getTitle());
        dto.setName(domain.getName());
        dto.setOrder(domain.getPosition());
        dto.setCreatedOn(domain.getCreatedOn());
        dto.setUpdatedOn(domain.getUpdatedOn());

        return dto;
    }

    public DocumentCategoryDTO toDto(DocumentCategory domain) {
        DocumentCategoryDTO dto = new DocumentCategoryDTO();
        dto.setId(domain.getId());
        dto.setName(domain.getName());
        dto.setOrder(domain.getPosition());
        dto.setCreatedOn(domain.getCreatedOn());
        dto.setUpdatedOn(domain.getUpdatedOn());

        List<DocumentDTO> documents = domain.getDocuments().stream().map(this::toDto).toList();
        dto.setDocuments(documents);

        return dto;
    }
}
