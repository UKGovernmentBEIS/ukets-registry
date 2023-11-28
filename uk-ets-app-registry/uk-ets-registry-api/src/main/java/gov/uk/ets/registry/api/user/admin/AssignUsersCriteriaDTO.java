package gov.uk.ets.registry.api.user.admin;

import java.util.List;

import gov.uk.ets.commons.logging.MDCParam;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import lombok.Getter;
import lombok.Setter;

import static gov.uk.ets.commons.logging.RequestParamType.TASK_REQUEST_ID;

@Getter
@Setter
public class AssignUsersCriteriaDTO {
    String term;
    @MDCParam(TASK_REQUEST_ID)
    List<Long> requestIds;
    List<Long> accountIdentifiers;
    List<RequestType> taskTypes;
}
