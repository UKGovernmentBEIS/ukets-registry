package gov.uk.ets.registry.api.alerts.service;

import gov.uk.ets.registry.api.alerts.web.model.AlertsResponse;
import gov.uk.ets.registry.api.common.search.PageParameters;
import gov.uk.ets.registry.api.itl.notice.domain.type.NoticeStatus;
import gov.uk.ets.registry.api.itl.notice.service.ITLNoticeManagementService;
import gov.uk.ets.registry.api.itl.notice.web.model.ITLNoticeResult;
import gov.uk.ets.registry.api.itl.notice.web.model.ITLNoticeSearchCriteria;
import gov.uk.ets.registry.api.itl.reconciliation.service.ITLReconciliationAdminService;
import gov.uk.ets.registry.api.reconciliation.service.ReconciliationService;
import gov.uk.ets.registry.api.reconciliation.web.DTOMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class AlertsService {

    private final ITLNoticeManagementService itlNoticeManagementService;
    private final ITLReconciliationAdminService itlReconciliationAdminService;
    private final ReconciliationService reconciliationService;
    private final DTOMapper mapper;

    /**
     * Gets all the alert Details containing:
     *
     * <ul>
     *     <li>the last date when user was successfully logged in</li>
     *     <li>the number of unsuccessful login attempts after the above date</li>
     *     <li>A list of {@link ITLNoticeResult ITL Notifications} if existent
     *          (there is an {@link ITLNoticeResult ITL Notifications} if we have at least one in the list with
     *          {@link NoticeStatus notice status} different than {@link NoticeStatus#COMPLETED})</li>
     *     <li>An {@link gov.uk.ets.registry.api.itl.reconciliation.web.model.ITLReconciliationDTO ITL reconciliationDTO}</li>
     *     <li>An {@link gov.uk.ets.registry.api.reconciliation.web.ReconciliationDTO UKETS reconciliationDTO}</li>
     * </ul>
     *
     *
     * @param urId the urid of the user
     * @return an alerts response containing above information
     */
    @Transactional(readOnly = true)
    public AlertsResponse getAlertsDetails(String urId) {
        List<ITLNoticeResult> itlNoticeResultList = new ArrayList<>();
        for (NoticeStatus noticeStatus : NoticeStatus.values()) {
            if (noticeStatus != NoticeStatus.COMPLETED) {
                ITLNoticeSearchCriteria itlNoticeSearchCriteria = new ITLNoticeSearchCriteria();
                itlNoticeSearchCriteria.setNoticeStatus(noticeStatus.name());
                PageParameters pageParameters = new PageParameters();
                pageParameters.setPageSize(2L);
                pageParameters.setTotalResults(2L);
                pageParameters.setSortDirection(Sort.Direction.ASC);
                itlNoticeResultList.addAll(itlNoticeManagementService.search(itlNoticeSearchCriteria, pageParameters).getItems());
            }
        }

        return AlertsResponse.builder()
                .searchResponseResults(itlNoticeResultList)
                .itlReconcileDTO(itlReconciliationAdminService.getLatestReconciliation())
                .reconciliationDTO(mapper.map(reconciliationService.getLatestReconciliation()))
                .build();
    }

}
