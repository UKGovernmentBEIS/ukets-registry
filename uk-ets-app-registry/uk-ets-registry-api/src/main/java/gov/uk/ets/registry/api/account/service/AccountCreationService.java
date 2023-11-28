package gov.uk.ets.registry.api.account.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.AuthorisedRepresentativeDTO;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.web.model.AccountOpeningTaskDetailsDTO;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import gov.uk.ets.registry.api.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AccountCreationService {

    private final AccountService accountService;

    private final AccountRepository accountRepository;

    private final TaskRepository taskRepository;

    private final UserService userService;

    private final UserRepository userRepository;

    private final Mapper mapper;

    public Account createAccountFromTask(AccountOpeningTaskDetailsDTO taskDTO) {
        AccountDTO newAccountDTO = mapper.convertToPojo(taskDTO.getDifference(), AccountDTO.class);
        newAccountDTO = accountService.openAccount(newAccountDTO);
        User currentUser = userService.getCurrentUser();
        Task task = taskRepository.findByRequestId(taskDTO.getRequestId());
        List<AuthorisedRepresentativeDTO> authorisedRepresentatives = newAccountDTO.getAuthorisedRepresentatives();
        for (AuthorisedRepresentativeDTO authorisedRepresentativeDTO : authorisedRepresentatives) {
            User authorisedRepresentative = userRepository.findByUrid(authorisedRepresentativeDTO.getUrid());
            userService.validateUserAndGenerateEvents(authorisedRepresentative, currentUser, task);
        }
        // refetch the newly created account in order to set some more thinks related to the task
        // possibly some of the thinks happening here can be moved to account service.
        // will reevaluate after the refactoring of the account opening task
        Account newAccount = accountRepository.findByIdentifier(newAccountDTO.getIdentifier()).orElseThrow(
                () -> new IllegalStateException("New account did not got created in DB."));

        task.setAccount(newAccount);
        taskRepository.save(task);
        return newAccount;
    }
}
