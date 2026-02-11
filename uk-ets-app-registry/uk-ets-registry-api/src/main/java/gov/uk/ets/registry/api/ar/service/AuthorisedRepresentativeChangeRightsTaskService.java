package gov.uk.ets.registry.api.ar.service;

import gov.uk.ets.registry.api.account.repository.AccountAccessRepository;
import gov.uk.ets.registry.api.account.service.AccountClaimService;
import gov.uk.ets.registry.api.account.service.AccountContactService;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim.SeniorAdminCanClaimTaskInitiatedByAdminRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.ARsCanBeOnlyNonSuspendedUser;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.AccountARsLimitShouldNotBeExceededRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.FourEyesPrincipleRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.OnlyNonAdminCandidateForArUpdateTaskIsEligibleForApproval;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.OnlySeniorRegistryAdminCanApproveTask;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.OnlySeniorRegistryAdminCanRejectTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.RegistryAdminCanApproveTaskWhenAccountNotClosedOrPendingClosureRule;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.file.upload.requesteddocs.service.RequestedDocsTaskService;
import gov.uk.ets.registry.api.payment.service.PaymentTaskAutoCompletionService;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskARStatusRepository;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.web.model.AuthoriseRepresentativeTaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.UserConversionService;
import gov.uk.ets.registry.api.user.admin.service.UserAdministrationService;
import gov.uk.ets.registry.api.user.admin.service.UserStatusService;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthorisedRepresentativeChangeRightsTaskService extends AuthorisedRepresentativeUpdateTaskService {

    public AuthorisedRepresentativeChangeRightsTaskService(AccountService accountService,
                                                           UserConversionService userConversionService,
                                                           UserAdministrationService userAdministrationService,
                                                           AccountAccessRepository accountAccessRepository,
                                                           UserService userService,
                                                           TaskRepository taskRepository,
                                                           AuthorizedRepresentativeService authorizedRepresentativeService,
                                                           UserStatusService userStateService,
                                                           RequestedDocsTaskService requestedDocsTaskService,
                                                           Mapper mapper,
                                                           TaskARStatusRepository taskARStatusRepository,
                                                           PaymentTaskAutoCompletionService paymentTaskAutoCompletionService,
                                                           AccountClaimService accountClaimService,
                                                           AccountContactService accountContactService) {
        super(accountService, userConversionService, userAdministrationService, accountAccessRepository, userService,
            taskRepository, authorizedRepresentativeService, userStateService, requestedDocsTaskService, mapper,taskARStatusRepository,paymentTaskAutoCompletionService,
                accountClaimService, accountContactService);
    }

    @Override
    public Set<RequestType> appliesFor() {
        return Set.of(RequestType.AUTHORIZED_REPRESENTATIVE_UPDATE_ACCESS_RIGHTS_REQUEST);
    }

    @Protected(
        {
            FourEyesPrincipleRule.class,
            RegistryAdminCanApproveTaskWhenAccountNotClosedOrPendingClosureRule.class,
            OnlySeniorRegistryAdminCanApproveTask.class,
            OnlySeniorRegistryAdminCanRejectTaskRule.class,
            OnlyNonAdminCandidateForArUpdateTaskIsEligibleForApproval.class,
            AccountARsLimitShouldNotBeExceededRule.class,
            ARsCanBeOnlyNonSuspendedUser.class
        }
    )
    @Transactional
    @Override
    public TaskCompleteResponse complete(AuthoriseRepresentativeTaskDetailsDTO taskDTO,
                                         TaskOutcome taskOutcome, String comment) {
        return super.complete(taskDTO, taskOutcome, comment);
    }

    @Protected({
    })
    @Override
    public void checkForInvalidAssignPermissions() {
        // implemented for being able to apply permissions using annotations
    }

    @Protected({
        SeniorAdminCanClaimTaskInitiatedByAdminRule.class
    })
    @Override
    public void checkForInvalidClaimantPermissions() {
        // implemented for being able to apply permissions using annotations
    }

}