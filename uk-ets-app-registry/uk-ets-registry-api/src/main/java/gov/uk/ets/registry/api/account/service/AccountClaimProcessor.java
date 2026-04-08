package gov.uk.ets.registry.api.account.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.shared.AccountActionError;
import gov.uk.ets.registry.api.account.shared.AccountActionException;
import gov.uk.ets.registry.api.account.web.model.accountcontact.AccountContactSendInvitationDTO;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountClaimProcessor {

    private final AccountRepository accountRepository;
    private final AccountService accountService;

    @Value("${aviation.account.claim.enabled}")
    private boolean aviationAccountClaimEnabled;

    @Value("${installation.account.claim.enabled}")
    private boolean installationAccountClaimEnabled;

    @Value("${maritime.account.claim.enabled}")
    private boolean maritimeAccountClaimEnabled;

    public String processSendInvitation(Long accountIdentifier,
                                      AccountContactSendInvitationDTO dto) {

        RegistryAccountType type = accountRepository.findByIdentifier(accountIdentifier)
                .map(Account::getRegistryAccountType)
                .orElse(null);

        if (!this.isAccountClaimEnabled(type)) {
            throw AccountActionException.create(
                    AccountActionError.build("Account claim is not enabled for this account type.")
            );
        }

        return accountService.sendInvitation(accountIdentifier, dto);
    }

    public boolean isAccountClaimEnabled(RegistryAccountType type) {
        if (type == null) return false;

        return switch (type) {
            case AIRCRAFT_OPERATOR_HOLDING_ACCOUNT -> aviationAccountClaimEnabled;
            case OPERATOR_HOLDING_ACCOUNT -> installationAccountClaimEnabled;
            case MARITIME_OPERATOR_HOLDING_ACCOUNT -> maritimeAccountClaimEnabled;
            default -> false;
        };
    }

    public List<RegistryAccountType> getEnabledAccountTypes() {
        List<RegistryAccountType> types = new ArrayList<>();

        if (installationAccountClaimEnabled) {
            types.add(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT);
        }
        if (aviationAccountClaimEnabled) {
            types.add(RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT);
        }
        if (maritimeAccountClaimEnabled) {
            types.add(RegistryAccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT);
        }

        return types;
    }
}
