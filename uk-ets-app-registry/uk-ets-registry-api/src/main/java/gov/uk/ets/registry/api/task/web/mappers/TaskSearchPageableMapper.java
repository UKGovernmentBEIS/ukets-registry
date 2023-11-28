package gov.uk.ets.registry.api.task.web.mappers;

import gov.uk.ets.registry.api.common.search.PageParameters;
import gov.uk.ets.registry.api.common.search.PageableMapper;
import gov.uk.ets.registry.api.task.shared.TaskPropertyPath;
import gov.uk.ets.registry.api.task.shared.TaskSearchAliases;

import java.util.function.Function;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

/**
 * Mapper which responsibility is to map the received {@link PageParameters} params to a {@link
 * org.springframework.data.domain.Pageable} object
 */
@Component
public class TaskSearchPageableMapper extends PageableMapper {

    public TaskSearchPageableMapper() {
        super(SortFieldParam.values(), SortFieldParam.REQUEST_ID);
    }

    /**
     * The Sort field param enum. It maps a sort filed request parameter to a {@link Sort} object.
     */
    public enum SortFieldParam implements SortParameter {
        REQUEST_ID("requestId",
            direction -> Sort.by(direction,
                TaskPropertyPath.TASK_REQUEST_ID)),
        TASK_TYPE("taskType",
            direction -> Sort.by(direction,
                TaskPropertyPath.TASK_TYPE)),
        INITIATOR("initiator",
            direction -> Sort.by(direction,
                TaskPropertyPath.TASK_INITIATOR_FIRST_NAME,
                TaskPropertyPath.TASK_INITIATOR_LAST_NAME)),
        CLAIMANT("claimant",
            direction -> Sort.by(direction,
                TaskPropertyPath.TASK_CLAIMANT_FIRST_NAME,
                TaskPropertyPath.TASK_CLAIMANT_LAST_NAME)),
        ACCOUNT_NUMBER("accountNumber", direction -> Sort.by(direction,
            TaskPropertyPath.ACCOUNT_IDENTIFIER)),
        ACCOUNT_TYPE("accountType", direction -> Sort.by(direction, TaskPropertyPath.ACCOUNT_TYPE_LABEL)),
        ACCOUNT_HOLDER("accountHolder", direction -> Sort.by(direction,
            TaskPropertyPath.ACCOUNT_HOLDER_NAME)),
        TRANSACTION_ID("transactionId", direction -> Sort.by(direction,
            TaskSearchAliases.TRANSACTION_IDENTIFIERS.getAlias())),
        CREATED_ON("createdOn", direction -> Sort.by(direction,
            TaskPropertyPath.TASK_CREATED_DATE)),
        TASK_STATUS("taskStatus", direction -> Sort.by(direction,
            TaskPropertyPath.TASK_STATUS)),
        REQUEST_STATUS("requestStatus", direction -> Sort.by(direction,
            TaskPropertyPath.TASK_STATUS));

        private String sortField;

        private Function<Sort.Direction, Sort> getSortFunc;

        SortFieldParam(String sortField, Function<Sort.Direction, Sort> getSortFunc) {
            this.sortField = sortField;
            this.getSortFunc = getSortFunc;
        }

        public String getName() {
            return sortField;
        }

        public Function<Sort.Direction, Sort> getSortProvider() {
            return getSortFunc;
        }
    }
}
