package gov.uk.ets.registry.api.authz.ruleengine;

import static java.util.stream.Collectors.toMap;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.repository.AccountAccessRepository;
import gov.uk.ets.registry.api.account.repository.AccountHolderRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.web.model.AccountOperatorDetailsUpdateDTO;
import gov.uk.ets.registry.api.allocation.service.AllocationStatusService;
import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.account.AccountSecurityStoreSliceLoader;
import gov.uk.ets.registry.api.authz.ruleengine.features.account.holder.AccountHolderSecurityStoreSliceLoader;
import gov.uk.ets.registry.api.authz.ruleengine.features.allocation.AllocationSecurityStoreSliceLoader;
import gov.uk.ets.registry.api.authz.ruleengine.features.ar.ArBusinessSecurityStoreSliceLoader;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessSecurityStoreSliceLoader;
import gov.uk.ets.registry.api.authz.ruleengine.features.transaction.TransactionBusinessSecurityStoreSliceLoader;
import gov.uk.ets.registry.api.authz.ruleengine.features.user.profile.EmailChangeSecurityStoreSliceLoader;
import gov.uk.ets.registry.api.authz.ruleengine.features.user.profile.StatusChangeSecurityStoreSliceLoader;
import gov.uk.ets.registry.api.business.configuration.service.BusinessConfigurationService;
import gov.uk.ets.registry.api.common.exception.BusinessRuleErrorException;
import gov.uk.ets.registry.api.common.exception.NotAuthorizedException;
import gov.uk.ets.registry.api.common.exception.NotFoundException;
import gov.uk.ets.registry.api.common.keycloak.OTPValidator;
import gov.uk.ets.registry.api.tal.repository.TrustedAccountRepository;
import gov.uk.ets.registry.api.tal.web.model.TrustedAccountDTO;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.service.TransactionPersistenceService;
import gov.uk.ets.registry.api.user.domain.UserRole;
import gov.uk.ets.registry.api.user.service.UserService;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * An aspect that executes a set of business rules on a method.
 */
@Component
@Aspect
@Order(1)
@RequiredArgsConstructor
public class BusinessRuleAppliance {
    private static final Logger log = LoggerFactory.getLogger(BusinessRuleAppliance.class);

    public static final String YOU_ARE_REQUESTING_TO_READ_OR_WRITE_A_NON_EXISTENT_ACCOUNT =
        "You are requesting to read or write a non-existent account";
    public static final String YOU_ARE_REQUESTING_TO_READ_OR_WRITE_A_NON_EXISTENT_ACCOUNT_HOLDER =
        "You are requesting to read or write a non-existent account holder";
    public static final String YOU_ARE_NOT_AUTHORIZED_TO_VIEW_THIS_RESOURCE =
        "You are not authorized to view this resource";
    private final TaskRepository taskRepository;
    private final AccountRepository accountRepository;
    private final AccountHolderRepository accountHolderRepository;
    private final UserService userService;
    private final AuthorizationService authorizationService;
    private final AccountAccessRepository accountAccessRepository;
    private final DontPermitIfOneRuleReturnsForbiddenBusinessRuleRunner businessRuleRunner;
    private final BusinessRuleBuilder businessRuleBuilder;
    private final RuleInputLoader ruleInputLoader;
    private final TaskBusinessSecurityStoreSliceLoader businessSecurityStoreLoaderForTask;
    private final ArBusinessSecurityStoreSliceLoader businessSecurityStoreLoaderForARUpdate;
    private final TransactionBusinessSecurityStoreSliceLoader transactionBusinessSecurityStoreSliceLoader;
    private final EmailChangeSecurityStoreSliceLoader emailChangeSecurityStoreSliceLoader;
    private final AccountHolderSecurityStoreSliceLoader accountHolderSecurityStoreSliceLoader;
    private final AccountSecurityStoreSliceLoader accountActionsSecurityStoreSliceLoader;
    private final OTPValidator otpValidator;
    private final BusinessConfigurationService businessConfigurationService;
    private final TrustedAccountRepository trustedAccountRepository;
    private final AllocationStatusService allocationStatusService;
    private final StatusChangeSecurityStoreSliceLoader statusChangeSecurityStoreSliceLoader;
    private final AllocationSecurityStoreSliceLoader allocationSecurityStoreSliceLoader;

    /**
     * Persistence service for transactions.
     */
    private final TransactionPersistenceService transactionPersistenceService;

    @Resource(name = "requestScopedBusinessSecurityStore")
    private BusinessSecurityStore businessSecurityStore;

    @Resource(name = "requestScopedRuleInputStore")
    private RuleInputStore ruleInputStore;

    /**
     * Applies the rules before proceeding with the join point.
     *
     * @param joinPoint           the join point to check
     * @param protectedAnnotation the rules used to protect
     */
    @Before(value = "@annotation(protectedAnnotation)")
    public void apply(JoinPoint joinPoint, Protected protectedAnnotation)
        throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ruleInputLoader.processInput(joinPoint);
        loadStore();
        BusinessRule.Outcome result = businessRuleRunner.run(
            businessRuleBuilder.build(protectedAnnotation.value())
        );

        if (result.isForbidden()) {
            throw new BusinessRuleErrorException(result.getErrorBody());
        }
    }

    private void loadStore() {
        if (businessSecurityStore.getUser() == null) {
            businessSecurityStore.setUser(userService.getCurrentUser());
        }
        if (businessSecurityStore.getUserRoles() == null) {
            businessSecurityStore.setUserRoles(
                authorizationService
                    .getClientLevelRoles(userService.getCurrentUser().getIamIdentifier())
                    .stream()
                    .map(clientRole -> UserRole.fromKeycloakLiteral(clientRole.getName()))
                    .collect(Collectors.toList()));
        }
        if (businessSecurityStore.getAccountAccesses() == null) {
            businessSecurityStore.setAccountAccesses(
                accountAccessRepository.findByUser_Urid(businessSecurityStore.getUser().getUrid()));
        }
        if (businessSecurityStore.getUserScopes() == null) {
            businessSecurityStore.setUserScopes(authorizationService.getScopes());
        }
        if (ruleInputStore.containsKey(RuleInputType.ACCOUNT_ID) && businessSecurityStore.getAccount() == null) {
            businessSecurityStore.setAccount(
                accountRepository.findByIdentifier((Long) ruleInputStore.get(RuleInputType.ACCOUNT_ID)).orElseThrow(
                    () -> new NotFoundException(YOU_ARE_REQUESTING_TO_READ_OR_WRITE_A_NON_EXISTENT_ACCOUNT)
                )
            );
            if (businessSecurityStore.getAccount() != null && businessSecurityStore.getAccount().getCompliantEntity() != null
                    && businessSecurityStore.getAllocationEntries() == null) {
                businessSecurityStore.setAllocationEntries(allocationStatusService
                        .getAllocationEntries(businessSecurityStore.getAccount().getCompliantEntity().getId()));
            }

            if (businessSecurityStore.getTrustedAccounts() == null) {
                businessSecurityStore.setTrustedAccounts(trustedAccountRepository.findAllByAccountIdentifier(
                        (Long) ruleInputStore.get(RuleInputType.ACCOUNT_ID)));
            }
            
        }

        if (ruleInputStore.containsKey(RuleInputType.ACCOUNT_OPERATOR_UPDATE)
                && businessSecurityStore.getRequestedOperatorUpdate() == null) {
            AccountOperatorDetailsUpdateDTO accountOperatorDetailsUpdateDTO =
                    (AccountOperatorDetailsUpdateDTO) ruleInputStore.get(RuleInputType.ACCOUNT_OPERATOR_UPDATE);
            if (accountOperatorDetailsUpdateDTO != null && accountOperatorDetailsUpdateDTO.getDiff() != null) {
                businessSecurityStore.setRequestedOperatorUpdate(accountOperatorDetailsUpdateDTO.getDiff());
            }
        }

        if (ruleInputStore.containsKey(RuleInputType.TRUSTED_ACCOUNT_ID) &&
            businessSecurityStore.getTrustedAccountCandidate() == null) {
            TrustedAccountDTO trustedAccountDTO =
                (TrustedAccountDTO) ruleInputStore.get(RuleInputType.TRUSTED_ACCOUNT_ID);
            businessSecurityStore.setTrustedAccountCandidate(accountRepository.findByFullIdentifier(
                trustedAccountDTO.getAccountFullIdentifier()).orElse(null));
        }
        if (ruleInputStore.containsKey(RuleInputType.TRUSTED_ACCOUNT_FULL_ID) &&
            businessSecurityStore.getTrustedAccountCandidate() == null) {
            String trustedAccountCandidateFullIdentifier =
                (String) ruleInputStore.get(RuleInputType.TRUSTED_ACCOUNT_FULL_ID);
            businessSecurityStore.setTrustedAccountCandidate(accountRepository.findByFullIdentifier(
                trustedAccountCandidateFullIdentifier).orElse(null));
        }
        if (ruleInputStore.containsKey(RuleInputType.ACCOUNT_HOLDER_ID) &&
            businessSecurityStore.getAccountHolder() == null) {
            AccountHolder accountHolder =
                accountHolderRepository.getAccountHolder((Long) ruleInputStore.get(RuleInputType.ACCOUNT_HOLDER_ID));

            if (accountHolder == null) {
                throw new NotFoundException(YOU_ARE_REQUESTING_TO_READ_OR_WRITE_A_NON_EXISTENT_ACCOUNT_HOLDER);
            }

            businessSecurityStore.setAccountHolder(accountHolder);
        }
        if (ruleInputStore.containsKey(RuleInputType.ACCOUNT_FULL_ID) && businessSecurityStore.getAccount() == null) {
            businessSecurityStore.setAccount(
                accountRepository.findByFullIdentifier((String) ruleInputStore.get(RuleInputType.ACCOUNT_FULL_ID))
                    .orElseThrow(
                        () -> new NotFoundException(YOU_ARE_REQUESTING_TO_READ_OR_WRITE_A_NON_EXISTENT_ACCOUNT)
                    )
            );
        }

        if (ruleInputStore.containsKey(RuleInputType.TASK_REQUEST_ID) && businessSecurityStore.getTask() == null) {

            Task task = taskRepository.findByRequestId((Long) ruleInputStore.get(RuleInputType.TASK_REQUEST_ID));
            if (task != null) {
                businessSecurityStore
                    .setTask(taskRepository.findByRequestId((Long) ruleInputStore.get(RuleInputType.TASK_REQUEST_ID)));
            }
        }

        if (ruleInputStore.containsKey(RuleInputType.URID) && businessSecurityStore.getRequestedUser() == null) {
            businessSecurityStore.setRequestedUser(
                userService.getUserByUrid((String) ruleInputStore.get(RuleInputType.URID))
            );
            businessSecurityStore.setRequestedUserRoles(new ArrayList<>());
            if (!Objects.isNull(businessSecurityStore.getRequestedUser())) {
                businessSecurityStore.setRequestedUserRoles(
                    authorizationService.getClientLevelRoles(businessSecurityStore.getRequestedUser()
                            .getIamIdentifier())
                        .stream()
                        .map(clientRole -> UserRole.fromKeycloakLiteral(clientRole.getName()))
                        .collect(Collectors.toList()));
            }
        }
        
        if (ruleInputStore.containsKey(RuleInputType.URIDS) && businessSecurityStore.getRequestedUsers() == null) {
            businessSecurityStore.setRequestedUsers(
                ((List<String>) ruleInputStore.get(RuleInputType.URIDS))
                .stream()
                .distinct()
                .map(userService::getUserByUrid)
                .collect(Collectors.toList())
            );
            businessSecurityStore.setRequestedUserRoles(new ArrayList<>());
            if (!Objects.isNull(businessSecurityStore.getRequestedUsers())) {
                businessSecurityStore.setRequestedUsersRoles(
                        businessSecurityStore.getRequestedUsers()
                        .stream()
                        .map(user -> Map.entry(user, getUserRoles(user.getIamIdentifier())))
                        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue))
                );                   
            }
        }

        loadAccountFromTransaction();
        loadAccountFromTransactionIdentifier();

        businessSecurityStoreLoaderForTask.load();
        businessSecurityStoreLoaderForARUpdate.load();
        transactionBusinessSecurityStoreSliceLoader.load();
        emailChangeSecurityStoreSliceLoader.load();
        statusChangeSecurityStoreSliceLoader.load();
        accountHolderSecurityStoreSliceLoader.load();
        accountActionsSecurityStoreSliceLoader.load();
        allocationSecurityStoreSliceLoader.load();

        if (ruleInputStore.containsKey(RuleInputType.OTP) && businessSecurityStore.getOtpValid() == null) {
            String otp = (String) ruleInputStore.get(RuleInputType.OTP);
            businessSecurityStore.setOtpValid(otpValidator.validate(otp));
        }

        try {
            Map<String, String> applicationProperties = businessConfigurationService.getApplicationProperties();
            String maxARsProperty =
                applicationProperties.get("business.property.account.max.number.of.authorised.representatives");
            businessSecurityStore.setMaxNumOfARs(Integer.parseInt(maxARsProperty));
            String minARsProperty =
                applicationProperties.get("business.property.account.min.number.of.authorised.representatives");
            businessSecurityStore.setMinNumOfARs(Integer.parseInt(minARsProperty));
        } catch (IOException e) {
            log.error("Error while getting application properties", e);
        }

    }

    private List<UserRole> getUserRoles(String iamIdentifier) {
        return authorizationService.getClientLevelRoles(iamIdentifier)
                .stream()
                .map(clientRole -> UserRole.fromKeycloakLiteral(clientRole.getName()))
                .collect(Collectors.toList());
    }

	/**
     * Loads the transferring account of a transaction.
     */
    private void loadAccountFromTransaction() {
        if (ruleInputStore.containsKey(RuleInputType.TRANSACTION) && businessSecurityStore.getAccount() == null) {
            TransactionSummary transaction = ruleInputStore.get(RuleInputType.TRANSACTION, TransactionSummary.class);
            Long accountIdentifier = transaction.getTransferringAccountIdentifier();
            if (accountIdentifier != null) {
                businessSecurityStore.setAccount(accountRepository.findByIdentifier(accountIdentifier).orElseThrow(
                    () -> new NotFoundException(YOU_ARE_REQUESTING_TO_READ_OR_WRITE_A_NON_EXISTENT_ACCOUNT)
                ));
            }
        }
    }

    /**
     * Loads the transferring account of a transaction, based on the transaction identifier.
     */
    private void loadAccountFromTransactionIdentifier() {
        if (ruleInputStore.containsKey(RuleInputType.TRANSACTION_IDENTIFIER) &&
            businessSecurityStore.getAccount() == null) {
            String transactionIdentifier = (String) ruleInputStore.get(RuleInputType.TRANSACTION_IDENTIFIER);
            Transaction transaction = transactionPersistenceService.getTransaction(transactionIdentifier);
            if (transaction == null) {
                throw new NotAuthorizedException(YOU_ARE_NOT_AUTHORIZED_TO_VIEW_THIS_RESOURCE);
            }
            Optional<Account> account =
                accountRepository.findByFullIdentifier(transaction.getTransferringAccount().getAccountFullIdentifier());
            account.ifPresent(value -> businessSecurityStore.setAccount(value));
        }
    }
}
