package gov.uk.ets.registry.api.tal.service;

import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.assign.SeniorAdminCanByAssigneeOfTaskInitiatedByAdminRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim.CanBeClaimedByEnrolledAROrSeniorRegistryAdminRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim.JuniorAdminCannotClaimTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim.SeniorAdminCanClaimTaskInitiatedByAdminRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.ARsCanApproveTaskWhenAccountHasSpecificStatusRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.ARsCanCompleteTaskNotInitiatedByAdministratorsRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.ARsCanCompleteTaskOnlyForAccountsWithApproveOrInitiateAndApproveAccessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.CanBeCompletedByEnrolledAROrSeniorRegistryAdminRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.FourEyesPrincipleRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.RegistryAdminCanApproveTaskWhenAccountNotClosedOrPendingClosureRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.SeniorAdminCanCompleteTaskInitiatedByAdminRule;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.error.UkEtsException;
import gov.uk.ets.registry.api.tal.domain.TrustedAccount;
import gov.uk.ets.registry.api.tal.domain.TrustedAccountTaskDifference;
import gov.uk.ets.registry.api.tal.domain.types.TrustedAccountStatus;
import gov.uk.ets.registry.api.tal.repository.TrustedAccountRepository;
import gov.uk.ets.registry.api.tal.web.model.TrustedAccountDTO;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.service.TaskTypeService;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TrustedAccountTaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.data.AccountInfo;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.transaction.service.TransactionDelayService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
@RequiredArgsConstructor
public class TrustedAccountTaskService implements TaskTypeService<TrustedAccountTaskDetailsDTO> {

    private final AccountService accountService;
    private final TrustedAccountConversionService trustedAccountConversionService;
    private final TrustedAccountRepository trustedAccountRepository;
    private final TransactionDelayService transactionDelayService;
    private final TrustedAccountCandidateRuleApplier ruleApplier;
    private final Mapper mapper;

    @Override
    public Set<RequestType> appliesFor() {
        return Set.of(RequestType.DELETE_TRUSTED_ACCOUNT_REQUEST, RequestType.ADD_TRUSTED_ACCOUNT_REQUEST);
    }

    @Override
    public TrustedAccountTaskDetailsDTO getDetails(TaskDetailsDTO taskDetailsDTO) {
        TrustedAccountTaskDetailsDTO result = new TrustedAccountTaskDetailsDTO(taskDetailsDTO);
        AccountInfo accountInfo = accountService.getAccountInfo(Long.valueOf(taskDetailsDTO.getAccountNumber()));
        result.setAccountInfo(accountInfo);
        result.setTrustedAccounts(extractTrustedAccountsDTO(result));
        return result;
    }


    @Protected(
        {
            FourEyesPrincipleRule.class,
            ARsCanCompleteTaskNotInitiatedByAdministratorsRule.class,
            ARsCanApproveTaskWhenAccountHasSpecificStatusRule.class,
            RegistryAdminCanApproveTaskWhenAccountNotClosedOrPendingClosureRule.class,
            SeniorAdminCanCompleteTaskInitiatedByAdminRule.class,
            ARsCanCompleteTaskOnlyForAccountsWithApproveOrInitiateAndApproveAccessRule.class,
            CanBeCompletedByEnrolledAROrSeniorRegistryAdminRule.class,
        }
    )
    @Transactional
    @Override
    public TaskCompleteResponse complete(TrustedAccountTaskDetailsDTO taskDetailsDTO,
                                         TaskOutcome taskOutcome,
                                         String comment) {
        List<TrustedAccount> trustedAccounts = extractTrustedAccountEntities(taskDetailsDTO);
        if (RequestType.ADD_TRUSTED_ACCOUNT_REQUEST.equals(taskDetailsDTO.getTaskType())) {
            if (trustedAccounts.size() == 1) {
                addAccountToTAL(trustedAccounts.get(0), taskOutcome);
            } else if (taskOutcome == TaskOutcome.APPROVED || trustedAccounts.size() > 1) {
                throw new UkEtsException("Expecting exactly one account");
            }
        } else if (RequestType.DELETE_TRUSTED_ACCOUNT_REQUEST.equals(taskDetailsDTO.getTaskType())) {
            removeAccountsFromTAL(trustedAccounts, taskOutcome);
        } else {
            throw new UkEtsException(String.format("Unsupported task type: %s", taskDetailsDTO.getTaskType()));
        }
        return defaultResponseBuilder(taskDetailsDTO).build();
    }

    @Protected( {
        SeniorAdminCanByAssigneeOfTaskInitiatedByAdminRule.class
    })
    @Override
    public void checkForInvalidAssignPermissions() {
        // implemented for being able to apply permissions using annotations
    }

    @Protected( {
        JuniorAdminCannotClaimTaskRule.class,
        SeniorAdminCanClaimTaskInitiatedByAdminRule.class,
        CanBeClaimedByEnrolledAROrSeniorRegistryAdminRule.class
    })
    @Override
    public void checkForInvalidClaimantPermissions() {
        // implemented for being able to apply permissions using annotations
    }

    /**
     * Extracts trusted accounts dtos from task.
     *
     * @param trustedAccountTaskDetailsDTO a taskDTO for TAL operations.
     * @return a list of trusted accounts
     */
    private List<TrustedAccountDTO> extractTrustedAccountsDTO(
        TrustedAccountTaskDetailsDTO trustedAccountTaskDetailsDTO) {
        List<TrustedAccount> trustedAccounts = extractTrustedAccountEntities(trustedAccountTaskDetailsDTO);
        return trustedAccounts.stream().map(trustedAccountConversionService::convertTrustedAccount)
            .collect(Collectors.toList());
    }

    /**
     * Extracts trusted accounts entities from task.
     *
     * @param trustedAccountTaskDetailsDTO a taskDTO for TAL operations.
     * @return a list of trusted accounts entities
     */
    private List<TrustedAccount> extractTrustedAccountEntities(
        TrustedAccountTaskDetailsDTO trustedAccountTaskDetailsDTO) {
        TrustedAccountTaskDifference trustedAccountTaskDifference =
            mapper.convertToPojo(trustedAccountTaskDetailsDTO.getDifference(),
                TrustedAccountTaskDifference.class);
        return trustedAccountRepository.findByIdIn(trustedAccountTaskDifference.getIds());
    }

    /**
     * Completes a {@link RequestType#ADD_TRUSTED_ACCOUNT_REQUEST}
     * when {@link TaskOutcome#APPROVED} set status to {@link TrustedAccountStatus#PENDING_ACTIVATION}
     * and calculates the activation date so the {@link TrustedAccountListScheduler} can take over
     * when {@link TaskOutcome#REJECTED} set status to {@link TrustedAccountStatus#REJECTED} .
     *
     * @param trustedAccount the account to add
     * @param taskOutcome    the task outcome
     */
    private void addAccountToTAL(TrustedAccount trustedAccount, TaskOutcome taskOutcome) {
        if (TaskOutcome.APPROVED.equals(taskOutcome)) {
            ruleApplier.applyTalAccountCandidateBusinessRules(trustedAccount.getTrustedAccountFullIdentifier());
            trustedAccount.setStatus(TrustedAccountStatus.PENDING_ACTIVATION);
            trustedAccount.setActivationDate(transactionDelayService.calculateTrustedAccountListDelay());
        } else {
            trustedAccount.setStatus(TrustedAccountStatus.REJECTED);
        }
    }

    /**
     * Complete a {@link RequestType#DELETE_TRUSTED_ACCOUNT_REQUEST}
     * when {@link TaskOutcome#APPROVED} set status to {@link TrustedAccountStatus#REJECTED}
     * when {@link TaskOutcome#REJECTED} set status to the previous state.
     *
     * @param trustedAccounts the trusted accounts to be removed
     * @param taskOutcome     the outcome
     */
    private void removeAccountsFromTAL(List<TrustedAccount> trustedAccounts, TaskOutcome taskOutcome) {
        if (TaskOutcome.APPROVED.equals(taskOutcome)) {
            trustedAccounts.forEach(trustedAccount -> trustedAccount.setStatus(TrustedAccountStatus.REJECTED));
        } else {
            // Handling multiple removal tasks
            trustedAccounts.stream()
                .filter(trustedAccount -> trustedAccount.getStatus() == TrustedAccountStatus.PENDING_REMOVAL_APPROVAL)
                .forEach(trustedAccount -> {
                    LocalDateTime activationDate = trustedAccount.getActivationDate();
                    if (Objects.nonNull(activationDate) && activationDate.isAfter(LocalDateTime.now())) {
                        trustedAccount.setStatus(TrustedAccountStatus.PENDING_ACTIVATION);
                    } else {
                        trustedAccount.setStatus(TrustedAccountStatus.ACTIVE);
                    }
                });
        }
    }
}
