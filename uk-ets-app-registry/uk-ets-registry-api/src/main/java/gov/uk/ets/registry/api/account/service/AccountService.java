package gov.uk.ets.registry.api.account.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.AccountFilter;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.AccountHolderRepresentative;
import gov.uk.ets.registry.api.account.domain.AccountOwnership;
import gov.uk.ets.registry.api.account.domain.AircraftOperator;
import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.domain.InstallationOwnership;
import gov.uk.ets.registry.api.account.domain.InstallationOwnershipStatus;
import gov.uk.ets.registry.api.account.domain.SalesContact;
import gov.uk.ets.registry.api.account.domain.UnitBlockFilter;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.domain.types.AccountContactType;
import gov.uk.ets.registry.api.account.domain.types.AccountOwnershipStatus;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.account.repository.AccountAccessRepository;
import gov.uk.ets.registry.api.account.repository.AccountHolderRepository;
import gov.uk.ets.registry.api.account.repository.AccountHolderRepresentativeRepository;
import gov.uk.ets.registry.api.account.repository.AccountOwnershipRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.repository.CompliantEntityRepository;
import gov.uk.ets.registry.api.account.repository.InstallationOwnershipRepository;
import gov.uk.ets.registry.api.account.shared.AccountActionError;
import gov.uk.ets.registry.api.account.shared.AccountActionException;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.shared.AccountHoldingsSummariesMerger;
import gov.uk.ets.registry.api.account.shared.AccountProjection;
import gov.uk.ets.registry.api.account.shared.AccountStatusActionOptionsFactory;
import gov.uk.ets.registry.api.account.web.model.AccountClosureDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDetailsDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderContactInfoDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderRepresentativeDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHoldingDetailsDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHoldingsSummaryDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHoldingsSummaryResultDTO;
import gov.uk.ets.registry.api.account.web.model.AccountStatusActionOptionDTO;
import gov.uk.ets.registry.api.account.web.model.AccountStatusChangeDTO;
import gov.uk.ets.registry.api.account.web.model.AuthorisedRepresentativeDTO;
import gov.uk.ets.registry.api.account.web.model.InstallationOrAircraftOperatorDTO;
import gov.uk.ets.registry.api.account.web.model.InstallationSearchResultDTO;
import gov.uk.ets.registry.api.account.web.model.LegalRepresentativeDetailsDTO;
import gov.uk.ets.registry.api.account.web.model.OperatorType;
import gov.uk.ets.registry.api.account.web.model.SalesContactDetailsDTO;
import gov.uk.ets.registry.api.ar.service.AuthorizedRepresentativeService;
import gov.uk.ets.registry.api.auditevent.web.AuditEventDTO;
import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.authz.Scope;
import gov.uk.ets.registry.api.common.ConversionService;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.display.DisplayNameUtils;
import gov.uk.ets.registry.api.common.error.UkEtsException;
import gov.uk.ets.registry.api.common.model.entities.Contact;
import gov.uk.ets.registry.api.common.model.services.PersistenceService;
import gov.uk.ets.registry.api.common.model.types.Status;
import gov.uk.ets.registry.api.common.reports.ReportRequestService;
import gov.uk.ets.registry.api.common.reports.ReportRoleMappingService;
import gov.uk.ets.registry.api.common.search.JpaQueryExtractor;
import gov.uk.ets.registry.api.common.view.ConversionParameters;
import gov.uk.ets.registry.api.common.view.RequestDTO;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.wrappers.BulkArAccountDTO;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckError;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckException;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGroup;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckService;
import gov.uk.ets.registry.api.transaction.checks.RequiredFieldException;
import gov.uk.ets.registry.api.transaction.common.FullAccountIdentifierParser;
import gov.uk.ets.registry.api.transaction.common.GeneratorService;
import gov.uk.ets.registry.api.transaction.domain.QUnitBlock;
import gov.uk.ets.registry.api.transaction.domain.TransactionFilter;
import gov.uk.ets.registry.api.transaction.domain.TransactionFilterFactory;
import gov.uk.ets.registry.api.transaction.domain.TransactionProjection;
import gov.uk.ets.registry.api.transaction.domain.UnitBlock;
import gov.uk.ets.registry.api.transaction.domain.data.AccountInfo;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TrustedAccountListRulesDTO;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.repository.AccountTotalRepository;
import gov.uk.ets.registry.api.transaction.repository.SearchableTransactionRepository;
import gov.uk.ets.registry.api.transaction.repository.UnitBlockRepository;
import gov.uk.ets.registry.api.transaction.service.TransactionProposalService;
import gov.uk.ets.registry.api.transaction.service.TransactionProposalTaskService;
import gov.uk.ets.registry.api.transaction.shared.TransactionSearchCriteria;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import gov.uk.ets.registry.api.user.service.UserService;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import gov.uk.ets.reports.model.ReportQueryInfo;
import gov.uk.ets.reports.model.ReportType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.keycloak.authorization.client.AuthorizationDeniedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static gov.uk.ets.registry.api.account.shared.InstallationAndAccountTransferError.MULTIPLE_INSTALLATION_PERMIT_IDS_NOT_ALLOWED;
import static java.util.stream.Collectors.toList;

/**
 * Service for accounts.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class AccountService {

    private static final String ACCOUNT_AUTHORISED_REPRESENTATIVE_RESTORED =
        "Account authorised representative restored";
    private static final String ACCOUNT_AUTHORISED_REPRESENTATIVE_SUSPENDED =
        "Account authorised representative suspended";
    private static final String REMOVAL_REASON = "installation transfer";
    // TODO: Remove dependency from persistence service and use only the repositories
    private final PersistenceService persistenceService;
    private final ConversionService conversionService;
    private final AccountConversionService accountConversionService;
    private final GeneratorService generatorService;
    private final UserService userService;
    private final AccountRepository accountRepository;
    private final AccountHolderRepository holderRepository;
    private final UserRepository userRepository;
    private final AccountHolderRepresentativeRepository accountHolderRepresentativeRepository;
    private final CompliantEntityRepository compliantEntityRepository;
    private final AccountDTOFactory accountDTOFactory;
    private final AuthorizationService authorizationService;
    private final UnitBlockRepository unitBlockRepository;

    private final TransactionProposalService transactionProposalService;

    private final BusinessCheckService businessCheckService;

    private final EventService eventService;

    private final TransactionFilterFactory transactionFilterFactory;
    private final SearchableTransactionRepository searchableTransactionRepository;
    private final AccountTotalRepository accountTotalRepository;
    private final ReportRequestService reportRequestService;

    private final Mapper mapper;

    private final TransferValidationService transferValidationService;

    private final InstallationOwnershipRepository installationOwnershipRepository;

    private final AccountOwnershipRepository accountOwnershipRepository;
    private final TaskEventService taskEventService;
    private final AccountValidationService accountValidationService;

    private final AccountAccessRepository accountAccessRepository;
    private final AuthorizedRepresentativeService authorizedRepresentativeService;
    private final AccountStatusService accountStatusService;
    private final ReportRoleMappingService reportRoleMappingService;

    /**
     * Creates an account.
     *
     * @param accountDTO The account transfer object.
     * @return the request dto
     */
    @Transactional
    @EmitsGroupNotifications(GroupNotificationType.ACCOUNT_PROPOSAL)
    public RequestDTO proposeAccount(AccountDTO accountDTO) {
        RequestType requestType = accountDTO.getOperator() != null &&
            OperatorType.INSTALLATION_TRANSFER.name().equals(accountDTO.getOperator().getType()) ?
            RequestType.ACCOUNT_OPENING_INSTALLATION_TRANSFER_REQUEST : RequestType.ACCOUNT_OPENING_REQUEST;
        accountDTO.setTrustedAccountListRules(getDefaultTrustedAccountRules(accountDTO));
        Task accountOpeningTask = createAccountOpeningTask(requestType, accountDTO, new Date());
        return new RequestDTO(accountOpeningTask.getRequestId(), accountOpeningTask.getType(),
            accountOpeningTask.getStatus());
    }

    private TrustedAccountListRulesDTO getDefaultTrustedAccountRules(AccountDTO accountDTO){
        TrustedAccountListRulesDTO rulesDTO = new TrustedAccountListRulesDTO();
        if(AccountType.OPERATOR_HOLDING_ACCOUNT.toString().equals(accountDTO.getAccountType()) ||
                AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT.toString().equals(accountDTO.getAccountType())){
            rulesDTO.setRule1(true);
            rulesDTO.setRule2(false);
            rulesDTO.setRule3(false);
        } else if(AccountType.TRADING_ACCOUNT.toString().equals(accountDTO.getAccountType()) ||
                AccountType.PERSON_HOLDING_ACCOUNT.toString().equals(accountDTO.getAccountType())) {
            rulesDTO.setRule1(true);
            rulesDTO.setRule2(false);
        } else {
            rulesDTO = accountDTO.getTrustedAccountListRules();
        }
        return rulesDTO;
    }

    /**
     * Closes the account.
     *
     * @param fullIdentifier The account full identifier.
     * @param closureDTO     The account closure DTO.
     * @return The task request identifier.
     */
    @Transactional
    public Long closeAccount(String fullIdentifier, AccountClosureDTO closureDTO) {

        Account account = accountRepository.findByFullIdentifier(fullIdentifier).orElseThrow(
            () -> new UkEtsException("Requested account was not found"));
        account.setAccountStatus(AccountStatus.CLOSURE_PENDING);

        Task accountClosureTask = new Task();
        accountClosureTask.setRequestId(persistenceService.getNextBusinessIdentifier(Task.class));
        accountClosureTask.setType(RequestType.ACCOUNT_CLOSURE_REQUEST);
        User currentUser = userService.getCurrentUser();
        accountClosureTask.setInitiatedBy(currentUser);
        accountClosureTask.setInitiatedDate(new Date());
        accountClosureTask.setStatus(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
        accountClosureTask.setAccount(account);
        accountClosureTask.setDifference(mapper.convertToJson(closureDTO));
        persistenceService.save(accountClosureTask);

        String action = "Account closure task request submitted.";
        eventService.createAndPublishEvent(account.getIdentifier().toString(), currentUser.getUrid(),
            String.format("Task requestId %s.", accountClosureTask.getRequestId()),
            EventType.ACCOUNT_CLOSURE_REQUEST, action);
        taskEventService.createAndPublishTaskAndAccountRequestEvent(accountClosureTask, currentUser.getUrid());

        return accountClosureTask.getRequestId();
    }

    /**
     * Creates an account and returns the enriched accountDTO
     * with e.g. the accountIdentifier, fullIdentifier and other generated information of the new account.
     *
     * @param accountDTO the accountDTO
     * @return the updated accountDTO
     */
    @Transactional
    public AccountDTO openAccount(AccountDTO accountDTO) {
        Account account = new Account();
        account.setAccountName(accountDTO.getAccountDetails().getName());
        final AccountType type = AccountType.parse(accountDTO.getAccountType());
        account.setRegistryAccountType(type.getRegistryType());
        account.setKyotoAccountType(type.getKyotoType());
        account.setIdentifier(accountRepository.getNextIdentifier());
        account.setStatus(Status.ACTIVE);
        account.setAccountStatus(AccountStatus.OPEN);
        account.setBillingAddressSameAsAccountHolderAddress(accountDTO.isAccountDetailsSameBillingAddress());
        Date insertDate = new Date();
        account.setOpeningDate(insertDate);
        account.setBalance(0L);
        if (AccountType.isCpIndependent(type)) {
            account.setCommitmentPeriodCode(CommitmentPeriod.CP0.getCode());
        } else {
            account.setCommitmentPeriodCode(CommitmentPeriod.getCurrentPeriod().getCode());
        }
        account.setCheckDigits(generatorService
            .calculateCheckDigits(type.getKyotoCode(), account.getIdentifier(), account.getCommitmentPeriodCode()));
        account.setFullIdentifier(String
            .format("%s-%d-%d-%d-%d", type.getRegistryCode(), type.getKyotoCode(), account.getIdentifier(),
                account.getCommitmentPeriodCode(), account.getCheckDigits()));
        account.setAccountType(type.getLabel());
        account.setRegistryCode(type.getRegistryCode());

        AccountDetailsDTO detailsDTO = accountDTO.getAccountDetails();
        if (detailsDTO != null) {
            account.setAccountName(detailsDTO.getName());
            if(detailsDTO.getSalesContactDetails() != null) {
            	account.setSalesContact(createSalesContactDetails(detailsDTO.getSalesContactDetails()));
            }
            if (detailsDTO.getAddress() != null) {
                Contact accountAddress = conversionService
                    .convert(ConversionParameters.builder()
                        .address(detailsDTO.getAddress())
                        .billingEmail1(detailsDTO.getBillingEmail1())
                        .billingEmail2(detailsDTO.getBillingEmail2())
                        .billingContactDetails(detailsDTO.getBillingContactDetails())
                        .build());
                accountAddress = persistenceService.save(accountAddress);
                account.setContact(accountAddress);
            }
        }

        if (accountDTO.getTrustedAccountListRules() != null) {
            accountDTO.setTrustedAccountListRules(provideTransactionRulesDefaultValues(
                accountDTO.getTrustedAccountListRules()));
            account.setApprovalOfSecondAuthorisedRepresentativeIsRequired(
                    accountDTO.getTrustedAccountListRules().getRule1());
            account.setTransfersToAccountsNotOnTheTrustedListAreAllowed(
                accountDTO.getTrustedAccountListRules().getRule2());
            account.setSinglePersonApprovalRequired(
                accountDTO.getTrustedAccountListRules().getRule3());
        }

        AccountHolder holder = new AccountHolder();
        if (accountDTO.getAccountHolder() != null) {
            if (accountDTO.getAccountHolder().getId() != null) {
                holder = holderRepository.getAccountHolder(accountDTO.getAccountHolder().getId());
            } else if (accountDTO.getChangedAccountHolderId() != null) {
                holder = holderRepository.getAccountHolder(accountDTO.getChangedAccountHolderId());
            } else {
                holder = createHolder(accountDTO.getAccountHolder());
                setupAccountHolderContactInfo(holder, accountDTO);
            }
        }
        account.setAccountHolder(holder);
        InstallationOrAircraftOperatorDTO installationOrAircraftOperatorDTO = accountDTO.getOperator();
        if (installationOrAircraftOperatorDTO != null) {
            OperatorType operatorType = OperatorType.valueOf(installationOrAircraftOperatorDTO.getType());
            switch (operatorType) {
                case INSTALLATION:
                    Installation installation = createInstallation(installationOrAircraftOperatorDTO);
                    account.setCompliantEntity(installation);
                    handleInstallationOwnership(account, installation, insertDate);
                    break;
                case INSTALLATION_TRANSFER:
                    Optional<CompliantEntity> compliantEntity =
                        compliantEntityRepository.findByIdentifier(installationOrAircraftOperatorDTO.getIdentifier());
                    if (compliantEntity.isEmpty()) {
                        throw new IllegalStateException("Compliant entity not found in DB.");
                    }
                    Installation existingInstallation = (Installation) Hibernate.unproxy(compliantEntity.get());
                    existingInstallation.setInstallationName(installationOrAircraftOperatorDTO.getName());
                    existingInstallation.setPermitIdentifier(installationOrAircraftOperatorDTO.getPermit().getId());
                    if (installationOrAircraftOperatorDTO.getChangedRegulator() != null) {
                        existingInstallation.setRegulator(installationOrAircraftOperatorDTO.getChangedRegulator());
                    }
                    account.setCompliantEntity(existingInstallation);
                    persistenceService.save(existingInstallation);
                    handleInstallationOwnershipOnTransfer(accountDTO.getInstallationToBeTransferred(), account,
                        existingInstallation, insertDate);
                    break;
                case AIRCRAFT_OPERATOR:
                    AircraftOperator aircraftOperator = createAircraftOperator(installationOrAircraftOperatorDTO);
                    account.setCompliantEntity(aircraftOperator);
                    break;
            }
        }
        handleAccountOwnership(account, holder);
        persistenceService.save(account);
        createAuthorisedRepresentatives(accountDTO, account, AccountAccessState.ACTIVE);
        return this.getAccountDTO(account.getIdentifier());
    }

    private TrustedAccountListRulesDTO provideTransactionRulesDefaultValues(TrustedAccountListRulesDTO dto) {
        if (dto.getRule1() == null) {
            dto.setRule1(true);
        }
        if (dto.getRule2() == null) {
            dto.setRule2(false);
        }
        if (dto.getRule3() == null) {
            dto.setRule3(true);
        }
        return dto;
    }

    private Installation createInstallation(InstallationOrAircraftOperatorDTO installationDTO) {
        Installation installation = new Installation();
        installation.setIdentifier(compliantEntityRepository.getNextIdentifier());
        installation.setActivityType(installationDTO.getActivityType().name());
        installation.setInstallationName(installationDTO.getName());
        installation.setPermitIdentifier(installationDTO.getPermit().getId());
        installation.setRegulator(getRegulatorIfChanged(installationDTO));
        installation.setStartYear(installationDTO.getFirstYear());
        installation.setEndYear(installationDTO.getLastYear());
        installation = persistenceService.save(installation);
        return installation;
    }

    private AircraftOperator createAircraftOperator(InstallationOrAircraftOperatorDTO aircraftOperatorDTO) {
        AircraftOperator aircraftOperator = new AircraftOperator();
        aircraftOperator.setIdentifier(compliantEntityRepository.getNextIdentifier());
        aircraftOperator.setRegulator(getRegulatorIfChanged(aircraftOperatorDTO));
        aircraftOperator.setMonitoringPlanIdentifier(aircraftOperatorDTO.getMonitoringPlan().getId());
        aircraftOperator.setStartYear(aircraftOperatorDTO.getFirstYear());
        aircraftOperator.setEndYear(aircraftOperatorDTO.getLastYear());
        aircraftOperator = persistenceService.save(aircraftOperator);
        return aircraftOperator;
    }
    
    private SalesContact createSalesContactDetails(SalesContactDetailsDTO salesContactDetailsDTO) {
    	SalesContact salesContact = new SalesContact();
    	if(salesContactDetailsDTO.getEmailAddress() != null && !salesContactDetailsDTO.getEmailAddress().isEmpty()) {
    		salesContact.setEmailAddress(salesContactDetailsDTO.getEmailAddress().getEmailAddress());
    	}
    	salesContact.setPhoneNumber(salesContactDetailsDTO.getPhoneNumber());
    	salesContact.setPhoneNumberCountry(salesContactDetailsDTO.getPhoneNumberCountryCode());
        return salesContact;
    }

    private RegulatorType getRegulatorIfChanged(InstallationOrAircraftOperatorDTO installationOrAircraftOperatorDTO) {
        if (installationOrAircraftOperatorDTO.getChangedRegulator() != null) {
            return installationOrAircraftOperatorDTO.getChangedRegulator();
        } else {
            return installationOrAircraftOperatorDTO.getRegulator();
        }
    }

    private void handleInstallationOwnership(Account account, Installation installation, Date insertDate) {
        InstallationOwnership newInstallationOwnership = new InstallationOwnership();
        newInstallationOwnership.setInstallation(installation);
        newInstallationOwnership.setAccount(account);
        newInstallationOwnership.setStatus(InstallationOwnershipStatus.ACTIVE);
        newInstallationOwnership.setPermitIdentifier(installation.getPermitIdentifier());
        newInstallationOwnership.setOwnershipDate(insertDate);
        installationOwnershipRepository.save(newInstallationOwnership);
    }

    private void handleInstallationOwnershipOnTransfer(InstallationOrAircraftOperatorDTO installationToBeTransferred,
                                                       Account newAccount, Installation installation,
                                                       Date insertDate) {
        Optional<Account> oldAccount = accountRepository
            .findByCompliantEntityIdentifier(installationToBeTransferred.getIdentifier());
        if (oldAccount.isEmpty()) {
            throw new IllegalStateException("Old account not found in DB.");
        }
        List<InstallationOwnership> activeInstallationOwnerships = installationOwnershipRepository
            .findByAccountAndInstallationAndStatus(oldAccount.get(), installation, InstallationOwnershipStatus.ACTIVE);
        if (activeInstallationOwnerships.size() != 1) {
            throw new IllegalStateException("Old installation ownership is in invalid state");
        }
        InstallationOwnership activeInstallationOwnership = activeInstallationOwnerships.get(0);
        activeInstallationOwnership.setStatus(InstallationOwnershipStatus.INACTIVE);
        installationOwnershipRepository.save(activeInstallationOwnership);
        InstallationOwnership newInstallationOwnership = new InstallationOwnership();
        newInstallationOwnership.setInstallation(installation);
        newInstallationOwnership.setAccount(newAccount);
        newInstallationOwnership.setStatus(InstallationOwnershipStatus.ACTIVE);
        newInstallationOwnership.setOwnershipDate(insertDate);
        newInstallationOwnership.setPermitIdentifier(installation.getPermitIdentifier());
        installationOwnershipRepository.save(newInstallationOwnership);
    }

    private void handleAccountOwnership(Account account, AccountHolder accountHolder) {
        AccountOwnership newAccountOwnership = new AccountOwnership();
        newAccountOwnership.setAccount(account);
        newAccountOwnership.setHolder(accountHolder);
        newAccountOwnership.setStatus(AccountOwnershipStatus.ACTIVE);
        newAccountOwnership.setDateOfOwnership(LocalDateTime.now());
        accountOwnershipRepository.save(newAccountOwnership);
    }

    /**
     * Updates the Account Holder and his contacts, if available.
     *
     * @param holderDTO the account holder DTO
     */
    @Transactional
    public void updateHolder(AccountHolderDTO holderDTO) {

        AccountHolder holder = holderRepository.getAccountHolder(holderDTO.getId());
        Contact contact = conversionService
            .convert(ConversionParameters.builder()
                .email(holderDTO.getEmailAddress())
                .address(holderDTO.getAddress())
                .phone(holderDTO.getPhoneNumber())
                .build());
        contact.setId(holder.getContact().getId());
        AccountHolder updatedHolder = accountConversionService.convert(holderDTO);
        updatedHolder.setId(holder.getId());
        updatedHolder.setIdentifier(holderDTO.getId());
        updatedHolder.setContact(contact);
        persistenceService.save(contact);
        persistenceService.save(updatedHolder);
    }


    /**
     * Updates the Account Holder Representative's contacts.
     *
     * @param representativeDTO the account holder representative DTO
     * @param primary           true for the primary contact, false for the alternative
     */
    @Transactional
    public void updateContact(Long accountHolderIdentifier,
                              AccountHolderRepresentativeDTO representativeDTO,
                              boolean primary) {
        AccountHolderRepresentative representative =
            accountHolderRepresentativeRepository.getAccountHolderRepresentative(accountHolderIdentifier,
                primary ?
                    AccountContactType.PRIMARY :
                    AccountContactType.ALTERNATIVE);
        Contact contact = conversionService
            .convert(ConversionParameters.builder()
                .email(representativeDTO.getEmailAddress())
                .address(representativeDTO.getAddress())
                .phone(representativeDTO.getPhoneNumber())
                .build());
        contact.setId(representative.getContact().getId());
        contact.setPositionInCompany(representativeDTO.getPositionInCompany());
        representative.setContact(contact);
        representative.setId(representative.getId());
        representative.setFirstName(representativeDTO.getDetails().getFirstName());
        representative.setLastName(representativeDTO.getDetails().getLastName());
        representative.setAlsoKnownAs(representativeDTO.getDetails().getAka());
        persistenceService.save(contact);
        accountHolderRepresentativeRepository.save(representative);
    }

    @Transactional
    public void insertContact(AccountHolder accountHolder,
                              AccountHolderRepresentativeDTO representativeDTO,
                              boolean primary) {
        Contact contact = conversionService
            .convert(ConversionParameters.builder()
                .email(representativeDTO.getEmailAddress())
                .address(representativeDTO.getAddress())
                .phone(representativeDTO.getPhoneNumber())
                .build());
        contact.setPositionInCompany(representativeDTO.getPositionInCompany());
        persistenceService.save(contact);
        AccountHolderRepresentative representative = new AccountHolderRepresentative();
        representative.setContact(contact);
        representative.setAccountContactType(primary ? AccountContactType.PRIMARY : AccountContactType.ALTERNATIVE);
        representative.setId(representative.getId());
        representative.setFirstName(representativeDTO.getDetails().getFirstName());
        representative.setLastName(representativeDTO.getDetails().getLastName());
        representative.setAlsoKnownAs(representativeDTO.getDetails().getAka());
        representative.setAccountHolder(accountHolder);
        persistenceService.save(representative);
        accountHolderRepresentativeRepository.save(representative);
    }

    @Transactional
    public void deleteContact(AccountHolderRepresentativeDTO representativeDTO) {
        Optional<AccountHolderRepresentative> accountHolderRepresentative =
            accountHolderRepresentativeRepository.findById(representativeDTO.getId());
        accountHolderRepresentative.ifPresent(accountHolderRepresentativeRepository::delete);
    }


    private void setupAccountHolderContactInfo(AccountHolder holder, AccountDTO accountDTO) {
        AccountHolderContactInfoDTO accountHolderContactInfoDTO = Optional
            .ofNullable(accountDTO.getAccountHolderContactInfo())
            .orElseThrow(
                () -> new RequiredFieldException("Account cannot be created without account holder contact info"));
        addAccountHolderContact(holder,
            Optional.ofNullable(accountHolderContactInfoDTO.getPrimaryContact())
                .orElseThrow(() -> new RequiredFieldException("Primary contact is required for account opening")
                ), true);
        if (accountHolderContactInfoDTO.getAlternativeContact() != null) {
            addAccountHolderContact(holder, accountHolderContactInfoDTO.getAlternativeContact(), false);
        }
    }

    public void addAccountHolderContact(AccountHolder holder,
                                        AccountHolderRepresentativeDTO accountHolderRepresentativeDTO,
                                        boolean primary) {
        AccountHolderRepresentative representative = new AccountHolderRepresentative();
        representative.setAccountContactType(primary ? AccountContactType.PRIMARY : AccountContactType.ALTERNATIVE);
        representative.setAccountHolder(holder);

        Contact contact = conversionService
            .convert(ConversionParameters.builder()
                .email(accountHolderRepresentativeDTO.getEmailAddress())
                .address(accountHolderRepresentativeDTO.getAddress())
                .phone(accountHolderRepresentativeDTO.getPhoneNumber())
                .positionInCompany(accountHolderRepresentativeDTO.getPositionInCompany())
                .build());
        persistenceService.save(contact);

        representative.setContact(contact);

        if (accountHolderRepresentativeDTO.getDetails() != null) {
            LegalRepresentativeDetailsDTO detailsDTO = accountHolderRepresentativeDTO.getDetails();
            representative.setFirstName(detailsDTO.getFirstName());
            representative.setLastName(detailsDTO.getLastName());
            representative.setAlsoKnownAs(detailsDTO.getAka());
        }
        accountHolderRepresentativeRepository.save(representative);
    }

    /**
     * Create authorised representatives.
     *
     * @param accountDTO The account data transfer object.
     * @param account    The account.
     */
    private void createAuthorisedRepresentatives(AccountDTO accountDTO, Account account,
                                                 AccountAccessState accountAccessState) {
        if (accountDTO.getAuthorisedRepresentatives() != null) {
            for (AuthorisedRepresentativeDTO ar : accountDTO.getAuthorisedRepresentatives()) {
                AccountAccess access = new AccountAccess();
                access.setAccount(account);
                User arUser = userRepository.findByUrid(ar.getUrid());
                access.setUser(arUser);
                access.setState(accountAccessState);
                access.setRight(ar.getRight());
                if (arUser != null) {
                    persistenceService.save(access);
                }
            }
        }
    }

    /**
     * Creates the account holder.
     *
     * @param holderDTO The account holder transfer object.
     * @return an account holder.
     */
    public AccountHolder createHolder(AccountHolderDTO holderDTO) {
        AccountHolder holder = null;
        if (holderDTO != null) {
            Contact contact = conversionService
                .convert(ConversionParameters.builder()
                    .email(holderDTO.getEmailAddress())
                    .address(holderDTO.getAddress())
                    .phone(holderDTO.getPhoneNumber())
                    .build());
            persistenceService.save(contact);

            holder = accountConversionService.convert(holderDTO);
            holder.setContact(contact);
            holder.setIdentifier(persistenceService.getNextBusinessIdentifier(AccountHolder.class));
            persistenceService.save(holder);
        }
        return holder;
    }

    /**
     * Retrieves the legal representatives of an account holder based on its business identifier.
     *
     * @param identifier The account holder business identifier.
     * @return The primary and alternative account holder representative.
     * @deprecated since 0.8.0
     */
    @Deprecated(since = "0.8.0")
    public List<AccountHolderRepresentativeDTO> getAccountHolderRepresentatives(Long identifier) {
        List<AccountHolderRepresentativeDTO> result = new ArrayList<>();
        List<AccountHolderRepresentative> accountHolderRepresentatives = accountHolderRepresentativeRepository
            .getAccountHolderRepresentatives(identifier);
        for (AccountHolderRepresentative legalRepresentative : accountHolderRepresentatives) {
            AccountHolderRepresentativeDTO convert = accountConversionService.convert(legalRepresentative);
            result.add(convert);
        }

        return result;
    }

    /**
     * Searches for Accounts.
     */
    public Page<AccountProjection> search(AccountFilter filter, Pageable pageable, boolean isReport) {
        if (authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ENROLLED_NON_ADMIN)) {
            filter.hasLimitedScope(true);
            filter.setExcludedAccessStates(
                Stream.of(AccountAccessState.values())
                    .filter(Predicate.not(AccountAccessState.ACTIVE::equals))
                    .collect(toList()));
            filter.addExcludedAccountStatus(
                AccountStatus.CLOSED, AccountStatus.SUSPENDED, AccountStatus.TRANSFER_PENDING,
                AccountStatus.SUSPENDED_PARTIALLY, AccountStatus.PROPOSED);
        }
        if (filter.getAuthorizedRepresentativeUrid() != null){
            filter.setAccountFullIdentifiers(
                    accountRepository.findAccountsByUrId(filter.getAuthorizedRepresentativeUrid()));
        }
        filter.setAuthorizedRepresentativeUrid(userService.getCurrentUser().getUrid());
        filter.addExcludedAccountStatus(AccountStatus.REJECTED);
        if (isReport) {
            JPAQuery<AccountProjection> query = accountRepository.getQuery(filter);
            reportRequestService
                .requestReport(ReportType.R0007, reportRoleMappingService.getUserReportRequestingRole(), 
                        JpaQueryExtractor.extractReportQueryInfo(query.createQuery()), null);
            return Page.empty();
        }
        return accountRepository.search(filter, pageable);
    }

    /**
     * Retrieves an account by its business identifier.
     */
    @Transactional(readOnly = true)
    public AccountDTO getAccountDTO(Long identifier) {
        Account account = accountRepository.findByIdentifierWithAccountHolder(identifier).orElse(null);
        if (account == null) {
            return new AccountDTO();
        }
        checkViewAccountPermissions(account);
        AccountDTO accountDTO = accountDTOFactory.create(account);
        accountDTO.setTransactionsAllowed(checkTransactionsAllowed(identifier, accountDTO));
        accountDTO.setCanBeClosed(accountValidationService.checkAccountClosureRequestEligibility(account));

        return accountDTO;
    }

    /**
     * Get Installation or Aircraft operator account information
     *
     * @param identifier Account Identifier
     * @return The Operator information
     */
    @Transactional(readOnly = true)
    public InstallationOrAircraftOperatorDTO getInstallationOrAircraftOperatorDTO(Long identifier) {
        Account account = accountRepository.findByIdentifier(identifier).orElse(null);
        if (account == null) {
            return new InstallationOrAircraftOperatorDTO();
        }
        checkViewAccountPermissions(account);
        AccountDTO accountDTO = new AccountDTO();
        accountDTOFactory.setupOperator(accountDTO, account);
        return accountDTO.getOperator();
    }

    private boolean checkTransactionsAllowed(Long identifier, AccountDTO accountDTO) {
        boolean currentUserIsAdmin = authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ANY_ADMIN);
        AccountSummary transferringAccount = new AccountSummary();
        transferringAccount.setAccountStatus(accountDTO.getAccountDetails().getAccountStatus());

        BusinessCheckContext context = new BusinessCheckContext(new TransactionSummary());
        context.store(BusinessCheckContext.TRANSFERRING_ACCOUNT, transferringAccount);
        boolean eligibleForTransaction = !transactionProposalService.getAvailableTransactionTypes(identifier,
                currentUserIsAdmin)
            .isEmpty();
        try {
            businessCheckService.performChecks(context, BusinessCheckGroup.TRANSFERRING_ACCOUNT, currentUserIsAdmin);
        } catch (BusinessCheckException businessCheckException) {
            eligibleForTransaction = false;
        }
        return eligibleForTransaction;
    }

    /**
     * Retrieves an accountInfo DTO by its business identifier. This is a minimal version of the {@link AccountDTO}
     */
    @Transactional(readOnly = true)
    public AccountInfo getAccountInfo(Long identifier) {
        AccountDTO accountDTO = this.getAccountDTO(identifier);
        AccountInfo.AccountInfoBuilder builder = AccountInfo.builder();
        builder.identifier(identifier).fullIdentifier(
            accountDTO.getAccountDetails().getAccountNumber());
        if (accountDTO.getAccountDetails() != null) {
            builder.accountName(accountDTO.getAccountDetails().getName())
                .accountHolderName(accountDTO.getAccountDetails().getAccountHolderName());
        }
        builder.kyotoAccountType(accountDTO.isKyotoAccountType());
        builder.accountType(accountDTO.getAccountType());
        return builder.build();
    }

    /**
     * Retrieves the display account name.
     *
     * @param fullIdentifier the account full identifier
     * @return the display name
     */
    @Transactional(readOnly = true)
    public String getAccountDisplayName(String fullIdentifier) {
        Account account = accountRepository.findByFullIdentifier(fullIdentifier).orElse(null);
        if (account == null) {
            return fullIdentifier;
        }

        AccountType accountType = AccountType.get(account.getRegistryAccountType(), account.getKyotoAccountType());
        String accountDisplayName = account.getFullIdentifier();
        if (accountType.isGovernmentAccount()) {
            accountDisplayName = account.getAccountName();
        }

        return accountDisplayName;
    }

    /**
     * Retrieves account by full identifier
     *
     * @param fullIdentifier The full description of the account
     * @return The account
     */
    @Transactional(readOnly = true)
    public Account getAccountFullIdentifier(String fullIdentifier) {
        return accountRepository.findByFullIdentifier(fullIdentifier).orElse(null);
    }

    private void checkViewAccountPermissions(Account account) {
        if (!authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ANY_ADMIN) &&
            (AccountStatus.SUSPENDED.equals(account.getAccountStatus())
                || AccountStatus.TRANSFER_PENDING.equals(account.getAccountStatus()))) {
            throw AccountActionException.create(AccountActionError.builder()
                .code(AccountActionError.ACCESS_NOT_ALLOWED)
                .message("You are not allowed to view this account")
                .build());
        }
    }

    /**
     * Retrieves an account by its identifier.
     */
    public Account getAccount(Long identifier) {
        return accountRepository.findByIdentifier(identifier).orElse(null);
    }

    /**
     * Retrieves the accounts owned by the same Account Holder.
     *
     * @param identifier the Account Holder identifier
     * @return the accounts
     */
    public List<Account> getAccountsByAccountHolder(Long identifier) {
        return accountRepository.getAccountsByAccountHolder_IdentifierEquals(identifier);
    }

    /**
     * Creates a task.
     *
     * @param account    The account.
     * @param insertDate The insert date.
     * @deprecated
     */
    @Deprecated
    private Task createTask(Account account, Date insertDate) {
        Task task = new Task();
        task.setRequestId(persistenceService.getNextBusinessIdentifier(Task.class));
        task.setType(RequestType.ACCOUNT_OPENING_REQUEST);
        User currentUser = userService.getCurrentUser();
        task.setInitiatedBy(currentUser);
        task.setInitiatedDate(insertDate);
        task.setAccount(account);
        task.setStatus(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);

        persistenceService.save(task);
        taskEventService.createAndPublishTaskAndAccountRequestEvent(task, currentUser.getUrid());

        return task;
    }


    private Task createAccountOpeningTask(RequestType requestType, AccountDTO accountDTO, Date insertDate) {
        Task task = new Task();
        task.setRequestId(persistenceService.getNextBusinessIdentifier(Task.class));
        task.setType(requestType);
        User currentUser = userService.getCurrentUser();
        task.setInitiatedBy(currentUser);
        task.setInitiatedDate(insertDate);

        //Keep the old account holder in case we need to reset it in task updates
        accountDTO.setOldAccountHolder(accountDTO.getAccountHolder());
        accountDTO.setOldAccountHolderContactInfo(accountDTO.getAccountHolderContactInfo());
        //Keep the old account holder id in case we need to reset it in task updates
        accountDTO.setChangedAccountHolderId(accountDTO.getAccountHolder() != null ?
            accountDTO.getAccountHolder().getId() : null);
        if (RequestType.ACCOUNT_OPENING_INSTALLATION_TRANSFER_REQUEST.equals(requestType)) {

            Long installationToBeTransferredId = accountDTO.getInstallationToBeTransferred().getIdentifier();

            Optional<Account> byIdentifier =
                accountRepository
                    .findByCompliantEntityIdentifier(accountDTO.getInstallationToBeTransferred().getIdentifier());

            if (byIdentifier.isEmpty()) {
                throw new IllegalArgumentException("Installation not found.");
            }
            Account account = byIdentifier.get();
            transferValidationService.validateNoPendingAllocationTasks(installationToBeTransferredId);

            //We need to keep the old account status in case of task rejection
            accountDTO.setOldAccountStatus(account.getAccountStatus());
            account.setAccountStatus(AccountStatus.TRANSFER_PENDING);

            //Save the transferring account into an AccountDTO for difference as well
            Account transferringAccount = (Account) Hibernate.unproxy(byIdentifier.get());
            setTransferringAccountDtoAndContactInfoDto(transferringAccount,accountDTO);
            task.setAccount(transferringAccount);
        }

        task.setDifference(mapper.convertToJson(accountDTO));
        task.setStatus(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);

        persistenceService.save(task);
        taskEventService.createAndPublishTaskAndAccountRequestEvent(task, currentUser.getUrid());
        return task;
    }

    /**
     * Retrieves the available account status change actions of an account based on its business identifier.
     *
     * @param identifier the account business identifier.
     * @return a list of available actions.
     */
    public List<AccountStatusActionOptionDTO> getAccountStatusAvailableActions(Long identifier) {

        Account account = accountRepository.findByIdentifier(identifier).orElseThrow();
        AccountType accountType = AccountType.get(account.getRegistryAccountType(), account.getKyotoAccountType());

        return AccountStatusActionOptionsFactory
            .createAvailableAccountStatusActionOptions(account.getAccountStatus(), accountType).getOptions();

    }

    /**
     * Sets a new account status to an account retrieved by its business identifier.
     */
    public AccountStatus updateAccountStatus(Long identifier, AccountStatusChangeDTO request) {

        Account account = accountRepository.findByIdentifier(identifier).orElseThrow();
        accountValidationService.validateAccountStatusChange(account.getAccountStatus(), request.getAccountStatus());
        account.setAccountStatus(request.getAccountStatus());
        persistenceService.save(account);

        User currentUser = userService.getCurrentUser();
        String action = "Change account status";
        String description = String.format("Account set to %s.", request.getAccountStatus().getDescription());
        eventService.createAndPublishEvent(account.getIdentifier().toString(), currentUser.getUrid(), description,
            EventType.ACCOUNT_STATUS_CHANGED, action);
        if (request.getComment() != null) {
            eventService
                .createAndPublishEvent(account.getIdentifier().toString(), currentUser.getUrid(), request.getComment(),
                    EventType.ACCOUNT_TASK_COMMENT, String.format("%s (comment)", action));
        }

        return request.getAccountStatus();
    }

    /**
     * Returns true/false if Monitoring Plan Identifier exists and account is not closed.
     *
     * @param monitoringPlanId The monitoring Plan Identifier to check.
     * @return true/false if Monitoring Plan Identifier exists and account is not closed.
     */
    public boolean monitoringPlanIdExists(String monitoringPlanId) {
        Long aircraftOperatorCount =
            accountRepository.getMonitoringPlanIdsForNonClosedAccounts(StringUtils.upperCase(monitoringPlanId));
        String error = "An account with the same monitoring plan ID already exists. " +
            " A second account is not permitted.";
        if (aircraftOperatorCount != 0) {
            throw AccountActionException.create(AccountActionError.builder()
                .code(AccountActionError.MULTIPLE_MONITORING_PLAN_IDS_NOT_ALLOWED)
                .message(error)
                .build());
        }
        return false;
    }

    /**
     * Fetches unit blocks belonging to the specified account and summarizes the result per unit-type/original
     * CP/applicable CP/SOP/reserved for transaction.
     *
     * @param identifier the account identifier.
     * @return a list of Summaries
     */
    public AccountHoldingsSummaryResultDTO getAccountHoldings(Long identifier) {

        List<UnitBlock> blocks = unitBlockRepository.findByAccountIdentifier(identifier);

        //Group unit blocks
        Map<Boolean, Map<UnitType, Map<CommitmentPeriod, Map<CommitmentPeriod, Map<Boolean, Long>>>>> result =
            blocks.stream().collect(
                Collectors.partitioningBy(t -> Objects.isNull(t.getReservedForTransaction()),
                    Collectors.groupingBy(UnitBlock::getType,
                        Collectors.groupingBy(UnitBlock::getOriginalPeriod,
                            Collectors.groupingBy(UnitBlock::getApplicablePeriod,
                                Collectors.groupingBy(UnitBlock::getSubjectToSop,
                                    Collectors.summingLong(t -> t.getEndBlock() - t.getStartBlock() + 1)))))));

        //Available Holdings
        List<AccountHoldingsSummaryDTO> available = toAccountHoldingsSummaryDTOList(
            result,
            Boolean.TRUE);

        //Reserved Holdings
        List<AccountHoldingsSummaryDTO> reserved = toAccountHoldingsSummaryDTOList(
            result,
            Boolean.FALSE);

        /* merge available and reserved results */
        AccountHoldingsSummariesMerger merger = AccountHoldingsSummariesMerger.
            builder().
            available(available).
            reserved(reserved).
            build();

        List<AccountHoldingsSummaryDTO> holdings = merger.merge();

        long totalAvailableQuantity = holdings.stream().filter(h -> !Objects.isNull(h.getAvailableQuantity()))
            .mapToLong(AccountHoldingsSummaryDTO::getAvailableQuantity).sum();
        long totalReserved = holdings.stream().filter(h -> !Objects.isNull(h.getReservedQuantity()))
            .mapToLong(AccountHoldingsSummaryDTO::getReservedQuantity).sum();


        AccountHoldingsSummaryResultDTO summaryResult = new AccountHoldingsSummaryResultDTO();
        summaryResult.setTotalAvailableQuantity(totalAvailableQuantity);
        summaryResult.setTotalReservedQuantity(totalReserved);
        summaryResult.setItems(holdings);
        accountRepository.findByIdentifier(identifier).ifPresent(t -> {
            summaryResult.setCurrentComplianceStatus(t.getComplianceStatus());
            if (EnumSet.of(RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT,
                RegistryAccountType.OPERATOR_HOLDING_ACCOUNT).contains(t.getRegistryAccountType())) {
                summaryResult.setShouldMeetEmissionsTarget(true);
            }
        });
        calculateUnitType(available).ifPresent(summaryResult::setAvailableUnitType);
        calculateUnitType(reserved).ifPresent(summaryResult::setReservedUnitType);
        return summaryResult;
    }

    private Optional<UnitType> calculateUnitType(List<AccountHoldingsSummaryDTO> holdingsSummaryDTOS) {
        long count =
            holdingsSummaryDTOS.stream().map(AccountHoldingsSummaryDTO::getType).distinct().count();
        if (count > 1) {
            return Optional.of(UnitType.MULTIPLE);
        }
        if (count == 1) {
            return Optional.of(holdingsSummaryDTOS.get(0).getType());
        }
        return Optional.empty();
    }

    private List<AccountHoldingsSummaryDTO> toAccountHoldingsSummaryDTOList(
        Map<Boolean, Map<UnitType, Map<CommitmentPeriod, Map<CommitmentPeriod, Map<Boolean, Long>>>>> result,
        Boolean available) {

        List<AccountHoldingsSummaryDTO> resultHoldings = new ArrayList<>();

        Map<UnitType, Map<CommitmentPeriod, Map<CommitmentPeriod, Map<Boolean, Long>>>> blocks = result.get(available);

        //Unit type
        blocks.keySet().forEach(u -> {
            //Original Commitment period
            blocks.get(u).keySet().forEach(o -> {
                //Applicable Commitment period
                blocks.get(u).get(o).keySet().forEach(a -> {
                    //Subject to SOP
                    blocks.get(u).get(o).get(a).keySet().forEach(s -> {
                        long quantity = blocks.get(u).get(o).get(a).get(s);
                        AccountHoldingsSummaryDTO summary = new AccountHoldingsSummaryDTO();
                        summary.setType(u);
                        summary.setOriginalPeriod(o);
                        summary.setApplicablePeriod(a);
                        summary.setSubjectToSop(s);
                        if (Boolean.TRUE.equals(available)) {
                            summary.setAvailableQuantity(quantity);
                        } else {
                            summary.setReservedQuantity(quantity);
                        }
                        resultHoldings.add(summary);
                    });
                });
            });
        });
        return resultHoldings;
    }

    /**
     * Fetches the unit blocks according to filter.
     *
     * @param filter the search filter.
     * @return a list of unit blocks.
     */
    public List<UnitBlock> getUnitBlocks(UnitBlockFilter filter) {
        QUnitBlock unitBlock = QUnitBlock.unitBlock;
        BooleanExpression predicate = QUnitBlock
            .unitBlock.accountIdentifier.eq(filter.getAccountIdentifier())
            .and(unitBlock.type.eq(filter.getUnitType()))
            .and(unitBlock.applicablePeriod.eq(filter.getApplicablePeriod()))
            .and(unitBlock.originalPeriod.eq(filter.getOriginalPeriod()))
            .and(filter.getSubjectToSoap() != null
                ? unitBlock.subjectToSop.eq(filter.getSubjectToSoap())
                : unitBlock.subjectToSop.isNull()
            );

        Iterable<UnitBlock> iterable = unitBlockRepository
            .findAll(
                    predicate,
                    unitBlock.projectNumber.asc(),           // project
                    unitBlock.environmentalActivity.asc(),   // activity
                    unitBlock.reservedForTransaction.asc(),  // reserved
                    unitBlock.startBlock.asc()               // serialNumberStart
            );

        return StreamSupport.stream(iterable.spliterator(), false).toList();
    }

    public static Comparator<AccountHoldingDetailsDTO.UnitBlockDTO> accountHoldingDetailsComparator() {
        return Comparator.comparing(AccountHoldingDetailsDTO.UnitBlockDTO::getProject,
                        Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(AccountHoldingDetailsDTO.UnitBlockDTO::getActivity,
                        Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(AccountHoldingDetailsDTO.UnitBlockDTO::isReserved,
                        Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(AccountHoldingDetailsDTO.UnitBlockDTO::getSerialNumberStart,
                        Comparator.nullsFirst(Comparator.naturalOrder()));
    }

    /**
     * Returns true/false if Installation Permit Identifier exists and account is not closed.
     *
     * @param installationPermitId The Installation Permit ID to check.
     * @return true/false if Installation Permit Identifier exists and account is not closed.
     */
    public boolean installationPermitIdExists(String installationPermitId) {
        Long installationPermitIdCount =
            accountRepository.countInstallationsByPermitIdForNonClosedOrRejectedAccounts(
                StringUtils.upperCase(installationPermitId));
        if (installationPermitIdCount != 0) {
			throw AccountActionException
					.create(AccountActionError.build(MULTIPLE_INSTALLATION_PERMIT_IDS_NOT_ALLOWED.name(),
							MULTIPLE_INSTALLATION_PERMIT_IDS_NOT_ALLOWED.getMessage()));
        }
        return false;
    }

    public InstallationOrAircraftOperatorDTO getInstallationByIdentifier(Long identifier) {
        Optional<CompliantEntity> byIdentifier = compliantEntityRepository.findByIdentifier(identifier);
        if (byIdentifier.isPresent()) {
            return accountConversionService.convert((Installation) byIdentifier.get());
        }
        throw AccountActionException.create(AccountActionError.builder()
                .message("The installation ID you entered could not be found")
                .build());
    }

	public List<InstallationSearchResultDTO> findInstallationById(String installationId, Optional<Long> excludeAccountHolderIdentifier) {
		return accountRepository.installationsByIdentifierForNonClosedOrRejectedAccounts(installationId, excludeAccountHolderIdentifier.orElse(null));
	}

    public void validateOperator(InstallationOrAircraftOperatorDTO operatorDTO) {
        //TODO refactor installationPermitIdExists which is  common rule for account opening
        //Transfer-BR4
        if(operatorDTO.getPermit().getPermitIdUnchanged() == null || !operatorDTO.getPermit().getPermitIdUnchanged()) {
            installationPermitIdExists(operatorDTO.getPermit().getId());
        }
        transferValidationService.validateTransferByCompliantEntityIdentifier(operatorDTO.getIdentifier(), Optional.ofNullable(operatorDTO.getAcquiringAccountHolderIdentifier()));
    }

    /**
     * Performs Business Check for the account full identifier provided by the user.
     *
     * @param accountFullIdentifier The full account identifier e.g GB-100-1001-1-89
     * @return True if the account identifier is correct otherwise throws exception
     */
    public Boolean checkAccountFullIdentifier(String accountFullIdentifier) {
        // TODO This will be refactored when FullAccountIdentifierParser is decoupled from the Transaction package
        FullAccountIdentifierParser instance = FullAccountIdentifierParser.getInstance(accountFullIdentifier);
        if (instance.isEmpty()) {
            throwBusinessException(2501, "Enter account number");
        }
        if (!instance.hasValidRegistryCode()) {
            throwBusinessException(2502, "Invalid account number format - The country code must be 2 letters long");
        }
        if (!instance.hasValidType()) {
            throwBusinessException(2503,
                "Invalid account number format - The account type must be 3 digits long and a valid Kyoto type");
        }
        if (!instance.hasValidIdentifier()) {
            throwBusinessException(2504, "Invalid account number format - The account ID must be numeric");
        } else if (!instance.hasValidIdentifierInRegistry()) {
            throwBusinessException(2504,
                "Invalid account number format - The account ID must be up to 8 digits long for UK Registry accounts");
        }
        if (!instance.hasValidCommitmentPeriod()) {
            throwBusinessException(2505, "Invalid account number format - The period must be 1 digit long");
        }
        if (instance.belongsToRegistry() && !instance.hasCommitmentPeriod()) {
            throwBusinessException(2505,
                "Invalid account number format – The period must be specified for UK registry accounts");
        }

        if (!instance.hasCheckDigits()) {
            throwBusinessException(2001,
                "Invalid account number format - The check digits must be up to 2 digits long");
        } else if (instance.belongsToRegistry() && instance.isEmptyCheckDigits()) {
            throwBusinessException(2001,
                "Invalid account number format – The check digits must be specified for UK registry account");
        } else if (instance.belongsToRegistry() && !instance.hasValidCheckDigits()) {
            throwBusinessException(2001, "Enter a valid account number");
        }

        if (instance.belongsToRegistry() && accountTotalRepository.getAccountSummary(accountFullIdentifier) == null) {
            throwBusinessException(2002,
                "Invalid account number - The account number does not exist in the registry");
        }
        return true;
    }

    /**
     * Fetches the account history events for the requested account.
     *
     * @param identifier The account identifier
     * @return A list of {@link AuditEventDTO}
     */
    public List<AuditEventDTO> getAccountHistory(Long identifier) {
        List<AuditEventDTO> eventsByDomainId;
        if (authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ENROLLED_NON_ADMIN)) {
            eventsByDomainId = eventService.getEventsByDomainIdForNonAdminUsers(identifier.toString(),
                List.of(Account.class.getName()));
        } else if (authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ANY_ADMIN)) {
            eventsByDomainId = eventService.getEventsByDomainIdForAdminUsers(identifier.toString(),
                List.of(Account.class.getName()));
        } else {
            throw new AuthorizationDeniedException("Invalid scope.", null);
        }
		final String descriptionPattern = "^Transaction identifier (.*)\\. Transferring account (.*)$";
		eventsByDomainId.removeIf(event -> TransactionProposalTaskService.TRANSACTION_PROPOSAL_TASK.equals(event.getDomainAction())
				&& StringUtils.defaultIfEmpty(event.getDescription(), StringUtils.EMPTY).matches(descriptionPattern));
        
        List<AuditEventDTO> accountSystemEvents =
            eventService.getSystemEventsByDomainIdOrderByCreationDateDesc(identifier.toString(),
                List.of(Account.class.getName()));
        if (!authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ANY_ADMIN)) {
            accountSystemEvents = accountSystemEvents.stream().filter(
                    auditEventDTO -> !ACCOUNT_AUTHORISED_REPRESENTATIVE_RESTORED.equals(auditEventDTO.getDomainAction()) &&
                        !ACCOUNT_AUTHORISED_REPRESENTATIVE_SUSPENDED.equals(auditEventDTO.getDomainAction()))
                .collect(toList());
            hideKyotoGovernmentAccountIdentifier(eventsByDomainId);
            hideRegistryGovernmentAccountIdentifier(eventsByDomainId);
        }
        return Stream.of(eventsByDomainId, accountSystemEvents)
            .flatMap(Collection::stream)
            .sorted(Comparator.comparing(AuditEventDTO::getCreationDate).reversed())
            .collect(toList());
    }

    /**
     * Replace Kyoto Government account identifier with account name.
     *
     * @param eventsByDomainId The list of domain events
     */
    private void hideKyotoGovernmentAccountIdentifier(List<AuditEventDTO> eventsByDomainId) {
        List<KyotoAccountType> allKyotoGovernmentTypes = AccountType.getAllKyotoGovernmentTypes().stream()
            .map(AccountType::getKyotoType).collect(toList());
        List<BulkArAccountDTO> kyotoAccountIdentifiers =
            accountRepository.findKyotoAccountIdentifiers(allKyotoGovernmentTypes);
        eventsByDomainId.stream()
            .filter(auditEventDTO -> Objects.nonNull(auditEventDTO.getDescription()))
            .forEach(auditEventDTO -> {
            Optional<BulkArAccountDTO> accountDto = kyotoAccountIdentifiers.stream()
                .filter(kyoto -> auditEventDTO.getDescription().contains(kyoto.getFullIdentifier())).findFirst();
            accountDto.ifPresent(bulkArAccountDTO ->
                auditEventDTO.setDescription(auditEventDTO.getDescription()
                    .replace(bulkArAccountDTO.getFullIdentifier(), bulkArAccountDTO.getAccountName())));
        });
    }

    /**
     * Replace Registry Government account identifier with account name.
     *
     * @param eventsByDomainId The list of domain events
     */
    private void hideRegistryGovernmentAccountIdentifier(List<AuditEventDTO> eventsByDomainId) {
        List<RegistryAccountType> allRegistryGovernmentTypes = AccountType.getAllRegistryGovernmentTypes().stream()
            .map(AccountType::getRegistryType).collect(toList());
        List<BulkArAccountDTO> registryAccountIdentifiers =
            accountRepository.findRegistryAccountIdentifiers(allRegistryGovernmentTypes);
        eventsByDomainId.forEach(auditEventDTO -> {
            Optional<BulkArAccountDTO> accountDto = registryAccountIdentifiers.stream()
                .filter(registry -> auditEventDTO.getDescription().contains(registry.getFullIdentifier())).findFirst();
            accountDto.ifPresent(bulkArAccountDTO ->
                auditEventDTO.setDescription(auditEventDTO.getDescription()
                    .replace(bulkArAccountDTO.getFullIdentifier(), bulkArAccountDTO.getAccountName())));
        });
    }

    /**
     * Fetches the transactions related to the requested account.
     *
     * @param accountFullIdentifier The account full identifier.
     * @return The {@link Page<TransactionProjection>} of results.
     */
    public Page<TransactionProjection> getAccountTransactions(String accountFullIdentifier, Pageable pageable) {
        TransactionSearchCriteria criteria = new TransactionSearchCriteria();
        criteria.setAcquiringAccountNumber(accountFullIdentifier);
        criteria.setTransferringAccountNumber(accountFullIdentifier);
        criteria.setShowRunningBalances(true);
        TransactionFilter filter = transactionFilterFactory.createTransactionFilter(criteria);
        
        return searchableTransactionRepository.search(filter, pageable);
    }

    private void throwBusinessException(int code, String error) {
        throw new BusinessCheckException(new BusinessCheckError(code, error));
    }

    /**
     * Updates the account details
     *
     * @param identifier               the account identifier
     * @param updatedAccountDetailsDTO the updated account details
     * @return The accountDTO.
     */
    @Transactional
    public AccountDTO updateAccountDetails(Long identifier, AccountDetailsDTO updatedAccountDetailsDTO) {
        Optional<Account> byIdentifier = accountRepository.findByIdentifier(identifier);
        if (byIdentifier.isEmpty()) {
            throw new UkEtsException(String
                .format("Requested update account details for account with identifier:%s which does not exist",
                    identifier));
        }
        Account account = byIdentifier.get();
        account.setBillingAddressSameAsAccountHolderAddress(updatedAccountDetailsDTO.isAccountDetailsSameBillingAddress());
        account.setAccountName(updatedAccountDetailsDTO.getName());
        if(updatedAccountDetailsDTO.getSalesContactDetails() != null) {
        	account.setSalesContact(createSalesContactDetails(updatedAccountDetailsDTO.getSalesContactDetails()));
        }
        if (account.getContact() != null) {
            Contact contact = conversionService
                .convert(ConversionParameters.builder()
                    .address(updatedAccountDetailsDTO.getAddress())
                    .billingEmail1(updatedAccountDetailsDTO.getBillingEmail1())
                    .billingEmail2(updatedAccountDetailsDTO.getBillingEmail2())
                    .billingContactDetails(updatedAccountDetailsDTO.getBillingContactDetails())
                    .build());
            contact.setId(account.getContact().getId());
            account.setContact(contact);
            persistenceService.save(contact);
        }
        AccountDTO accountDTO = accountDTOFactory.create(account);

        // record event

        User currentUser = userService.getCurrentUser();
        String action = "Update account details";
        eventService.createAndPublishEvent(account.getIdentifier().toString(), currentUser.getUrid(), "",
            EventType.ACCOUNT_DETAILS_UPDATED, action);

        return accountDTO;
    }

    /**
     * Removes ARs and closes the account after an installation transfer procedure.
     */
    public void handleInstallationTransferActions(Long accountIdentifier) {
        removeAccountArs(accountIdentifier, REMOVAL_REASON, null);
        accountStatusService.changeAccountStatus(accountIdentifier, AccountStatus.CLOSED,
            "Account closed", null);
    }

    /**
     * change status and delete role from keycloak if user is not ar in other accounts
     */
    public void removeAccountArs(Long accountIdentifier, String removalReason, User currentUser) {
        accountAccessRepository.finARsByAccount_Identifier(accountIdentifier)
            .stream()
            .filter(accountAccess -> accountAccess.getState() != AccountAccessState.REMOVED)
            .forEach(ar -> {
                ar.setState(AccountAccessState.REMOVED);
                User user = ar.getUser();
                authorizedRepresentativeService
                    .removeKeycloakRoleIfNoOtherAccountAccess(user.getUrid(), user.getIamIdentifier());
                String action = "Authorised representative removed due to " + removalReason;
                eventService.createAndPublishEvent(accountIdentifier.toString(),
                    currentUser != null ? currentUser.getUrid() : null,
                    		DisplayNameUtils.getDisplayName(user), EventType.ACCOUNT_AR_REMOVED, action);
            });
    }

    /**
     * Asks the report-api to generate the account transaction details report.
     *
     * @param accountFullIdentifier the business identifier of the account
     * @return the
     */
    @Transactional(readOnly = true)
    public Long requestAccountTransactionDetailsReport(@Valid String accountFullIdentifier) {
        ReportQueryInfo reportQueryInfo = ReportQueryInfo
            .builder()
            .accountFullIdentifier(accountFullIdentifier)
            .build();
        return reportRequestService
                .requestReport(ReportType.R0035, reportRoleMappingService.getUserReportRequestingRole(),
                    reportQueryInfo, null);
    }

    public void setTransferringAccountDtoAndContactInfoDto(Account transferringAccount, AccountDTO accountDTO) {
        AccountHolder transferringAccountHolder =
                (AccountHolder) Hibernate.unproxy(transferringAccount.getAccountHolder());

        AccountHolderContactInfoDTO oldAccountHolderContactInfoDTO = new AccountHolderContactInfoDTO();

        transferringAccountHolder.getAccountHolderRepresentatives().forEach(ar -> {
            ar.setContact((Contact) Hibernate.unproxy(ar.getContact()));
            if (ar.getAccountContactType().equals(AccountContactType.PRIMARY)) {
                oldAccountHolderContactInfoDTO.setPrimaryContact(accountConversionService.convert(ar));
            } else {
                oldAccountHolderContactInfoDTO.setAlternativeContact(accountConversionService.convert(ar));
            }
        });
        accountDTO.setTransferringAccountHolder(accountConversionService.convert(transferringAccountHolder));
        accountDTO.setTransferringAccountHolderContactInfo(oldAccountHolderContactInfoDTO);
    }
    
    @Transactional
	public void markAccountExcludedFromBilling(Long accountIdentifier, boolean excluded, String exclusionRemarks) {
		this.validateExcludedFromBillingRequest(accountIdentifier);
		final String exclusionReason = excluded ? exclusionRemarks : StringUtils.EMPTY;
		accountRepository.updateExcludedFromBilling(accountIdentifier, excluded, exclusionReason);
		User currentUser = userService.getCurrentUser();
		eventService.createAndPublishEvent(accountIdentifier.toString(), currentUser.getUrid(), exclusionReason,
				excluded ? EventType.ACCOUNT_EXCLUSION_FROM_BILLING : EventType.ACCOUNT_INCLUSION_IN_BILLING,
				excluded ? "Account exclusion from Billing" : "Account inclusion in Billing");
	}
	
	private void validateExcludedFromBillingRequest(Long accountIdentifier) {
		Account account = accountRepository.findByIdentifier(accountIdentifier)
				.orElseThrow(() -> AccountActionException.create(AccountActionError.builder()
																				   .code(AccountActionError.ACCOUNT_EXCLUDED_FROM_BILLING_CHANGE_NOT_ALLOWED)
																				   .message(String.format("Account with identifier %s not found.", accountIdentifier))
																				   .build()));
		if (!accountValidationService.checkExcludedFromBillingRequest(account)) {
			throw AccountActionException.create(AccountActionError.builder()
																  .code(AccountActionError.ACCOUNT_EXCLUDED_FROM_BILLING_CHANGE_NOT_ALLOWED)
																  .message("You are not allowed to perform this action.").build());
		}
	}
}
