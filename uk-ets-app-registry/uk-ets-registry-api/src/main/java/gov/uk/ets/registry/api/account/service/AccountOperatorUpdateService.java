package gov.uk.ets.registry.api.account.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AircraftOperator;
import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.messaging.compliance.UpdateFirstYearOfVerifiedEmissionsEvent;
import gov.uk.ets.registry.api.account.messaging.compliance.UpdateLastYearOfVerifiedEmissionsEvent;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.web.model.AccountOperatorDetailsUpdateDTO;
import gov.uk.ets.registry.api.account.web.model.InstallationOrAircraftOperatorDTO;
import gov.uk.ets.registry.api.common.ConversionService;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.error.UkEtsException;
import gov.uk.ets.registry.api.common.model.services.PersistenceService;
import gov.uk.ets.registry.api.compliance.domain.ExcludeEmissionsEntry;
import gov.uk.ets.registry.api.compliance.messaging.ComplianceEventService;
import gov.uk.ets.registry.api.compliance.service.ComplianceService;
import gov.uk.ets.registry.api.file.upload.emissionstable.model.EmissionsEntry;
import gov.uk.ets.registry.api.file.upload.emissionstable.services.EmissionsTableService;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.sis.internal.util.StandardDateFormat;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.time.ZoneOffset.UTC;

@Service
@RequiredArgsConstructor
public class AccountOperatorUpdateService {

    private final EmissionsTableService emissionsTableService;
    private final AccountRepository accountRepository;
    private final PersistenceService persistenceService;
    private final UserService userService;
    private final TaskEventService taskEventService;
    private final ComplianceEventService complianceEventService;
    private final ConversionService conversionService;
    private final ComplianceService complianceService;
    private final Mapper mapper;

    @Transactional
    public Long submitAccountOperatorUpdateRequest(Long accountIdentifier, AccountOperatorDetailsUpdateDTO dto) {
        Optional<Account> account = accountRepository.findByIdentifier(accountIdentifier);
        if (account.isEmpty()) {
            throw new UkEtsException(String
                .format("Requested update account operator details for account with identifier:%s which does not exist",
                    accountIdentifier));
        }

        Account actualAccount = account.get();

        validateAccountOperatorUpdateRequest(actualAccount, dto.getDiff());

        Task task = new Task();
        task.setRequestId(persistenceService.getNextBusinessIdentifier(Task.class));
        User currentUser = userService.getCurrentUser();
        task.setInitiatedBy(currentUser);
        task.setInitiatedDate(new Date());
        task.setStatus(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
        task.setAccount(actualAccount);
        if (dto.getCurrent().getType().equals("AIRCRAFT_OPERATOR")) {
            task.setType(RequestType.AIRCRAFT_OPERATOR_UPDATE_REQUEST);
        } else {
            task.setType(RequestType.INSTALLATION_OPERATOR_UPDATE_REQUEST);
        }
        task.setDifference(mapper.convertToJson(dto.getDiff()));
        task.setBefore(mapper.convertToJson(dto.getCurrent()));

        persistenceService.save(task);

        taskEventService.createAndPublishTaskAndAccountRequestEvent(task, currentUser.getUrid());

        return task.getRequestId();
    }

    /**
     * Updates the Operator (Installation or Aircraft) details.
     *
     * @param updatedValues     The Operator updated info
     * @param accountIdentifier The account identifier related with this operotor
     * @param type              The task type (Aircraft or Installation)
     */
    @Transactional
    public void updateOperator(InstallationOrAircraftOperatorDTO updatedValues, Long accountIdentifier,
                               RequestType type, Account account) {

        CompliantEntity compliantEntity = account.getCompliantEntity();

        validateAccountOperatorUpdateRequest(account, updatedValues);

        if (type == RequestType.INSTALLATION_OPERATOR_UPDATE_REQUEST) {
            Installation installation = (Installation) Hibernate.unproxy(compliantEntity);
            updateInstallationInfo(updatedValues, installation);
            updateCommonOperatorInfo(updatedValues, installation);
            persistenceService.save(installation);
        } else if (type == RequestType.AIRCRAFT_OPERATOR_UPDATE_REQUEST) {
            AircraftOperator aircraftOperator = (AircraftOperator) Hibernate.unproxy(compliantEntity);
            if (updatedValues.getMonitoringPlan() != null && updatedValues.getMonitoringPlan().getId() != null) {
                aircraftOperator.setMonitoringPlanIdentifier(updatedValues.getMonitoringPlan().getId());
            }
            updateCommonOperatorInfo(updatedValues, aircraftOperator);
            persistenceService.save(aircraftOperator);
        } else {
            throw new IllegalStateException("Unsupported Update Operator Type " + type + ".");
        }
    }

    public void sendComplianceEvents(InstallationOrAircraftOperatorDTO dto, String actorId,
                                     Long compliantEntityId, Date taskCompletionDate) {
        Integer firstYear = dto.getFirstYear();
        Integer lastYear = dto.getLastYear();

        if (firstYear != null) {
            complianceEventService.processEvent(UpdateFirstYearOfVerifiedEmissionsEvent.builder()
                .firstYearOfVerifiedEmissions(firstYear)
                .actorId(actorId)
                .compliantEntityId(compliantEntityId)
                .dateTriggered(LocalDateTime.now(ZoneId.of(StandardDateFormat.UTC)))
                .dateRequested(LocalDateTime.ofInstant(taskCompletionDate.toInstant(), UTC))
                .build());
        }

        if (Boolean.TRUE.equals(dto.getLastYearChanged())) {
            complianceEventService.processEvent(UpdateLastYearOfVerifiedEmissionsEvent.builder()
                .lastYearOfVerifiedEmissions(lastYear)
                .actorId(actorId)
                .compliantEntityId(compliantEntityId)
                .dateTriggered(LocalDateTime.now(ZoneId.of(StandardDateFormat.UTC)))
                .dateRequested(LocalDateTime.ofInstant(taskCompletionDate.toInstant(), UTC))
                .build());
        }
    }

    private void validateAccountOperatorUpdateRequest(Account account, InstallationOrAircraftOperatorDTO dto) {

        Integer firstYear = dto.getFirstYear();
        Integer lastYear = dto.getLastYear();

        if (firstYear != null) {
            validateFirstYear(account, firstYear);
        }

        if (lastYear != null) {
            validateLastYear(account, account.getCompliantEntity().getStartYear(), firstYear, lastYear);
        }
    }

    private void validateFirstYear(Account account, Integer firstYear) {
        if (firstYear < 2021) {
            throw new IllegalStateException("First year of Verified Emission Submission cannot be before 2021");
        }
        
        //Key Year , Value Emission
        Map<Long,Optional<EmissionsEntry>> emissionsPerYear = emissionsTableService
            .findByCompliantEntityIdAndYearBefore(account.getCompliantEntity().getIdentifier(),
                firstYear.longValue())
            .stream()
            .collect(Collectors.groupingBy(EmissionsEntry::getYear,Collectors.maxBy(Comparator.comparing(EmissionsEntry::getUploadDate))));
        
        Map<Long,Optional<ExcludeEmissionsEntry>> excludedEmissionsPerYear = complianceService
            .findExcludedEntriesForCompliantEntityForYearBefore(account.getCompliantEntity().getIdentifier(),
                firstYear.longValue())
            .stream()
            .collect(Collectors.groupingBy(ExcludeEmissionsEntry::getYear,Collectors.maxBy(Comparator.comparing(ExcludeEmissionsEntry::getLastUpdated))));
        
       boolean emissionsHaveBeenUploadedForYearsBefore = emissionsPerYear
           .values()
           .stream()
           .filter(Optional::isPresent)
           .map(Optional::get)
           .map(EmissionsEntry::getEmissions)
           .filter(Objects::nonNull)
           .filter( t -> t >= 0)
           .count() > 0;
           
       boolean emissionsHaveBeenExcludedForYearsBefore = excludedEmissionsPerYear
           .values()
           .stream()
           .filter(Optional::isPresent)
           .count() > 0;
       
        if ( emissionsHaveBeenUploadedForYearsBefore ||  emissionsHaveBeenExcludedForYearsBefore) {
            throw new IllegalStateException(
                "First year of Verified Emissions Submission cannot be set to a later date " +
                    "if emissions (zero, positive integer or excluded) have been reported for the years " +
                    "that will be excluded after the update.");
        }
    }

    private void validateLastYear(Account account, Integer previousFirstYear, Integer firstYear,
                                  Integer lastYear) {
        if (lastYear < 2021) {
            throw new IllegalStateException("\"Last year of verified emission submission\" must be > = 2021");
        }

        // we need to compare with either the changed first year or the current first year (in case the user has also
        // changed the first year in this request)
        Integer currentFirstYear = firstYear != null ? firstYear : previousFirstYear;
        if (lastYear < currentFirstYear) {
            throw new IllegalStateException(
                "\"Last year of verified emission submission\" must be > = \"First year of verified emission submission\"");

        }

        List<EmissionsEntry> emissions = emissionsTableService
            .findNonEmptyEntriesForCompliantEntityForYearAfter(account.getCompliantEntity().getIdentifier(),
                lastYear.longValue());

        List<ExcludeEmissionsEntry> excludedEmissions = complianceService
            .findExcludedEntriesForCompliantEntityForYearAfter(account.getCompliantEntity().getIdentifier(),
                lastYear.longValue());

        if (emissions.size() > 0 || excludedEmissions.size() > 0) {
            throw new IllegalStateException(
                "Last Year of Verified Emission Submission cannot be set to an earlier date " +
                    "if emissions (zero, positive integer or excluded) have been reported for the years " +
                    "that will be excluded after the update. ");
        }
    }


    private void updateInstallationInfo(InstallationOrAircraftOperatorDTO updatedValues, Installation installation) {
        if (updatedValues.getName() != null) {
            installation.setInstallationName(updatedValues.getName());
        }
        if (updatedValues.getActivityType() != null) {
            installation.setActivityType(updatedValues.getActivityType().name());
        }
        if (updatedValues.getPermit() != null) {
            if (updatedValues.getPermit().getId() != null) {
                installation.setPermitIdentifier(updatedValues.getPermit().getId());
            }
        }
    }

    private void updateCommonOperatorInfo(InstallationOrAircraftOperatorDTO updatedValues,
                                          CompliantEntity compliantEntity) {
        if (updatedValues.getChangedRegulator() != null) {
            compliantEntity.setRegulator(updatedValues.getChangedRegulator());
        }
        if (updatedValues.getFirstYear() != null) {
            compliantEntity.setStartYear(updatedValues.getFirstYear());
        }
        if (updatedValues.getLastYearChanged() != null && updatedValues.getLastYearChanged()) {
            compliantEntity.setEndYear(updatedValues.getLastYear());
        }
    }


}
