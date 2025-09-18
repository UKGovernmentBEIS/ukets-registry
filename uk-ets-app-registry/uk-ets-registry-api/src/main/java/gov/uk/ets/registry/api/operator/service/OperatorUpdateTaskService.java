package gov.uk.ets.registry.api.operator.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.service.AccountOperatorUpdateService;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.web.model.OperatorDTO;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim.OnlySeniorAdminCanClaimTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.FourEyesPrincipleRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.OnlySeniorRegistryAdminCanApproveTask;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.RegistryAdminCanApproveTaskWhenAccountNotClosedOrPendingClosureRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.UniqueEmitterIdBusinessRule;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.error.UkEtsException;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.service.TaskTypeService;
import gov.uk.ets.registry.api.task.web.model.OperatorUpdateTaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
@RequiredArgsConstructor
public class OperatorUpdateTaskService implements TaskTypeService<OperatorUpdateTaskDetailsDTO> {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final AccountOperatorUpdateService accountOperatorUpdateService;
    private final Mapper mapper;

    @Override
    public Set<RequestType> appliesFor() {
        return Set.of(
                RequestType.INSTALLATION_OPERATOR_UPDATE_REQUEST,
                RequestType.AIRCRAFT_OPERATOR_UPDATE_REQUEST,
                RequestType.MARITIME_OPERATOR_UPDATE_REQUEST);
    }

    @Override
    public OperatorUpdateTaskDetailsDTO getDetails(TaskDetailsDTO taskDetailsDTO) {
        OperatorUpdateTaskDetailsDTO dto = new OperatorUpdateTaskDetailsDTO(taskDetailsDTO);
        dto.setAccountInfo(accountService.getAccountInfo(Long.valueOf(taskDetailsDTO.getAccountNumber())));
        dto.setCurrent(deserializeRequest(dto.getBefore()));
        dto.setChanged(deserializeRequest(dto.getDifference()));
        return dto;
    }

    @Protected({
        FourEyesPrincipleRule.class,
        RegistryAdminCanApproveTaskWhenAccountNotClosedOrPendingClosureRule.class,
        OnlySeniorRegistryAdminCanApproveTask.class,
        UniqueEmitterIdBusinessRule.class
    })
    @Transactional
    @Override
    public TaskCompleteResponse complete(OperatorUpdateTaskDetailsDTO taskDTO, TaskOutcome taskOutcome,
                                         String comment) {
        if (TaskOutcome.APPROVED.equals(taskOutcome)) {
            Long accountIdentifier = taskDTO.getAccountInfo().getIdentifier();
            Account account =
                accountRepository.findByIdentifier(accountIdentifier).orElseThrow(() -> new UkEtsException(String
                    .format("Update account operator details for account with identifier:%s which does not exist",
                        accountIdentifier)));
            OperatorDTO diff = deserializeRequest(taskDTO.getDifference());

            accountOperatorUpdateService.updateOperator(diff, accountIdentifier,
                taskDTO.getTaskType(), account);

            accountOperatorUpdateService.sendComplianceEvents(diff, taskDTO.getInitiatorUrid(),
                account.getCompliantEntity().getIdentifier(), taskDTO.getCompletedDate());
        }
        return defaultResponseBuilder(taskDTO).build();
    }

    @Protected({

    })
    @Override
    public void checkForInvalidAssignPermissions() {
        // implemented for being able to apply permissions using annotations
    }

    @Protected({
        OnlySeniorAdminCanClaimTaskRule.class
    })
    @Override
    public void checkForInvalidClaimantPermissions() {
        // implemented for being able to apply permissions using annotations
    }

    private OperatorDTO deserializeRequest(String difference) {
        return mapper.convertToPojo(difference, OperatorDTO.class);
    }
}
