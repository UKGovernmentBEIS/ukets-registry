package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessRuleInfo;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.common.JsonMappingException;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.file.upload.requesteddocs.domain.RequestDocumentsTaskDifference;
import gov.uk.ets.registry.api.task.domain.Task;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskActionsForUserRequestedDocumentsRule extends AbstractTaskBusinessRule {

    private String checkUserUrid;

    private static final ObjectMapper mapper = new ObjectMapper();

    public TaskActionsForUserRequestedDocumentsRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from("Only the user to whom the documents refer can perform actions");
    }

    @Override
    public Outcome permit() {
        TaskBusinessSecurityStoreSlice slice = getSlice();
        if (slice.getTaskBusinessRuleInfoList().isEmpty()) {
            return forbiddenOutcome();
        }
        for (TaskBusinessRuleInfo taskBusinessRuleInfo : slice.getTaskBusinessRuleInfoList()) {
            if (!checkUserRequestDocument(taskBusinessRuleInfo.getTask())) {
                return forbiddenOutcome();
            }
        }
        return Outcome.PERMITTED_OUTCOME;
    }

    /**
     * This function checks if the task requested documents refers to User documents.
     *
     * @param task the {@link Task}
     * @return true/false
     */
    private boolean checkUserRequestDocument(Task task) {
        RequestDocumentsTaskDifference dto;
        try {
            dto = mapper.readValue(task.getDifference(), RequestDocumentsTaskDifference.class);
        } catch (JsonProcessingException e) {
            throw new JsonMappingException("Exception during JSON deserialization");
        }

        if (dto.getUserUrid() == null) {
            return true;
        } else {
            return getCheckUserUrid().equals(dto.getUserUrid());
        }
    }
}
