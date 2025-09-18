package gov.uk.ets.registry.api.accounttransfer.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.AccountOwnership;
import gov.uk.ets.registry.api.account.domain.types.AccountOwnershipStatus;
import gov.uk.ets.registry.api.account.repository.AccountHolderRepository;
import gov.uk.ets.registry.api.account.repository.AccountOwnershipRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.service.AccountConversionService;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.service.TransferValidationService;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.accounttransfer.web.model.AccountTransferAction;
import gov.uk.ets.registry.api.accounttransfer.web.model.AccountTransferTaskDetailsDTO;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.assign.OnlySeniorOrJuniorRegistryAdminCanBeAssignedTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim.OnlySeniorOrJuniorCanClaimTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim.SeniorAdminCanClaimTaskInitiatedByAdminRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.CannotCompletePendingTALRequestsRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.FourEyesPrincipleRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.OnlySeniorRegistryAdminCanApproveTask;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.tal.domain.TrustedAccount;
import gov.uk.ets.registry.api.tal.repository.TrustedAccountRepository;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.service.TaskTypeService;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AccountTransferTaskService implements TaskTypeService<AccountTransferTaskDetailsDTO> {

    private final Mapper mapper;
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final AccountHolderRepository accountHolderRepository;
    private final TrustedAccountRepository trustedAccountRepository;
    private final AccountConversionService accountConversionService;
    private final TransferValidationService transferValidationService;
    private final EventService eventService;
    private final UserService userService;
    private final AccountOwnershipRepository accountOwnershipRepository;

    private static final String REMOVAL_REASON = "account transfer";


    @Override
    public Set<RequestType> appliesFor() {
        return Set.of(RequestType.ACCOUNT_TRANSFER);
    }

    @Override
    public AccountTransferTaskDetailsDTO getDetails(TaskDetailsDTO taskDetails) {
        AccountTransferTaskDetailsDTO response = new AccountTransferTaskDetailsDTO(taskDetails);
        AccountTransferAction accountTransferAction;
        accountTransferAction =
            mapper.convertToPojo(taskDetails.getDifference(), AccountTransferAction.class);

        AccountHolderDTO originalAH = Optional.ofNullable(taskDetails.getBefore())
            .map(before -> mapper.convertToPojo(before, AccountHolderDTO.class))
            .orElseGet(() -> { // fallback to the original logic
                AccountHolder accountHolder =
                    accountHolderRepository.getAccountHolderOfAccount(Long.parseLong(taskDetails.getAccountNumber()));
                return accountConversionService.convert(accountHolder);
            });
        AccountDTO accountDTO = accountService.getAccountDTO(Long.valueOf(taskDetails.getAccountNumber()));

        response.setAction(accountTransferAction);
        response.setCurrentAccountHolder(originalAH);
        response.setAccount(accountDTO.getAccountDetails());
        return response;
    }

    @Protected({
            FourEyesPrincipleRule.class,
            OnlySeniorRegistryAdminCanApproveTask.class,
            CannotCompletePendingTALRequestsRule.class
    })
    @Transactional
    @Override
    public TaskCompleteResponse complete(AccountTransferTaskDetailsDTO task, TaskOutcome taskOutcome,
                                         String comment) {
        User currentUser = userService.getCurrentUser();
        final Long accountIdentifier = Long.valueOf(task.getAccountNumber());
        AccountTransferAction action = task.getAction();
        Account account =
            accountRepository.findByIdentifier(accountIdentifier).orElseThrow(() -> new IllegalStateException(
                String.format("Account with identifier %s not found.", accountIdentifier)));
        AccountHolder oldAccountHolder = account.getAccountHolder();

        account.setAccountStatus(action.getPreviousAccountStatus());

        AccountHolder newAccountHolder;

        if (TaskOutcome.APPROVED == taskOutcome) {
            newAccountHolder = getOrCreateAccountHolder(action);

            // TODO BRs should be externalised
            transferValidationService
                .validateTransferTaskForAccountIdentifier(accountIdentifier, newAccountHolder.getId());
            // first handle ownership (before updating the AH)
            handleAccountOwnership(account, newAccountHolder);
            //then update AH
            account.setAccountHolder(newAccountHolder);
            // handle TAL for existing AH
            handleTrustedAccounts(accountIdentifier, newAccountHolder);
            // remove ARs and (potentially) their keycloak role
            accountService.removeAccountArs(accountIdentifier, REMOVAL_REASON, currentUser);
            // the Sales contact details should be removed
            clearSalesContactDetails(account);
        }

        publishAccountEvent(taskOutcome, currentUser, accountIdentifier, oldAccountHolder.actualName(),
            action.getAccountHolderDTO().actualName());

        return defaultResponseBuilder(task).build();
    }

    @Protected( {
            OnlySeniorOrJuniorCanClaimTaskRule.class,
            SeniorAdminCanClaimTaskInitiatedByAdminRule.class
    })
    @Override
    public void checkForInvalidClaimantPermissions() {

    }

    @Protected( {
            OnlySeniorOrJuniorRegistryAdminCanBeAssignedTaskRule.class,
    })
    @Override
    public void checkForInvalidAssignPermissions() {

    }

    /**
     * Retrieves or creates account holder and account holder contact.
     */
    private AccountHolder getOrCreateAccountHolder(AccountTransferAction action) {
        AccountHolder accountHolder;
        switch (action.getType()) {
            case ACCOUNT_TRANSFER_TO_EXISTING_HOLDER:
                accountHolder =
                    accountHolderRepository.getAccountHolder(action.getAccountHolderDTO().getId());
                break;
            case ACCOUNT_TRANSFER_TO_CREATED_HOLDER:
                accountHolder = accountService.createHolder(action.getAccountHolderDTO());
                accountService.addAccountHolderContact(accountHolder,
                    action.getAccountHolderContactInfo(), true);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + action.getType());
        }
        return accountHolder;
    }

    /**
     * Remove from trusted_account accounts of the new AH (if any).
     *
     * @param accountIdentifier the account identifier of the account to be transferred
     * @param accountHolder     the new account holder
     */
    private void handleTrustedAccounts(Long accountIdentifier, AccountHolder accountHolder) {
        List<TrustedAccount> trustedAccounts =
            trustedAccountRepository.findAllByAccountIdentifier(accountIdentifier);
        if (Optional.ofNullable(accountHolder.getAccounts()).isPresent()) {
            trustedAccountRepository.deleteAll(trustedAccounts);
        }
    }

    /**
     * Update Account Ownership records.
     *
     * @param account          the account to be transferred
     * @param newAccountHolder the account holder where the account is being transferred to.
     */
    private void handleAccountOwnership(Account account, AccountHolder newAccountHolder) {
        // set state of previous entry to INACTIVE
        List<AccountOwnership> previousAccountownwerships =
            accountOwnershipRepository
                .findByAccountAndHolderAndStatus(account, account.getAccountHolder(),
                    AccountOwnershipStatus.ACTIVE);
        if (previousAccountownwerships.size() != 1) {
            throw new IllegalStateException("Old account ownership is in invalid state");
        }
        previousAccountownwerships.get(0).setStatus(AccountOwnershipStatus.INACTIVE);
        // add new entry with status ACTIVE
        AccountOwnership newAccountOwnership = new AccountOwnership();
        newAccountOwnership.setAccount(account);
        newAccountOwnership.setHolder(newAccountHolder);
        newAccountOwnership.setStatus(AccountOwnershipStatus.ACTIVE);
        newAccountOwnership.setDateOfOwnership(LocalDateTime.now());
        accountOwnershipRepository.save(newAccountOwnership);
    }

    private void publishAccountEvent(TaskOutcome taskOutcome, User currentUser, Long accountIdentifier,
                                     String oldHolderName, String newAccountHolderName) {

        String eventComment = String.format("From ‘%s’ to ‘%s’", oldHolderName, newAccountHolderName);
        String what = String
            .format("Account transfer request %s", taskOutcome == TaskOutcome.APPROVED ? "approved" : "rejected");

        eventService.createAndPublishEvent(accountIdentifier.toString(), currentUser.getUrid(), eventComment,
            EventType.ACCOUNT_TASK_COMPLETED, what);
    }
    
	private void clearSalesContactDetails(Account account) {
		account.setSalesContact(null);
	}
}
