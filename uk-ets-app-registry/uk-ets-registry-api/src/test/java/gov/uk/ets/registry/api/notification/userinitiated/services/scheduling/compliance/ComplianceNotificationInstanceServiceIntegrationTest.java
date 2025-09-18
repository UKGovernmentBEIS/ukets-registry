package gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.compliance;


import freemarker.template.Configuration;
import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.common.model.types.Status;
import gov.uk.ets.registry.api.common.test.PostgresJpaTest;
import gov.uk.ets.registry.api.compliance.service.ComplianceService;
import gov.uk.ets.registry.api.compliance.web.model.ComplianceOverviewDTO;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper;
import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.notification.emailgeneration.EmailException;
import gov.uk.ets.registry.api.notification.userinitiated.NotificationModelTestHelper;
import gov.uk.ets.registry.api.notification.userinitiated.domain.Notification;
import gov.uk.ets.registry.api.notification.userinitiated.domain.NotificationDefinition;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationType;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.IdentifiableEmailNotification;
import gov.uk.ets.registry.api.notification.userinitiated.repository.NotificationDefinitionRepository;
import gov.uk.ets.registry.api.notification.userinitiated.repository.NotificationRepository;
import gov.uk.ets.registry.api.notification.userinitiated.services.StringEmailTemplateProcessor;
import gov.uk.ets.registry.api.notification.userinitiated.services.UserInitiatedNotificationService;
import gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.NotificationUpdater;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.domain.UserWorkContact;
import gov.uk.ets.registry.api.user.domain.UserWorkContactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@PostgresJpaTest
@DataJpaTest(includeFilters = {
    @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*notification.userinitiated.services.scheduling.*"),
    @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*notification.userinitiated.repository.*")
})
@Import({Configuration.class, StringEmailTemplateProcessor.class})
class ComplianceNotificationInstanceServiceIntegrationTest {

    private static final String OHA_LABEL = "ETS - Operator holding account";
    private static final String PERMIT_ID = "permit-id";
    private static final String USER_FIRST_NAME = "first";
    private static final String USER_LAST_NAME = "last";
    public static final String ACCOUNT_FULL_IDENTIFIER = "UK-100-123456-324";
    public static final String MASKED_ACCOUNT_FULL_IDENTIFIER = "UK-100-12XXXX-324";
    public static final String INSTALLATION_NAME = "Test installation";
    public static final String WORK_EMAIL_ADDRESS = "test@test.com";

    @Autowired
    ComplianceNotificationInstanceService cut;

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    private NotificationDefinitionRepository definitionRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UploadedFilesRepository uploadedFilesRepository;
    @Autowired
    private NotificationUpdater updater;

    @MockBean
    private UserWorkContactRepository userWorkContactRepository;

    @MockBean
    private ComplianceService complianceService;

    @MockBean
    private EventService eventService;

    @MockBean
    private GroupNotificationClient groupNotificationClient;

    @MockBean
    UserInitiatedNotificationService userInitiatedNotificationService;

    NotificationModelTestHelper notificationModelTestHelper;
    private AccountModelTestHelper accountModelTestHelper;

    private AccountModelTestHelper.AddAccountHolderCommand addAccountHolderCommand;
    private AccountModelTestHelper.AddAccountCommand addAccountCommand;
    private AccountModelTestHelper.AddUserToAccountAccessCommand addAuthorizedRepresentativeToAccountCommand;
    private AccountModelTestHelper.AddInstallationEntityToAccountCommand addInstallationEntityToAccountCommand;

    Account account;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUrid(NotificationModelTestHelper.TEST_URID);
        user.setState(UserStatus.ENROLLED);
        user.setFirstName(USER_FIRST_NAME);
        user.setLastName(USER_LAST_NAME);
        entityManager.persist(user);
        notificationModelTestHelper = new NotificationModelTestHelper(definitionRepository, notificationRepository, uploadedFilesRepository);
        notificationModelTestHelper.setUp();

        accountModelTestHelper = new AccountModelTestHelper(entityManager.getEntityManager());
        addAccountHolderCommand = AccountModelTestHelper.AddAccountHolderCommand.builder()
            .accountHolderType(AccountHolderType.GOVERNMENT)
            .identifier(100L)
            .name("Test account holder name")
            .firstName("  account  ")
            .lastName("   HOLDER  ")
            .status(Status.ACTIVE)
            .build();
        AccountHolder accountHolder = accountModelTestHelper.addAccountHolder(addAccountHolderCommand);

        addAccountCommand = AccountModelTestHelper.AddAccountCommand.builder()
            .accountHolder(accountHolder)
            .accountId(101L)
            .fullIdentifier(ACCOUNT_FULL_IDENTIFIER)
            .accountName("Test-Account")
            .accountStatus(AccountStatus.OPEN)
            .complianceStatus(ComplianceStatus.C)
            .registryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT)
            .kyotoAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT)
            .accountType(OHA_LABEL)
            .build();
        account = accountModelTestHelper.addAccount(addAccountCommand);

        addAuthorizedRepresentativeToAccountCommand =
            AccountModelTestHelper.AddUserToAccountAccessCommand.builder()
                .account(account)
                .enrollmentKey("UK1234567")
                .state(AccountAccessState.ACTIVE)
                .urid(NotificationModelTestHelper.TEST_URID)
                .build();
        accountModelTestHelper.addUserToAccountAccess(addAuthorizedRepresentativeToAccountCommand);

        addInstallationEntityToAccountCommand = AccountModelTestHelper.AddInstallationEntityToAccountCommand.builder()
            .account(account)
            .identifier(123L)
            .name(INSTALLATION_NAME)
            .permitId(PERMIT_ID)
            .regulatorType(RegulatorType.EA)
            .activityType("PRODUCTION_OF_CEMENT_CLINKER")
            .build();
        accountModelTestHelper.addInstallationToAccount(addInstallationEntityToAccountCommand);

        UserWorkContact contact = new UserWorkContact();
        contact.setEmail(WORK_EMAIL_ADDRESS);
        contact.setUrid(NotificationModelTestHelper.TEST_URID);
        when(userWorkContactRepository.fetchUserWorkContactsInBatches(Set.of(NotificationModelTestHelper.TEST_URID)))
            .thenReturn(List.of(contact));

        ComplianceOverviewDTO complianceOverviewDTO = new ComplianceOverviewDTO();
        when(complianceService.getComplianceOverview(any())).thenReturn(complianceOverviewDTO);
    }

    @Test
    void shouldGenerateNotifications() {

        List<IdentifiableEmailNotification> identifiableEmailNotifications = cut.generateNotificationInstances();

        assertThat(identifiableEmailNotifications).hasSize(1);
        IdentifiableEmailNotification notification = identifiableEmailNotifications.get(0);

        Notification notificationEntity =
            notificationRepository.findActiveNotificationByType(NotificationType.EMISSIONS_MISSING_FOR_OHA)
                .orElse(null);
        assertThat(notification.getSubject())
            .isEqualTo(
                String.format(
                    "(1-%s-1) The UK Emissions Trading Scheme (UK ETS): Absence of Verified Reportable Emissions",
                    notificationEntity.getId())
            );

        assertThat(notification.getNotificationInstanceId())
            .isEqualTo(String.format("1-%s-1", notificationEntity.getId()));
        assertThat(notification.getRecipients()).containsExactly(WORK_EMAIL_ADDRESS);

        assertThat(notification.getBodyHtml()).contains(MASKED_ACCOUNT_FULL_IDENTIFIER);
        assertThat(notification.getBodyHtml()).contains(PERMIT_ID);
        assertThat(notification.getBodyHtml()).contains(INSTALLATION_NAME);
        assertThat(notification.getBodyHtml()).contains(USER_FIRST_NAME + " " + USER_LAST_NAME);
    }

    @Test
    void shouldThrowWhenTemplateHasErrors() {
        NotificationDefinition definition2 =
            definitionRepository.findByType(NotificationType.EMISSIONS_MISSING_FOR_OHA)
                .orElse(null);
        String wrongText = definition2.getLongText().replace("${installation.permitId}", "${installation.permId}");

        definition2.setLongText(wrongText);

        assertThrows(EmailException.class, () -> cut.generateNotificationInstances());
    }
    
}
