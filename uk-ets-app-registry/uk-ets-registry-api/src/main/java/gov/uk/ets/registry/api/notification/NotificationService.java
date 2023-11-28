package gov.uk.ets.registry.api.notification;

import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.AccountHolderRepresentative;
import gov.uk.ets.registry.api.account.repository.AccountAccessRepository;
import gov.uk.ets.registry.api.account.repository.AccountHolderRepository;
import gov.uk.ets.registry.api.account.repository.AccountHolderRepresentativeRepository;
import gov.uk.ets.registry.api.account.service.AccountOpeningTaskService;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.file.upload.requesteddocs.domain.RequestDocumentsTaskDifference;
import gov.uk.ets.registry.api.notification.repository.RecipientRepository;
import gov.uk.ets.registry.api.tal.repository.TrustedAccountRepository;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.service.TaskService;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.admin.service.UserAdministrationService;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import gov.uk.ets.registry.api.user.domain.UserRoleMapping;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.domain.UserWorkContact;
import gov.uk.ets.registry.api.user.repository.UserWorkContactRepositoryImpl;
import gov.uk.ets.registry.api.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Service used by notification appliance to delegate for common operations that interact with repositories.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class NotificationService {

    private final UserWorkContactRepositoryImpl userWorkContactRepository;
    private final RecipientRepository recipientRepository;
    private final TaskRepository taskRepository;
    private final AccountHolderRepresentativeRepository accountHolderRepresentativeRepository;
    private final AccountHolderRepository accountHolderRepository;
    private final AccountAccessRepository accountAccessRepository;
    private final TrustedAccountRepository trustedAccountRepository;

    private final UserService userService;
    private final UserAdministrationService userAdministrationService;
    private final TaskService taskService;
    private final Mapper mapper;


    /**
     * Retrieves authorised representative emails using service account token if withServiceAccountAccess is true.
     */
    public Set<String> findEmailsOfArsByAccountIdentifier(Long accountId, boolean withServiceAccountAccess) {
        return findEmailsOfArsByAccountIdentifier(accountId, withServiceAccountAccess, true);
    }

    public Set<String> findEmailsOfArsByAccountIdentifier(Long accountId, boolean withServiceAccountAccess,
                                                          boolean includeAuthorityUsers) {
        return findEmails(findARRecipients(accountId, includeAuthorityUsers), withServiceAccountAccess);
    }

    public Set<String> findEmailsOfAdministrators(boolean withServiceAccountAccess) {
        List<User> usersByRoleNameAndState = userService.findUsersByRoleNameAndState(
            List.of(UserRole.JUNIOR_REGISTRY_ADMINISTRATOR.getKeycloakLiteral(),
                UserRole.SENIOR_REGISTRY_ADMINISTRATOR.getKeycloakLiteral(),
                UserRole.READONLY_ADMINISTRATOR.getKeycloakLiteral()),
            List.of(UserStatus.ENROLLED, UserStatus.SUSPENDED));

        return findEmails(removeSuspendedUsers(usersByRoleNameAndState)
            .stream().map(User::getUrid).collect(Collectors.toSet()), withServiceAccountAccess);
    }
    
    public Set<String> findEmailsOfAuthorityUsers(boolean withServiceAccountAccess) {
        List<User> usersByRoleNameAndState = userService.findUsersByRoleNameAndState(
            List.of(UserRole.AUTHORITY_USER.getKeycloakLiteral()),
            List.of(UserStatus.ENROLLED, UserStatus.SUSPENDED));

        return findEmails(removeSuspendedUsers(usersByRoleNameAndState)
            .stream().map(User::getUrid).collect(Collectors.toSet()), withServiceAccountAccess);
    }

    public Set<String> findEmailOfArByUserUrid(String userUrid, boolean withServiceAccountAccess) {
        Set<String> userUrids = new HashSet<>();
        userUrids.add(userUrid);
        return findEmails(userUrids, withServiceAccountAccess);
    }

    public Long findAccountIdentifierByIdAndAccountFullIdentifier(Long id, String accountFullIdentifier) {
        return trustedAccountRepository.findByIdAndAccountFullIdentifier(id, accountFullIdentifier);
    }

    /**
     * Retrieves the RequestType of a Task using its requestId.
     */
    public RequestType findRequestTypeById(Long requestId) {
        Task task = taskRepository.findByRequestId(requestId);
        if (task == null) {
            log.error("Task with request id {} was not found, notification email might not be generated correctly",
                requestId);
            return null;
        }
        return task.getType();
    }

    /**
     * Retrieves Account Identifier related to the Task using the requestId.
     */
    public Long findAccountIdentifierByRequestId(Long requestId) {
        Task task = findTaskByRequestId(requestId);
        return task != null ? task.getAccount().getIdentifier() : null;
    }

    /**
     * Retrieves Account Full Identifier related to the Task using the requestId.
     */
    public String findAccountFullIdentifierByRequestId(Long requestId) {
        Task task = findTaskByRequestId(requestId);
        return task != null ? task.getAccount().getFullIdentifier() : null;
    }

    /**
     * Retrieves emails of applicant and account holder representatives.
     * //     * TODO: method moved from {@link AccountOpeningTaskService}, might need improvement
     *
     * @param outcome           the outcome of the account creation task
     * @param accountIdentifier identifier of the created account
     * @param userId            user id of applicant
     */
    public Set<String> getEmailAddressesForAccountOpening(TaskOutcome outcome, Long accountIdentifier, Long userId) {
        Set<String> emailSet = new HashSet<>();

        String applicantMail =
            userAdministrationService.findByIamId(userService.getUserById(userId).getIamIdentifier())
                .getEmail();
        emailSet.add(applicantMail);
        log.debug("Added applicant mail");
        if (TaskOutcome.APPROVED.equals(outcome)) {
            Long accountHolderIdentifier = findAccountHolderIdByAccountIdentifier(accountIdentifier);
            log.debug("Adding legal representative mails for account holder {}", accountHolderIdentifier);
            for (AccountHolderRepresentative accountHolderRepresentative : accountHolderRepresentativeRepository
                .getAccountHolderRepresentatives(accountHolderIdentifier)) {
                emailSet.add(accountHolderRepresentative.getContact().getEmailAddress());
                log.debug("Added legal representative mail for account holder {}: {}", accountHolderIdentifier,
                    accountHolderRepresentative.getContact().getEmailAddress());
            }
        }
        return emailSet;
    }

    public String getEmailAddressWhenChangingUserStatus(String iamIdentifier) {
        return userAdministrationService.findByIamId(iamIdentifier)
            .getEmail();
    }

    /**
     * Retrieves the task for this request id.
     */
    public Task findTaskByRequestId(Long requestId) {
        Task task = taskRepository.findByRequestId(requestId);
        if (task == null) {
            log.error("Task with request id {} was not found, notification email might not be generated correctly",
                requestId);
            return null;
        }
        return task;
    }

    /**
     * Retrieves authorised representative emails only for AR users with status VALIDATED.
     */
    public Set<String> findEmailsOfValidatedArs(Long accountHolderIdentifier) {
        return findEmails(findValidatedArRecipients(accountHolderIdentifier), false);
    }

    /**
     * Retrieves request id for specific transaction.
     */
    public Long findRequestIdForTransaction(String transactionId) {
        return taskRepository.findByTransactionIdentifier(transactionId)
            .map(Task::getRequestId)
            .orElse(null);
    }
    
    /**
     * @param requestId the request ID
     * @return Pair of Account Name and Account Holder Name
     */
	public Pair<String, String> findAccountNameAndAccountHolderNameFromDifference(Long requestId) {

		TaskDetailsDTO taskDetailsDTO = taskService.getTaskDetailsDTO(requestId);
		RequestDocumentsTaskDifference requestDocumentsTaskDifference = mapper
				.convertToPojo(taskDetailsDTO.getDifference(), RequestDocumentsTaskDifference.class);

		String accountName = taskDetailsDTO.getAccountName() == null ? requestDocumentsTaskDifference.getAccountName()
				: taskDetailsDTO.getAccountName();
		String accountHolderName = null;
		if (requestDocumentsTaskDifference.getAccountHolderIdentifier() != null) {
			accountHolderName = accountHolderRepository
					.getAccountHolder(requestDocumentsTaskDifference.getAccountHolderIdentifier()).actualName();
		} // workaround - will not work for individual holders. see UKETS-4828.
		else {
			accountHolderName = requestDocumentsTaskDifference.getAccountHolderName();
		}

		return Pair.of(accountName, accountHolderName);
	}

    public Set<String> findEmails(Set<String> recipients, boolean withServiceAccountAccess) {
        return userWorkContactRepository
            .fetch(recipients, withServiceAccountAccess)
            .stream()
            .map(UserWorkContact::getEmail)
            .collect(Collectors.toSet());
    }

    /**
     * Either includes all users (includeAuthorityUsers = true) OR excludes users with authority role
     */
    private Set<String> findARRecipients(Long relatedAccountIdentifier, boolean includeAuthorityUsers) {
        List<User> notificationRecipientsOfAccount =
            recipientRepository.getNotificationRecipientsOfAccount(relatedAccountIdentifier, includeAuthorityUsers);

        return removeSuspendedUsers(notificationRecipientsOfAccount)
            .stream()
            .filter(u -> includeAuthorityUsers || u.getUserRoles().stream().noneMatch(userHasRoleAuthority()))
            .map(User::getUrid)
            .collect(Collectors.toSet());
    }

    private List<User> removeSuspendedUsers(List<User> users) {

        List<String> suspendedUsers = users.stream()
            .filter(user -> user.getState() == UserStatus.SUSPENDED)
            .filter(user -> user.getPreviousState() == UserStatus.ENROLLED)
            .map(User::getUrid)
            .toList();

        if (suspendedUsers.isEmpty()) {
            return users;
        }

        List<User> suspendedBySystem =
            taskRepository.findPendingTasksByTypesAndUsers(RequestType.getTasksCausingUserSuspension(), suspendedUsers)
                .stream()
                .map(Task::getUser)
                .toList();

        return users.stream()
            .filter(user -> user.getState() != UserStatus.SUSPENDED || suspendedBySystem.contains(user))
            .toList();
    }

    private Long findAccountHolderIdByAccountIdentifier(Long accountIdentifier) {
        AccountHolder accountHolderOfAccount = accountHolderRepository.getAccountHolderOfAccount(accountIdentifier);
        if (accountHolderOfAccount == null) {
            log.error(
                "AccountHolder with identifier {} was not found, notification email might not be generated correctly",
                accountIdentifier);
            return null;
        }
        return accountHolderOfAccount.getIdentifier();
    }

    private Set<String> findValidatedArRecipients(Long accountHolderIdentifier) {
        return accountAccessRepository.finARsByAccount_Identifier(accountHolderIdentifier)
            .stream()
            .filter(access -> UserStatus.VALIDATED.equals(access.getUser().getState()))
            .map(access -> access.getUser().getUrid())
            .collect(Collectors.toSet());
    }

    private Predicate<UserRoleMapping> userHasRoleAuthority() {
        return r -> r.getRole().getRoleName().equals(UserRole.AUTHORITY_USER.getKeycloakLiteral());
    }
}
