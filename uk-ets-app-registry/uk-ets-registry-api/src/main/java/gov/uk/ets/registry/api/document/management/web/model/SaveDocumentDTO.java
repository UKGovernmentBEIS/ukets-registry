package gov.uk.ets.registry.api.document.management.web.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class SaveDocumentDTO {

    private Long id;
    private Long categoryId;
    private String title;
    private MultipartFile file;
    private Integer order;
}
