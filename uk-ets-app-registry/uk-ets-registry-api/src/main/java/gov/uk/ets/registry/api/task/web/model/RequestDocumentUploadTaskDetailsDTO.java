package gov.uk.ets.registry.api.task.web.model;

import gov.uk.ets.registry.api.file.reference.dto.ReferenceFileDTO;
import gov.uk.ets.registry.api.file.upload.dto.FileHeaderDto;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestDocumentUploadTaskDetailsDTO extends TaskDetailsDTO {

    private String recipient;
    private Long accountHolderIdentifier;
    private String accountHolderName;
    private String userUrid;
    @Deprecated
    private List<String> documentNames;
    @Deprecated
    private Set<Long> documentIds;
    /**
     * To Replace the above two for new file uploads. to keep the match between
     * the name and the id.
     */
//  private Map<String, Long> uploadedFileNameIdMap;

    private List<FileHeaderDto> uploadedFiles;
    private List<ReferenceFileDTO> referenceFiles;
    private String comment;
    private String reasonForAssignment;

    public RequestDocumentUploadTaskDetailsDTO(TaskDetailsDTO taskDetailsDTO) {
        super(taskDetailsDTO);
    }

}
