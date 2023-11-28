package gov.uk.ets.registry.api.accountholder;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderFileDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderRepresentativeDTO;
import gov.uk.ets.registry.api.accountholder.service.AccountHolderService;
import gov.uk.ets.registry.api.accountholder.service.AccountHolderUpdateService;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderContactUpdateDTO;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderContactUpdateDiffDTO;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderDetailsUpdateDTO;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderDetailsUpdateDiffDTO;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderTypeAheadSearchResultDTO;
import gov.uk.ets.registry.api.common.model.types.Status;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import gov.uk.ets.registry.api.task.domain.types.RequestType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


class AccountHolderControllerTest {

    private MockMvc mockMvc;

    private AccountHolderController controller;

    @Mock
    private AccountHolderService accountHolderService;

    @Mock
    private AccountHolderUpdateService accountHolderUpdateService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        controller = new AccountHolderController(accountHolderService, accountHolderUpdateService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testGetByIdentifierSuccessReponse() throws Exception {

        final String identifier = "100000002";
        AccountHolderTypeAheadSearchResultDTO holder =
            new AccountHolderTypeAheadSearchResultDTO(Long.valueOf(identifier), "Organisation Name", "Your first name",
                "Your last name", AccountHolderType.ORGANISATION);
        List<AccountHolderTypeAheadSearchResultDTO> holders = new ArrayList<>();
        holders.add(holder);
        Mockito.when(accountHolderService.getAccountHolders(identifier,
            EnumSet.of(AccountHolderType.GOVERNMENT, AccountHolderType.INDIVIDUAL)))
            .thenReturn(holders);

        mockMvc.perform(get("/api-registry/account-holder.get.by-identifier")
            .contentType(MediaType.APPLICATION_JSON)
            .param("identifier", identifier)
            .param("types", AccountHolderType.GOVERNMENT.toString(), AccountHolderType.INDIVIDUAL.toString())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void testGetByNameSuccessReponse() throws Exception {
        final String name = "Your first name";
        AccountHolderTypeAheadSearchResultDTO holder =
            new AccountHolderTypeAheadSearchResultDTO(100000002L, "Organisation Name", name, "Your last name",
                AccountHolderType.ORGANISATION);
        List<AccountHolderTypeAheadSearchResultDTO> holders = new ArrayList<>();
        holders.add(holder);
        Mockito.when(accountHolderService.getAccountHolders(name, AccountHolderType.INDIVIDUAL))
            .thenReturn(holders);

        mockMvc.perform(get("/api-registry/account-holder.get.by-name")
            .contentType(MediaType.APPLICATION_JSON)
            .param("name", name)
            .param("type", AccountHolderType.INDIVIDUAL.toString())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void test_updateAccountHolderDetails() throws Exception {
        String template = "/api-registry/account-holder.update-details";
        AccountHolderDetailsUpdateDTO dto = new AccountHolderDetailsUpdateDTO();
        dto.setAccountHolderDiff(new AccountHolderDetailsUpdateDiffDTO());
        dto.setCurrentAccountHolder(new AccountHolderDTO());
        Long accountIdentifier = 1000001L;
        Long taskRequestId = 100000002L;
        long accountHolderIdentifier = 10000039L;
        Mockito.when(accountHolderUpdateService.submitAccountHolderDetailsUpdateRequest(dto, accountIdentifier))
            .thenReturn(taskRequestId);

        ObjectMapper mapper = new ObjectMapper();
        mockMvc.perform(post(template)
            .accept(MediaType.APPLICATION_JSON)
            .param("accountIdentifier", accountIdentifier.toString())
            .param("accountHolderIdentifier", String.valueOf(accountHolderIdentifier))
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(dto))
        ).andExpect(status().isOk()).andExpect(jsonPath("$", is(100000002)));
    }

    @Test
    void test_updateAccountHolderPrimaryContact() throws Exception {
        String template = "/api-registry/account-holder.update-primary-contact";
        Long accountIdentifier = 1000005L;
        Long taskRequestId = 100000004L;
        long accountHolderIdentifier = 10000039L;
        AccountHolderContactUpdateDTO dto = new AccountHolderContactUpdateDTO();
        dto.setCurrentAccountHolder(new AccountHolderRepresentativeDTO());
        dto.setAccountHolderDiff(new AccountHolderContactUpdateDiffDTO());
        dto.setUpdateType(RequestType.ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS);
        Mockito.when(accountHolderUpdateService
            .submitAccountHolderContactUpdateRequest(any(AccountHolderContactUpdateDTO.class), eq(accountIdentifier)))
            .thenReturn(taskRequestId);

        ObjectMapper mapper = new ObjectMapper();
        mockMvc.perform(post(template)
            .accept(MediaType.APPLICATION_JSON)
            .param("accountIdentifier", accountIdentifier.toString())
            .param("accountHolderIdentifier", String.valueOf(accountHolderIdentifier))
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(dto))
        ).andExpect(status().isOk()).andExpect(jsonPath("$", is(100000004)));
    }

    @Test
    void test_updateAccountHolderAlternativePrimaryContact() throws Exception {
        String template = "/api-registry/account-holder.update-alternative-primary-contact";
        Long accountIdentifier = 1000005L;
        Long taskRequestId = 100000004L;
        long accountHolderIdentifier = 10000039L;
        AccountHolderContactUpdateDTO dto = new AccountHolderContactUpdateDTO();
        dto.setCurrentAccountHolder(new AccountHolderRepresentativeDTO());
        dto.setAccountHolderDiff(new AccountHolderContactUpdateDiffDTO());
        dto.setUpdateType(RequestType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_UPDATE);
        Mockito.when(accountHolderUpdateService
                .submitAccountHolderContactUpdateRequest(any(AccountHolderContactUpdateDTO.class), eq(accountIdentifier)))
                .thenReturn(taskRequestId);

        ObjectMapper mapper = new ObjectMapper();
        mockMvc.perform(post(template)
                .accept(MediaType.APPLICATION_JSON)
                .param("accountIdentifier", accountIdentifier.toString())
                .param("accountHolderIdentifier", String.valueOf(accountHolderIdentifier))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto))
        ).andExpect(status().isOk()).andExpect(jsonPath("$", is(100000004)));
    }

    @Test()
    @DisplayName("Test retrieval of account holder files. ")
    void getAccountHolderFiles() throws Exception {
        Long accountIdentifier = 1001L;
        List<AccountHolderFileDTO> results = new ArrayList<>();
        AccountHolderFileDTO dto1 = new AccountHolderFileDTO(1L, "File", "Type", LocalDateTime.now());
        results.add(dto1);
        Mockito.when(accountHolderService.getAccountHolderFiles(accountIdentifier)).thenReturn(results);
        mockMvc.perform(get("/api-registry/account-holder.get.files")
            .param("accountIdentifier", accountIdentifier.toString())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].id", is(1)))
            .andExpect(jsonPath("$.[0].name", is("File")))
            .andExpect(jsonPath("$.[0].type", is("Type")));
    }
}
