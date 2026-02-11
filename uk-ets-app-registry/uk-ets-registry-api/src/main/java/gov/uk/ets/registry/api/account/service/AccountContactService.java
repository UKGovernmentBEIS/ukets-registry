package gov.uk.ets.registry.api.account.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.AccountHolderRepresentative;
import gov.uk.ets.registry.api.account.domain.MetsAccountContact;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.notification.InvitationAccount;
import gov.uk.ets.registry.api.account.notification.InvitationPayload;
import gov.uk.ets.registry.api.account.repository.AccountAccessRepository;
import gov.uk.ets.registry.api.account.repository.AccountContactRepository;
import gov.uk.ets.registry.api.account.repository.AccountHolderRepresentativeRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.shared.AccountActionError;
import gov.uk.ets.registry.api.account.shared.AccountActionException;
import gov.uk.ets.registry.api.account.web.model.AccountClaimDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.accountcontact.AccountContactDTO;
import gov.uk.ets.registry.api.account.web.model.accountcontact.AccountContactSendInvitationDTO;
import gov.uk.ets.registry.api.account.web.model.accountcontact.MetsContactDTO;
import gov.uk.ets.registry.api.ar.domain.ARUpdateActionType;
import gov.uk.ets.registry.api.ar.service.AuthorizedRepresentativeService;
import gov.uk.ets.registry.api.ar.service.dto.ARUpdateActionDTO;
import gov.uk.ets.registry.api.ar.service.dto.AuthorizedRepresentativeDTO;
import gov.uk.ets.registry.api.common.view.PhoneNumberDTO;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.user.service.UserService;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountContactService {

    private final AccountRepository accountRepository;
    private final AccountAccessRepository accountAccessRepository;
    private final AccountContactRepository accountContactRepository;
    private final TaskRepository taskRepository;
    private final AccountHolderRepresentativeRepository accountHolderRepresentativeRepository;
    private final AuthorizedRepresentativeService authorizedRepresentativeService;
    private final UserService userService;

    @Transactional
    public Account saveOrUpdate(
            Long complianceEntityIdentifier,
            List<MetsContactDTO> incomingContacts) {

        Account account = accountRepository
                .findByCompliantEntityIdentifier(complianceEntityIdentifier)
                .orElseThrow(() -> new IllegalStateException("Account not found"));

        Map<String, MetsAccountContact> existingByEmail =
                account.getMetsAccountContacts().stream()
                        .collect(Collectors.toMap(
                                MetsAccountContact::getEmailAddress,
                                Function.identity()
                        ));

        Set<String> incomingEmails = new HashSet<>();

        for (MetsContactDTO dto : incomingContacts) {
            String email = dto.getEmail();
            incomingEmails.add(email);

            MetsAccountContact existing = existingByEmail.get(email);

            if (existing != null) {
                // ---- PARTIAL UPDATE ----
                if (dto.getFullName() != null) {
                    existing.setName(dto.getFullName());
                }

                PhoneNumberDTO phone = dto.getPhoneNumber();
                if (phone != null) {
                    existing.setPhoneNumber1(phone.getPhoneNumber1());
                    existing.setCountryCode1(phone.getCountryCode1());
                    existing.setPhoneNumber2(phone.getPhoneNumber2());
                    existing.setCountryCode2(phone.getCountryCode2());
                }

                if (dto.getContactTypes() != null) {
                    existing.setContactTypes(dto.getContactTypes());
                }

                if (dto.getOperatorType() != null) {
                    existing.setOperatorType(dto.getOperatorType());
                }

                accountContactRepository.save(existing);

            } else {
                MetsAccountContact newContact = MetsAccountContact.builder()
                        .name(dto.getFullName())
                        .emailAddress(email)
                        .account(account)
                        .contactTypes(dto.getContactTypes())
                        .operatorType(dto.getOperatorType())
                        .build();

                PhoneNumberDTO phone = dto.getPhoneNumber();
                if (phone != null) {
                    newContact.setPhoneNumber1(phone.getPhoneNumber1());
                    newContact.setCountryCode1(phone.getCountryCode1());
                    newContact.setPhoneNumber2(phone.getPhoneNumber2());
                    newContact.setCountryCode2(phone.getCountryCode2());
                }
                newContact.setAccount(account);
                accountContactRepository.save(newContact);
            }
        }

        List<MetsAccountContact> toRemove =
                account.getMetsAccountContacts().stream()
                        .filter(c -> !incomingEmails.contains(c.getEmailAddress()))
                        .toList();

        account.getMetsAccountContacts().removeAll(toRemove);
        accountContactRepository.deleteAll(toRemove);
        return accountRepository.save(account);
    }

    @Transactional
    @EmitsGroupNotifications(GroupNotificationType.SEND_INVITATION_TO_CONTACTS)
    public String sendInvitation(
            Long accountIdentifier,
            @InvitationAccount AccountDTO accountDTO,
            @InvitationPayload AccountContactSendInvitationDTO sendInvitationDTO
    ) {
        validateSendInvitation(accountDTO, sendInvitationDTO);
        final Set<String> metContactEmails = sendInvitationDTO.getMetsContacts().stream()
                .map(AccountContactDTO::getEmail)
                .collect(Collectors.toSet());
        final Set<String> registryContactEmails = sendInvitationDTO.getRegistryContacts().stream()
                .map(AccountContactDTO::getEmail)
                .collect(Collectors.toSet());

        final LocalDateTime now = LocalDateTime.now();
        accountContactRepository.findByAccountIdentifier(accountIdentifier).stream()
                .filter(metsContact -> metContactEmails.contains(metsContact.getEmailAddress()))
                .forEach(accountContact -> accountContact.setInvitedOn(now));
        Account account = accountRepository.findByIdentifier(accountIdentifier).orElseThrow(() -> new IllegalStateException(
                String.format("Account with identifier %s not found.", accountIdentifier)));
        List<AccountHolderRepresentative> accountHolderRepresentatives =
                accountHolderRepresentativeRepository
                        .getAccountHolderRepresentatives(Long.valueOf(accountDTO.getAccountDetails().getAccountHolderId()));
        accountHolderRepresentatives.stream()
                .filter(accountHolderRepresentative -> registryContactEmails.contains(accountHolderRepresentative.getContact().getEmailAddress()))
                .forEach(accountHolderRepresentative ->
                        account.inviteRepresentative(accountHolderRepresentative, now));
        return accountDTO.getAccountClaimCode();
    }

    @Transactional
    public Long claimAccount(AccountClaimDTO accountClaimDTO) {

        Account account = accountRepository
                .findByCompliantEntityIdentifierAndAccountClaimCode(
                        accountClaimDTO.getRegistryId(),
                        accountClaimDTO.getAccountClaimCode()
                )
                .orElseThrow(() -> AccountActionException.create(
                        AccountActionError.build("The claim account code, or the METS Registry ID is incorrect.")
                ));

        validateClaimAccount(account.getId(), account.getIdentifier());

        //ADD Authorized representative
        final ARUpdateActionDTO arUpdateAction = ARUpdateActionDTO.builder()
                .accountIdentifier(account.getIdentifier())
                .candidate(AuthorizedRepresentativeDTO.builder()
                        .urid(userService.getCurrentUser().getUrid())
                        .right(AccountAccessRight.SURRENDER_INITIATE_AND_APPROVE)
                        .build())
                .updateType(ARUpdateActionType.ADD)
                .build();
        return authorizedRepresentativeService.placeUpdateRequest(arUpdateAction);
    }

    private void validateSendInvitation(AccountDTO accountDTO, AccountContactSendInvitationDTO sendInvitationDTO) {

        if (accountDTO.getMetsContacts().isEmpty() && accountDTO.getRegistryContacts().isEmpty()) {
            throw AccountActionException.create(
                    AccountActionError.build("Account must have at least one METS or Registry contact to send invitation to."));
        }

        if (sendInvitationDTO.getMetsContacts().isEmpty() && sendInvitationDTO.getRegistryContacts().isEmpty()) {
            throw AccountActionException.create(
                    AccountActionError.build("At least one contact must be specified."));
        }

        if (!new HashSet<>(accountDTO.getMetsContacts()).containsAll(sendInvitationDTO.getMetsContacts()) ||
                !new HashSet<>(accountDTO.getRegistryContacts()).containsAll(sendInvitationDTO.getRegistryContacts())) {

            throw AccountActionException.create(
                    AccountActionError.build("The specified contacts are not related with this account."));
        }
    }

    private void validateClaimAccount(Long accountId, Long accountIdentifier) {

        final List<AccountAccess> accountAccesses = accountAccessRepository.finARsByAccount_Identifier(accountIdentifier)
                .stream()
                .filter(access -> access.getState().equals(AccountAccessState.ACTIVE))
                .toList();

        final Long pendingAuthorizedRepresentativeTasks = taskRepository.countPendingTasksByAccountIdInAndType(
                List.of(accountId),
                List.of(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST)
        );

        if (!accountAccesses.isEmpty() || pendingAuthorizedRepresentativeTasks > 0) {
            throw AccountActionException.create(
                    AccountActionError.build("The account has already been claimed."));
        }
    }
}
