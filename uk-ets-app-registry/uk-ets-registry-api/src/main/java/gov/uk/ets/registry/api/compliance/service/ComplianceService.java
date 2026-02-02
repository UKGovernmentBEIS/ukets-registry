package gov.uk.ets.registry.api.compliance.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.repository.CompliantEntityRepository;
import gov.uk.ets.registry.api.account.repository.InstallationOwnershipRepository;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.allocation.repository.AllocationEntryRepository;
import gov.uk.ets.registry.api.allocation.service.AllocationCalculationService;
import gov.uk.ets.registry.api.allocation.type.AllocationClassification;
import gov.uk.ets.registry.api.auditevent.web.AuditEventDTO;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.common.error.UkEtsException;
import gov.uk.ets.registry.api.common.exception.BusinessRuleErrorException;
import gov.uk.ets.registry.api.common.exception.NotFoundException;
import gov.uk.ets.registry.api.compliance.domain.ExcludeEmissionsEntry;
import gov.uk.ets.registry.api.compliance.domain.StaticComplianceStatus;
import gov.uk.ets.registry.api.compliance.messaging.ComplianceEventService;
import gov.uk.ets.registry.api.compliance.messaging.events.outgoing.ExclusionEvent;
import gov.uk.ets.registry.api.compliance.messaging.events.outgoing.ExclusionReversalEvent;
import gov.uk.ets.registry.api.compliance.messaging.events.outgoing.RecalculateDynamicStatusEvent;
import gov.uk.ets.registry.api.compliance.repository.ExcludeEmissionsRepository;
import gov.uk.ets.registry.api.compliance.repository.StaticComplianceStatusRepository;
import gov.uk.ets.registry.api.compliance.web.model.*;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.emissionstable.model.EmissionsEntry;
import gov.uk.ets.registry.api.file.upload.emissionstable.repository.EmissionsEntryRepository;
import gov.uk.ets.registry.api.file.upload.emissionstable.services.EmissionsTableService;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.repository.TransactionRepository;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static org.apache.sis.internal.util.StandardDateFormat.UTC;

@Service
@RequiredArgsConstructor
public class ComplianceService {

    public static final String ACCOUNT_EXCLUSION_STATUS_UPDATED = "Exclusion status updated";
    public static final String EXCLUDED = "Excluded";
    private final AccountService accountService;
    private final EventService eventService;
    private final UserService userService;
    private final ComplianceEventService complianceEventService;
    private final ExcludeEmissionsRepository excludeEmissionsRepository;
    private final EmissionsEntryRepository emissionsEntryRepository;
    private final StaticComplianceStatusRepository staticComplianceStatusRepository;
    private final TransactionRepository transactionRepository;
    private final CompliantEntityRepository compliantEntityRepository;
    private final InstallationOwnershipRepository installationOwnershipRepository;
    private final AllocationCalculationService allocationCalculationService;
    private final AllocationEntryRepository allocationEntryRepository;
    private final EmissionsTableService emissionsTableService;

    @Transactional
    public void updateExclusionStatus(Long accountIdentifier, OperatorEmissionsExclusionStatusChangeDTO patch) {
        updateExclusionStatus(accountIdentifier, patch.getYear(), patch.isExcluded(), patch.getReason(), false);
    }

    @Transactional
    public void updateExclusionStatus(Long accountIdentifier, Long year, boolean isExcluded) {
        updateExclusionStatus(accountIdentifier, year, isExcluded, "Exclusion flag updated", true);
    }

    private void updateExclusionStatus(Long accountIdentifier, Long year, boolean isExcluded, String reason, boolean isSystemUpdate) {
        Account account = accountService.getAccount(accountIdentifier);
        if (account == null) {
            throw new UkEtsException(String.format(
                    "Requested update exclusion status for account with identifier:%s which does not exist",
                    accountIdentifier));
        }
        // verify that the exclusion status can be updated for the selected year
        verifySelectedYearIsValid(year, account);

        Optional<Long> emissionsForThisYear = emissionsEntryRepository
            .findAllByCompliantEntityIdAndYear(
                account.getCompliantEntity().getIdentifier(), year)
            .stream()
            .max(Comparator.comparing(EmissionsEntry::getUploadDate))
            .map(EmissionsEntry::getEmissions);

        String urid = isSystemUpdate ? null : userService.getCurrentUser().getUrid();
        String actorId = urid == null ? "system" : urid;

        if (emissionsForThisYear.isPresent()) {
            // Update emissions to null
            EmissionsEntry emissionsEntry = createNullEmissionsEntry(account.getCompliantEntity().getIdentifier(), year);
            emissionsEntryRepository.save(emissionsEntry);
            emissionsTableService.publishUpdateOfVerifiedEmissionsEvent(emissionsEntry, new Date(), actorId);
        }

        ExcludeEmissionsEntry existingEntry = excludeEmissionsRepository
            .findByCompliantEntityIdAndYear(
                account.getCompliantEntity().getIdentifier(), year);
        if (existingEntry != null
            && existingEntry.isExcluded() == isExcluded) {
            throw new BusinessRuleErrorException(
                ErrorBody.from("You must select the other option"));
        }

        if (existingEntry != null) {
            existingEntry.setExcluded(isExcluded);
            existingEntry.setLastUpdated(new Date());
            excludeEmissionsRepository.save(existingEntry);
        } else {
            ExcludeEmissionsEntry entry = new ExcludeEmissionsEntry();
            entry.setCompliantEntityId(
                account.getCompliantEntity().getIdentifier());
            entry.setYear(year);
            entry.setExcluded(isExcluded);
            entry.setLastUpdated(new Date());
            excludeEmissionsRepository.save(entry);
        }

        String allocationClassification = allocationCalculationService.calculateAllocationClassification(
            LocalDate.now().getYear(), account.getCompliantEntity().getId()).getAllocationClassification();

        Optional.ofNullable(allocationClassification)
            .map(AllocationClassification::valueOf)
            .ifPresent(classification -> allocationEntryRepository.updateAllocationClassification(
                account.getCompliantEntity().getId(), classification.name()));

        // record event in account history
        eventService.createAndPublishEvent(accountIdentifier.toString(),
            urid, "",
            EventType.ACCOUNT_EXCLUSION_STATUS_UPDATED,
            ACCOUNT_EXCLUSION_STATUS_UPDATED);
        // publish compliance event
        if (isExcluded) {
            publishAccountExclusionEvent(
                account.getCompliantEntity().getIdentifier(), year, actorId, reason, emissionsForThisYear.orElse(null));
        } else {
            publishAccountExclusionReversalEvent(account.getCompliantEntity().getIdentifier(), year, actorId, reason);
        }
    }

    private EmissionsEntry createNullEmissionsEntry(Long compliantEntityId, Long year) {
        EmissionsEntry emissionsEntry = new EmissionsEntry();
        emissionsEntry.setCompliantEntityId(compliantEntityId);
        emissionsEntry.setYear(year);
        emissionsEntry.setFilename("N/A");
        emissionsEntry.setUploadDate(LocalDateTime.now());
        return emissionsEntry;
    }

    /**
     * Creates a overall picture of emissions , excluded years and missing
     * entries for the period (i.e. start & end year) that the compliant entity
     * must report.
     *
     * @param compliantEntityId
     * @param currentYear
     * @return
     */
    public VerifiedEmissionsResultDTO getReportableVerifiedEmissions(Long compliantEntityId, int currentYear) {
        CompliantEntity compliantEntity =
            compliantEntityRepository.findByIdentifier(compliantEntityId).orElseThrow(IllegalArgumentException::new);

        Integer[] reportingPeriod = getReportingPeriod(compliantEntity.getStartYear(), compliantEntity.getEndYear(),
            currentYear);

        Map<Long, VerifiedEmissionsDTO> reportableEmissions = IntStream
            .rangeClosed(reportingPeriod[0], reportingPeriod[1])
            .boxed()
            .map(y -> new VerifiedEmissionsDTO(compliantEntityId, y.longValue(),
                null, null))
            .collect(toMap(VerifiedEmissionsDTO::getYear, t -> t));

        if (reportableEmissions.isEmpty()) {
            return new VerifiedEmissionsResultDTO(Collections.emptyList(), compliantEntity.getEndYear());
        }

        // Merge the reportableEmissions map with the values from emissions
        // entry
        mergeEmissionsEntries(compliantEntityId, reportableEmissions);

        // Merge the reportableEmissions map with the excluded emissions entries
        mergeExcludedEntries(compliantEntityId, reportableEmissions);

        int finalReportingEndYear = reportingPeriod[1];
        List<VerifiedEmissionsDTO> verifiedEmissions = reportableEmissions.values().stream()
            .filter(e -> e.getYear() <= finalReportingEndYear)
            .sorted(Comparator.comparing(VerifiedEmissionsDTO::getYear))
            .collect(toList());

        return new VerifiedEmissionsResultDTO(verifiedEmissions, compliantEntity.getEndYear());
    }

    /**
     * Fetch the static compliance statuses computed at 30 April each year.
     *
     * @param compliantEntityId
     * @return a list of ComplianceStatuses per year
     */
    public ComplianceStatusHistoryResultDTO getComplianceStatusHistory(
        Long compliantEntityId) {

        CompliantEntity compliantEntity =
            compliantEntityRepository.findByIdentifier(compliantEntityId).orElseThrow(() -> new NotFoundException("Compliant " +
                "entity id does not exist"));

        long startYear = compliantEntity.getStartYear().longValue();
        Integer endYear = compliantEntity.getEndYear();
        long currentYear = LocalDate.now().getYear();

        List<StaticComplianceStatus> staticComplianceStatusList;

        if (currentYear <= startYear) {
            return new ComplianceStatusHistoryResultDTO(null, endYear);
        } else if (endYear == null || currentYear <= endYear + 1) {
            staticComplianceStatusList =
                staticComplianceStatusRepository.findByCompliantEntityIdentifierAndYearBetween(compliantEntityId,
                    startYear, currentYear - 1);
        } else {
            staticComplianceStatusList =
                staticComplianceStatusRepository.findByCompliantEntityIdentifierAndYearGreaterThanEqual(compliantEntityId,
                    startYear);
        }

        List<ComplianceStatusHistoryDTO> statusHistoryDTOList = staticComplianceStatusList.stream()
            .map(this::toComplianceHistoryDTO)
            .sorted(Comparator.comparing(ComplianceStatusHistoryDTO::getYear))
            .collect(toList());
        return new ComplianceStatusHistoryResultDTO(statusHistoryDTOList, endYear);
    }

    public ComplianceOverviewDTO getComplianceOverview(
        @NotNull Long accountIdentifier) {
        ComplianceOverviewDTO result = new ComplianceOverviewDTO();
        Account account = accountService.getAccount(accountIdentifier);
        result.setCurrentComplianceStatus(account.getComplianceStatus());

        List<Long> totalVerifiedEmissions = emissionsEntryRepository
                .findTotalVerifiedEmissionsByAccountIdentifier(accountIdentifier)
                .stream().filter(Objects::nonNull).collect(toList());

        result.setTotalVerifiedEmissions(!totalVerifiedEmissions.isEmpty() ?
                totalVerifiedEmissions.stream().collect(Collectors.summingLong(t -> t)) :
                null);

        // Query the transactions to find the total amounts
        Set<Long> accountIdentifiers = Stream.of(accountIdentifier)
            .collect(toSet());
        if (RegistryAccountType.OPERATOR_HOLDING_ACCOUNT
            .equals(account.getRegistryAccountType())) {
            accountIdentifiers.addAll(installationOwnershipRepository
                .findByInstallation((Installation) Hibernate
                    .unproxy(account.getCompliantEntity()))
                .stream().map(t -> t.getAccount().getIdentifier())
                .collect(toList()));
        }

        Long surrendered = accountIdentifiers.stream()
            .map(t -> transactionRepository
                .findByTransferringAccount_AccountIdentifierAndTypeAndStatus(
                    t, TransactionType.SurrenderAllowances,
                    TransactionStatus.COMPLETED))
            .flatMap(List::stream)
            .collect(Collectors.summingLong(Transaction::getQuantity));

        Long surrenderedReversed = accountIdentifiers.stream()
            .map(t -> transactionRepository
                .findByAcquiringAccount_AccountIdentifierAndTypeAndStatus(
                    t,
                    TransactionType.ReverseSurrenderAllowances,
                    TransactionStatus.COMPLETED))
            .flatMap(List::stream)
            .collect(Collectors.summingLong(Transaction::getQuantity));

        if (surrendered == 0 && surrenderedReversed == 0) {
            //We need to indicate the absence , see UKETS-6781
            result.setTotalNetSurrenders(null);
        } else {
            result.setTotalNetSurrenders(surrendered - surrenderedReversed);            
        }

        return result;
    }

    public List<ExcludeEmissionsEntry> findExcludedEntriesForCompliantEntityForYearBefore(
        Long compliantEntityId, long year) {
        return excludeEmissionsRepository
            .findExcludedEntriesBeforeYear(compliantEntityId, year);
    }

    public List<ExcludeEmissionsEntry> findExcludedEntriesForCompliantEntityForYearAfter(
        Long compliantEntityId, long year) {
        return excludeEmissionsRepository
            .findExcludedEntriesAfterYear(compliantEntityId, year);
    }

    /**
     * Fetch events related to compliance (assume only system events).
     *
     * @param compliantEntityId
     * @return
     */
    public List<AuditEventDTO> getComplianceEventsHistory(
        Long compliantEntityId) {

        List<AuditEventDTO> eventsByDomainId = eventService
            .getSystemEventsByDomainIdOrderByCreationDateDesc(
                compliantEntityId.toString(),
                List.of(CompliantEntity.class.getName()));

        return eventsByDomainId.stream()
            .sorted(
                Comparator.comparing(AuditEventDTO::getCreationDate).reversed())
            .collect(toList());
    }

    private void verifySelectedYearIsValid(Long year, Account account) {
        int currentYear = LocalDate.now().getYear();
        CompliantEntity compliantEntity = compliantEntityRepository.findByIdentifier(
            account.getCompliantEntity().getIdentifier()).orElseThrow(IllegalArgumentException::new);
        boolean endYearExists = compliantEntity.getEndYear() != null;
        if (year < compliantEntity.getStartYear() ||
            endYearExists && year > compliantEntity.getEndYear()) {
            throw new BusinessRuleErrorException(ErrorBody.from(
                "The selected year should be within the reporting period"));
        } else if (year > currentYear &&
            (endYearExists && year <= compliantEntity.getEndYear() || !endYearExists)) {
            throw new BusinessRuleErrorException(ErrorBody.from(
                "The exclusion status cannot be updated for future years"));
        }
    }

    private void publishAccountExclusionEvent(Long compliantEntityId, Long year, String urid, String reason, Long emissions) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of(UTC));
        ExclusionEvent event = ExclusionEvent.builder()
            .compliantEntityId(compliantEntityId).actorId(urid)
            .dateTriggered(now).dateRequested(now).year(year.intValue())
            .reason(reason).emissions(emissions)
            .build();
        complianceEventService.processEvent(event);
    }

    private void publishAccountExclusionReversalEvent(Long compliantEntityId,
                                                      Long year,
                                                      String urid,
                                                      String reason) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of(UTC));

        ExclusionReversalEvent event = ExclusionReversalEvent.builder()
            .compliantEntityId(compliantEntityId).actorId(urid)
            .dateTriggered(now).dateRequested(now).year(year.intValue())
            .reason(reason)
            .build();
        complianceEventService.processEvent(event);
    }

    private void mergeExcludedEntries(Long compliantEntityId,
                                      Map<Long, VerifiedEmissionsDTO> reportableEmissions) {
        excludeEmissionsRepository.findByCompliantEntityId(compliantEntityId)
            .stream()
            .filter(ExcludeEmissionsEntry::isExcluded)
            .map(this::toVerifiedEmissionsDTO)
            .collect(toMap(VerifiedEmissionsDTO::getYear, t -> t))
            .forEach((key, value) -> reportableEmissions.merge(key, value,
                (oldVal, newVal) -> newVal));
    }

    private void mergeEmissionsEntries(Long compliantEntityId,
                                       Map<Long, VerifiedEmissionsDTO> reportableEmissions) {
        emissionsEntryRepository
            .findLatestByCompliantEntityIdentifier(compliantEntityId).stream()
            .collect(toMap(VerifiedEmissionsDTO::getYear, t -> t))
            .forEach((key, value) -> reportableEmissions.merge(key, value,
                (oldVal, newVal) -> newVal));
    }

    private VerifiedEmissionsDTO toVerifiedEmissionsDTO(
        ExcludeEmissionsEntry excluded) {
        if (excluded.isExcluded()) {
            return new VerifiedEmissionsDTO(excluded.getCompliantEntityId(),
                excluded.getYear(), EXCLUDED, LocalDateTime.ofInstant(
                excluded.getLastUpdated().toInstant(), ZoneId.of(UTC)));
        }
        return new VerifiedEmissionsDTO(excluded.getCompliantEntityId(),
            excluded.getYear(), null, LocalDateTime.ofInstant(
            excluded.getLastUpdated().toInstant(), ZoneId.of(UTC)));

    }

    private ComplianceStatusHistoryDTO toComplianceHistoryDTO(
        StaticComplianceStatus status) {
        return new ComplianceStatusHistoryDTO(status.getYear(), status.getComplianceStatus());
    }

    private Integer[] getReportingPeriod(Integer fyve,  Integer lyve, int currentYear) {
        //by default no year is displayed
        int reportingStartYear = 1;
        int reportingEndYear = -1;

        if (fyve.equals(lyve) && currentYear >= fyve) {
            reportingStartYear = fyve;
            reportingEndYear = fyve;
        }

        if (lyve == null && currentYear >= fyve) {
            reportingStartYear = fyve;
            reportingEndYear = currentYear;
        }

        if (lyve != null && fyve < lyve) {
            if (currentYear >= fyve && currentYear <= lyve) {
                reportingStartYear = fyve;
                reportingEndYear = currentYear;
            } else if (currentYear > lyve) {
                reportingStartYear = fyve;
                reportingEndYear = lyve;
            }
        }

        return new Integer[]{reportingStartYear, reportingEndYear};
    }

    /**
     * Generates and process a RecalculateDynamicStatusEvent for all compliant entities.
     */
    @Transactional
    public void recalculateDynamicStatus() {
        compliantEntityRepository
        .findAll()
        .stream()
        .map(this::toRecalculateDynamicStatusEvent)
        .forEach(complianceEventService::processEvent);
    }
    
    private RecalculateDynamicStatusEvent toRecalculateDynamicStatusEvent(CompliantEntity compliantEntity) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of(UTC));
        return RecalculateDynamicStatusEvent.builder()
            .actorId("system")
            .compliantEntityId(compliantEntity.getIdentifier())
            .dateTriggered(now)
            .dateRequested(now)
            .build();
    }
}
