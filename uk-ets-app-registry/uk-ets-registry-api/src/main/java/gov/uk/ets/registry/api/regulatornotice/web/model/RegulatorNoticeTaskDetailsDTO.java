package gov.uk.ets.registry.api.regulatornotice.web.model;

import gov.uk.ets.registry.api.file.upload.dto.FileHeaderDto;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegulatorNoticeTaskDetailsDTO extends TaskDetailsDTO {

    private String processType;
    private String accountHolderName;
    private Long accountHolderIdentifier;
    private Long operatorId;
    private String permitOrMonitoringPlanIdentifier;
    private FileHeaderDto file;


    public RegulatorNoticeTaskDetailsDTO(TaskDetailsDTO taskDetailsDTO) {
        super(taskDetailsDTO);
    }
}
