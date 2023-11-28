package gov.uk.ets.registry.api.tal.service;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TransactionRuleUpdateTaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.data.AccountInfo;
import gov.uk.ets.registry.api.transaction.domain.data.TrustedAccountListRulesDTO;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import java.util.Objects;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TransactionRuleUpdateTaskServiceTest {

    @Mock
    private AccountService mockAccountService;

    @Mock
    private Mapper mapper;

    private ObjectMapper jacksonMapper = new ObjectMapper();

    TransactionRuleUpdateTaskService transactionRuleUpdateTaskService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        transactionRuleUpdateTaskService = new TransactionRuleUpdateTaskService(mockAccountService, mapper);
    }

    @DisplayName("Retrieve transaction rule update values successfully.")
    @Test
    void deserializeTransactionRuleUpdateTest() throws JsonProcessingException {
        String diff = "{\"rule1\": false, \"rule2\": true, \"rule3\": true}";
        TrustedAccountListRulesDTO dto =
            new ObjectMapper().readValue(diff, TrustedAccountListRulesDTO.class);
        Assertions.assertEquals(false, dto.getRule1());
        Assertions.assertEquals(true, dto.getRule2());
        Assertions.assertEquals(true, dto.getRule3());
    }

    @DisplayName("In case of Approval initial state of the rules should change " +
        "but on Rejection the initial state should not change.")
    @MethodSource("getArguments")
    @ParameterizedTest(name = "#{index} - {0}")
    void testAccountTransactionRuleStateInCaseOfApprovalOrRejection(TaskOutcome outcome)
        throws JsonProcessingException {
        AccountInfo accountInfo = AccountInfo.builder()
            .accountHolderName("Account Holder Name")
            .accountName("Account Name")
            .fullIdentifier("GB-100-1004-1-89")
            .identifier(1004L)
            .registryCode("GB")
            .build();
        when(mockAccountService.getAccountInfo(1004L))
            .thenReturn(accountInfo);

        Account account = new Account();
        account.setApprovalOfSecondAuthorisedRepresentativeIsRequired(true);
        account.setTransfersToAccountsNotOnTheTrustedListAreAllowed(false);
        account.setIdentifier(accountInfo.getIdentifier());
        account.setFullIdentifier(accountInfo.getFullIdentifier());
        account.setRegistryCode(accountInfo.getRegistryCode());
        when(mockAccountService.getAccount(1004L))
            .thenReturn(account);

        TaskDetailsDTO taskDetailsDTO = new TaskDetailsDTO();
        taskDetailsDTO.setTaskType(RequestType.TRANSACTION_RULES_UPDATE_REQUEST);
        taskDetailsDTO.setDifference("{\"rule1\": false, \"rule2\": true}");
        taskDetailsDTO.setAccountNumber(Objects.toString(accountInfo.getIdentifier(), null));

        when(mapper.convertToPojo(taskDetailsDTO.getDifference(), TrustedAccountListRulesDTO.class))
            .thenReturn(jacksonMapper.readValue(taskDetailsDTO.getDifference(), TrustedAccountListRulesDTO.class));

        TransactionRuleUpdateTaskDetailsDTO dto = transactionRuleUpdateTaskService.getDetails(taskDetailsDTO);
        transactionRuleUpdateTaskService.complete(dto, outcome, null);

        if (TaskOutcome.APPROVED.equals(outcome)) {
            Assertions.assertEquals(dto.getTrustedAccountListRules().getRule1(),
                account.getApprovalOfSecondAuthorisedRepresentativeIsRequired());
            Assertions.assertEquals(dto.getTrustedAccountListRules().getRule2(),
                account.getTransfersToAccountsNotOnTheTrustedListAreAllowed());
            Assertions.assertTrue(account.getSinglePersonApprovalRequired());
        } else {
            Assertions.assertNotEquals(dto.getTrustedAccountListRules().getRule1(),
                account.getApprovalOfSecondAuthorisedRepresentativeIsRequired());
            Assertions.assertNotEquals(dto.getTrustedAccountListRules().getRule2(),
                account.getTransfersToAccountsNotOnTheTrustedListAreAllowed());
            Assertions.assertEquals(dto.getTrustedAccountListRules().getRule3(),
                                    account.getSinglePersonApprovalRequired());
        }
    }

    static Stream<Arguments> getArguments() {
        return Stream.of(
            Arguments.of(TaskOutcome.APPROVED),
            Arguments.of(TaskOutcome.REJECTED)
        );
    }
}
