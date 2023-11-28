package gov.uk.ets.registry.api.transaction;

import static gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus.COMPLETED;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.helper.AuthorizationTestHelper;
import gov.uk.ets.registry.api.transaction.domain.AccountBasicInfo;
import gov.uk.ets.registry.api.transaction.domain.ExcessAllocationTransactionFactory;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.service.TransactionManagementService;
import gov.uk.ets.registry.api.transaction.service.TransactionPersistenceService;
import gov.uk.ets.registry.api.transaction.service.TransactionWithTaskService;
import gov.uk.ets.registry.api.transaction.web.mapper.TransactionSearchResultMapper;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.ArrayList;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class TransactionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TransactionService transactionService;

    @Mock
    private TransactionWithTaskService transactionWithTaskService;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private TransactionManagementService transactionManagementService;

    @Mock
    private TransactionPersistenceService transactionPersistenceService;

    @Mock
    private UserService userService;

    @Mock
    private TransactionSearchResultMapper resultMapper;

    @Mock
    ExcessAllocationTransactionFactory excessAllocationTransactionFactory;

    private AuthorizationTestHelper authorizationTestHelper;

    private TransactionController controller;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        controller = new TransactionController(transactionService, transactionWithTaskService,
                                               authorizationService, transactionManagementService,
                                               transactionPersistenceService,excessAllocationTransactionFactory,
                                               userService, resultMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        authorizationTestHelper = new AuthorizationTestHelper(authorizationService);
    }

    @Test()
    @DisplayName("Test transaction search filters descriptor contract")
    void testTransactionFiltersDescriptorApi() throws Exception {
        authorizationTestHelper.mockAuthAsAdmin();
        mockMvc.perform(get("/api-registry/transactions.list.filters")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accountTypeOptions", is(notNullValue())));
    }

    @Test()
    @DisplayName("Test retrieval of specific transaction")
    void test_getTransactionResponse() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setIdentifier("GB100002");
        transaction.setLastUpdated(new Date());
        transaction.setStatus(COMPLETED);
        transaction.setQuantity(110L);
        transaction.setBlocks(new ArrayList<>());
        transaction.setResponseEntries(new ArrayList<>());

        AccountBasicInfo transferAccount = new AccountBasicInfo();
        transaction.setTransferringAccount(transferAccount);
        AccountBasicInfo acquiringAccount = new AccountBasicInfo();
        transaction.setAcquiringAccount(acquiringAccount);

        Mockito.when(transactionPersistenceService.getTransaction(any())).thenReturn(transaction);
        mockMvc.perform(get("/api-registry/transactions.get?transactionIdentifier=GB100002")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
    
    @Test()
    @DisplayName("Test generation of transaction details report")
    void test_requestTransactionDetailsReportResponse() throws Exception {
        String transactionIdentifier = "GB100002";

        Mockito.when(transactionManagementService
            .requestTransactionDetailsReport(transactionIdentifier)).thenReturn(1234L);
        mockMvc.perform(get("/api-registry/transactions.generate.details.report?transactionIdentifier=" 
            + transactionIdentifier)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
}
