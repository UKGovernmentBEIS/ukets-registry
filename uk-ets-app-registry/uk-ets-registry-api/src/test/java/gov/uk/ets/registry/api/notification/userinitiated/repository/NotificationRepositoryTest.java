package gov.uk.ets.registry.api.notification.userinitiated.repository;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.account.repository.AccountAccessRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.common.model.types.Status;
import gov.uk.ets.registry.api.common.test.PostgresJpaTest;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper.AddAccountCommand;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper.AddAccountHolderCommand;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper.AddInstallationEntityToAccountCommand;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper.AddUserToAccountAccessCommand;
import gov.uk.ets.registry.api.notification.userinitiated.domain.Notification;
import gov.uk.ets.registry.api.notification.userinitiated.domain.NotificationChannel;
import gov.uk.ets.registry.api.notification.userinitiated.domain.NotificationDefinition;
import gov.uk.ets.registry.api.notification.userinitiated.domain.NotificationSchedule;
import gov.uk.ets.registry.api.notification.userinitiated.domain.SelectionCriteria;
import gov.uk.ets.registry.api.notification.userinitiated.domain.SupportedParameters;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.ChannelType;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationStatus;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationType;
import gov.uk.ets.registry.api.notification.userinitiated.web.model.DashboardNotificationDTO;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@PostgresJpaTest
class NotificationRepositoryTest {

    private static final String TEST_URID = "UK1234567890";
    public static final String TEST_PARAMETER = "test-parameter";
    public static final String OHA_LABEL = "ETS - Operator holding account";

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NotificationRepository cut;

    @Autowired
    private NotificationDefinitionRepository definitionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    AccountAccessRepository accountAccessRepository;

    Notification complianceNotification;
    Notification adHocNotification;
    LocalDateTime current = LocalDateTime.of(2021, 11, 13, 10, 0);

    NotificationDefinition definition1;

    private AccountModelTestHelper helper;

    private AddAccountHolderCommand addAccountHolderCommand;
    private AddAccountCommand addAccountCommand;
    private AddUserToAccountAccessCommand addAuthorizedRepresentativeToAccountCommand;
    private AddInstallationEntityToAccountCommand addInstallationEntityToAccountCommand;

    @BeforeEach
    public void setUp() {
        definition1 = NotificationDefinition.builder()
            .type(NotificationType.AD_HOC)
            .channel(new NotificationChannel(ChannelType.DASHBOARD))

            .build();

        NotificationDefinition definition2 = NotificationDefinition.builder()
            .type(NotificationType.EMISSIONS_MISSING_FOR_AOHA)
            .channel(new NotificationChannel(ChannelType.EMAIL))
            .shortText("Emissions missing for OHA accounts")
            .longText(
                "<p><strong>bold</strong></p><p><br></p>")
            .selectionCriteria(SelectionCriteria.builder()
                .accountStatuses(List.of(AccountStatus.OPEN, AccountStatus.TRANSFER_PENDING))
                .build()
            )
            .supportedParameters(SupportedParameters.builder()
                .parameters(List.of(TEST_PARAMETER))
                .build())
            .build();

        entityManager.persist(definition1);
        entityManager.persist(definition2);

        complianceNotification = Notification.builder()
            .definition(definition2)
            .status(NotificationStatus.ACTIVE)
            .schedule(NotificationSchedule.builder()
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusMinutes(1))
                .build())
            .creator(TEST_URID)
            .build();
        entityManager.persist(complianceNotification);

        adHocNotification = Notification.builder()
            .definition(definition1)
            .status(NotificationStatus.ACTIVE)
            .schedule(NotificationSchedule.builder()
                .startDateTime(LocalDateTime.of(2021, 10, 1, 10, 0))
                .endDateTime(LocalDateTime.of(2021, 11, 16, 10, 0))
                .build())
            .creator(TEST_URID)
            .build();
        entityManager.persist(adHocNotification);

        entityManager.persist(Notification.builder()
            .definition(definition1)
            .schedule(NotificationSchedule.builder()
                .startDateTime(LocalDateTime.of(2021, 9, 13, 10, 0))
                .endDateTime(LocalDateTime.of(2021, 10, 13, 10, 0))
                .build())
            .status(NotificationStatus.EXPIRED)
            .creator(TEST_URID)
            .build());

        entityManager.persist(Notification.builder()
            .definition(definition1)
            .schedule(NotificationSchedule.builder()
                .startDateTime(LocalDateTime.of(2021, 9, 13, 10, 0))
                .endDateTime(LocalDateTime.of(2021, 10, 14, 10, 0))
                .build())
            .status(NotificationStatus.EXPIRED)
            .creator(TEST_URID)
            .build());

        entityManager.persist(Notification.builder()
            .definition(definition1)
            .schedule(NotificationSchedule.builder()
                .startDateTime(LocalDateTime.of(2021, 11, 13, 9, 30))
                .build())
            .status(NotificationStatus.PENDING)
            .creator(TEST_URID)
            .build());

        entityManager.persist(Notification.builder()
            .definition(definition1)
            .schedule(NotificationSchedule.builder()
                .startDateTime(LocalDateTime.of(2021, 11, 12, 9, 30))
                .endDateTime(LocalDateTime.of(2021, 11, 14, 9, 0))
                .build())
            .status(NotificationStatus.PENDING)
            .creator(TEST_URID)
            .build());

        entityManager.persist(Notification.builder()
            .definition(definition1)
            .schedule(NotificationSchedule.builder()
                .startDateTime(LocalDateTime.of(2021, 12, 12, 9, 30))
                .build())
            .status(NotificationStatus.PENDING)
            .creator(TEST_URID)
            .build());

        adHocNotification = Notification.builder()
            .definition(definition1)
            .status(NotificationStatus.ACTIVE)
            .schedule(NotificationSchedule.builder()
                .startDateTime(LocalDateTime.of(2021, 10, 1, 10, 0))
                .endDateTime(LocalDateTime.of(2021, 11, 13, 9, 0))
                .build())
            .creator(TEST_URID)
            .build();
        entityManager.persist(adHocNotification);

        entityManager.flush();
        entityManager.clear();


        helper = new AccountModelTestHelper(entityManager.getEntityManager());
        addAccountHolderCommand = AddAccountHolderCommand.builder()
            .accountHolderType(AccountHolderType.GOVERNMENT)
            .identifier(100L)
            .name("Test account holder name")
            .firstName("  account  ")
            .lastName("   HOLDER  ")
            .status(Status.ACTIVE)
            .build();
        AccountHolder accountHolder = helper.addAccountHolder(addAccountHolderCommand);

        addAccountCommand = AddAccountCommand.builder()
            .accountHolder(accountHolder)
            .accountId(101L)
            .fullIdentifier("UK-100-123456-324")
            .accountName("Test-Account")
            .accountStatus(AccountStatus.OPEN)
            .complianceStatus(ComplianceStatus.C)
            .registryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT)
            .kyotoAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT)
            .accountType(OHA_LABEL)
            .build();
        helper.addAccount(addAccountCommand);

        Account account = accountRepository.findByFullIdentifier("UK-100-123456-324").get();

        addAuthorizedRepresentativeToAccountCommand =
            AddUserToAccountAccessCommand.builder()
                .account(account)
                .enrollmentKey("UK1234567")
                .state(AccountAccessState.ACTIVE)
                .urid("UK98567654")
                .build();
        helper.addUserToAccountAccess(addAuthorizedRepresentativeToAccountCommand);

        addInstallationEntityToAccountCommand = AddInstallationEntityToAccountCommand.builder()
            .account(account)
            .identifier(123L)
            .name("Test installation")
            .permitId("permit-id")
            .regulatorType(RegulatorType.EA)
            .activityType("PRODUCTION_OF_CEMENT_CLINKER")
            .build();
        helper.addInstallationToAccount(addInstallationEntityToAccountCommand);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void shouldRetrieveActiveNotificationsByType() {
        Optional<Notification> activeNotificationByType =
            cut.findActiveNotificationByType(NotificationType.EMISSIONS_MISSING_FOR_AOHA);
        assertThat(activeNotificationByType).isNotNull();
        Assertions.assertTrue(activeNotificationByType.isPresent());
        Assertions.assertEquals(NotificationStatus.ACTIVE, activeNotificationByType.get().getStatus());
    }

    @Test
    void shouldRetrieveNotificationsById() {
        Optional<Notification> activeNotificationByType = cut.getByIdWithDefinition(complianceNotification.getId());
        assertThat(activeNotificationByType).isNotNull();
        Assertions.assertTrue(activeNotificationByType.isPresent());
        Assertions.assertEquals(NotificationType.EMISSIONS_MISSING_FOR_AOHA,
            activeNotificationByType.get().getDefinition().getType());
    }

    @Test
    void shouldRetrieveNotificationsWithTheirDefinitions() {

        List<Notification> notifications = cut.findAllWithDefinitions();

        assertThat(notifications).hasSize(8);

        NotificationDefinition definition =
            getDefinitionByType(notifications, NotificationType.EMISSIONS_MISSING_FOR_AOHA);
        assertThat(definition).isNotNull();
        assertThat(definition.getType()).isEqualTo(NotificationType.EMISSIONS_MISSING_FOR_AOHA);
    }

    @Test
    void shouldRetrieveJsonPropertiesAsObjects() {

        List<Notification> notifications = cut.findAllWithDefinitions();

        NotificationDefinition definition =
            getDefinitionByType(notifications, NotificationType.EMISSIONS_MISSING_FOR_AOHA);

        assertThat(definition.getSelectionCriteria()).isNotNull();
        assertThat(definition.getSelectionCriteria().getAccountStatuses())
            .containsExactlyInAnyOrderElementsOf(List.of(AccountStatus.OPEN, AccountStatus.TRANSFER_PENDING));

        assertThat(definition.getSupportedParameters()).isNotNull();
        assertThat(definition.getSupportedParameters().getParameters()).hasSize(1);
        assertThat(definition.getSupportedParameters().getParameters().get(0)).isEqualTo(TEST_PARAMETER);
    }

    @Test
    void shouldRetrieveNotificationsWithoutDefinitions() {
        List<Notification> notifications = cut.findAll();

        assertThat(Hibernate.isInitialized(notifications.get(0).getDefinition())).isFalse();
    }

    @Test
    void shouldPersistNewNotificationAfterRetrievingExistingDefinition() {

        // NOTE: when retrieving by id it is better to use getById, otherwise on insert Hibernate will execute
        // an extra select
        NotificationDefinition definition = definitionRepository.getById(1L);

        Notification newNotification = Notification.builder()
            .definition(definition)
            .status(NotificationStatus.ACTIVE)
            .schedule(NotificationSchedule.builder()
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusMinutes(1))
                .build())
            .creator(TEST_URID)
            .build();

        cut.save(newNotification);

        entityManager.flush();
        entityManager.clear();

        assertThat(cut.findAll()).hasSize(9);
    }

    @Test
    public void shouldRetrieveDashboardNotifications() {
        Notification adHocNotificationPending = Notification.builder()
            .definition(definition1)
            .status(NotificationStatus.PENDING)
            .creator(TEST_URID)
            .build();
        entityManager.persist(adHocNotificationPending);

        Notification adHocNotificationExpired = Notification.builder()
            .definition(definition1)
            .status(NotificationStatus.EXPIRED)
            .creator(TEST_URID)
            .build();
        entityManager.persist(adHocNotificationExpired);

        List<DashboardNotificationDTO> dashboardNotifications = cut.findDashBoardNotifications();

        assertThat(dashboardNotifications).hasSize(2);

    }


    private NotificationDefinition getDefinitionByType(List<Notification> notifications, NotificationType type) {
        return notifications.stream()
            .filter(n -> n.getDefinition().getType().equals(type))
            .findFirst()
            .map(Notification::getDefinition)
            .orElse(null);
    }
}
