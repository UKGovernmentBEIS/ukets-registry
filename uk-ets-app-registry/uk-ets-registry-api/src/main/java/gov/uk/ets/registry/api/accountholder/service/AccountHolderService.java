package gov.uk.ets.registry.api.accountholder.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.AccountHolderRepresentative;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.domain.types.AccountContactType;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.repository.AccountHolderRepository;
import gov.uk.ets.registry.api.account.repository.AccountHolderRepresentativeRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.service.AccountConversionService;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderContactInfoDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderFileDTO;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderTypeAheadSearchResultDTO;
import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.authz.Scope;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.error.FileUploadException;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.file.upload.requesteddocs.domain.AccountHolderFile;
import gov.uk.ets.registry.api.file.upload.requesteddocs.repository.AccountHolderFileRepository;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountHolderService {

    private final AccountHolderRepository holderRepository;
    private final AccountHolderFileRepository holderFileRepository;
    private final AccountRepository accountRepository;
    private final UploadedFilesRepository uploadedFilesRepository;
    private final AccountHolderRepresentativeRepository accountHolderRepresentativeRepository;
    private final AccountConversionService accountConversionService;
    private final EventService eventService;
    private final AuthorizationService authorizationService;

    /**
     * Retrieves the account holders with the provided criteria.
     *
     * @param identifier   The business identifier.
     * @param includeTypes The AccountHolderType's to include in the result set
     * @return a list of account holders.
     */
    public List<AccountHolderTypeAheadSearchResultDTO> getAccountHolders(String identifier,
                                                                         Set<AccountHolderType> includeTypes) {
        return holderRepository.getAccountHolders(identifier, includeTypes);
    }

    /**
     * Retrieves the account holders with the provided criteria.
     *
     * @param name The name.
     * @param type The type (e.g. organisation, individual, government).
     * @return a list of account holders.
     */
    public List<AccountHolderTypeAheadSearchResultDTO> getAccountHolders(String name, AccountHolderType type) {
        return holderRepository.getAccountHolders(name, type);
    }

    /**
     * Retrieves the account holders with the provided criteria.
     *
     * @param input   The account holder name or identifier.
     * @param type The AccountHolderType to include in the result set
     * @return a list of account holders.
     */
    @Transactional(readOnly = true)
    public List<AccountHolderTypeAheadSearchResultDTO> getAllAccountHoldersByNameAndIdentifier(String input,
                                                                                               AccountHolderType type) {
        boolean isCurrentUserAdmin = authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ANY_ADMIN);
        if (isCurrentUserAdmin) {
            List<AccountHolderTypeAheadSearchResultDTO> holdersByName =
                    this.getAccountHolders(input.toUpperCase(), type);
            List<AccountHolderTypeAheadSearchResultDTO> holdersByIdentifier =
                    this.getAccountHolders(input, Set.of(type));
            return Stream.concat(holdersByName.stream(), holdersByIdentifier.stream())
                    .toList();
        } else {
            final String urid = authorizationService.getUrid();
            final List<AccountHolderTypeAheadSearchResultDTO> holdersByName =
                    holderRepository.findByNameAndTypeAndUser(input, type, urid, AccountAccessState.ACTIVE);
            final List<AccountHolderTypeAheadSearchResultDTO> holdersByIdentifier =
                    holderRepository.findByIdentifierAndTypeAndUser(input, Set.of(type), urid, AccountAccessState.ACTIVE);
            return Stream.concat(holdersByName.stream(), holdersByIdentifier.stream())
                    .toList();
        }
    }

    /**
     * Retrieves the submitted account holder files
     *
     * @param accountIdentifier The account identifier
     * @return The list of account holder files
     */
    @Transactional(readOnly = true)
    public List<AccountHolderFileDTO> getAccountHolderFiles(Long accountIdentifier) {
        Account account = accountRepository.findByIdentifier(accountIdentifier).orElse(null);
        if (account == null) {
            return new ArrayList<>();
        }
        return holderRepository.getAccountHolderFiles(account.getAccountHolder().getIdentifier());
    }

    /**
     * Searches for account holders connected to the provided user.
     *
     * @param type        The account holder type.
     * @param accessState The access state.
     * @return some account holders.
     */
    @Transactional(readOnly = true)
    public List<AccountHolderDTO> getAccountHolders(AccountHolderType type, AccountAccessState accessState) {
        boolean isCurrentUserAdmin = authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ANY_ADMIN);
        final String urid = authorizationService.getUrid();
        return isCurrentUserAdmin ? holderRepository.findAccountHoldersByType(type) :
                holderRepository.findAccountHoldersByUserTypeAndState(urid, type, accessState);
    }

    /**
     * Retrieves the primary and alternative account holder contacts.
     *
     * @param identifier The account holder business identifier.
     * @return The primary and alternative account holder representative.
     */
    public AccountHolderContactInfoDTO getAccountHolderContactInfo(Long identifier) {
        AccountHolderContactInfoDTO dto = new AccountHolderContactInfoDTO();

        List<AccountHolderRepresentative> accountHolderRepresentatives = accountHolderRepresentativeRepository
            .getAccountHolderRepresentatives(identifier);

        Optional<AccountHolderRepresentative> primaryContact = accountHolderRepresentatives.stream()
            .filter(ahr -> AccountContactType.PRIMARY == ahr.getAccountContactType()).findFirst();
        primaryContact.ifPresent(accountHolderRepresentative -> dto
            .setPrimaryContact(accountConversionService.convert(accountHolderRepresentative)));

        Optional<AccountHolderRepresentative> alternativeContact = accountHolderRepresentatives.stream()
            .filter(ahr -> AccountContactType.ALTERNATIVE == ahr.getAccountContactType()).findFirst();
        alternativeContact.ifPresent(accountHolderRepresentative -> dto
            .setAlternativeContact(accountConversionService.convert(accountHolderRepresentative)));

        return dto;
    }

    /**
     * Retrieves the account holder based on its business identifier.
     *
     * @param identifier The business identifier.
     * @return an account holder domain object.
     */
    @Transactional(readOnly = true)
    public AccountHolder findAccountHolder(Long identifier) {
        return holderRepository.getAccountHolder(identifier);
    }

    /**
     * Retrieves the account holder based on its business identifier.
     *
     * @param identifier The business identifier.
     * @return an account holder transfer object.
     */
    @Transactional(readOnly = true)
    public AccountHolderDTO getAccountHolder(Long identifier) {
        AccountHolderDTO result = null;
        AccountHolder holder = holderRepository.getAccountHolder(identifier);
        if (holder != null) {
            result = accountConversionService.convert(holder);
        }
        return result;
    }

    /**
     * Retrieves the file by id
     *
     * @param fileId The id of the file
     * @return The file
     */
    public UploadedFile getFileById(Long fileId) {
        return uploadedFilesRepository.findById(fileId)
            .orElseThrow(() -> new FileUploadException("Error while fetching the file"));
    }

    /**
     * Deletes an account holder file.
     *
     * @param id     the account holder id
     * @param fileId The id of the file
     */
    public void deleteAccountHolderFile(Long id, Long fileId) {
        AccountHolder accountHolder = holderRepository.getAccountHolder(id);
        // verify that this file belongs to the specified account holder
        AccountHolderFile holderFile = holderFileRepository.findByIdAndAccountHolder(fileId, accountHolder);
        if (holderFile == null) {
            throw new AuthorizationServiceException("File not found");
        }
        holderFileRepository.delete(holderFile);
        uploadedFilesRepository.delete(holderFile.getUploadedFile());

        // publish account event
        var urId = authorizationService.getUrid();
        eventService.createAndPublishEvent(
            holderFile.getUploadedFile().getTask().getAccount().getIdentifier().toString(),
            urId, holderFile.getUploadedFile().getFileName(),
            EventType.ACCOUNT_HOLDER_DELETE_SUBMITTED_DOCUMENT, "Account Holder documentation deleted");
    }

    @Transactional(readOnly = true)
    public Boolean isOrphanedAccountHolder(Long accountHolderIdentifier, Long accountIdentifier) {
        return Optional.ofNullable(holderRepository.getAccountHolder(accountHolderIdentifier)).map(accountHolder ->
                        accountHolder.getAccounts().stream()
                                .noneMatch(account -> !account.getIdentifier().equals(accountIdentifier)))
                .orElse(true);
    }
}
