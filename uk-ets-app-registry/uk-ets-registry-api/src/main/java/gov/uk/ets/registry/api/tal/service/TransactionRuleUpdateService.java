package gov.uk.ets.registry.api.tal.service;

import static gov.uk.ets.registry.api.task.domain.types.RequestType.TRANSACTION_RULES_UPDATE_REQUEST;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.common.exception.BusinessRuleErrorException;
import gov.uk.ets.registry.api.common.model.services.PersistenceService;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.transaction.domain.data.TrustedAccountListRulesDTO;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionRuleUpdateService {

    private final AccountService accountService;

    private final UserService userService;

    private final PersistenceService persistenceService;

    private final TaskEventService taskEventService;

    private final TaskRepository taskRepository;

    private final Mapper mapper;
    /**
     * Creates a trusted account list rules DTO from an account.
     * @param accountId the account ID
     * @return the trusted account list rules DTO
     */
    public TrustedAccountListRulesDTO createTrustedAccountListRulesDtoFromAccount(Long accountId) {

        Account account = accountService.getAccount(accountId);
        TrustedAccountListRulesDTO trustedAccountListRulesDTO = new TrustedAccountListRulesDTO();

        if (account != null) {
            trustedAccountListRulesDTO.setRule1(account.getApprovalOfSecondAuthorisedRepresentativeIsRequired());
            trustedAccountListRulesDTO.setRule2(account.getTransfersToAccountsNotOnTheTrustedListAreAllowed());
            trustedAccountListRulesDTO.setRule3(account.getSinglePersonApprovalRequired());
        }
        return trustedAccountListRulesDTO;
    }

    /**
     * Updates the trusted account list rules of the account.
     * @param trustedAccountListRulesDTO the trusted account list rules DTO
     * @param accountId the account ID
     * @return the business identifier of the task
     */
    @Transactional
    @EmitsGroupNotifications(GroupNotificationType.ACCOUNT_UPDATE_PROPOSAL)
    public Long updateTalTransactionRules(TrustedAccountListRulesDTO trustedAccountListRulesDTO, Long accountId) {

        Account account = accountService.getAccount(accountId);

        if (taskRepository.countPendingTasksByAccountIdInAndType(List.of(account.getId()),
            List.of(TRANSACTION_RULES_UPDATE_REQUEST)) > 0) {
            throw new BusinessRuleErrorException(ErrorBody.from(
                "Another request to update the transaction rules is pending approval"));
        }

        Date initiatedDate = new Date();
        User currentUser = userService.getCurrentUser();
        Task task = new Task();
        task.setRequestId(persistenceService.getNextBusinessIdentifier(Task.class));
        task.setType(TRANSACTION_RULES_UPDATE_REQUEST);
        task.setInitiatedBy(currentUser);
        task.setInitiatedDate(initiatedDate);
        task.setAccount(account);
        trustedAccountListRulesDTO.setCurrentRule1(account.getApprovalOfSecondAuthorisedRepresentativeIsRequired());
        trustedAccountListRulesDTO.setCurrentRule2(account.getTransfersToAccountsNotOnTheTrustedListAreAllowed());
        trustedAccountListRulesDTO.setCurrentRule3(account.getSinglePersonApprovalRequired());
        task.setStatus(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
        task.setDifference(mapper.convertToJson(trustedAccountListRulesDTO));
        persistenceService.save(task);
        taskEventService.createAndPublishTaskAndAccountRequestEvent(task, currentUser.getUrid());

        return task.getRequestId();
    }
}
