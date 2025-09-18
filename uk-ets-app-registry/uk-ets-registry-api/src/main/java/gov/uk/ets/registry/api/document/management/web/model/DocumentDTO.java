package gov.uk.ets.registry.api.document.management.web.model;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentDTO {

    private Long id;
    private String title;
    private String name;
    private Integer order;
    private Date createdOn;
    private Date updatedOn;
}
