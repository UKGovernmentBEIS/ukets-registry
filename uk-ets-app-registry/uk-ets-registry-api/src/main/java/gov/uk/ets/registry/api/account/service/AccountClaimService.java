package gov.uk.ets.registry.api.account.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.shared.AccountActionError;
import gov.uk.ets.registry.api.account.shared.AccountActionException;
import gov.uk.ets.registry.api.account.web.model.AccountClaimDTO;
import gov.uk.ets.registry.api.account.web.model.accountcontact.AccountContactSendInvitationDTO;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountClaimService {

    private final static String ERROR_MESSAGE = "Account claim is not enabled for this account type.";

    private final AccountService accountService;

    private final AccountRepository accountRepository;

    @Value("${aviation.account.claim.enabled}")
    private boolean aviationAccountClaimEnabled;

    @Value("${installation.account.claim.enabled}")
    private boolean installationAccountClaimEnabled;

    @Value("${maritime.account.claim.enabled}")
    private boolean maritimeAccountClaimEnabled;

    @Transactional
    public String sendInvitation(Long accountIdentifier, AccountContactSendInvitationDTO sendInvitationDTO) {
        final RegistryAccountType registryAccountType = accountRepository.findByIdentifier(accountIdentifier).map(Account::getRegistryAccountType).orElse(null);
        if (isAccountClaimEnabled(registryAccountType)) {
            return accountService.sendInvitation(accountIdentifier, sendInvitationDTO);
        }
        throw AccountActionException.create(
                AccountActionError.build(ERROR_MESSAGE));
    }

    @Transactional
    public Long claimAccount(AccountClaimDTO accountClaimDTO) {
        final RegistryAccountType registryAccountType = accountRepository.findByCompliantEntityIdentifierAndAccountClaimCode(accountClaimDTO.getRegistryId(), accountClaimDTO.getAccountClaimCode())
                .map(Account::getRegistryAccountType).orElse(null);
        if (isAccountClaimEnabled(registryAccountType)) {
            return accountService.claimAccount(accountClaimDTO);
        }
        throw AccountActionException.create(
                AccountActionError.build(ERROR_MESSAGE));
    }

    public boolean isAccountClaimEnabled(RegistryAccountType registryAccountType) {
        if (registryAccountType == null) {
            return false;
        } else if (registryAccountType.equals(RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT)) {
            return aviationAccountClaimEnabled;
        } else if (registryAccountType.equals(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT)) {
            return installationAccountClaimEnabled;
        } else if (registryAccountType.equals(RegistryAccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT)) {
            return maritimeAccountClaimEnabled;
        } else return false;
    }
}
