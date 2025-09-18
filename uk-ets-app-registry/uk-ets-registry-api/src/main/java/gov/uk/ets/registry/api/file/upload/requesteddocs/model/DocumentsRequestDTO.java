package gov.uk.ets.registry.api.file.upload.requesteddocs.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentsRequestDTO {
    private DocumentsRequestType type;
    private List<String> documentNames = new ArrayList<>();
    private Long accountHolderIdentifier;
    private Long accountHolderName;
    private String recipientUrid;
    private String comment;
    private Long parentRequestId;
    private String accountFullIdentifier;
    private Date deadline;
}
