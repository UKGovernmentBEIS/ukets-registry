package gov.uk.ets.registry.api.allocation.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.allocation.data.AllocationOverview;
import gov.uk.ets.registry.api.allocation.data.AllocationSummary;
import gov.uk.ets.registry.api.allocation.type.AllocationCategory;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import gov.uk.ets.registry.api.allocation.web.model.RequestAllocationProposalCompleteResponse;
import gov.uk.ets.registry.api.allocation.web.model.RequestAllocationTaskDetailsDTO;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.features.allocation.rules.SufficientAllocationUnitsRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.assign.OnlySeniorRegistryAdminCanBeAssignedTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim.OnlySeniorAdminCanClaimTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.FourEyesPrincipleRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.OnlySeniorRegistryAdminCanApproveTask;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.service.TaskTypeService;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TaskFileDownloadInfoDTO;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.service.AccountHoldingService;

import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RequestAllocationTaskService implements TaskTypeService<TaskDetailsDTO> {

    private final Mapper mapper;
    private final AccountHoldingService holdingService;
    private final AccountRepository accountRepository;
    private final AllocationJobService allocationJobService;
    private final CronExpressionExtractor cronExpressionExtractor;
    private final UploadedFilesRepository uploadedFilesRepository;

    @Value("${scheduler.allocation.start}")
    private String schedulerAllocationStart;

    @Override
    public Set<RequestType> appliesFor() {
        return Set.of(RequestType.ALLOCATION_REQUEST);
    }

    @Override
    public TaskDetailsDTO getDetails(TaskDetailsDTO taskDetailsDTO) {

        AllocationOverview allocationOverview = mapper.convertToPojo(taskDetailsDTO.getDifference(), AllocationOverview.class);

        Set<Account> accounts = new HashSet<>();

        for (AllocationSummary recipient : allocationOverview.getBeneficiaryRecipients()) {
            switch (recipient.getType()) {
                case NER ->
                        accounts.add(accountRepository.findByRegistryAccountTypeForNonClosedAccounts(RegistryAccountType.UK_NEW_ENTRANTS_RESERVE_ACCOUNT)
                                .orElseThrow(() -> new IllegalArgumentException("Account of type new entrants reserve not found.")));
                case NAT, NAVAT -> accounts.add(
                        (Account) accountRepository.findByRegistryAccountTypeForNonClosedAccounts(RegistryAccountType.UK_ALLOCATION_ACCOUNT)
                                .map(Hibernate::unproxy)
                                .orElseThrow(() -> new IllegalArgumentException("Account of type allocation was not found."))
                );
            }
        }

        RequestAllocationTaskDetailsDTO requestAllocationTaskDetails =
                new RequestAllocationTaskDetailsDTO(taskDetailsDTO);
        requestAllocationTaskDetails.setAllocationOverview(allocationOverview);

            for (Account account : accounts) {
                switch (account.getAccountName()) {
                    case "UK New Entrants Reserve Account" -> {
                        requestAllocationTaskDetails.setNerAccountName(account.getAccountName());
                        requestAllocationTaskDetails.setNerCurrentHoldings(retrieveCurrentHoldings(account.getIdentifier()));
                    }
                    case "UK Allocation Account" -> {
                        requestAllocationTaskDetails.setNatAccountName(account.getAccountName());
                        requestAllocationTaskDetails.setCurrentHoldings(retrieveCurrentHoldings(account.getIdentifier()));
                    }
                }
            }
        return requestAllocationTaskDetails;
    }

    @Override
    @Transactional
    public UploadedFile getRequestedTaskFile(TaskFileDownloadInfoDTO infoDTO) {
        return uploadedFilesRepository.findFirstByTaskRequestId(infoDTO.getTaskRequestId());
    }

    @Protected(
        {
            FourEyesPrincipleRule.class,
            OnlySeniorRegistryAdminCanApproveTask.class,
            SufficientAllocationUnitsRule.class
        })
    @Override
    @Transactional
    public TaskCompleteResponse complete(TaskDetailsDTO taskDTO, TaskOutcome taskOutcome, String comment) {
        if (TaskOutcome.APPROVED == taskOutcome) {
            AllocationOverview allocationOverview = mapper.convertToPojo(taskDTO.getDifference(), AllocationOverview.class);
            AllocationCategory allocationCategory = allocationOverview.getRows()
                .keySet()
                .stream()
                .findFirst()
                .map(AllocationType::getCategory)
                .orElseThrow(IllegalArgumentException::new);
            allocationJobService.scheduleJob(taskDTO.getRequestId(), allocationOverview.getYear(), allocationCategory);
        }
        String[] nextExecutionDateTime = cronExpressionExtractor.extractNextExecutionTime(schedulerAllocationStart)
                                                                .split("\\.");
        return RequestAllocationProposalCompleteResponse.requestAllocationProposalCompleteResponse()
                                                        .requestIdentifier(taskDTO.getRequestId())
                                                        .executionDate(nextExecutionDateTime[0])
                                                        .executionTime(nextExecutionDateTime[1])
                                                        .build();
    }

    @Protected({
        OnlySeniorAdminCanClaimTaskRule.class
    })
    @Override
    public void checkForInvalidClaimantPermissions() {
        // implemented for being able to apply permissions using annotations
    }

    @Protected({
        OnlySeniorRegistryAdminCanBeAssignedTaskRule.class
    })
    @Override
    public void checkForInvalidAssignPermissions() {
        // implemented for being able to apply permissions using annotations
    }

    private Long retrieveCurrentHoldings(Long accountIdentifier) {
        TransactionBlockSummary block = new TransactionBlockSummary();
        block.setType(UnitType.ALLOWANCE);
        return holdingService.getQuantity(accountIdentifier, block);
    }
}
