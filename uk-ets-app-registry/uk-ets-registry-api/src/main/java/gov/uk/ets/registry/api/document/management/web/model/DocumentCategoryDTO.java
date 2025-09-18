package gov.uk.ets.registry.api.document.management.web.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentCategoryDTO {

    private Long id;
    @NotBlank
    @Size(max = 100, message = "Category name must not exceed 100 characters.")
    private String name;
    @NotNull
    private Integer order;
    private List<DocumentDTO> documents;
    private Date createdOn;
    private Date updatedOn;
}
