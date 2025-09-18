package gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.userinactivity;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.common.model.types.Status;
import gov.uk.ets.registry.api.common.test.PostgresJpaTest;
import gov.uk.ets.registry.api.compliance.service.ComplianceService;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper;
import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.notification.emailgeneration.EmailException;
import gov.uk.ets.registry.api.notification.userinitiated.NotificationModelTestHelper;
import gov.uk.ets.registry.api.notification.userinitiated.domain.Notification;
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
import static org.mockito.Mockito.when;

@PostgresJpaTest
@DataJpaTest(includeFilters = {
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*notification.userinitiated.services.scheduling.*"),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*notification.userinitiated.repository.*")
})
@Import({Configuration.class, StringEmailTemplateProcessor.class})
public class UserInactivityNotificationInstanceServiceTest {
    
    private static final String OHA_LABEL = "ETS - Aircraft operator holding account";
    private static final String USER_ID = "UK4574787-IL";
    private static final String USER_FIRST_NAME = "first";
    private static final String USER_LAST_NAME = "last";
    public static final String ACCOUNT_FULL_IDENTIFIER = "UK-999-123456-324";
    public static final String MASKED_ACCOUNT_FULL_IDENTIFIER = "UK-100-12XXXX-324";
    public static final String INSTALLATION_NAME = "Test installation";
    public static final String WORK_EMAIL_ADDRESS = "test@test.com";   
    
    @Autowired
    UserInactivityNotificationInstanceService cut;
    
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    private NotificationDefinitionRepository definitionRepository;
    @Autowired
    private UploadedFilesRepository uploadedFilesRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private NotificationUpdater updater;

    @MockBean
    private UserWorkContactRepository userWorkContactRepository;

    NotificationModelTestHelper notificationModelTestHelper;
    private AccountModelTestHelper accountModelTestHelper;

    private AccountModelTestHelper.AddAccountHolderCommand addAccountHolderCommand;
    private AccountModelTestHelper.AddAccountCommand addAccountCommand;
    private AccountModelTestHelper.AddUserToAccountAccessCommand addAuthorizedRepresentativeToAccountCommand;

    Account account;
    
    @MockBean
    GroupNotificationClient groupNotificationClient;

    @MockBean
    EventService eventService;
    
    @MockBean
    ComplianceService complianceService;

    @MockBean
    UserInitiatedNotificationService userInitiatedNotificationService;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUrid(NotificationModelTestHelper.INACTIVE_USER_TEST_URID);
        user.setState(UserStatus.ENROLLED);
        user.setFirstName(USER_FIRST_NAME);
        user.setLastName(USER_LAST_NAME);
        entityManager.persist(user);
        notificationModelTestHelper = new NotificationModelTestHelper(definitionRepository, notificationRepository, uploadedFilesRepository);
        notificationModelTestHelper.setUp();
        
        UserWorkContact contact = new UserWorkContact();
        contact.setEmail(WORK_EMAIL_ADDRESS);
        contact.setUrid(NotificationModelTestHelper.INACTIVE_USER_TEST_URID);
        when(userWorkContactRepository.fetchUserWorkContactsInBatches(Set.of(NotificationModelTestHelper.INACTIVE_USER_TEST_URID)))
            .thenReturn(List.of(contact));

        when(userInitiatedNotificationService.getInactiveUsers())
            .thenReturn(List.of(NotificationModelTestHelper.INACTIVE_USER_TEST_URID));
        
        accountModelTestHelper = new AccountModelTestHelper(entityManager.getEntityManager());
        addAccountHolderCommand = AccountModelTestHelper.AddAccountHolderCommand.builder()
            .accountHolderType(AccountHolderType.ORGANISATION)
            .identifier(200L)
            .name("INACTIVE_USER Test account holder name")
            .firstName("  INACTIVE_USERaccount  ")
            .lastName("   INACTIVE_USERHOLDER  ")
            .status(Status.ACTIVE)
            .build();
        AccountHolder accountHolder = accountModelTestHelper.addAccountHolder(addAccountHolderCommand);

        addAccountCommand = AccountModelTestHelper.AddAccountCommand.builder()
            .accountHolder(accountHolder)
            .accountId(201L)
            .fullIdentifier(ACCOUNT_FULL_IDENTIFIER)
            .accountName("INACTIVE_USER-Test-Account")
            .accountStatus(AccountStatus.OPEN)
            .registryAccountType(RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT)
            .kyotoAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT)
            .accountType(OHA_LABEL)
            .build();
        account = accountModelTestHelper.addAccount(addAccountCommand);

        addAuthorizedRepresentativeToAccountCommand =
            AccountModelTestHelper.AddUserToAccountAccessCommand.builder()
                .account(account)
                .enrollmentKey("UK987654321")
                .state(AccountAccessState.ACTIVE)
                .urid(NotificationModelTestHelper.INACTIVE_USER_TEST_URID)
                .build();
        accountModelTestHelper.addUserToAccountAccess(addAuthorizedRepresentativeToAccountCommand);        
    }
    
    @Test
    public void shouldGenerateNotifications() {

        List<IdentifiableEmailNotification> identifiableEmailNotifications = cut.generateNotificationInstances();

        assertThat(identifiableEmailNotifications).hasSize(1);
        IdentifiableEmailNotification notification = identifiableEmailNotifications.get(0);

        Notification notificationEntity =
            notificationRepository.findActiveNotificationByType(NotificationType.USER_INACTIVITY)
                .orElse(null);

        assertThat(notification.getNotificationInstanceId())
            .isEqualTo(String.format("8-%s-1", notificationEntity.getId()));
        assertThat(notification.getRecipients()).containsExactly(WORK_EMAIL_ADDRESS);

        assertThat(notification.getBodyHtml()).contains(USER_FIRST_NAME);
        assertThat(notification.getBodyHtml()).contains(USER_LAST_NAME);
        assertThat(notification.getBodyHtml()).contains(NotificationModelTestHelper.INACTIVE_USER_TEST_URID);
        assertThat(notification.getBodyHtml()).contains(USER_FIRST_NAME + " " + USER_LAST_NAME);
    }

    @Test
    public void shouldThrowWhenTemplateHasErrors() {
        Notification notificationEntity =
                notificationRepository.findActiveNotificationByType(NotificationType.USER_INACTIVITY)
                        .orElse(null);
        assert notificationEntity != null;
        String wrongText = notificationEntity.getLongText().replace("${user.firstName}", "${user.fName}");
        notificationEntity.setLongText(wrongText);

        assertThrows(EmailException.class, () -> cut.generateNotificationInstances());
    }
    
}
