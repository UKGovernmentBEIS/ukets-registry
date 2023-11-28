package gov.uk.ets.registry.api.tal.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.features.SeniorAdminRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim.OnlySeniorOrJuniorCanClaimTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim.SeniorAdminCanClaimTaskInitiatedByAdminRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.FourEyesPrincipleRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.RegistryAdminCanApproveTaskWhenAccountNotClosedOrPendingClosureRule;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.error.UkEtsException;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.service.TaskTypeService;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TransactionRuleUpdateTaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.data.TrustedAccountListRulesDTO;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
@RequiredArgsConstructor
public class TransactionRuleUpdateTaskService implements TaskTypeService<TransactionRuleUpdateTaskDetailsDTO> {

    private final AccountService accountService;

    private final Mapper mapper;

    @Override
    public Set<RequestType> appliesFor() {
        return Set.of(RequestType.TRANSACTION_RULES_UPDATE_REQUEST);
    }

    @Override
    public TransactionRuleUpdateTaskDetailsDTO getDetails(TaskDetailsDTO taskDetailsDTO) {
        TransactionRuleUpdateTaskDetailsDTO result = new TransactionRuleUpdateTaskDetailsDTO(taskDetailsDTO);
        result.setAccountInfo(accountService.getAccountInfo(Long.valueOf(taskDetailsDTO.getAccountNumber())));
        result.setTrustedAccountListRules(extractTrustedAccountListRulesDTO(result));
        return result;
    }

    @Protected(
        {
            FourEyesPrincipleRule.class,
            SeniorAdminRule.class,
            RegistryAdminCanApproveTaskWhenAccountNotClosedOrPendingClosureRule.class
        }
    )
    @Transactional
    @Override
    public TaskCompleteResponse complete(TransactionRuleUpdateTaskDetailsDTO taskDTO, TaskOutcome taskOutcome,
                                         String comment) {
        if (TaskOutcome.APPROVED.equals(taskOutcome)) {
            Account account = extractAccountEntity(taskDTO);
            if (account == null) {
                throw new UkEtsException("Expecting exactly one account");
            }
            TrustedAccountListRulesDTO dto = mapper.convertToPojo(taskDTO.getDifference(),
                TrustedAccountListRulesDTO.class);
            provideDefaultValues(dto);
            account.setApprovalOfSecondAuthorisedRepresentativeIsRequired(dto.getRule1());
            account.setTransfersToAccountsNotOnTheTrustedListAreAllowed(dto.getRule2());
            account.setSinglePersonApprovalRequired(dto.getRule3());
        }
        return defaultResponseBuilder(taskDTO).build();
    }

    @Protected( {

    })
    @Override
    public void checkForInvalidAssignPermissions() {
        // implemented for being able to apply permissions using annotations
    }

    @Protected( {
            OnlySeniorOrJuniorCanClaimTaskRule.class,
            SeniorAdminCanClaimTaskInitiatedByAdminRule.class
    })
    @Override
    public void checkForInvalidClaimantPermissions() {
        // implemented for being able to apply permissions using annotations
    }

    /**
     * Extracts Account entity from task.
     *
     * @param transactionRuleUpdateTaskDetailsDTO a taskDTO for Transaction rule update operation.
     * @return a {@link Account}
     */
    private Account extractAccountEntity(
        TransactionRuleUpdateTaskDetailsDTO transactionRuleUpdateTaskDetailsDTO) {
        return accountService
            .getAccount(transactionRuleUpdateTaskDetailsDTO.getAccountInfo().getIdentifier());
    }

    /**
     * Extracts trusted account list rules dto from task.
     *
     * @param transactionRuleUpdateTaskDetailsDTO a taskDTO for Transaction rule update operation.
     * @return a {@link TrustedAccountListRulesDTO}
     */
    private TrustedAccountListRulesDTO extractTrustedAccountListRulesDTO(
        TransactionRuleUpdateTaskDetailsDTO transactionRuleUpdateTaskDetailsDTO) {
        return mapper.convertToPojo(transactionRuleUpdateTaskDetailsDTO.getDifference(), TrustedAccountListRulesDTO.class);
    }

    private void provideDefaultValues(TrustedAccountListRulesDTO dto) {
        if (dto.getRule1() == null) {
            dto.setRule1(true);
        }
        if (dto.getRule2() == null) {
            dto.setRule2(false);
        }
        if (dto.getRule3() == null) {
            dto.setRule3(true);
        }
    }
}
