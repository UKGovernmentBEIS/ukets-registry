package gov.uk.ets.registry.api.operator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.service.AccountOperatorUpdateService;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.web.model.OperatorDTO;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.web.model.OperatorUpdateTaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.data.AccountInfo;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.Optional;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class OperatorUpdateTaskServiceTest {

    @Mock
    private AccountService mockAccountService;

    @Mock
    private AccountOperatorUpdateService accountOperatorUpdateService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private Mapper mapper;

    OperatorUpdateTaskService operatorUpdateTaskService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        operatorUpdateTaskService =
            new OperatorUpdateTaskService(mockAccountService, accountRepository, accountOperatorUpdateService, mapper);
    }

    @Test
    void deserializeTaskDifferenceTest() throws JsonProcessingException {
        String diff =
            "{\"type\":\"INSTALLATION\",\"regulator\":\"OPRED\",\"permit\":{\"id\":\"1234581\"}}";

        OperatorDTO dto =
            new ObjectMapper().readValue(diff, OperatorDTO.class);
        Assertions.assertEquals("INSTALLATION", dto.getType());
        Assertions.assertEquals(RegulatorType.OPRED, dto.getRegulator());
        Assertions.assertEquals("1234581", dto.getPermit().getId());
    }

    @DisplayName("Test approve")
    @Test
    void testApprove() throws JsonProcessingException {
        Account account = new Account();
        Installation installation = new Installation();
        installation.setIdentifier(1234L);
        account.setCompliantEntity(installation);
        when(accountRepository.findByIdentifier(10001L)).thenReturn(Optional.of(account));
        String diff =
            "{\"type\":\"INSTALLATION\",\"regulator\":\"OPRED\",\"permit\":{\"id\":\"1234581\"}}";
        OperatorDTO diffDto =
            new ObjectMapper().readValue(diff, OperatorDTO.class);
        String before =
            "{\"type\":\"INSTALLATION\",\"identifier\":1000001,\"name\":\"Installation Name for update1\",\"activityType\":\"PRODUCTION_OF_BULK_CHEMICALS\",\"regulator\":\"OPRED\",\"firstYear\":2023,\"lastYear\":2024,\"permit\":{\"id\":\"1234581\"}}";
        OperatorDTO beforeDto =
            new ObjectMapper().readValue(before, OperatorDTO.class);

        TaskDetailsDTO taskDetailsDTO = new TaskDetailsDTO();
        taskDetailsDTO.setTaskType(RequestType.INSTALLATION_OPERATOR_UPDATE_REQUEST);
        taskDetailsDTO.setDifference(diff);
        OperatorUpdateTaskDetailsDTO dto = new OperatorUpdateTaskDetailsDTO(taskDetailsDTO);
        dto.setCurrent(beforeDto);
        dto.setChanged(diffDto);
        dto.setInitiatorUrid("UK12345");
        Date now = new Date();
        dto.setCompletedDate(now);
        AccountInfo accountInfo = AccountInfo.builder().identifier(10001L).build();
        dto.setAccountInfo(accountInfo);

        when(mapper.convertToPojo(diff, OperatorDTO.class)).thenReturn(diffDto);
        operatorUpdateTaskService.complete(dto, TaskOutcome.APPROVED, "");

        ArgumentCaptor<OperatorDTO> captor1 =
            ArgumentCaptor.forClass(OperatorDTO.class);
        ArgumentCaptor<Long> captor2 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<RequestType> captor3 = ArgumentCaptor.forClass(RequestType.class);
        ArgumentCaptor<Account> captor4 = ArgumentCaptor.forClass(Account.class);
        then(accountOperatorUpdateService).should(times(1))
            .updateOperator(captor1.capture(), captor2.capture(), captor3.capture(), captor4.capture());

        then(accountOperatorUpdateService).should(times(1)).sendComplianceEvents(diffDto, "UK12345", 1234L, now);

    }

    @DisplayName("Test reject")
    @Test
    void testReject() throws JsonProcessingException {
        String diff =
            "{\"type\":\"INSTALLATION\",\"regulator\":\"OPRED\",\"permit\":{\"id\":\"1234581\"}}";
        OperatorDTO diffDto =
            new ObjectMapper().readValue(diff, OperatorDTO.class);
        String before =
            "{\"type\":\"INSTALLATION\",\"identifier\":1000001,\"name\":\"Installation Name for update1\",\"activityType\":\"PRODUCTION_OF_BULK_CHEMICALS\",\"regulator\":\"OPRED\",\"firstYear\":2023,\"lastYear\":2024,\"permit\":{\"id\":\"1234581\"}}";
        OperatorDTO beforeDto =
            new ObjectMapper().readValue(before, OperatorDTO.class);

        TaskDetailsDTO taskDetailsDTO = new TaskDetailsDTO();
        taskDetailsDTO.setTaskType(RequestType.INSTALLATION_OPERATOR_UPDATE_REQUEST);
        taskDetailsDTO.setDifference(diff);
        OperatorUpdateTaskDetailsDTO dto = new OperatorUpdateTaskDetailsDTO(taskDetailsDTO);
        dto.setCurrent(beforeDto);
        dto.setChanged(diffDto);
        AccountInfo accountInfo = AccountInfo.builder().identifier(10001L).build();
        dto.setAccountInfo(accountInfo);

        operatorUpdateTaskService.complete(dto, TaskOutcome.REJECTED, "");

        ArgumentCaptor<OperatorDTO> captor1 =
            ArgumentCaptor.forClass(OperatorDTO.class);
        ArgumentCaptor<Long> captor2 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<RequestType> captor3 = ArgumentCaptor.forClass(RequestType.class);
        ArgumentCaptor<Account> captor4 = ArgumentCaptor.forClass(Account.class);
        then(accountOperatorUpdateService).should(times(0))
            .updateOperator(captor1.capture(), captor2.capture(), captor3.capture(), captor4.capture());
    }
}
