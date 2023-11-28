package gov.uk.ets.registry.api.tal.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.common.Utils;
import gov.uk.ets.registry.api.tal.domain.TrustedAccount;
import gov.uk.ets.registry.api.tal.domain.types.TrustedAccountStatus;
import gov.uk.ets.registry.api.tal.error.TrustedAccountListActionError;
import gov.uk.ets.registry.api.tal.error.TrustedAccountListActionException;
import gov.uk.ets.registry.api.tal.web.model.TrustedAccountDTO;
import gov.uk.ets.registry.api.transaction.domain.data.AcquiringAccountInfo;
import java.util.Optional;

import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Converts trusted account entities to dto and vise versa.
 */
@Service
@RequiredArgsConstructor
public class TrustedAccountConversionService {

    private final AccountRepository accountRepository;
    
    TrustedAccountDTO convertTrustedAccount(TrustedAccount trustedAccount) {
        Optional<Account> account = accountRepository.findByFullIdentifier(trustedAccount.getTrustedAccountFullIdentifier());
        Boolean isKyotoAccountType = null;
        if(account.isPresent()) {
            AccountType accountType = AccountType.get(account.get().getRegistryAccountType(), account.get().getKyotoAccountType());
            isKyotoAccountType = accountType.getKyoto();
        }
        return
            TrustedAccountDTO.builder()
                .id(trustedAccount.getId())
                .accountFullIdentifier(trustedAccount.getTrustedAccountFullIdentifier())
                .description(trustedAccount.getDescription())
                .underSameAccountHolder(false)
                .status(trustedAccount.getStatus())
                .activationDate(
                    trustedAccount.getActivationDate() != null ? Utils.formatDay(trustedAccount.getActivationDate()) :
                        null)
                .activationTime(
                    trustedAccount.getActivationDate() != null ? Utils.formatTime(trustedAccount.getActivationDate()) :
                        null)
                .kyotoAccountType(isKyotoAccountType)
                .build();
    }

    TrustedAccountDTO convertTrustedAccount(Account account) {
        return TrustedAccountDTO.builder().accountFullIdentifier(account.getFullIdentifier())
            .name(account.getAccountName())
            .underSameAccountHolder(true)
            .build();
    }

    TrustedAccount convertTrustedAccount(TrustedAccountDTO trustedAccountDTO, Long accountId) {
        TrustedAccount trustedAccount = new TrustedAccount();
        Optional<Account> account = accountRepository.findByIdentifier(accountId);
        if (account.isEmpty()) {
            throw TrustedAccountListActionException.create(TrustedAccountListActionError.builder()
                .code(TrustedAccountListActionError.ACCOUNT_NOT_FOUND)
                .message("The host account does not exist in the registry.")
                .build());
        }
        trustedAccount.setAccount(account.get());
        if (trustedAccountDTO.getId() != null) {
            trustedAccount.setId(trustedAccountDTO.getId());
        }
        trustedAccount.setTrustedAccountFullIdentifier(trustedAccountDTO.getAccountFullIdentifier());
        trustedAccount.setDescription(trustedAccountDTO.getDescription());
        trustedAccount.setStatus(TrustedAccountStatus.PENDING_ADDITION_APPROVAL);
        return trustedAccount;
    }

    AcquiringAccountInfo convertTrustedAccountToAcquiringAccount(TrustedAccount trustedAccount) {
        return AcquiringAccountInfo.acquiringAccountInfoBuilder()
            .fullIdentifier(trustedAccount.getTrustedAccountFullIdentifier()).trusted(true)
            .accountName(trustedAccount.getDescription()).build();
    }
}
