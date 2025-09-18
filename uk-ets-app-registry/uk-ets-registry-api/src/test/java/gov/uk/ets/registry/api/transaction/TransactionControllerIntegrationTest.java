package gov.uk.ets.registry.api.transaction;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.common.collect.Lists;
import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.repository.AccountAccessRepository;
import gov.uk.ets.registry.api.account.repository.AccountHolderRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDetailsDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderContactInfoDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderRepresentativeDTO;
import gov.uk.ets.registry.api.account.web.model.AuthorisedRepresentativeDTO;
import gov.uk.ets.registry.api.account.web.model.LegalRepresentativeDetailsDTO;
import gov.uk.ets.registry.api.authz.DisabledKeycloakAuthorizationService;
import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.common.model.entities.Contact;
import gov.uk.ets.registry.api.common.model.repositories.ContactRepository;
import gov.uk.ets.registry.api.common.test.RegistryIntegrationTest;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckResult;
import gov.uk.ets.registry.api.transaction.domain.UnitBlock;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TrustedAccountListRulesDTO;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.repository.UnitBlockRepository;
import gov.uk.ets.registry.api.user.UserDTO;
import gov.uk.ets.registry.api.user.UserGeneratorService;
import gov.uk.ets.registry.api.user.admin.service.DisabledKeycloakUserAdministrationService;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.RoleRepresentation;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RegistryIntegrationTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "/integration-test-application.properties", properties = {
    "keycloak.enabled=false"
})
@Log4j2
class TransactionControllerIntegrationTest {

    @Autowired
    private TransactionService transactionService;

    @MockBean
    private ServiceAccountAuthorizationService serviceAccountAuthorizationService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountAccessRepository accountAccessRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @SpyBean
    private DisabledKeycloakAuthorizationService disabledKeycloakAuthorizationService;

    @SpyBean
    private DisabledKeycloakUserAdministrationService disabledKeycloakUserAdministrationService;

    @Autowired
    private UnitBlockRepository unitBlockRepository;

    @Test
    @Disabled
    /**
     *
     * Disabled due to conflict with other integration tests. It works standalone.
     * For now this scenario describes the happy path only. It creates two accounts with the same accountholder
     * then it creates a transaction.proposal between those two accounts.
     * The transaction is getting to DELAYED status
     * finally the manually cancel method is called and a successfull response is returned back
     * More work is needed in the following areas
     * 1) Create sample data for all integration tests
     * 2) Create more scenarios for the different business rules
     * 3) Break controller per feature. So in a seperate class ol the integration tests that relate to propose, manually
     * cancel and the rest of the controller methods can be grouped.
     */

    void testManuallyCancelTransaction() throws Exception {

        // the following code part sets a keycloak id for the current user
        AccessToken accessToken = new AccessToken();

        String currentUserKeycloakId = "fc4c4b91-efd8-4cc9-a96e-4efdae59b4ba";
        accessToken.setSubject(currentUserKeycloakId);

        // create two users one is the task initiator and one is the task claimant and store them in the database
        UserDTO taskInitiatorDTO = new UserDTO();
        taskInitiatorDTO.setFirstName("Nick");
        taskInitiatorDTO.setLastName("Dorsey");
        String initiatorUrid = new UserGeneratorService().generateURID();
        taskInitiatorDTO.setUrid(initiatorUrid);
        String initiatorKeykloakId = "fc4c4b91-efd8-4cc9-a96e-4efdae59b4ba";
        taskInitiatorDTO.setKeycloakId(initiatorKeykloakId);
        userService.registerUser(taskInitiatorDTO);

        UserDTO taskClaimantDTO = new UserDTO();
        taskClaimantDTO.setFirstName("John");
        taskClaimantDTO.setLastName("Sinclair");
        taskClaimantDTO.setKeycloakId("fc4c4b91-efd8-4cc9-a96e-4efdae59b4bc");
        String taskClaimantDTOUrid = new UserGeneratorService().generateURID();
        taskClaimantDTO.setUrid(taskClaimantDTOUrid);
        userService.registerUser(taskClaimantDTO);

        // Set role for the current user, this needs to be the senior-registry-administrator in order to be able to approve the task
        RoleRepresentation admin =
            new RoleRepresentation("authorized-representative", "authorized representative", true);
        Mockito.when(disabledKeycloakAuthorizationService.getClientLevelRoles(currentUserKeycloakId)).thenReturn(
            Lists.newArrayList(admin));

        // create an individual holder so it can be added to both accounts. A common holder is needed for the transfer to be considered trusted.
        AccountHolder commonHolder = new AccountHolder();
        commonHolder.setType(AccountHolderType.INDIVIDUAL);
        commonHolder.setIdentifier(2000007L);
        Contact accountHolderContact = new Contact();
        accountHolderContact.setEmailAddress("accountHolderContact@trasys.gr");
        contactRepository.save(accountHolderContact);
        commonHolder.setContact(accountHolderContact);
        accountHolderRepository.save(commonHolder);
        // find the two users and set them in a task
        User initiator = userRepository.findByUrid(initiatorUrid);
        User claimant = userRepository.findByUrid(taskClaimantDTOUrid);


        // create two accounts

        Account sampleAccount1 = createSampleAccount("Sample account 1", initiator);
        Account sampleAccount2 = createSampleAccount("Sample account 2", initiator);

        UnitBlock unitBlock = new UnitBlock();
        unitBlock.setStartBlock(100L);
        unitBlock.setEndBlock(200L);

        unitBlock.setAccountIdentifier(sampleAccount1.getIdentifier());
        unitBlock.setType(UnitType.AAU);
        unitBlock.setOriginatingCountryCode("GB");
        unitBlock.setOriginalPeriod(CommitmentPeriod.CP2);
        unitBlock.setApplicablePeriod(CommitmentPeriod.CP2);
        unitBlock.setSubjectToSop(false);

        unitBlockRepository.save(unitBlock);
        // create one transaction


        TransactionSummary transactionSummary = new TransactionSummary();
        transactionSummary.setType(TransactionType.InternalTransfer);

        transactionSummary.setTransferringAccountFullIdentifier(sampleAccount1.getFullIdentifier());
        transactionSummary.setTransferringAccountIdentifier(sampleAccount1.getIdentifier());

        transactionSummary.setAcquiringAccountFullIdentifier(sampleAccount2.getFullIdentifier());
        transactionSummary.setAcquiringAccountIdentifier(sampleAccount2.getIdentifier());


        List<TransactionBlockSummary> blocks = new ArrayList<>();
        TransactionBlockSummary sampleBlock =
            new TransactionBlockSummary(UnitType.AAU, CommitmentPeriod.CP2, CommitmentPeriod.CP2, 100L, false);
        sampleBlock.setStartBlock(1000L);
        sampleBlock.setEndBlock(1010L);
        blocks.add(sampleBlock);
        sampleBlock.setQuantity("1");
        transactionSummary.setBlocks(blocks);
        BusinessCheckResult businessCheckResult = transactionService.proposeTransaction(transactionSummary, false);


        // Send a complete task http request and make assertions
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
            .post("/api-registry/transactions.cancel")
            .content("test")
            .param("comment", "This is why i am cancelling the delayed transaction")
            .param("transactionIdentifier", businessCheckResult.getTransactionIdentifier())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    }

    // creates and activates a sample account
    private Account createSampleAccount(String accountName, User user) {
        AccountDTO sampleAccountDTO = new AccountDTO();
        sampleAccountDTO.setAccountType("PERSON_HOLDING_ACCOUNT");
        AccountDetailsDTO dto = new AccountDetailsDTO();
        dto.setName(accountName);
        sampleAccountDTO.setAccountDetails(dto);
        List<AccountHolder> all = accountHolderRepository.findAll();
        Optional<AccountHolder> first =
            all.stream().filter(a -> a.getType().equals(AccountHolderType.INDIVIDUAL)).findFirst();
        AccountHolderDTO sampleHolder = new AccountHolderDTO();
        sampleHolder.setId(first.get().getIdentifier());

//        EmailAddressDTO sampleEmailAddress = new EmailAddressDTO();
//        sampleEmailAddress.setEmailAddress("sampleholder@trasys.gr");
//        sampleHolder.setEmailAddress(sampleEmailAddress);
        TrustedAccountListRulesDTO trustedAccountListRulesDTO = new TrustedAccountListRulesDTO();
        trustedAccountListRulesDTO.setRule1(false);
        trustedAccountListRulesDTO.setRule2(false);
        sampleAccountDTO.setTrustedAccountListRules(trustedAccountListRulesDTO);
        sampleAccountDTO.setAccountHolder(sampleHolder);
        AccountHolderContactInfoDTO sampleHolderContactInfo = new AccountHolderContactInfoDTO();
        AccountHolderRepresentativeDTO primaryContact = new AccountHolderRepresentativeDTO();

        LegalRepresentativeDetailsDTO details = new LegalRepresentativeDetailsDTO();
        details.setFirstName("Harry");
        details.setLastName("Howard");
        details.setAka("HarryHoward");
        primaryContact.setDetails(details);
        sampleHolderContactInfo.setPrimaryContact(primaryContact);

        sampleAccountDTO.setAccountHolderContactInfo(sampleHolderContactInfo);
        List<AuthorisedRepresentativeDTO> representatives = new ArrayList<>();
        AuthorisedRepresentativeDTO ar = new AuthorisedRepresentativeDTO();
        ar.setUrid(user.getUrid());
        ar.setRight(AccountAccessRight.INITIATE_AND_APPROVE);

        representatives.add(ar);
        sampleAccountDTO.setAuthorisedRepresentatives(representatives);
        accountService.openAccount(sampleAccountDTO);

        return null;


    }
}

