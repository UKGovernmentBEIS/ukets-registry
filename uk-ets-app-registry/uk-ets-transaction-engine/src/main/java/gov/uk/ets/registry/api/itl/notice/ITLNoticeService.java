package gov.uk.ets.registry.api.itl.notice;

import gov.uk.ets.registry.api.itl.notice.domain.ITLNotification;
import gov.uk.ets.registry.api.itl.notice.domain.ITLNotificationBlock;
import gov.uk.ets.registry.api.itl.notice.domain.ITLNotificationHistory;
import gov.uk.ets.registry.api.itl.notice.domain.type.NoticeAcquiringAccountMode;
import gov.uk.ets.registry.api.itl.notice.domain.type.NoticeStatus;
import gov.uk.ets.registry.api.itl.notice.domain.type.NoticeType;
import gov.uk.ets.registry.api.itl.notice.repository.NoticeLogHistoryRepository;
import gov.uk.ets.registry.api.itl.notice.repository.NoticeLogRepository;
import gov.uk.ets.registry.api.itl.notice.repository.NoticeUnitBlockRepository;
import gov.uk.ets.registry.api.transaction.domain.UnitBlock;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.EnvironmentalActivity;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.repository.AccountHoldingRepository;
import gov.uk.ets.registry.api.transaction.repository.UnitBlockRepository;
import gov.uk.ets.registry.api.transaction.service.PredefinedAcquiringAccountsProperties;
import gov.uk.ets.registry.api.transaction.service.ProjectService;
import gov.uk.ets.registry.api.transaction.service.TransactionPersistenceService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.ets.lib.commons.kyoto.types.ITLNoticeRequest;
import uk.gov.ets.lib.commons.kyoto.types.UnitBlockIdentifier;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@Transactional
@AllArgsConstructor
public class ITLNoticeService {

    private final NoticeLogHistoryRepository noticeLogHistoryRepository;

    private final NoticeLogRepository noticeLogRepository;

    private final NoticeUnitBlockRepository noticeUnitBlockRepository;

    private final UnitBlockRepository unitBlockRepository;

    /**
     * Predefined acquiring accounts.
     */
    private final PredefinedAcquiringAccountsProperties predefinedAcquiringAccountsProperties;

    /**
     * Transaction persistence service.
     */
    private final TransactionPersistenceService transactionPersistenceService;

    /**
     * Repository for account holdings.
     */
    private final AccountHoldingRepository accountHoldingRepository;

    /**
     * Service for projects.
     */
    private final ProjectService projectService;

    /**
     * Processes an incoming ITL notification.
     *
     * @param request The incoming ITL notification request.
     */
    public void processIncomingNotice(ITLNoticeRequest request) {
        log.debug("Creating notices for request with notification identifier: {}",
                request.getNotificationIdentifier());

        ITLNotification notification =
                noticeLogRepository.findNoticeLogsByNotificationIdentifier(request.getNotificationIdentifier());

        if (notification == null ) {
            notification = new ITLNotification();
            notification.setNotificationIdentifier(request.getNotificationIdentifier());
            notification.setNoticeLogHistories(new HashSet<>());
            notification.setUnitBlockIdentifiers(new HashSet<>());
            notification.setStatus(NoticeStatus.of(request.getNotificationStatus()));
            notification.setType(NoticeType.of(request.getNotificationType()));
            notification = noticeLogRepository.save(notification);
        }

        createNoticeLogHistory(notification, request);
        createNoticeUnitBlocks(notification, request.getUnitBlockIdentifiers());

    }

    private void createNoticeUnitBlocks(ITLNotification noticeLog, UnitBlockIdentifier[] unitBlockIdentifiers) {
        log.debug("Creating notice units for request with notification identifier: {}",
                noticeLog.getNotificationIdentifier());
        if (!ArrayUtils.isEmpty(unitBlockIdentifiers)) {
            Arrays.stream(unitBlockIdentifiers).forEach(unitBlockIdentifier -> {
                ITLNotificationBlock unitBlock = new ITLNotificationBlock();
                unitBlock.setUnitSerialBlockStart(unitBlockIdentifier.getUnitSerialBlockStart());
                unitBlock.setUnitSerialBlockEnd(unitBlockIdentifier.getUnitSerialBlockEnd());
                unitBlock.setOriginatingRegistryCode(unitBlockIdentifier.getOriginatingRegistryCode());
                unitBlock.setNotification(noticeLog);
                noticeUnitBlockRepository.save(unitBlock);
            });
        }
        noticeUnitBlockRepository.flush();
    }

    private void createNoticeLogHistory(ITLNotification noticeLog, ITLNoticeRequest itlNoticeRequest) {
        ITLNotificationHistory noticeLogHistory = new ITLNotificationHistory();
        if(Optional.ofNullable(itlNoticeRequest.getCommitPeriod()).isPresent()) {
            noticeLogHistory.setCommitmentPeriod(CommitmentPeriod.findByCode(itlNoticeRequest.getCommitPeriod()));
        }
        noticeLogHistory.setEnvironmentalActivity(EnvironmentalActivity.of(itlNoticeRequest.getLULUCFActivity() == null ?
                999999999 : itlNoticeRequest.getLULUCFActivity().intValue()));
        if(Optional.ofNullable(itlNoticeRequest.getUnitType()).isPresent()) {
            noticeLogHistory.setUnitType(UnitType.exceptForUkAllowances(itlNoticeRequest.getUnitType()));
        }
        noticeLogHistory.setMessageDate(itlNoticeRequest.getMessageDate().getTime());
        noticeLogHistory.setActionDueDate(itlNoticeRequest.getActionDueDate());
        noticeLogHistory.setProjectNUmber(itlNoticeRequest.getProjectNumber());
        noticeLogHistory.setTargetDate(itlNoticeRequest.getTargetDate());
        noticeLogHistory.setStatus(NoticeStatus.of(itlNoticeRequest.getNotificationStatus()));
        noticeLogHistory.setContent(itlNoticeRequest.getMessageContent());
        noticeLogHistory.setTargetValue(itlNoticeRequest.getTargetValue());
        noticeLogHistory.setType(NoticeType.of(itlNoticeRequest.getNotificationType()));
        noticeLogHistory.setNotification(noticeLog);
        noticeLogHistoryRepository.saveAndFlush(noticeLogHistory);
    }

    /**
     * Retrieves the acquiring account for the transaction initiated for this ITL notification.
     * @param identifier The notification identifier.
     * @param transactionType The transaction type.
     * @return the acquiring account.
     */
    public Optional<AccountSummary> getAcquiringAccount(Long identifier, TransactionType transactionType,
                                                        Integer commitmentPeriod) {
        if (identifier == null) {
            return Optional.empty();
        }
        AccountSummary result;

        ITLNotification notification = noticeLogRepository.findNoticeLogsByNotificationIdentifier(identifier);
        NoticeType type = notification.getType();

        if (type.hasMode(NoticeAcquiringAccountMode.CDM)) {
            result = AccountSummary.parse(predefinedAcquiringAccountsProperties.getAccount(type.getCdmAccount()),
                RegistryAccountType.NONE, AccountStatus.OPEN);

        } else {
            List<ITLNotificationHistory> data = noticeLogRepository.retrieveIncomingNotificationData(identifier);
            ITLNotificationHistory latestData = data.get(0);
            CommitmentPeriod cp = latestData.getCommitmentPeriod();
            if (cp == null) {
                cp = commitmentPeriod != null ? CommitmentPeriod.findByCode(commitmentPeriod) : null;
            }
            if (cp == null) {
                throw new IllegalStateException("The Commitment Period in ITLNoticeService.getAcquiringAccount should not be null");
            }
            UnitType unitType = latestData.getUnitType();
            if (latestData.getUnitType() == null && type.hasMode(NoticeAcquiringAccountMode.VARIABLE_BY_UNIT_TYPE)) {
                List<UnitBlock> itlUnitBlocks = retrieveUnitBlockByITLSerialBlockRange(notification);
                unitType = itlUnitBlocks.get(0).getType();
            }
            AccountType accountType = type.getAcquiringAccountType(unitType, transactionType);
            result = transactionPersistenceService.getAccount(accountType.getRegistryType(),
                                                              accountType.getKyotoType(), List.of(AccountStatus.OPEN, AccountStatus.SOME_TRANSACTIONS_RESTRICTED), cp);
        }

        return Optional.ofNullable(result);
    }

    /**
     * Retrieves the available account units for a specific transaction.
     *
     * @param notificationIdentifier The ITL notification identifier.
     * @param accountIdentifier The account identifier.
     * @param transactionType   The transaction type.
     * @return some units.
     */
    public <T extends TransactionBlockSummary> List<T> getAvailableUnitsForNotification(
        Long notificationIdentifier,
        Long accountIdentifier,
        TransactionType transactionType) {
        List<T> result;

        List<ITLNotificationHistory> data = noticeLogRepository.retrieveIncomingNotificationData(notificationIdentifier);
        if (data.isEmpty()) {
            return List.of();
        }
        Optional<ITLNotificationHistory> noticeOptional =  data.stream().filter(t-> !NoticeType.NOTIFICATION_UPDATE.equals(t.getType())).findFirst();
        if(noticeOptional.isEmpty()) {
        	return List.of();
        }
        ITLNotificationHistory latestData = noticeOptional.get();
        List<UnitType> unitTypes = getUnitTypes(transactionType, latestData);
        String projectNumber = getProjectNumber(transactionType, latestData);
        CommitmentPeriod commitmentPeriod = latestData.getCommitmentPeriod();
        if (TransactionType.Replacement.equals(transactionType) && 
        		(latestData.getProjectNUmber() != null && (latestData.getType().equals(NoticeType.REVERSAL_OF_STORAGE_FOR_CDM_PROJECT) || latestData.getType().equals(NoticeType.NON_SUBMISSION_OF_CERTIFICATION_REPORT_FOR_CDM_PROJECT)) || 
                        latestData.getType().equals(NoticeType.IMPENDING_EXPIRY_OF_TCER_AND_LCER))) {
        	unitTypes = unitTypes.stream().filter(u -> !UnitType.LCER.equals(u) && !UnitType.TCER.equals(u)).collect(Collectors.toList());
            result = (List<T>) accountHoldingRepository.getHoldingsForTransactionReplacement(accountIdentifier,
                    unitTypes, latestData.getProjectNUmber(), UnitType.LCER);
        } else if (StringUtils.isEmpty(projectNumber) && commitmentPeriod == null) {
            result = (List<T>) accountHoldingRepository.getHoldingsForTransaction(accountIdentifier, unitTypes);

        } else if (StringUtils.isEmpty(projectNumber) && commitmentPeriod != null) {
            result = (List<T>) accountHoldingRepository
                .getHoldingsForTransaction(accountIdentifier, unitTypes, commitmentPeriod);

        } else if (!StringUtils.isEmpty(projectNumber) && commitmentPeriod == null) {
            result = (List<T>) accountHoldingRepository
                .getHoldingsForTransaction(accountIdentifier, unitTypes, projectNumber);

        } else {
            result = (List<T>) accountHoldingRepository
                .getHoldingsForTransaction(accountIdentifier, unitTypes, commitmentPeriod, projectNumber);
        }
        populateAdditionalInformation(accountIdentifier, transactionType.getUnitsApplicableCommitmentPeriod(), result, projectNumber);
        return result;
    }

    private String getProjectNumber(TransactionType transactionType, ITLNotificationHistory latestData) {
        String projectNumber = latestData.getProjectNUmber();
        if (TransactionType.Replacement.equals(transactionType) ||
        		TransactionType.ExternalTransfer.equals(transactionType) ||
        		TransactionType.InternalTransfer.equals(transactionType)) {
            // If types 4 & 5 are fulfilled with a Replacement, the project number should not be used for filtering
            // the replacing units. Instead, the project number is used to retrieve the units to be replaced.
            projectNumber = null;
        }
        return projectNumber;
    }

    private List<UnitType> getUnitTypes(TransactionType transactionType, ITLNotificationHistory latestData) {
        List<UnitType> unitTypes = latestData.getType().getUnits();
        UnitType unitType = latestData.getUnitType();
        if (unitType != null && !TransactionType.ExternalTransfer.equals(transactionType)
            && !TransactionType.InternalTransfer.equals(transactionType)) {
            unitTypes = Collections.singletonList(unitType);
        }
        return unitTypes;
    }

    /**
     * Populates additional information, namely the projects & environmental activities where applicable.
     *
     * @param accountIdentifier The account identifier.
     * @param commitmentPeriod  The commitment period.
     * @param blocks            The unit blocks.
     */
    private void populateAdditionalInformation(Long accountIdentifier,
                                               CommitmentPeriod commitmentPeriod,
                                               List<? extends TransactionBlockSummary> blocks,
                                               String projectNumber) {
        for (TransactionBlockSummary block : blocks) {
            if (Boolean.TRUE.equals(block.getType().getRelatedWithProject())) {
                if (!StringUtils.isEmpty(projectNumber)) {
                    block.setProjectNumbers(Collections.singletonList(projectNumber));
                } else {
                    block.setProjectNumbers(
                        projectService.retrieveProjects(accountIdentifier, block.getType(), commitmentPeriod));
                }
            }
            if (Boolean.TRUE.equals(block.getType().getRelatedWithEnvironmentalActivity())) {
                block.setEnvironmentalActivities(projectService
                    .retrieveEnvironmentalActivities(accountIdentifier, block.getType(), commitmentPeriod));
            }
        }
    }

    /**
     * Retrieve the unit blocks based on the serial block range of the ITL notification.
     *
     * @param notification  The relevant ITL notification
     * @return              The requested unit blocks.
     */
    private List<UnitBlock> retrieveUnitBlockByITLSerialBlockRange(ITLNotification notification) {
        ITLNotificationBlock itlNotificationBlock = notification.getUnitBlockIdentifiers().iterator().next();
        if (itlNotificationBlock == null) {
            throw new IllegalStateException("This ITL Notification does not have any unit block available.");
        }
        
        List<UnitBlock> itlUnitBlocks = unitBlockRepository
                .findBySerialBlockRange(itlNotificationBlock.getUnitSerialBlockStart(),
                        itlNotificationBlock.getUnitSerialBlockEnd(),
                        itlNotificationBlock.getOriginatingRegistryCode(),
                        List.of(UnitType.TCER, UnitType.LCER));
        if (itlUnitBlocks.isEmpty()) {
            throw new IllegalStateException("The Registry does not have any unit blocks available for this range.");
        }
        return itlUnitBlocks;
    }

    @Transactional
    public ITLNotification getNotification(Long notificationIdentifier) {
        return noticeLogRepository.findNoticeLogsByNotificationIdentifier(notificationIdentifier);
    }

    @Transactional
    public ITLNotificationHistory getNotificationData(Long notificationIdentifier) {
        List<ITLNotificationHistory> data = noticeLogRepository.retrieveIncomingNotificationData(notificationIdentifier);
        return data.get(0);
    }

    @Transactional
    public List<UnitBlock> getUnitBlocksToBeReplaced(Long notificationIdentifier) {
        List<UnitBlock> result = new ArrayList<>();
        ITLNotification notification = noticeLogRepository.findNoticeLogsByNotificationIdentifier(notificationIdentifier);

        notification.getUnitBlockIdentifiers().forEach(notificationBlock -> {
            List<UnitBlock> unitBlocks = unitBlockRepository.findBySerialBlockRange(
                notificationBlock.getUnitSerialBlockStart(),
                notificationBlock.getUnitSerialBlockEnd(),
                notificationBlock.getOriginatingRegistryCode(),
                Arrays.asList(UnitType.LCER,UnitType.TCER));
            result.addAll(unitBlocks);
        });
        
        return result.stream().filter(t->  Objects.isNull(t.getReplaced()) || Boolean.FALSE.equals(t.getReplaced()))
        .filter(t -> Objects.isNull(t.getReservedForReplacement()))
        .sorted(Comparator.comparing(UnitBlock::getStartBlock).thenComparing(UnitBlock::getOriginatingCountryCode))
        .collect(Collectors.toList());
    }

}
