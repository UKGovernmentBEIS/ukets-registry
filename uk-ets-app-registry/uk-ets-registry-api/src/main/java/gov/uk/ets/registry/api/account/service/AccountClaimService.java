package gov.uk.ets.registry.api.account.service;

import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.shared.AccountActionError;
import gov.uk.ets.registry.api.account.shared.AccountActionException;
import gov.uk.ets.registry.api.account.web.model.AccountClaimDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.BulkClaimResult;
import gov.uk.ets.registry.api.account.web.model.accountcontact.AccountContactSendInvitationDTO;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class AccountClaimService {

    private final static String ERROR_MESSAGE = "Account claim is not enabled for this account type.";

    private final AccountService accountService;

    private final AccountRepository accountRepository;

    private final AccountSendInvitationService accountSendInvitationService;

    private final AccountClaimProcessor processor;

    @Transactional
    public String sendInvitation(Long accountIdentifier, AccountContactSendInvitationDTO sendInvitationDTO) {
        return processor.processSendInvitation(accountIdentifier, sendInvitationDTO);
    }

    @Transactional
    public Long claimAccount(AccountClaimDTO accountClaimDTO) {
        accountRepository.findByCompliantEntityIdentifierAndAccountClaimCode(accountClaimDTO.getRegistryId(), accountClaimDTO.getAccountClaimCode())
                .ifPresent(account -> {
                    if (!processor.isAccountClaimEnabled(account.getRegistryAccountType())) {
                        throw AccountActionException.create(
                                AccountActionError.build(ERROR_MESSAGE)
                        );
                    }
                });

        // Delegate to service for all actual validations (missing account, invalid code)
        return accountService.claimAccount(accountClaimDTO);
    }

    @Transactional
    public Long countEligibleBulkClaimAccounts() {

        List<RegistryAccountType> enabledAccountTypes = processor.getEnabledAccountTypes();

        if (enabledAccountTypes.isEmpty()) {
            return 0L;
        }

        return accountRepository.countAccountsWithoutActiveARsAndPendingTasksWithContacts(
                enabledAccountTypes,
                List.of(
                        RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST
                )
        );
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public BulkClaimResult sendBulkClaimInvitations() {

        List<RegistryAccountType> enabledAccountTypes = processor.getEnabledAccountTypes();

        if (enabledAccountTypes.isEmpty()) {
            return new BulkClaimResult(0, 0, 0);
        }

        List<Long> accountIdentifiers =
                accountRepository.findAccountIdentifierWithoutActiveARsAndPendingTaskWithContacts(
                        enabledAccountTypes,
                        List.of(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST)
                );

        int success = 0;
        int failed = 0;

        for (Long identifier : accountIdentifiers) {

            try {
                AccountDTO accountDTO = accountService.getAccountDTO(identifier);

                AccountContactSendInvitationDTO dto =
                        AccountContactSendInvitationDTO.builder()
                                .metsContacts(new HashSet<>(accountDTO.getMetsContacts()))
                                .registryContacts(new HashSet<>(accountDTO.getRegistryContacts()))
                                .build();

                if (dto.getMetsContacts().isEmpty() && dto.getRegistryContacts().isEmpty()) {
                    continue;
                }

                accountSendInvitationService.sendInvitation(identifier, dto);
                success++;

            } catch (AccountActionException | IllegalStateException ex) {
                failed++;

                log.error("Failed to send invitation for account {}", identifier, ex);
            }
        }

        return new BulkClaimResult(accountIdentifiers.size(), success, failed);
    }
}
