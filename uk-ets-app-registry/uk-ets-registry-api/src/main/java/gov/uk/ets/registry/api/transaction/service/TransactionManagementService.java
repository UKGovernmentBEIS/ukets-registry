package gov.uk.ets.registry.api.transaction.service;

import com.querydsl.jpa.impl.JPAQuery;
import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.accountaccess.service.AccountAccessService;
import gov.uk.ets.registry.api.ar.domain.ARAccountAccessRepository;
import gov.uk.ets.registry.api.auditevent.domain.types.TransactionEventType;
import gov.uk.ets.registry.api.auditevent.web.AuditEventDTO;
import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.authz.Scope;
import gov.uk.ets.registry.api.common.reports.ReportRequestService;
import gov.uk.ets.registry.api.common.reports.ReportRoleMappingService;
import gov.uk.ets.registry.api.common.search.JpaQueryExtractor;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.itl.notice.service.ITLNoticeManagementService;
import gov.uk.ets.registry.api.task.service.TaskService;
import gov.uk.ets.registry.api.transaction.TransactionReversalService;
import gov.uk.ets.registry.api.transaction.TransactionService;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.TransactionFilter;
import gov.uk.ets.registry.api.transaction.domain.TransactionFilterFactory;
import gov.uk.ets.registry.api.transaction.domain.TransactionProjection;
import gov.uk.ets.registry.api.transaction.domain.data.ItlNotificationSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionConnectionSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionResponseSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.repository.SearchableTransactionRepository;
import gov.uk.ets.registry.api.transaction.shared.TransactionSearchCriteria;
import gov.uk.ets.registry.api.user.domain.UserRole;
import gov.uk.ets.registry.api.user.service.UserService;
import gov.uk.ets.reports.model.ReportQueryInfo;
import gov.uk.ets.reports.model.ReportType;

import java.time.ZoneId;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.keycloak.authorization.client.AuthorizationDeniedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionManagementService {

    private final TransactionFilterFactory transactionFilterFactory;
    private final SearchableTransactionRepository searchableTransactionRepository;
    private final AccountService accountService;
    private final TaskService taskService;
    private final TransactionService transactionService;
    private final AuthorizationService authorizationService;
    private final ARAccountAccessRepository accountAccessRepository;
    private final UserService userService;
    private final EventService eventService;
    private final ITLNoticeManagementService itlNoticeManagementService;
    private final TransactionReversalService transactionReversalService;
    private final ReportRequestService reportRequestService;
    private final ReportRoleMappingService reportRoleMappingService;
    private final AccountAccessService accountAccessService;


    /**
     * Searches for transaction projection results
     *
     * @param criteria The search criteria
     * @return The {@link Page<TransactionProjection>} of results
     */
    @Transactional(readOnly = true)
    public Page<TransactionProjection> search(TransactionSearchCriteria criteria, Pageable pageable, boolean isReport) {
        TransactionFilter filter = transactionFilterFactory.createTransactionFilter(criteria);
        if (isReport) {
            JPAQuery<TransactionProjection> jpaQuery = searchableTransactionRepository.getQuery(filter, pageable);
            reportRequestService
                .requestReport(ReportType.R0006, reportRoleMappingService.getUserReportRequestingRole(),
                        JpaQueryExtractor.extractReportQueryInfo(jpaQuery.createQuery()), null);
            return Page.empty();
        }
        return searchableTransactionRepository.search(filter, pageable);
    }

    /**
     * Asks the report-api to generate the transaction details report.
     * 
     * @param transactionIdentifier the business identifier of the transaction
     * @return the 
     */
    @Transactional(readOnly = true)
    public Long requestTransactionDetailsReport(String transactionIdentifier) {
        ReportQueryInfo reportQueryInfo = ReportQueryInfo
            .builder()
            .transactionIdentifier(transactionIdentifier)
            .urid(userService.getCurrentUser().getUrid())
            .build();
        return reportRequestService
                .requestReport(ReportType.R0034, reportRoleMappingService.getUserReportRequestingRole(),
                    reportQueryInfo, null);
    }    
    
    /**
     * Transforms a {@link Transaction} entity object to a {@link TransactionSummary} data transfer object (DTO).
     *
     * @param transaction {@link Transaction}
     * @return {@link TransactionSummary}
     */
    @Transactional(readOnly = true)
    public TransactionSummary getTransactionSummary(Transaction transaction) {

        Account transferringAccount =
            accountService.getAccount(transaction.getTransferringAccount().getAccountIdentifier());
        ItlNotificationSummary itlNotification = getItlNotificationSummary(transaction);

        Account acquiringAccount =
            accountService.getAccount(transaction.getAcquiringAccount().getAccountIdentifier());

        Long taskIdentifier = taskService.getTaskIdentifier(transaction.getIdentifier());
        return TransactionSummary
            .transactionSummaryBuilder()
            .identifier(transaction.getIdentifier())
            .taskIdentifier(taskIdentifier)
            .lastUpdated(transaction.getLastUpdated())
            .status(transaction.getStatus())
            .transferringAccountName(accountService
                .getAccountDisplayName(transaction.getTransferringAccount()
                    .getAccountFullIdentifier()))
            .isExternalTransferringAccount(transferringAccount == null)
            .transferringAccountFullIdentifier(transaction.getTransferringAccount().getAccountFullIdentifier())
            .transferringAccountType(transaction.getTransferringAccount().getAccountType())
            .transferringRegistryCode(transaction.getTransferringAccount().getAccountRegistryCode())
            .transferringAccountIdentifier(transaction.getTransferringAccount().getAccountIdentifier())
            .hasAccessToTransferringAccount(accountAccessService.checkAccountAccess(
                transaction.getTransferringAccount().getAccountIdentifier()))
            .transferringAccountStatus(transferringAccount == null ? null : transferringAccount.getAccountStatus())
            .acquiringAccountName(accountService
                .getAccountDisplayName(transaction.getAcquiringAccount()
                    .getAccountFullIdentifier()))
            .isExternalAcquiringAccount(accountService.getAccountFullIdentifier(transaction.getAcquiringAccount()
                .getAccountFullIdentifier()) == null)
            .acquiringAccountFullIdentifier(transaction.getAcquiringAccount().getAccountFullIdentifier())
            .acquiringAccountType(transaction.getAcquiringAccount().getAccountType())
            .acquiringAccountRegistryCode(transaction.getAcquiringAccount().getAccountRegistryCode())
            .acquiringAccountIdentifier(transaction.getAcquiringAccount().getAccountIdentifier())
            .hasAccessToAcquiringAccount(accountAccessService.checkAccountAccess(
                transaction.getAcquiringAccount().getAccountIdentifier()))
            .acquiringAccountStatus(acquiringAccount == null ? null : acquiringAccount.getAccountStatus())
            .type(transaction.getType())
            .unitType(transaction.getUnitType())
            .quantity(transaction.getQuantity())
            .attributes(transaction.getAttributes())
            .blocks(transactionService.getTransactionBlockSummariesFromBlocks(transaction.getBlocks(),
                transferringAccount != null ? transferringAccount.getBalance() : null))
            .responses(transaction.getResponseEntries().stream()
                .map(response -> TransactionResponseSummary.builder()
                    .dateOccurred(response.getDateOccurred())
                    .details(response.getDetails())
                    .errorCode(response.getErrorCode())
                    .transactionBlockId(response.getTransactionBlockId())
                    .build())
                .collect(Collectors.toList()))
            .started(transaction.getStarted())
            .itlNotification(itlNotification)
            .canBeReversed(authorizationService.hasClientRole(UserRole.SENIOR_REGISTRY_ADMINISTRATOR) &&
                transactionReversalService.canBeReversed(transaction))
            .transactionConnectionSummary(transactionService.populateConnectionSummary(transaction))
            .reference(transaction.getReference())
            .executionDateTime(Objects.nonNull(transaction.getExecutionDate()) ? transaction.getExecutionDate().atZone(ZoneId.systemDefault()) : null)
            .build();
    }

    /**
     * Transforms a {@link Transaction} entity object to a {@link TransactionSummary} data transfer object (DTO).
     * This method tries to replicate the shape of the {@link TransactionSummary} object during the transaction proposal.
     *
     * @param transaction {@link Transaction}
     * @return {@link TransactionSummary}
     */
    @Transactional(readOnly = true)
    public TransactionSummary constructTransactionSummary(Transaction transaction) {

        Account transferringAccount =
            accountService.getAccount(transaction.getTransferringAccount().getAccountIdentifier());

        TransactionConnectionSummary connectionSummary = transactionService.populateConnectionSummary(transaction);
        ItlNotificationSummary itlNotification = getItlNotificationSummary(transaction);

        return TransactionSummary.transactionSummaryBuilder()
                                 .identifier(transaction.getIdentifier())
                                 .reversedIdentifier(connectionSummary != null ? connectionSummary.getOriginalIdentifier() : null)
                                 .transferringAccountIdentifier(transaction.getTransferringAccount().getAccountIdentifier())
                                 .acquiringAccountFullIdentifier(transaction.getAcquiringAccount().getAccountFullIdentifier())
                                 .type(transaction.getType())
                                 .unitType(transaction.getUnitType())
                                 .quantity(transaction.getQuantity())
                                 .attributes(transaction.getAttributes())
                                 .blocks(transactionService.getTransactionBlockSummariesFromBlocks(
                                     transaction.getBlocks(),
                                     transferringAccount != null ? transferringAccount.getBalance() : null))
                                 .itlNotification(itlNotification)
                                 .build();
    }

    private ItlNotificationSummary getItlNotificationSummary (Transaction transaction) {
        ItlNotificationSummary itlNotification = null;
        if (transaction.getNotificationIdentifier() != null) {
            Optional<ItlNotificationSummary> itlNotificationOpt =
                itlNoticeManagementService.getITLDetails(transaction.getNotificationIdentifier()).stream()
                                          .map(itlNoticeDetailResult -> ItlNotificationSummary.builder()
                                              .commitPeriod(itlNoticeDetailResult.getCommitPeriod())
                                              .notificationIdentifier(itlNoticeDetailResult.getNotificationIdentifier())
                                              .projectNumber(itlNoticeDetailResult.getProjectNumber())
                                              .quantity(itlNoticeDetailResult.getTargetValue())
                                              .targetDate(itlNoticeDetailResult.getTargetDate())
                                              .build()
                                              ).findAny();
            if (itlNotificationOpt.isPresent()) {
                itlNotification = itlNotificationOpt.get();
            }
        }
        return itlNotification;
    }

    /**
     * Gets the transaction history based on scope.
     * If the current user is AR of the acquiring account only then only system events are returned.
     * UKETS-7191 ~ Remove Event referring to delayed transaction for Acquiring account ARs
     *
     * @param transactionIdentifier the transaction identifier
     * @return the transaction event history
     */
    public List<AuditEventDTO> getTransactionHistory(String transactionIdentifier) {
        List<AuditEventDTO> transactionSystemEvents =
            eventService.getSystemEventsByDomainIdOrderByCreationDateDesc(transactionIdentifier,
                List.of(Transaction.class.getName(), TransactionService.class.getName()));
        List<AuditEventDTO> transactionEvents;
        if (authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ENROLLED_NON_ADMIN)) {
            if (!isActiveNonRoleBasedArOfTransferringAccount(transactionIdentifier)) {
            	transactionSystemEvents.removeIf(systemEvent -> TransactionEventType.TRANSACTION_DELAYED.getEventAction().equals(systemEvent.getDomainAction()));
                return transactionSystemEvents;
            }
            transactionEvents = eventService.getEventsByDomainIdForNonAdminUsers(transactionIdentifier,
                List.of(Transaction.class.getName(), TransactionService.class.getName()));
        } else if (authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ANY_ADMIN)) {
            transactionEvents = eventService.getEventsByDomainIdForAdminUsers(transactionIdentifier,
                List.of(Transaction.class.getName(), TransactionService.class.getName()));
        } else {
            throw new AuthorizationDeniedException("Invalid scope.", null);
        }
        return Stream.of(transactionEvents, transactionSystemEvents)
            .flatMap(Collection::stream)
            .sorted(Comparator.comparing(AuditEventDTO::getCreationDate).reversed())
            .collect(Collectors.toList());
    }

    private boolean isActiveNonRoleBasedArOfTransferringAccount(String transactionIdentifier) {
        Transaction transaction = transactionService.getTransaction(transactionIdentifier);
        List<AccountAccess> accountAccesses = accountAccessRepository
            .fetchARs(transaction.getTransferringAccount()
                    .getAccountIdentifier(),
                AccountAccessState.ACTIVE);
        return accountAccesses.stream()
                              .anyMatch(hasActiveNonRoleBasedAccountAccess(userService.getCurrentUser().getUrid()));
    }

	private Predicate<AccountAccess> hasActiveNonRoleBasedAccountAccess(String urid) {
		Predicate<AccountAccess> isActiveAccountAccess = aa -> AccountAccessState.ACTIVE.equals(aa.getState());
		return isActiveAccountAccess
				.and(aa -> aa.getUser().getUrid().equals(urid))
				.and(Predicate.not(aa -> AccountAccessRight.ROLE_BASED.equals(aa.getRight())));
	}
}
