package gov.uk.ets.registry.api.accounttransfer;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.account.service.TransferValidationService;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.accounttransfer.service.AccountTransferService;
import gov.uk.ets.registry.api.accounttransfer.web.model.AccountTransferActionType;
import gov.uk.ets.registry.api.accounttransfer.web.model.AccountTransferRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


public class AccountTransferControllerTest {

    private MockMvc mockMvc;

    private AccountTransferController controller;

    @Mock
    private AccountTransferService accountTransferService;

    @Mock
    private TransferValidationService transferValidationService;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        controller =
            new AccountTransferController(accountTransferService, transferValidationService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .build();
    }


    @Test
    public void testClaimSuccessReponse() throws Exception {

        final Long taskRequestId = 100000002L;
        AccountTransferRequestDTO request = new AccountTransferRequestDTO();
        request.setAccountIdentifier(1022L);
        request.setAccountTransferType(AccountTransferActionType.ACCOUNT_TRANSFER_TO_EXISTING_HOLDER);
        request.setExistingAcquiringAccountHolderIdentifier(862663L);
        AccountHolderDTO acquiringAccountHolder = new AccountHolderDTO();
        acquiringAccountHolder.setId(1L);
        request.setAcquiringAccountHolder(acquiringAccountHolder);

        Mockito.when(accountTransferService.accountTransfer(request))
            .thenReturn(taskRequestId);

        ObjectMapper mapper = new ObjectMapper();
        mockMvc.perform(post("/api-registry/account-transfer.perform")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", is(100000002)));
    }
}
