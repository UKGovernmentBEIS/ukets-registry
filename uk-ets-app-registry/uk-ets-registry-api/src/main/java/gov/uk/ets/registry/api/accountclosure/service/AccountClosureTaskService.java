package gov.uk.ets.registry.api.accountclosure.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AircraftOperator;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.web.model.AccountClosureDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDetailsDTO;
import gov.uk.ets.registry.api.accountclosure.web.model.AccountClosureTaskDetailsDTO;
import gov.uk.ets.registry.api.allocation.service.RequestAllocationService;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim.OnlySeniorOrJuniorCanClaimTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim.SeniorAdminCanClaimTaskInitiatedByAdminRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.CommentMandatoryWhenApproved;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.FourEyesPrincipleRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.OnlyRegistryAdminCanCompleteTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.RegistryAdminCanApproveTaskWhenAccountNotClosedRule;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.error.UkEtsException;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.allocationtable.services.AllocationTableService;
import gov.uk.ets.registry.api.tal.domain.TrustedAccount;
import gov.uk.ets.registry.api.tal.repository.TrustedAccountRepository;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.service.TaskTypeService;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AccountClosureTaskService implements TaskTypeService<AccountClosureTaskDetailsDTO> {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final TrustedAccountRepository trustedAccountRepository;
    private final EventService eventService;
    private final Mapper mapper;
    private final AccountClosureBusinessRulesApplier accountClosureBusinessRulesApplier;
    private final UserService userService;
    private final AllocationTableService allocationTableService;
    private final RequestAllocationService requestAllocationService;

    private static final String REMOVAL_REASON = "account closure";

    @Override
    public Set<RequestType> appliesFor() {
        return Set.of(RequestType.ACCOUNT_CLOSURE_REQUEST);
    }

    @Override
    public AccountClosureTaskDetailsDTO getDetails(TaskDetailsDTO taskDetails) {
        AccountClosureTaskDetailsDTO response = new AccountClosureTaskDetailsDTO(taskDetails);

        AccountClosureDTO accountClosureDTO = mapper
            .convertToPojo(taskDetails.getDifference(), AccountClosureDTO.class);

        AccountDetailsDTO accountDetailsDTO = accountClosureDTO.getAccountDetails();
        if (accountDetailsDTO != null) {
            AccountType accountType = AccountType.get(accountDetailsDTO.getAccountType());
            accountDetailsDTO.setAccountTypeEnum(accountType);
            if (AccountType.OPERATOR_HOLDING_ACCOUNT.equals(accountType)) {
                Optional<Account> accountOptional =  accountRepository.findByAccountIdentifierWithCompliantEntity(Long.valueOf(taskDetails.getAccountNumber()));
                if (accountOptional.isPresent()) {
                    Installation installation = (Installation) Hibernate.unproxy(accountOptional.get().getCompliantEntity());
                    response.setPermitId(installation.getPermitIdentifier());      
                    response.setPendingAllocationTaskExists(allocationTableService.getEntitiesInPendingAllocationTableUploadTask().contains(installation.getIdentifier()) ||
                        requestAllocationService.getEntitiesInPendingAllocationRequestTaskOrJob()
                        .contains(installation.getIdentifier()));
                }
            } else if (AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT.equals(accountType)) {
                Optional<Account> accountOptional =  accountRepository.findByAccountIdentifierWithCompliantEntity(Long.valueOf(taskDetails.getAccountNumber()));
                if (accountOptional.isPresent()) {
                    AircraftOperator aircraftOperator = (AircraftOperator) Hibernate.unproxy(accountOptional.get().getCompliantEntity());
                    response.setMonitoringPlanId(aircraftOperator.getMonitoringPlanIdentifier());    
                    response.setPendingAllocationTaskExists(allocationTableService.getEntitiesInPendingAllocationTableUploadTask().contains(aircraftOperator.getIdentifier()) ||
                        requestAllocationService.getEntitiesInPendingAllocationRequestTaskOrJob()
                        .contains(aircraftOperator.getIdentifier()));
                }
            }
        }
        response.setAccountDetails(accountDetailsDTO);
        response.setClosureComment(accountClosureDTO.getClosureComment());
        response.setAllocationClassification(accountClosureDTO.getAllocationClassification());
        response.setNoActiveAR(accountClosureDTO.isNoActiveAR());
        
        return response;
    }

    @Protected(
            {
                    FourEyesPrincipleRule.class,
                    OnlyRegistryAdminCanCompleteTaskRule.class,
                    RegistryAdminCanApproveTaskWhenAccountNotClosedRule.class,
                    CommentMandatoryWhenApproved.class
            }
    )
    @Transactional
    @Override
    @EmitsGroupNotifications({GroupNotificationType.ACCOUNT_CLOSURE_COMPLETED})
    public TaskCompleteResponse complete(AccountClosureTaskDetailsDTO taskDTO, TaskOutcome taskOutcome,
                                         String comment) {

        String accountFullIdentifier = taskDTO.getAccountDetails().getAccountNumber();
        Account account =
            accountRepository.findByFullIdentifier(accountFullIdentifier).orElseThrow(() -> new UkEtsException(
                String.format("Account with identifier %s not found.", accountFullIdentifier)));

        if (TaskOutcome.APPROVED == taskOutcome) {
            accountClosureBusinessRulesApplier.applyAccountClosureBusinessRules(accountFullIdentifier);

            // handle TAL for closed account
            handleTrustedAccounts(account.getFullIdentifier());
            User currentUser = userService.getCurrentUser();

            // remove ARs and their keycloak role - if any
            accountService.removeAccountArs(account.getIdentifier(), REMOVAL_REASON, currentUser);

            account.setAccountStatus(AccountStatus.CLOSED);
            account.setClosureReason(taskDTO.getClosureComment());
            account.setClosingDate(new Date());

        } else {
            account.setAccountStatus(taskDTO.getAccountDetails().getAccountStatus());
        }

        publishEvent(taskOutcome, accountFullIdentifier);

        return defaultResponseBuilder(taskDTO).build();
    }

    @Protected( {
            OnlySeniorOrJuniorCanClaimTaskRule.class,
            SeniorAdminCanClaimTaskInitiatedByAdminRule.class
    })
    @Override
    public void checkForInvalidClaimantPermissions() {

    }


    /**
     * Remove the closed account from trusted_account.
     *
     * @param fullIdentifier the account full identifier of the account to be closed
     */
    private void handleTrustedAccounts(String fullIdentifier) {
        List<TrustedAccount> trustedAccountsToRemove =
            trustedAccountRepository.findAllByTrustedAccountFullIdentifier(fullIdentifier);
        trustedAccountRepository.deleteAll(trustedAccountsToRemove);
    }

    private void publishEvent(TaskOutcome taskOutcome, String accountFullIdentifier) {
        eventService.createAndPublishEvent(accountFullIdentifier,
            null,
            null,
            EventType.ACCOUNT_TASK_COMPLETED,
            String.format("Account closure request %s", taskOutcome == TaskOutcome.APPROVED ? "approved" : "rejected")
        );
    }
}
