package gov.uk.ets.registry.api.regulatornotice.service;

import com.querydsl.jpa.impl.JPAQuery;
import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.shared.AccountActionError;
import gov.uk.ets.registry.api.account.shared.AccountActionException;
import gov.uk.ets.registry.api.common.model.services.PersistenceService;
import gov.uk.ets.registry.api.common.reports.ReportRequestService;
import gov.uk.ets.registry.api.common.reports.ReportRoleMappingService;
import gov.uk.ets.registry.api.common.search.JpaQueryExtractor;
import gov.uk.ets.registry.api.regulatornotice.domain.RegulatorNotice;
import gov.uk.ets.registry.api.regulatornotice.repository.RegulatorNoticeRepository;
import gov.uk.ets.registry.api.regulatornotice.shared.RegulatorNoticeProjection;
import gov.uk.ets.registry.api.regulatornotice.web.model.RegulatorNoticeSearchCriteria;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.reports.model.ReportType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RegulatorNoticeService {

    private final PersistenceService persistenceService;
    private final AccountRepository accountRepository;
    private final RegulatorNoticeRepository regulatorNoticeRepository;
    private final TaskEventService taskEventService;
    private final ReportRequestService reportRequestService;
    private final ReportRoleMappingService reportRoleMappingService;

    @Transactional
    public Long createRegulatorNotice(Long operatorId, String processType) {

        final Account account = accountRepository.findByCompliantEntityIdentifier(operatorId)
                .orElseThrow(() -> AccountActionException.create(
                        AccountActionError.build("Requested account was not found.")));

        RegulatorNotice regulatorNotice = new RegulatorNotice();
        regulatorNotice.setRequestId(persistenceService.getNextBusinessIdentifier(Task.class));
        regulatorNotice.setType(RequestType.REGULATOR_NOTICE);
        regulatorNotice.setInitiatedDate(new Date());
        regulatorNotice.setStatus(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
        regulatorNotice.setProcessType(processType);
        regulatorNotice.setDifference(processType);
        regulatorNotice.setAccount(account);
        persistenceService.save(regulatorNotice);

        taskEventService.createAndPublishTaskAndAccountRequestEvent(regulatorNotice, null);
        return regulatorNotice.getRequestId();
    }

    /**
     * Searches for transaction projection results
     *
     * @param criteria The search criteria
     * @return The {@link Page<RegulatorNoticeProjection>} of results
     */
    @Transactional(readOnly = true)
    public Page<RegulatorNoticeProjection> search(RegulatorNoticeSearchCriteria criteria, Pageable pageable, boolean isReport) {
        if (isReport) {
            JPAQuery<RegulatorNoticeProjection> jpaQuery = regulatorNoticeRepository.getQuery(criteria);
            reportRequestService
                    .requestReport(ReportType.R0052, reportRoleMappingService.getUserReportRequestingRole(),
                            JpaQueryExtractor.extractReportQueryInfo(jpaQuery.createQuery()), null);
            return Page.empty();
        }
        return regulatorNoticeRepository.search(criteria, pageable);
    }

    public List<String> getRegulatorNoticeTypes() {
        return regulatorNoticeRepository.findAllProcessTypes();
    }

    public RegulatorNotice findByIdentifier(Long identifier) {
        return regulatorNoticeRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new IllegalArgumentException("Regulator Notice with identifier " + identifier + " not found"));
    }
}
