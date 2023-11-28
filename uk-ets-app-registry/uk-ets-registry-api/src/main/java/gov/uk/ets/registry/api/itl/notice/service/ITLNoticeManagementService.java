package gov.uk.ets.registry.api.itl.notice.service;

import gov.uk.ets.registry.api.common.search.PageParameters;
import gov.uk.ets.registry.api.common.search.PageableMapper;
import gov.uk.ets.registry.api.common.search.SearchResponse;
import gov.uk.ets.registry.api.itl.notice.domain.ITLNotificationHistory;
import gov.uk.ets.registry.api.itl.notice.domain.ITLNotificationBlock;
import gov.uk.ets.registry.api.itl.notice.domain.type.NoticeStatus;
import gov.uk.ets.registry.api.itl.notice.domain.type.NoticeType;
import gov.uk.ets.registry.api.itl.notice.repository.ITLNoticeLogHistoryRepository;
import gov.uk.ets.registry.api.itl.notice.repository.NoticeUnitBlockRepository;
import gov.uk.ets.registry.api.itl.notice.web.model.*;
import gov.uk.ets.registry.api.transaction.domain.UnitBlock;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.repository.UnitBlockRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@Service
@AllArgsConstructor
public class ITLNoticeManagementService {

    private final String ERROR_MESSAGE = "Invalid notification ID - please try again";
    private final NoticeUnitBlockRepository noticeUnitBlockRepository;

    private final ITLNoticeLogHistoryRepository itlNoticeLogHistoryRepository;
    private final UnitBlockRepository unitBlockRepository;

    public Page<ITLNoticeResult> search(@Valid ITLNoticeSearchCriteria criteria, Pageable pageable) {
        return itlNoticeLogHistoryRepository.search(criteria, pageable);
    }

    @Transactional(readOnly = true)
    public List<ITLNoticeDetailResult> getITLDetails(Long notificationIdentity) {
        ITLNoticeSearchDetailsResultMapper itlNoticeSearchDetailsResultMapper = new ITLNoticeSearchDetailsResultMapper();
        Set<ITLNotificationHistory> noticeLogHistories = itlNoticeLogHistoryRepository.findAllByNotificationIdentifier(notificationIdentity);
        Set<ITLNotificationBlock> noticeUnitBlocks = noticeUnitBlockRepository.findAllNoticeUnitBlocksOfNotificationIdentifier(notificationIdentity);
        return itlNoticeSearchDetailsResultMapper.map(notificationIdentity, noticeLogHistories, noticeUnitBlocks);
    }

    public SearchResponse<ITLNoticeResult> search(@Valid ITLNoticeSearchCriteria criteria, PageParameters pageParameters) {
        SearchResponse<ITLNoticeResult> response = new SearchResponse<>();
        PageableMapper pageableMapper = new PageableMapper(ITLNoticeSortFieldParam.values(),
                ITLNoticeSortFieldParam.NOTICE_DATE_RECEIVED_ON);
        Pageable pageable = pageableMapper.get(pageParameters);
        Page<ITLNoticeResult> page = search(criteria, pageable);
        response.setItems(new ArrayList<>(page.getContent()));
        pageParameters.setTotalResults(page.getTotalElements());
        response.setPageParameters(pageParameters);
        return response;
    }

    /**
     * Performs checks on the provided ITL notification ID.
     *
     * @param notificationIdentifier The ITL Notification ID
     * @param transactionType        The transaction type
     * @return The ITL Notification result
     */
    @Transactional(readOnly = true)
    public ITLNoticeDetailResult performChecks(Long notificationIdentifier,
                                               TransactionType transactionType) {
        if (notificationIdentifier == null) {
            throw new IllegalStateException(ERROR_MESSAGE);
        }

        List<ITLNoticeDetailResult> results = this.getITLDetails(notificationIdentifier);
        results.removeIf(f -> f.getStatus().equals(NoticeStatus.COMPLETED));
        if (results.isEmpty()) {
            throw new IllegalStateException(ERROR_MESSAGE);
        }

        ITLNoticeDetailResult itlNoticeDetailResult = results.stream()
                .sorted(Comparator.comparing(ITLNoticeDetailResult::getCreatedDate))
                .collect(Collectors.toList())
                .get(0);
        if (!itlNoticeDetailResult.getType().getTransactionTypes().contains(transactionType)) {
            throw new IllegalStateException(ERROR_MESSAGE);
        }

        if (transactionType.equals(TransactionType.Replacement) &&
                (itlNoticeDetailResult.getType().equals(NoticeType.REVERSAL_OF_STORAGE_FOR_CDM_PROJECT) ||
                        itlNoticeDetailResult.getType().equals(NoticeType.NON_SUBMISSION_OF_CERTIFICATION_REPORT_FOR_CDM_PROJECT))) {
            List<UnitBlock> unitBlocks = unitBlockRepository
                    .findByUnitTypeAndProject(UnitType.LCER, itlNoticeDetailResult.getProjectNumber());
            if (CollectionUtils.isEmpty(unitBlocks)) {
                throw new IllegalStateException("This notification cannot be fulfilled with a Replacement");
            }
        }
        return itlNoticeDetailResult;
    }
}
