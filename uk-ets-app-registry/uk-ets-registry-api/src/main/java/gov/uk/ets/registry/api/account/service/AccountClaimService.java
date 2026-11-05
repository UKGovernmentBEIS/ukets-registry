package gov.uk.ets.registry.api.account.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountRepresentativeInvitation;
import gov.uk.ets.registry.api.account.domain.types.AccountContactType;
import gov.uk.ets.registry.api.account.repository.AccountContactRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepresentativeInvitationRepository;
import gov.uk.ets.registry.api.account.shared.AccountActionError;
import gov.uk.ets.registry.api.account.shared.AccountActionException;
import gov.uk.ets.registry.api.account.web.model.AccountClaimDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.BulkClaimResult;
import gov.uk.ets.registry.api.account.web.model.accountcontact.AccountContactChangeInvitationStatusDTO;
import gov.uk.ets.registry.api.account.web.model.accountcontact.AccountContactSendInvitationDTO;
import gov.uk.ets.registry.api.account.web.model.accountcontact.ContactInvitationStatusDTO;
import gov.uk.ets.registry.api.account.web.model.accountcontact.MetsContactDTO;
import gov.uk.ets.registry.api.account.web.model.accountcontact.RegistryContactDTO;
import gov.uk.ets.registry.api.account.web.model.accountcontact.RegistryContactInvitationStatusDTO;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class AccountClaimService {

    private final static String ERROR_MESSAGE = "Account claim is not enabled for this account type.";

    private final static String HELP_DESK_EMAIL = "etregistryhelp@environment-agency.gov.uk";

    private final AccountService accountService;

    private final AccountRepository accountRepository;

    private final AccountSendInvitationService accountSendInvitationService;

    private final AccountClaimProcessor processor;

    private final AccountContactRepository accountContactRepository;

    private final AccountRepresentativeInvitationRepository accountRepresentativeInvitationRepository;

    @Transactional
    public String sendInvitation(Long accountIdentifier, AccountContactSendInvitationDTO sendInvitationDTO, boolean isManual) {
        return processor.processSendInvitation(accountIdentifier, sendInvitationDTO, isManual);
    }

    @Transactional
    public Long claimAccount(AccountClaimDTO accountClaimDTO) {
        final Long registryId;
        try {
            registryId = accountClaimDTO.getRegistryIdAsLong();
        } catch (IllegalArgumentException e) {
            throw AccountActionException.create(
                    AccountActionError.build("The claim account code, or the METS Registry ID is incorrect.")
            );
        }
        accountRepository.findByCompliantEntityIdentifierAndAccountClaimCode(registryId, accountClaimDTO.getAccountClaimCode())
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
                List.of(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST),
                HELP_DESK_EMAIL
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
                        List.of(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST),
                        HELP_DESK_EMAIL);

        int success = 0;
        int failed = 0;

        for (Long identifier : accountIdentifiers) {

            try {
                AccountDTO accountDTO = accountService.getAccountDTOWithoutAuthorization(identifier);

                AccountContactSendInvitationDTO dto =
                        AccountContactSendInvitationDTO.builder()
                                .metsContacts(accountDTO.getMetsContacts().stream()
                                        .filter(MetsContactDTO::isInvitationStatus)
                                        .collect(Collectors.toSet()))
                                .registryContacts(accountDTO.getRegistryContacts().stream()
                                        .filter(RegistryContactDTO::isInvitationStatus)
                                        .collect(Collectors.toSet()))
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

    @Transactional
    public void changeAutoSendInvitationEnabled(Long accountIdentifier) {
        final Account account = accountRepository.findByAccountIdentifierFetchCompliantEntity(accountIdentifier)
                .orElseThrow(() -> AccountActionException.create(
                        AccountActionError.build(
                                "Cannot change auto-send invitation status. Account with identifier "
                                        + accountIdentifier + " does not exist.")));

        if (RegistryAccountType.OPERATOR_HOLDING_ACCOUNT.equals(account.getRegistryAccountType()) ||
                RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT.equals(account.getRegistryAccountType()) ||
                RegistryAccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT.equals(account.getRegistryAccountType())) {

            if (Objects.isNull(account.getCompliantEntity())) {
                throw AccountActionException.create(AccountActionError
                        .build("Cannot change auto-send invitation status. Account with identifier " + accountIdentifier + " has no compliant entity."));
            }
            account.getCompliantEntity()
                    .setAutoSendClaimInvitationEnabled(!account.getCompliantEntity().isAutoSendClaimInvitationEnabled());
        } else {
            throw AccountActionException.create(
                    AccountActionError.build("Cannot change auto-send invitation status. Account type "
                            + account.getRegistryAccountType() + " is not supported.")
            );
        }
    }
    
    @Transactional
    public void changeContactsInvitationStatus(Long accountIdentifier, AccountContactChangeInvitationStatusDTO accountContactChangeInvitationStatusDTO) {

        
        final Map<String, ContactInvitationStatusDTO> metsContactsMap = accountContactChangeInvitationStatusDTO.getMetsContacts().stream()
                .collect(Collectors.toMap(
                        ContactInvitationStatusDTO::getEmail,
                    Function.identity(),
                    (existing, replacement) -> replacement
                ));
        
        accountContactRepository.findByAccountIdentifier(accountIdentifier).stream()
		        .filter(contact -> metsContactsMap.containsKey(contact.getEmailAddress()))
		        .forEach(contact -> contact.setInvitationStatus(metsContactsMap.get(contact.getEmailAddress()).getInvitationStatus()));


        final Map<RegistryContactKey, RegistryContactInvitationStatusDTO>
                registryContactsMap =
                accountContactChangeInvitationStatusDTO.getRegistryContacts().stream()
                        .collect(Collectors.toMap(
                                contact -> new RegistryContactKey(
                                        contact.getEmail(),
                                        contact.getContactType()
                                ),
                                Function.identity(),
                                (existing, replacement) -> replacement
                        ));

        if (registryContactsMap.isEmpty()) {
            return;
        }

        final Set<String> registryEmails = registryContactsMap.keySet()
                .stream()
                .map(RegistryContactKey::email)
                .collect(Collectors.toSet());

        final List<AccountRepresentativeInvitation> invitations =
                accountRepresentativeInvitationRepository
                        .findByAccount_IdentifierAndAccountHolderRepresentative_Contact_EmailAddressIn(
                                accountIdentifier,
                                registryEmails
                        );

        invitations.forEach(invitation -> {
            final String email = invitation.getAccountHolderRepresentative()
                    .getContact()
                    .getEmailAddress();

            final AccountContactType contactType =
                    invitation.getAccountHolderRepresentative()
                            .getAccountContactType();

            final RegistryContactInvitationStatusDTO requestedRegistryContact =
                    registryContactsMap.get(
                            new RegistryContactKey(email, contactType)
                    );

            if (requestedRegistryContact != null) {
                invitation.setInvitationStatus(
                        requestedRegistryContact.getInvitationStatus()
                );
            }
        });
    }

    private record RegistryContactKey(
            String email,
            AccountContactType contactType
    ) {}
}
