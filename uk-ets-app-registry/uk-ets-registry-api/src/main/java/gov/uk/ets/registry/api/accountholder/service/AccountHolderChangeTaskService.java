package gov.uk.ets.registry.api.accountholder.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.AccountHolderRepresentative;
import gov.uk.ets.registry.api.account.domain.AccountOwnership;
import gov.uk.ets.registry.api.account.domain.types.AccountOwnershipStatus;
import gov.uk.ets.registry.api.account.repository.AccountHolderRepository;
import gov.uk.ets.registry.api.account.repository.AccountHolderRepresentativeRepository;
import gov.uk.ets.registry.api.account.repository.AccountOwnershipRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.service.AccountConversionService;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderFileDTO;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderChangeAction;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderChangeTaskDetailsDTO;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.assign.OnlySeniorOrJuniorRegistryAdminCanBeAssignedTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim.OnlySeniorOrJuniorCanClaimTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim.SeniorAdminCanClaimTaskInitiatedByAdminRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.CannotCompletePendingTALRequestsRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.FourEyesPrincipleRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.OnlySeniorRegistryAdminCanApproveTask;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.requesteddocs.repository.AccountHolderFileRepository;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.service.TaskTypeService;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountHolderChangeTaskService implements TaskTypeService<AccountHolderChangeTaskDetailsDTO> {

    private final Mapper mapper;
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final AccountHolderRepository accountHolderRepository;
    private final AccountConversionService accountConversionService;
    private final EventService eventService;
    private final UserService userService;
    private final AccountHolderChangeValidationService accountHolderChangeValidationService;
    private final AccountOwnershipRepository accountOwnershipRepository;
    private final AccountHolderRepresentativeRepository accountHolderRepresentativeRepository;
    private final AccountHolderFileRepository accountHolderFileRepository;

    @Override
    public Set<RequestType> appliesFor() {
        return Set.of(RequestType.ACCOUNT_HOLDER_CHANGE);
    }

    @Override
    public AccountHolderChangeTaskDetailsDTO getDetails(TaskDetailsDTO taskDetailsDTO) {
        AccountHolderChangeTaskDetailsDTO response = new AccountHolderChangeTaskDetailsDTO(taskDetailsDTO);
        AccountHolderChangeAction accountHolderChangeAction;
        accountHolderChangeAction =
                mapper.convertToPojo(taskDetailsDTO.getDifference(), AccountHolderChangeAction.class);

        AccountHolderDTO currentAccountHolderDTO = Optional.ofNullable(taskDetailsDTO.getBefore())
                .map(before -> mapper.convertToPojo(before, AccountHolderDTO.class))
                .orElseGet(() -> {
                    AccountHolder accountHolder =
                            accountHolderRepository.getAccountHolderOfAccount(Long.parseLong(taskDetailsDTO.getAccountNumber()));
                    return accountConversionService.convert(accountHolder);
                });

        AccountDTO accountDTO = accountService.getAccountDTO(Long.valueOf(taskDetailsDTO.getAccountNumber()));

        response.setAccount(accountDTO.getAccountDetails());
        response.setCurrentAccountHolder(currentAccountHolderDTO);
        response.setAction(accountHolderChangeAction);

        return response;
    }

    @Protected({
            FourEyesPrincipleRule.class,
            OnlySeniorRegistryAdminCanApproveTask.class,
            CannotCompletePendingTALRequestsRule.class
    })
    @Transactional
    @Override
    public TaskCompleteResponse complete(AccountHolderChangeTaskDetailsDTO taskDTO, TaskOutcome taskOutcome, String comment) {
        User currentUser = userService.getCurrentUser();
        final Long accountIdentifier = Long.valueOf(taskDTO.getAccountNumber());
        AccountHolderChangeAction action = taskDTO.getAction();
        Account account =
                accountRepository.findByIdentifier(accountIdentifier).orElseThrow(() -> new IllegalStateException(
                        String.format("Account with identifier %s not found.", accountIdentifier)));
        AccountHolder currentAccountHolder = account.getAccountHolder();

        AccountHolder newAccountHolder;

        if (TaskOutcome.APPROVED == taskOutcome) {
            newAccountHolder = getOrCreateAccountHolder(action);

            final Boolean shouldDeleteAccountHolder = action.getAccountHolderDelete() != null &&
                    action.getAccountHolderDelete();

            accountHolderChangeValidationService
                    .validateChangeAccountHolderTaskForAccountIdentifier(accountIdentifier, newAccountHolder.getId(), shouldDeleteAccountHolder);

            handleAccountOwnership(account, newAccountHolder, shouldDeleteAccountHolder);

            account.setAccountHolder(newAccountHolder);

            if (Boolean.TRUE.equals(shouldDeleteAccountHolder)) {
                handleAuthorizedRepresentatives(currentAccountHolder.getIdentifier());
                handleAccountHolderFiles(currentAccountHolder.getIdentifier());
                accountHolderRepository.delete(currentAccountHolder);
            }
        }

        publishAccountEvent(taskOutcome, currentUser, accountIdentifier, currentAccountHolder.actualName(),
                action.getAccountHolderDTO().actualName());

        return defaultResponseBuilder(taskDTO).build();
    }

    @Protected({
            OnlySeniorOrJuniorCanClaimTaskRule.class,
            SeniorAdminCanClaimTaskInitiatedByAdminRule.class
    })
    @Override
    public void checkForInvalidClaimantPermissions() {
    }

    @Protected({
            OnlySeniorOrJuniorRegistryAdminCanBeAssignedTaskRule.class,
    })
    @Override
    public void checkForInvalidAssignPermissions() {
    }

    private AccountHolder getOrCreateAccountHolder(AccountHolderChangeAction action) {
        AccountHolder accountHolder;
        switch (action.getType()) {
            case ACCOUNT_HOLDER_CHANGE_TO_EXISTING_HOLDER:
                accountHolder =
                        accountHolderRepository.getAccountHolder(action.getAccountHolderDTO().getId());
                break;
            case ACCOUNT_HOLDER_CHANGE_TO_CREATED_HOLDER:
                accountHolder = accountService.createHolder(action.getAccountHolderDTO());
                accountService.insertContact(accountHolder,
                        action.getAccountHolderContactInfo(), true);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + action.getType());
        }
        return accountHolder;
    }

    private void publishAccountEvent(TaskOutcome taskOutcome, User currentUser, Long accountIdentifier,
                                     String oldHolderName, String newAccountHolderName) {

        String eventComment = String.format("From ‘%s’ to ‘%s’", oldHolderName, newAccountHolderName);
        String what = String
                .format("Change account holder request %s", taskOutcome == TaskOutcome.APPROVED ? "approved" : "rejected");

        eventService.createAndPublishEvent(accountIdentifier.toString(), currentUser.getUrid(), eventComment,
                EventType.ACCOUNT_TASK_COMPLETED, what);
    }

    private void handleAccountOwnership(Account account, AccountHolder newAccountHolder,
                                        boolean accountHolderDelete) {
        List<AccountOwnership> previousAccountOwnerships =
                accountOwnershipRepository
                        .findByAccountAndHolderAndStatus(account, account.getAccountHolder(),
                                AccountOwnershipStatus.ACTIVE);
        if (previousAccountOwnerships.size() != 1) {
            throw new IllegalStateException("Old account ownership is in invalid state");
        }
        AccountOwnership previousOwnership = previousAccountOwnerships.get(0);
        if (Boolean.TRUE.equals(accountHolderDelete)) {
            accountOwnershipRepository.delete(previousOwnership);
            //Check if there are INACTIVE ownerships for the previous account holder
            final List<AccountOwnership> inactiveOwnerships =
                    accountOwnershipRepository.findByHolderAndStatus(account.getAccountHolder(), AccountOwnershipStatus.INACTIVE);
            if (!inactiveOwnerships.isEmpty()) {
                accountOwnershipRepository.deleteAll(inactiveOwnerships);
            }
        } else {
            previousOwnership.setStatus(AccountOwnershipStatus.INACTIVE);
        }

        AccountOwnership newAccountOwnership = new AccountOwnership();
        newAccountOwnership.setAccount(account);
        newAccountOwnership.setHolder(newAccountHolder);
        newAccountOwnership.setStatus(AccountOwnershipStatus.ACTIVE);
        newAccountOwnership.setDateOfOwnership(LocalDateTime.now());
        accountOwnershipRepository.save(newAccountOwnership);
    }

    private void handleAuthorizedRepresentatives(Long accountHolderIdentifier) {
        List<AccountHolderRepresentative> accountHolderRepresentatives =
                accountHolderRepresentativeRepository.getAccountHolderRepresentatives(accountHolderIdentifier);
        if (!accountHolderRepresentatives.isEmpty()) {
            accountHolderRepresentativeRepository.deleteAll(accountHolderRepresentatives);
        }
    }

    private void handleAccountHolderFiles(Long accountHolderIdentifier) {
        final Set<Long> accountHolderFileIds = accountHolderRepository.getAccountHolderFiles(accountHolderIdentifier).stream()
                .map(AccountHolderFileDTO::getId)
                .collect(Collectors.toSet());

        if (!accountHolderFileIds.isEmpty()) {accountHolderFileRepository.deleteAllById(accountHolderFileIds);}
    }
}
