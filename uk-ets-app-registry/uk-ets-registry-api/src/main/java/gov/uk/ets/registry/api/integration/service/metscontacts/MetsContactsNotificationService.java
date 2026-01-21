package gov.uk.ets.registry.api.integration.service.metscontacts;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.MetsAccountContact;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.service.AccountClaimService;
import gov.uk.ets.registry.api.account.service.AccountContactService;
import gov.uk.ets.registry.api.account.service.AccountDTOFactory;
import gov.uk.ets.registry.api.account.service.AccountGeneratorService;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.accountcontact.AccountContactSendInvitationDTO;
import gov.uk.ets.registry.api.account.web.model.accountcontact.MetsContactDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.integration.model.metscontacts.MetsContactsEventOutcome;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MetsContactsNotificationService {

    private final AccountDTOFactory accountDTOFactory;
    private final AccountContactService accountContactService;
    private final AccountRepository accountRepository;
    private final MetsContactMapper metsContactMapper;
    private final AccountGeneratorService accountGeneratorService;
    private final AccountClaimService accountClaimService;

    public void sendInvitation(MetsContactsEventOutcome outcome) throws NoSuchAlgorithmException {

        String accountIdentifier = outcome.getAccountIdentifier();

        Account account = accountRepository
                .findByFullIdentifier(accountIdentifier)
                .orElseThrow(() -> new IllegalStateException("Account not found"));

        if (accountClaimService.isAccountClaimEnabled(account.getRegistryAccountType())) {
            sendInvitation(account, metsContactMapper.convert(outcome.getEvent()));
        }
    }

    public void sendInvitation(Account updatedAccount,
                               List<MetsContactDTO> incomingContacts) throws NoSuchAlgorithmException {

        Set<String> incomingEmails = incomingContacts.stream()
                .map(MetsContactDTO::getEmail)
                .collect(Collectors.toSet());

        List<MetsAccountContact> contactsToInvite =
                updatedAccount.getMetsAccountContacts().stream()
                        .filter(c -> incomingEmails.contains(c.getEmailAddress()))
                        .filter(c -> c.getInvitedOn() == null)
                        .toList();

        if (contactsToInvite.isEmpty()) {
            return;
        }

        AccountContactSendInvitationDTO dto =
                AccountContactSendInvitationDTO.builder()
                        .metsContacts(
                                contactsToInvite.stream()
                                        .map(accountDTOFactory::createMetsContactDTO)
                                        .collect(Collectors.toSet())
                        )
                        .build();

        Account accountWithAccountClaimCode = updateAccountIfMissingClaimAccountCode(updatedAccount);
        AccountDTO accountDTO = accountDTOFactory.create(accountWithAccountClaimCode, true);

        accountContactService.sendInvitation(updatedAccount.getIdentifier(), accountDTO, dto);
    }

    private Account updateAccountIfMissingClaimAccountCode(Account account) throws NoSuchAlgorithmException {
        if(Objects.isNull(account.getAccountClaimCode())) {
            account.setAccountClaimCode(accountGeneratorService.generateAccountClaimCode());
            return accountRepository.save(account);
        }
        return account;
    }
}
