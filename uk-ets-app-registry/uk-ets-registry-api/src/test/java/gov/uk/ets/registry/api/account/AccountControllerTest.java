package gov.uk.ets.registry.api.account;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.commons.logging.Config;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.account.domain.types.InstallationActivityType;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.account.service.AccountOperatorUpdateService;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.shared.AccountProjection;
import gov.uk.ets.registry.api.account.validation.AccountValidator;
import gov.uk.ets.registry.api.account.web.mappers.AccountFilterMapper;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDetailsDTO;
import gov.uk.ets.registry.api.account.web.model.AccountExclusionFromBillingRequestDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderContactInfoDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderRepresentativeDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHoldingDetailsCriteria;
import gov.uk.ets.registry.api.account.web.model.AccountOperatorDetailsUpdateDTO;
import gov.uk.ets.registry.api.account.web.model.AccountStatusAction;
import gov.uk.ets.registry.api.account.web.model.AccountStatusActionOptionDTO;
import gov.uk.ets.registry.api.account.web.model.AccountStatusChangeDTO;
import gov.uk.ets.registry.api.account.web.model.DetailsDTO;
import gov.uk.ets.registry.api.account.web.model.OperatorDTO;
import gov.uk.ets.registry.api.account.web.model.LegalRepresentativeDetailsDTO;
import gov.uk.ets.registry.api.account.web.model.PermitDTO;
import gov.uk.ets.registry.api.account.web.model.search.AccountTypeOption;
import gov.uk.ets.registry.api.auditevent.web.AuditEventDTO;
import gov.uk.ets.registry.api.authz.AuthorizationServiceImpl;
import gov.uk.ets.registry.api.common.model.types.Status;
import gov.uk.ets.registry.api.common.search.SearchFiltersUtils;
import gov.uk.ets.registry.api.common.view.AddressDTO;
import gov.uk.ets.registry.api.common.view.EmailAddressDTO;
import gov.uk.ets.registry.api.common.view.PhoneNumberDTO;
import gov.uk.ets.registry.api.helper.AuthorizationTestHelper;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.transaction.domain.UnitBlock;
import gov.uk.ets.registry.api.transaction.domain.data.TrustedAccountListRulesDTO;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.web.mapper.TransactionSearchResultMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = AccountController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class AccountControllerTest {

    private static final String SEARCH_URL = "/api-registry/accounts.list";

    @MockBean
    private BuildProperties buildProperties;

    @MockBean
    private Config config;

    @Autowired
    private MockMvc mockMvc;

    private AccountController controller;

    @MockBean
    private AccountService accountService;

    @MockBean
    private AuthorizationServiceImpl authorizationService;

    @MockBean
    private AccountValidator accountValidator;

    @MockBean
    private AccountOperatorUpdateService accountOperatorUpdateService;

    @MockBean
    private TransactionSearchResultMapper transactionSearchResultMapper;

    private AuthorizationTestHelper authorizationTestHelper;

    @BeforeEach
    public void setup() {
        controller =
            new AccountController(accountService, authorizationService, accountValidator,
                                  accountOperatorUpdateService, transactionSearchResultMapper);
        authorizationTestHelper = new AuthorizationTestHelper(authorizationService);
    }

    @Test
    void accountIdOrName_can_be_null_or_with_length_greater_or_equals_with_3() throws Exception {
        testLengthValidation("accountIdOrName", 3);
    }

    @Test
    void accountHolderName_can_be_null_or_with_length_greater_or_equals_with_3() throws Exception {
        testLengthValidation("accountHolderName", 3);
    }

    @Test
    void permitOrMonitoringPlanIdentifier_can_be_null_or_with_length_greater_or_equals_with_3()
        throws Exception {
        testLengthValidation("permitOrMonitoringPlanIdentifier", 3);
    }

    @Test
    void authorizedRepresentativeUrid_can_be_null_or_with_length_greater_or_equals_with_3()
        throws Exception {
        testLengthValidation("authorizedRepresentativeUrid", 3);
    }

    @Test
    void installationOrAircraftOperatorIdentifier_can_be_null_or_with_length_greater_or_equals_with_3()
        throws Exception {
        testLengthValidation("installationOrAircraftOperatorId", 3);
    }

    @Test
    void getAccountStatusAvailableActions() throws Exception {
        String template = "/api-registry/accounts.get.statuses";
        AccountStatusActionOptionDTO option = AccountStatusActionOptionDTO.
            builder().
            value(AccountStatusAction.RESTRICT_SOME_TRANSACTIONS).
            label(AccountStatusAction.RESTRICT_SOME_TRANSACTIONS.getDescription()).
            newStatus(AccountStatus.ALL_TRANSACTIONS_RESTRICTED).
            message("A message").
            build();
        Mockito.when(accountService.getAccountStatusAvailableActions(any()))
            .thenReturn(Collections.singletonList(option));

        mockMvc.perform(get(template)
                .param("accountId", "1000")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].value", is(option.getValue().toString())))
            .andExpect(jsonPath("$[0].label", is(option.getLabel())))
            .andExpect(jsonPath("$[0].newStatus", is(option.getNewStatus().toString())))
            .andExpect(jsonPath("$[0].message", is(option.getMessage())));
    }

    @Test
    @DisplayName("Get Account Operator information, expect to succeed")
    void getAccountOperator() throws Exception {
        String template = "/api-registry/accounts.get.operator";
        OperatorDTO dto = new OperatorDTO();
        dto.setIdentifier(10001L);
        dto.setFirstYear(2022);
        dto.setActivityTypes(Set.of(InstallationActivityType.MANUFACTURE_OF_CERAMICS));

        Mockito.when(accountService.getInstallationOrAircraftOperatorDTO(any()))
            .thenReturn(dto);

        mockMvc.perform(get(template)
                .param("accountId", "1000")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.identifier", is(10001)))
            .andExpect(jsonPath("$.firstYear", is(2022)))
            .andExpect(jsonPath("$.activityTypes", contains(InstallationActivityType.MANUFACTURE_OF_CERAMICS.toString())));
    }

    @Test
    @DisplayName("Change Account Status with empty comment cannot be updated , expected to fail")
    void whenChangingStatusCommentIsRequired() throws Exception {

        AccountStatusChangeDTO request = new AccountStatusChangeDTO();
        request.setAccountStatus(AccountStatus.OPEN);
        request.setComment("A comment");
        ObjectMapper mapper = new ObjectMapper();
        String template = "/api-registry/accounts.update";

        //Execute a valid request
        mockMvc.perform(patch(template)
                .param("accountId", "1000")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        request.setComment(null);
        //Execute an invalid request
        mockMvc.perform(patch(template)
                .param("accountId", "1000")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Change Account Status with comment less than 3 chars cannot be updated , expected to fail")
    void whenChangingStatusMinimumCommentLengthIsRequired() throws Exception {

        AccountStatusChangeDTO request = new AccountStatusChangeDTO();
        request.setAccountStatus(AccountStatus.OPEN);
        request.setComment("A comment");
        ObjectMapper mapper = new ObjectMapper();
        String template = "/api-registry/accounts.update";

        //Execute a valid request
        mockMvc.perform(patch(template)
                .param("accountId", "1000")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON)).
            andExpect(status().isOk());

        request.setComment("12");
        //Execute an invalid request
        mockMvc.perform(patch(template)
                .param("accountId", "1000")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON)).
            andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update Account Operator information")
    void updateAccountOperatorDetails() throws Exception {

        AccountOperatorDetailsUpdateDTO dto = new AccountOperatorDetailsUpdateDTO();
        OperatorDTO diff = new OperatorDTO();
        diff.setActivityType(InstallationActivityType.MANUFACTURE_OF_CERAMICS);
        diff.setIdentifier(10001L);
        diff.setFirstYear(2022);
        diff.setType("INSTALLATION");
        diff.setRegulator(RegulatorType.EA);
        dto.setDiff(diff);
        OperatorDTO current = new OperatorDTO();
        current.setActivityType(InstallationActivityType.MANUFACTURE_OF_CERAMICS);
        current.setFirstYear(2024);
        current.setType("INSTALLATION");
        current.setRegulator(RegulatorType.OPRED);
        dto.setCurrent(current);
        ObjectMapper mapper = new ObjectMapper();
        String template = "/api-registry/accounts-operator.update-details";

        //Execute a valid request
        mockMvc.perform(post(template)
                .param("accountIdentifier", "1000")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto))
                .accept(MediaType.APPLICATION_JSON)).
            andExpect(status().isOk());
    }


    @Test
    @DisplayName("Change Account Status with empty status cannot be updated , expected to fail")
    void whenChangingStatusNewStatusIsRequired() throws Exception {

        AccountStatusChangeDTO request = new AccountStatusChangeDTO();
        request.setAccountStatus(AccountStatus.OPEN);
        request.setComment("A comment");
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(request));
        String template = "/api-registry/accounts.update";

        //Execute a valid request
        mockMvc.perform(patch(template)
                .param("accountId", "1000")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        request.setAccountStatus(null);
        //Execute an invalid request
        mockMvc.perform(patch(template)
                .param("accountId", "1000")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test()
    @DisplayName("Test the validation of the account full identifier. ")
    void validateAccountFullIdentifier() throws Exception {
        String accountFullIdentifier = "JP-100-123456";
        Mockito.when(accountService.checkAccountFullIdentifier(accountFullIdentifier)).thenReturn(true);
        Mockito.when(accountService.getAccountFullIdentifier(accountFullIdentifier)).thenReturn(null);
        mockMvc.perform(post("/api-registry/accounts.validate")
                .param("accountFullIdentifier", accountFullIdentifier)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.validAccount", is(true)))
            .andExpect(jsonPath("$.kyotoAccountType", nullValue()));
    }

    @Test
    void test_AccountFiltersDescriptor_test_available_account_type_options_to_user_or_admin() {
        authorizationTestHelper.mockAuthAsAdmin();
        List<AccountTypeOption> accountTypeOptions = Stream
            .of(AccountType.OPERATOR_HOLDING_ACCOUNT, AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT,
                AccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT,
                AccountType.TRADING_ACCOUNT, AccountType.UK_AUCTION_DELIVERY_ACCOUNT,
                AccountType.PERSON_HOLDING_ACCOUNT, AccountType.FORMER_OPERATOR_HOLDING_ACCOUNT)
            .sorted(Comparator.comparing(AccountType::getKyoto).thenComparing(AccountType::getLabel))
            .map(AccountTypeOption::of).collect(Collectors.toList());
        List<AccountTypeOption> adminOptions = new ArrayList<>(accountTypeOptions);
        adminOptions.addAll(List.of(
            AccountTypeOption.builder().label("All KP government accounts")
                .value(SearchFiltersUtils.ALL_KP_GOVERNMENT_ACCOUNTS).build(),
            AccountTypeOption.builder().label("All ETS government accounts")
                .value(SearchFiltersUtils.ALL_ETS_GOVERNMENT_ACCOUNTS).build()));
        assertThat(controller.getAccountFiltersDescriptor().getAccountTypeOptions(), is(adminOptions));

        authorizationTestHelper.mockAuthAsNonAdmin();
        assertThat(controller.getAccountFiltersDescriptor().getAccountTypeOptions(), not(adminOptions));
        assertThat(controller.getAccountFiltersDescriptor().getAccountTypeOptions(), is(accountTypeOptions));
    }

    @Test
    void test_AccountFiltersDescriptor_test_available_account_status_options_to_user_or_admin() {
        List<String> expectedAccountStatuses = Stream.of(AccountStatus.values())
            .filter(status -> !List.of(AccountStatus.CLOSED,
                AccountStatus.SUSPENDED,
                AccountStatus.TRANSFER_PENDING,
                AccountStatus.PROPOSED,
                AccountStatus.REJECTED).contains(status))
            .map(AccountStatus::name)
            .collect(Collectors.toList());
        authorizationTestHelper.mockAuthAsNonAdmin();
        assertThat(controller.getAccountFiltersDescriptor().getAccountStatusOptions(), is(expectedAccountStatuses));

        authorizationTestHelper.mockAuthAsAdmin();
        expectedAccountStatuses = Stream.of(AccountStatus.values())
            .filter(status -> !AccountStatus.REJECTED.equals(status))
            .map(AccountStatus::name)
            .collect(Collectors.toList());
        expectedAccountStatuses.add(AccountFilterMapper.ALL_EXCEPT_CLOSED);
        assertThat(controller.getAccountFiltersDescriptor().getAccountStatusOptions(), is(expectedAccountStatuses));
    }

    @Test
    void test_get_account_filters_descriptor_contract() throws Exception {
        authorizationTestHelper.mockAuthAsAdmin();
        mockMvc.perform(get("/api-registry/accounts.list.filters").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andExpect(jsonPath("$.accountTypeOptions", is(notNullValue())))
            .andExpect(jsonPath("$.accountStatusOptions", is(notNullValue())));
    }

    @Test
    void test_get_account_history() throws Exception {
        List<AuditEventDTO> eventDTOS = new ArrayList<>();
        String description = "test comment";
        String creator = "Test creator";
        Long identifier = 100023L;
        String creatorType = "user";
        AuditEventDTO dto = new AuditEventDTO(String.valueOf(identifier), EventType.TASK_COMMENT.name(), description,
            creator, creatorType, new Date());
        eventDTOS.add(dto);
        Mockito.when(accountService.getAccountHistory(identifier)).thenReturn(eventDTOS);
        mockMvc.perform(get("/api-registry/accounts.get.history")
                .accept(MediaType.APPLICATION_JSON).param("identifier", identifier.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].creatorType", is(creatorType)))
            .andExpect(jsonPath("$.[0].creator", is(creator)))
            .andExpect(jsonPath("$.[0].creatorType", is(creatorType)))
            .andExpect(jsonPath("$.[0].description", is(description)))
            .andExpect(jsonPath("$.[0].domainId", is(identifier.toString())));
    }

    @Test
    void test_search_accounts_service_contract() throws Exception {
        MockAccountCommand expectedAccountInResults = MockAccountCommand.builder()
            .accountHolderName("account holder name").accountName("account name").accountStatus(AccountStatus.OPEN)
            .accountType("A TYPE").balance(1234L).complianceStatus(ComplianceStatus.A)
            .fullIdentifier("UK-100-3324-333").identifier(12233L).build();
        AccountProjection account = mockAccount(expectedAccountInResults);

        mockServiceResult(account);

        mockMvc.perform(get(SEARCH_URL).param("page", "0").param("pageSize", "30")
                .param("sortDirection", Direction.ASC.toString()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andExpect(jsonPath("$.page", is(0)))
            .andExpect(jsonPath("$.pageSize", is(30))).andExpect(jsonPath("$.totalResults", is(1)))
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].accountId", is(expectedAccountInResults.identifier.intValue())))
            .andExpect(jsonPath("$.items[0].fullAccountNo", is(expectedAccountInResults.fullIdentifier)))
            .andExpect(jsonPath("$.items[0].accountName", is(expectedAccountInResults.accountName)))
            .andExpect(jsonPath("$.items[0].accountType", is(expectedAccountInResults.accountType)))
            .andExpect(jsonPath("$.items[0].accountHolderName", is(expectedAccountInResults.accountHolderName)))
            .andExpect(jsonPath("$.items[0].accountStatus", is(expectedAccountInResults.accountStatus.name())))
            .andExpect(
                jsonPath("$.items[0].complianceStatus", is(expectedAccountInResults.complianceStatus.name())))
            .andExpect(jsonPath("$.items[0].balance", is(expectedAccountInResults.balance.intValue())));
    }

    @Test
    @DisplayName("Create operator holding account with valid properties return status 201")
    void testCreateOperatorHoldingAccount() throws Exception {
        String template = "/api-registry/accounts.propose";

        AccountDTO accountDto = mockValidAccountDTO();
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(post(template)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(accountDto))
        ).andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Create operator holding account with account status null returns error 400")
    void whenCreatingAccountHolderIsRequired() throws Exception {
        String template = "/api-registry/accounts.propose";

        AccountDTO accountDto = mockValidAccountDTO();
        accountDto.setAccountType(null);
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(post(template)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(accountDto))
        ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Get Holding Details without original/applicable period, expected to fail since they are mandatory")
    void test_getHoldingDetailsWithoutPeriod() throws Exception {
        String template = "/api-registry/accounts.get.holding-details";

        UnitBlock unitBlock = new UnitBlock();
        unitBlock.setAccountIdentifier(12345L);
        unitBlock.setReservedForTransaction(null);
        unitBlock.setStartBlock(1000L);
        unitBlock.setEndBlock(1030L);
        unitBlock.setType(UnitType.CER);

        AccountHoldingDetailsCriteria criteria = new AccountHoldingDetailsCriteria();
        criteria.setAccountId(unitBlock.getAccountIdentifier());
        criteria.setUnit(unitBlock.getType().name());

        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(get(template)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(criteria))
        ).andExpect(status().isBadRequest());
    }

    @Test
    void test_excludeAccountFromBilling() throws Exception {
        String template = "/api-registry/accounts.exclude.from.billing";

        AccountExclusionFromBillingRequestDTO request = new AccountExclusionFromBillingRequestDTO();
        request.setExclusionRemarks("Exclusion Remarks...");

        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(post(template)
            .param("identifier", "1000")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request))
        ).andExpect(status().isOk());
    }

    @Test
    void test_includeAccountInBilling() throws Exception {
        String template = "/api-registry/accounts.include.in.billing";

        mockMvc.perform(post(template)
            .param("identifier", "1000")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    private void testLengthValidation(String parameter, int minCharsLength) throws Exception {
        mockServiceResult(
            mockAccount(MockAccountCommand.builder()
                .accountHolderName("account holder name")
                .accountName("account name")
                .accountStatus(AccountStatus.OPEN)
                .accountType("A TYPE")
                .balance(1234L)
                .complianceStatus(ComplianceStatus.A)
                .fullIdentifier("UK-100-3324-333")
                .identifier(12233L)
                .build()));
        mockMvc.perform(
                get(SEARCH_URL).param("sortDirection", Direction.ASC.toString()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        String term = "";
        for (int i = 1; i < minCharsLength; i++) {
            term += "x";
        }
        mockMvc.perform(get(SEARCH_URL).param(parameter, term).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    private void mockServiceResult(AccountProjection... results) {
        Page<AccountProjection> searchResult = Mockito.mock(Page.class);
        Mockito.when(searchResult.getContent()).thenReturn(Arrays.asList(results));
        Mockito.when(searchResult.getTotalElements()).thenReturn(Long.valueOf(results.length));
        Mockito.when(accountService.search(any(), any(), eq(false))).thenReturn(searchResult);
    }

    private AccountProjection mockAccount(MockAccountCommand command) {
        AccountProjection account = Mockito.mock(AccountProjection.class);
        Mockito.when(account.getIdentifier()).thenReturn(command.identifier);
        Mockito.when(account.getFullIdentifier()).thenReturn(command.fullIdentifier);
        Mockito.when(account.getAccountName()).thenReturn(command.accountName);
        Mockito.when(account.getTypeLabel()).thenReturn(command.accountType);
        Mockito.when(account.getAccountHolderName()).thenReturn(command.accountHolderName);
        Mockito.when(account.getAccountStatus()).thenReturn(command.accountStatus);
        Mockito.when(account.getComplianceStatus()).thenReturn(command.complianceStatus);
        Mockito.when(account.getBalance()).thenReturn(command.balance);

        return account;
    }

    @Builder
    private static class MockAccountCommand {
        private final Long identifier;
        private final String fullIdentifier;
        private final String accountName;
        private final String accountType;
        private final String accountHolderName;
        private final AccountStatus accountStatus;
        private final ComplianceStatus complianceStatus;
        private final Long balance;
    }


    private AccountDTO mockValidAccountDTO() {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountType("OPERATOR_HOLDING_ACCOUNT");

        AccountHolderDTO accountHolderDTO = new AccountHolderDTO();
        accountHolderDTO.setStatus(Status.ACTIVE);

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setCountry("UK");
        addressDTO.setPostCode("12211");
        addressDTO.setCity("London");
        addressDTO.setLine1("Line 1");
        accountHolderDTO.setAddress(addressDTO);
        accountDTO.setAccountHolder(accountHolderDTO);

        DetailsDTO detailsDto = new DetailsDTO();
        detailsDto.setName("Test Name Co");
        detailsDto.setRegistrationNumber("223312");
        accountHolderDTO.setDetails(detailsDto);
        accountHolderDTO.setType(AccountHolderType.ORGANISATION);

        TrustedAccountListRulesDTO rulesDTO = new TrustedAccountListRulesDTO();
        rulesDTO.setRule1(false);
        rulesDTO.setRule2(false);
        accountDTO.setTrustedAccountListRules(rulesDTO);

        PermitDTO permitDTO = new PermitDTO();
        permitDTO.setId("12345678");

        OperatorDTO operator = new OperatorDTO();
        operator.setName("installation");
        operator.setActivityType(InstallationActivityType.MANUFACTURE_OF_MINERAL_WOOL);
        operator.setRegulator(RegulatorType.DAERA);
        operator.setType("INSTALLATION");
        operator.setFirstYear(2021);
        operator.setPermit(permitDTO);
        accountDTO.setOperator(operator);

        AccountHolderRepresentativeDTO representativeDTO = new AccountHolderRepresentativeDTO();
        representativeDTO.setAddress(addressDTO);

        EmailAddressDTO emailAddressDTO = new EmailAddressDTO();
        emailAddressDTO.setEmailAddress("test@mail.com");
        emailAddressDTO.setEmailAddressConfirmation("test@mail.com");
        representativeDTO.setEmailAddress(emailAddressDTO);

        PhoneNumberDTO phoneNumberDTO = new PhoneNumberDTO();
        phoneNumberDTO.setCountryCode1("44");
        phoneNumberDTO.setPhoneNumber1("33334444");

        LegalRepresentativeDetailsDTO legalRepresentativeDetailsDTO = new LegalRepresentativeDetailsDTO();
        legalRepresentativeDetailsDTO.setFirstName("Firstname");
        legalRepresentativeDetailsDTO.setLastName("Lastname");
        representativeDTO.setDetails(legalRepresentativeDetailsDTO);
        representativeDTO.setPositionInCompany("Head");
        representativeDTO.setEmailAddress(emailAddressDTO);
        representativeDTO.setPhoneNumber(phoneNumberDTO);
        representativeDTO.setAddress(addressDTO);

        AccountHolderContactInfoDTO contactInfoDTO = new AccountHolderContactInfoDTO();
        contactInfoDTO.setPrimaryContact(representativeDTO);
        accountDTO.setAccountHolderContactInfo(contactInfoDTO);

        AccountDetailsDTO accountDetails = new AccountDetailsDTO();
        accountDetails.setName("New acc1");
        accountDTO.setAccountDetails(accountDetails);

        return accountDTO;
    }

}
