package gov.uk.ets.registry.api.integration.service.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.MaritimeOperator;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.messaging.CompliantEntityInitializationEvent;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.validation.AccountValidationException;
import gov.uk.ets.registry.api.account.validation.AccountValidator;
import gov.uk.ets.registry.api.account.validation.Violation;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.OperatorType;
import gov.uk.ets.registry.api.accountaccess.service.AccountAccessService;
import gov.uk.ets.registry.api.compliance.messaging.ComplianceEventService;
import gov.uk.ets.registry.api.integration.service.IntegrationHeadersUtil;
import gov.uk.ets.registry.api.integration.service.operator.OperatorEventService;
import gov.uk.ets.registry.api.messaging.UktlAccountNotifyMessageService;
import gov.uk.ets.registry.api.messaging.domain.AccountNotification;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.user.domain.UserRole;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.integration.model.account.AccountDetailsMessage;
import uk.gov.netz.integration.model.account.AccountHolderMessage;
import uk.gov.netz.integration.model.account.AccountOpeningEvent;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;

@ExtendWith(MockitoExtension.class)
class AccountEventOpeningServiceTest {

    @Mock
    private AccountEventValidator eventValidator;
    @Mock
    private AccountValidator accountValidator;
    @Mock
    private AccountService accountService;
    @Mock
    private AccountAccessService accountAccessService;
    @Mock
    private ComplianceEventService complianceEventService;
    @Mock
    private AccountEventMapper accountEventMapper;
    @Mock
    private OperatorEventService operatorEventService;
    @Mock
    private UktlAccountNotifyMessageService uktlAccountNotifyMessageService;
    @Mock
    private IntegrationHeadersUtil util;

    private AccountEventOpeningService service;

    @BeforeEach
    public void setup() {
        service = new AccountEventOpeningService(eventValidator, accountValidator, accountService,
            accountAccessService, complianceEventService, accountEventMapper, operatorEventService, uktlAccountNotifyMessageService, util);
    }

    @Test
    void testProcessEventSuccessful() {
        // given
        AccountOpeningEvent event = new AccountOpeningEvent();
        AccountDetailsMessage accountDetailsMessage = new AccountDetailsMessage();
        accountDetailsMessage.setAccountName("A maritime account");
        accountDetailsMessage.setCompanyImoNumber("IMO Number");
        accountDetailsMessage.setFirstYearOfVerifiedEmissions(2025);
        accountDetailsMessage.setEmitterId("1234");
        event.setAccountDetails(accountDetailsMessage);
        AccountHolderMessage accountHolderMessage = new AccountHolderMessage();
        accountHolderMessage.setAccountHolderType(AccountHolderType.ORGANISATION.toString());
        accountHolderMessage.setAddressLine1("Address Line 1");
        accountHolderMessage.setAddressLine2("Address Line 2");
        accountHolderMessage.setCompanyRegistrationNumber("CRN");
        accountHolderMessage.setCountry("UK");
        accountHolderMessage.setName("A name");
        accountHolderMessage.setPostalCode("UR 6789");
        accountHolderMessage.setStateOrProvince("State");
        accountHolderMessage.setTownOrCity("Essex");
        event.setAccountHolder(accountHolderMessage);
        OperatorType operatorType = OperatorType.MARITIME_OPERATOR;
        Map<String, Object> headers = Map.of();
        AccountDTO accountDTO = new AccountDTO();
        Account newAccount = new Account();
        newAccount.setFullIdentifier("fullIdentifier");
        MaritimeOperator maritimeOperator = new MaritimeOperator();
        maritimeOperator.setIdentifier(123L);
        maritimeOperator.setEmitterId("emitterId");
        newAccount.setCompliantEntity(maritimeOperator);
        newAccount.setRegistryAccountType(RegistryAccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT);
        newAccount.setAccountType(AccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT.name());
        newAccount.setAccountName("accountName");

        Mockito.when(util.getCorrelationId(headers)).thenReturn("correlationId");
        Mockito.when(util.getOperatorType(headers)).thenReturn(operatorType);
        Mockito.when(eventValidator.validate(event, operatorType)).thenReturn(List.of());
        Mockito.when(accountEventMapper.convert(event, operatorType)).thenReturn(accountDTO);
        Mockito.when(accountService.createAccount(accountDTO)).thenReturn(newAccount);

        // when
        AccountOpeningResult result = service.process(event, headers);

        // then
        assertThat(result.getAccountFullIdentifier()).isEqualTo("fullIdentifier");
        assertThat(result.getErrors()).isEmpty();

        Mockito.verify(accountValidator, Mockito.times(1)).validate(accountDTO);
        Mockito.verify(accountAccessService, Mockito.times(1))
            .createAccountAccess(newAccount, UserRole.getRolesForRoleBasedAccess(), AccountAccessRight.ROLE_BASED);
        Mockito.verify(complianceEventService, Mockito.times(1))
            .processEvent(any(CompliantEntityInitializationEvent.class));
        Mockito.verify(uktlAccountNotifyMessageService, Mockito.times(1))
        .sendAccountOpeningNotification(AccountNotification.convert(newAccount));
        Mockito.verify(operatorEventService, Mockito.times(1))
            .updateOperator(maritimeOperator, "MARITIME_OPERATOR_HOLDING_ACCOUNT", "correlationId");
    }

    @Test
    void testProcessEventWithBusinessErrors() {
        // given
        AccountOpeningEvent event = new AccountOpeningEvent();
        OperatorType operatorType = OperatorType.MARITIME_OPERATOR;
        Map<String, Object> headers = Map.of();
        IntegrationEventErrorDetails errorDetails = new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0101, "Test");
        List<IntegrationEventErrorDetails> errors = List.of(errorDetails);

        Mockito.when(util.getCorrelationId(headers)).thenReturn("correlationId");
        Mockito.when(util.getOperatorType(headers)).thenReturn(operatorType);
        Mockito.when(eventValidator.validate(event, operatorType)).thenReturn(errors);

        // when
        AccountOpeningResult result = service.process(event, headers);

        // then
        assertThat(result.getAccountFullIdentifier()).isNull();
        assertThat(result.getErrors()).containsOnly(errorDetails);
    }

    @Test
    void testProcessEventSuccessfulWithArNumberException() {
        // given
        AccountOpeningEvent event = new AccountOpeningEvent();
        OperatorType operatorType = OperatorType.MARITIME_OPERATOR;
        Map<String, Object> headers = Map.of();
        AccountDTO accountDTO = new AccountDTO();
        Account newAccount = new Account();
        newAccount.setFullIdentifier("fullIdentifier");
        MaritimeOperator maritimeOperator = new MaritimeOperator();
        maritimeOperator.setIdentifier(123L);
        maritimeOperator.setEmitterId("emitterId");
        newAccount.setCompliantEntity(maritimeOperator);
        newAccount.setRegistryAccountType(RegistryAccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT);
        newAccount.setAccountName("accountName");

        Mockito.when(util.getCorrelationId(headers)).thenReturn("correlationId");
        Mockito.when(util.getOperatorType(headers)).thenReturn(operatorType);
        Mockito.when(eventValidator.validate(event, operatorType)).thenReturn(List.of());
        Mockito.when(accountEventMapper.convert(event, operatorType)).thenReturn(accountDTO);

        Violation violation =
            new Violation("account.authorisedRepresentatives.min.number.violated", "To be ignored");
        Mockito.doThrow(new AccountValidationException(List.of(violation))).when(accountValidator).validate(accountDTO);

        Mockito.when(accountService.createAccount(accountDTO)).thenReturn(newAccount);

        // when
        AccountOpeningResult result = service.process(event, headers);

        // then
        assertThat(result.getAccountFullIdentifier()).isEqualTo("fullIdentifier");
        assertThat(result.getErrors()).isEmpty();

        Mockito.verify(accountAccessService, Mockito.times(1))
            .createAccountAccess(newAccount, UserRole.getRolesForRoleBasedAccess(), AccountAccessRight.ROLE_BASED);
        Mockito.verify(complianceEventService, Mockito.times(1))
            .processEvent(any(CompliantEntityInitializationEvent.class));
        Mockito.verify(operatorEventService, Mockito.times(1))
            .updateOperator(maritimeOperator, "MARITIME_OPERATOR_HOLDING_ACCOUNT", "correlationId");
    }

}
