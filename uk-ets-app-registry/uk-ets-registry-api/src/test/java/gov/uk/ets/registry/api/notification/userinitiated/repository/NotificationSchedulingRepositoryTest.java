package gov.uk.ets.registry.api.notification.userinitiated.repository;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.common.model.types.Status;
import gov.uk.ets.registry.api.common.test.PostgresJpaTest;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper.AddAccountCommand;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper.AddAccountHolderCommand;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper.AddAircraftEntityToAccountCommand;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper.AddInstallationEntityToAccountCommand;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper.AddUserToAccountAccessCommand;
import gov.uk.ets.registry.api.notification.userinitiated.NotificationModelTestHelper;
import gov.uk.ets.registry.api.notification.userinitiated.domain.Notification;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationStatus;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationType;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.AircraftOperatorParameters;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.BaseNotificationParameters;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.InstallationParameters;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@PostgresJpaTest
class NotificationSchedulingRepositoryTest {

    private static final String OHA_LABEL = "ETS - Operator holding account";
    public static final String PERMIT_ID = "permit-id";
    public static final long AIRCRAFT_OPERATOR_ID = 1234567L;
    public static final String TEST_USER_ID = "UK405681794859";
    
    public static final Long INACTIVE_USER_DAYS = 10L;
    public static final String INACTIVE_USER_TEST_URID = "UK3456789012";
    public static final String USER_FIRST_NAME = "Test Firstname";
    public static final String USER_LAST_NAME = "Test Surname";
    public static final String WORK_EMAIL_ADDRESS = "test@email.com";
    public static final String ACCOUNT_FULL_IDENTIFIER = "UK-777-123456-324";
    
    
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private NotificationDefinitionRepository notificationDefinitionRepository;
    @Autowired
    private UploadedFilesRepository uploadedFilesRepository;
    @Autowired
    private NotificationDefinitionRepository definitionRepository;

    @Autowired
    private NotificationSchedulingRepository notificationSchedulingRepository;

    NotificationModelTestHelper notificationModelTestHelper;
    private AccountModelTestHelper accountModelTestHelper;

    private AddAccountHolderCommand addAccountHolderCommand,IUAddAccountHolderCommand;
    private AddAccountCommand addAccountCommand,IUAddAccountCommand;
    private AddUserToAccountAccessCommand addAuthorizedRepresentativeToAccountCommand,IUAddAuthorizedRepresentativeToAccountCommand;
    private AddInstallationEntityToAccountCommand addInstallationEntityToAccountCommand;

    @Autowired
    private TestEntityManager entityManager;

    LocalDateTime dateTime = LocalDateTime.of(2021, 11, 13, 10, 0);

    Account account,inactiveUserAccount;

    @BeforeEach
    public void setUp() {

        notificationModelTestHelper = new NotificationModelTestHelper(definitionRepository, notificationRepository, uploadedFilesRepository);
        notificationModelTestHelper.setUp();

        accountModelTestHelper = new AccountModelTestHelper(entityManager.getEntityManager());
        addAccountHolderCommand = AddAccountHolderCommand.builder()
            .accountHolderType(AccountHolderType.GOVERNMENT)
            .identifier(100L)
            .name("Test account holder name")
            .firstName("  account  ")
            .lastName("   HOLDER  ")
            .status(Status.ACTIVE)
            .build();
        AccountHolder accountHolder = accountModelTestHelper.addAccountHolder(addAccountHolderCommand);

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
        account = accountModelTestHelper.addAccount(addAccountCommand);

        addAuthorizedRepresentativeToAccountCommand =
            AddUserToAccountAccessCommand.builder()
                .account(account)
                .enrollmentKey("UK1234567")
                .state(AccountAccessState.ACTIVE)
                .urid("UK98567654")
                .build();
        accountModelTestHelper.addUserToAccountAccess(addAuthorizedRepresentativeToAccountCommand);

        // Setup INACTIVE_USER
        User user = new User();
        user.setUrid(INACTIVE_USER_TEST_URID);
        user.setState(UserStatus.ENROLLED);
        user.setFirstName(USER_FIRST_NAME);
        user.setLastName(USER_LAST_NAME);
        user.setEmail(WORK_EMAIL_ADDRESS);
        entityManager.persist(user);
        
        IUAddAccountHolderCommand = AccountModelTestHelper.AddAccountHolderCommand.builder()
            .accountHolderType(AccountHolderType.ORGANISATION)
            .identifier(200L)
            .name("Test account holder name2")
            .firstName("  Testaccount  ")
            .lastName("   HOLDERTester  ")
            .status(Status.ACTIVE)
            .build();        
        AccountHolder inactiveUserAccountHolder = accountModelTestHelper.addAccountHolder(IUAddAccountHolderCommand);

        IUAddAccountCommand = AccountModelTestHelper.AddAccountCommand.builder()
            .accountHolder(inactiveUserAccountHolder)
            .accountId(201L)
            .fullIdentifier(ACCOUNT_FULL_IDENTIFIER)
            .accountName("Test-Account2")
            .accountStatus(AccountStatus.OPEN)
            .complianceStatus(ComplianceStatus.C)
            .registryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT)
            .accountType(OHA_LABEL)
            .build();
        inactiveUserAccount = accountModelTestHelper.addAccount(IUAddAccountCommand);

        IUAddAuthorizedRepresentativeToAccountCommand =
            AccountModelTestHelper.AddUserToAccountAccessCommand.builder()
                .account(inactiveUserAccount)
                .enrollmentKey("UK123457999")
                .state(AccountAccessState.ACTIVE)
                .urid(INACTIVE_USER_TEST_URID)
                .build();
        accountModelTestHelper.addUserToAccountAccess(IUAddAuthorizedRepresentativeToAccountCommand);
    }


    @Test
    void test_changeAdHocNotificationsStatus() {
        List<Notification> before = notificationRepository.findAllWithDefinitions();
        assertEquals(2, before.stream()
            .filter(n -> n.getDefinition().getType().equals(NotificationType.AD_HOC)
                && n.getStatus().equals(NotificationStatus.ACTIVE)).count());
        assertEquals(3, before.stream()
            .filter(n -> !n.getDefinition().getType().equals(NotificationType.AD_HOC)
                && n.getStatus().equals(NotificationStatus.ACTIVE)).count());
        assertEquals(2, before.stream()
            .filter(n -> n.getDefinition().getType().equals(NotificationType.AD_HOC)
                && n.getStatus().equals(NotificationStatus.EXPIRED)).count());
        assertEquals(3, before.stream()
            .filter(n -> n.getDefinition().getType().equals(NotificationType.AD_HOC)
                && n.getStatus().equals(NotificationStatus.PENDING)).count());

        notificationSchedulingRepository.changeNotificationsStatus(dateTime, List.of(NotificationType.AD_HOC.name()));

        flushAndClear();
        List<Notification> after = notificationRepository.findAllWithDefinitions();

        assertEquals(3, after.stream()
            .filter(n -> n.getDefinition().getType().equals(NotificationType.AD_HOC)
                && n.getStatus().equals(NotificationStatus.ACTIVE)).count());
        assertEquals(3, after.stream()
            .filter(n -> !n.getDefinition().getType().equals(NotificationType.AD_HOC)
                && n.getStatus().equals(NotificationStatus.ACTIVE)).count());
        assertEquals(3, after.stream()
            .filter(n -> n.getDefinition().getType().equals(NotificationType.AD_HOC)
                && n.getStatus().equals(NotificationStatus.EXPIRED)).count());
        assertEquals(1, after.stream()
            .filter(n -> n.getDefinition().getType().equals(NotificationType.AD_HOC)
                && n.getStatus().equals(NotificationStatus.PENDING)).count());
    }


    @Test
    public void shouldRetrieveBasicNotificationParameters() {

        List<BaseNotificationParameters> basicNotificationParameters =
            notificationSchedulingRepository.getBasicNotificationParameters(List.of(OHA_LABEL), List.of(AccountStatus.OPEN),
                List.of(UserStatus.ENROLLED),
                List.of(AccountAccessState.ACTIVE), List.of(
                    ComplianceStatus.C));

        assertThat(basicNotificationParameters).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    public void shouldRetrieveInstallationParameters() {

        addInstallationEntityToAccountCommand = AddInstallationEntityToAccountCommand.builder()
            .account(account)
            .identifier(123L)
            .name("Test installation")
            .permitId(PERMIT_ID)
            .regulatorType(RegulatorType.EA)
            .activityType("PRODUCTION_OF_CEMENT_CLINKER")
            .build();
        accountModelTestHelper.addInstallationToAccount(addInstallationEntityToAccountCommand);
        List<InstallationParameters> installationParameters = notificationSchedulingRepository.getInstallationParams(List.of(account.getId()));

        assertThat(installationParameters).hasSize(1);
        assertThat(installationParameters.get(0).getPermitId()).isEqualTo(PERMIT_ID);
    }

    @Test
    public void shouldRetrieveAircraftOperatorParameters() {
        AddAircraftEntityToAccountCommand addAircraftEntityToAccountCommand =
            AddAircraftEntityToAccountCommand.builder()
                .account(account)
                .identifier(AIRCRAFT_OPERATOR_ID)
                .monitoringPlanId("monitoring-plan")
                .name("aircraft")
                .regulatorType(RegulatorType.DAERA)
                .build();
        accountModelTestHelper.addAircraftToAccount(addAircraftEntityToAccountCommand);

        List<AircraftOperatorParameters> aircraftOperatorParameters =
            notificationSchedulingRepository.getAircraftOperatorParameters(List.of(account.getId()));

        assertThat(aircraftOperatorParameters).hasSize(1);
        assertThat(aircraftOperatorParameters.get(0).getId()).isEqualTo(AIRCRAFT_OPERATOR_ID);
    }

    @Test
    public void shouldRetrieveUserInactivitityNotificationParameters() {        
        List<BaseNotificationParameters> basicNotificationParameters =
            notificationSchedulingRepository.getUserInactivityNotificationParameters(
                List.of(NotificationModelTestHelper.INACTIVE_USER_TEST_URID), 
                List.of(AccountStatus.OPEN),
                List.of(UserStatus.ENROLLED),
                List.of(AccountAccessState.ACTIVE));

        assertThat(basicNotificationParameters).hasSize(1);
    }

    @Test
    public void test_changeAdHocEmailNotificationsStatus() {

        List<String> types = Arrays.asList(NotificationType.AD_HOC_EMAIL.name());

        Notification notification = notificationRepository.findAll().stream()
            .filter(n -> n.getDefinition().getType().equals(NotificationType.AD_HOC_EMAIL))
            .collect(Collectors.toList()).get(0);

        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.ACTIVE);

        // Case: `startDateTime <= now` and `endDateTime is null` -> should be 'ACTIVE'
        notification.getSchedule().setEndDateTime(null);
        notificationSchedulingRepository.save(notification);
        flushAndClear();
        notificationSchedulingRepository.changeNotificationsStatus(dateTime, types);
        notification = notificationRepository.findById(notification.getId()).orElseThrow();
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.ACTIVE);

        // Case: `startDateTime > now` -> should be 'PENDING'
        notification.getSchedule().setEndDateTime(null);
        notification.getSchedule().setStartDateTime(dateTime.plusDays(4));
        notificationSchedulingRepository.save(notification);
        flushAndClear();
        notificationSchedulingRepository.changeNotificationsStatus(dateTime, types);
        notification = notificationRepository.findById(notification.getId()).orElseThrow();
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.PENDING);

        // Case: `endDateTime < now` -> should be 'EXPIRED'
        notification.getSchedule().setStartDateTime(dateTime);
        notification.getSchedule().setEndDateTime(dateTime.minusMonths(2));
        notificationSchedulingRepository.save(notification);
        flushAndClear();
        notificationSchedulingRepository.changeNotificationsStatus(dateTime, types);
        notification = notificationRepository.findById(notification.getId()).orElseThrow();
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.EXPIRED);

        // Case: `startDateTime <= now` and `timesFired = 1`, `endDateTime is null` -> should be 'EXPIRED'
        notification.getSchedule().setEndDateTime(null);
        notification.getSchedule().setStartDateTime(dateTime);
        notification.setTimesFired(1L);
        notificationSchedulingRepository.save(notification);
        flushAndClear();
        notificationSchedulingRepository.changeNotificationsStatus(dateTime, types);
        notification = notificationRepository.findById(notification.getId()).orElseThrow();
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.EXPIRED);

        notificationSchedulingRepository.deleteAll();
    }

    private void flushAndClear(){
        entityManager.flush();
        entityManager.clear();
    }
}
