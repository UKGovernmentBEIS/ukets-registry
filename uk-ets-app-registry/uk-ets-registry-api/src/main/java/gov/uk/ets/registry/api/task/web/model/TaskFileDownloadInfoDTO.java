package gov.uk.ets.registry.api.task.web.model;

import gov.uk.ets.commons.logging.MDCParam;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import lombok.Getter;
import lombok.Setter;

import static gov.uk.ets.commons.logging.RequestParamType.FILE_ID;

@Getter
@Setter
public class TaskFileDownloadInfoDTO {
    @MDCParam(FILE_ID)
    private Long fileId;
    private RequestType taskType;
    private Long taskRequestId;
}
