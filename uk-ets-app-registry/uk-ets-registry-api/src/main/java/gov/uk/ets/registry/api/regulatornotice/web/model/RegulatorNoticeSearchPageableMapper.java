package gov.uk.ets.registry.api.regulatornotice.web.model;

import gov.uk.ets.registry.api.common.search.PageParameters;
import gov.uk.ets.registry.api.common.search.PageableMapper;
import gov.uk.ets.registry.api.regulatornotice.shared.RegulatorNoticePropertyPath;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Mapper which responsibility is to map the received {@link PageParameters} params to a {@link
 * org.springframework.data.domain.Pageable} object
 */

@Component
public class RegulatorNoticeSearchPageableMapper extends PageableMapper {

    public RegulatorNoticeSearchPageableMapper() {
        super(RegulatorNoticeSearchPageableMapper.SortFieldParam.values(), RegulatorNoticeSearchPageableMapper.SortFieldParam.REQUEST_ID);
    }

    /**
     * The Sort field param enum. It maps a sort filed request parameter to a {@link Sort} object.
     */
    public enum SortFieldParam implements SortParameter {
        REQUEST_ID("requestId",
                direction -> Sort.by(direction,
                        RegulatorNoticePropertyPath.TASK_REQUEST_ID)),
        ACCOUNT_HOLDER_NAME("accountHolderName", direction -> Sort.by(direction,
                RegulatorNoticePropertyPath.ACCOUNT_HOLDER_NAME)),
        TASK_PROCESS_TYPE("processType", direction -> Sort.by(direction,
                RegulatorNoticePropertyPath.TASK_PROCESS_TYPE)),
        PERMIT_OR_MONITORING_PLAN_IDENTIFIER("permitOrMonitoringPlanIdentifier", direction -> Sort.by(direction,
                RegulatorNoticePropertyPath.PERMIT_OR_MONITORING_PLAN_IDENTIFIER)),
        TASK_INITIATED_DATE("initiatedDate", direction -> Sort.by(direction,
                RegulatorNoticePropertyPath.TASK_INITIATED_DATE)),
        CLAIMANT("claimant",
                direction -> Sort.by(direction,
                        RegulatorNoticePropertyPath.TASK_CLAIMANT_FIRST_NAME,
                        RegulatorNoticePropertyPath.TASK_CLAIMANT_LAST_NAME)),
        TASK_STATUS("taskStatus", direction -> Sort.by(direction,
                RegulatorNoticePropertyPath.TASK_STATUS));


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
