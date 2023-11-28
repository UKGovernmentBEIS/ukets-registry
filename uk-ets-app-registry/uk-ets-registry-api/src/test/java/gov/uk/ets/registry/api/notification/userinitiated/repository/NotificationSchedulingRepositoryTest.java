package gov.uk.ets.registry.api.notification.userinitiated.repository;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.common.model.types.Status;
import gov.uk.ets.registry.api.common.test.PostgresJpaTest;
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
import gov.uk.ets.registry.api.user.domain.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@PostgresJpaTest
class NotificationSchedulingRepositoryTest {

    private static final String OHA_LABEL = "ETS - Operator holding account";
    public static final String PERMIT_ID = "permit-id";
    public static final long AIRCRAFT_OPERATOR_ID = 1234567L;

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private NotificationDefinitionRepository definitionRepository;

    @Autowired
    NotificationSchedulingRepository cut;

    NotificationModelTestHelper notificationModelTestHelper;
    private AccountModelTestHelper accountModelTestHelper;

    private AddAccountHolderCommand addAccountHolderCommand;
    private AddAccountCommand addAccountCommand;
    private AddUserToAccountAccessCommand addAuthorizedRepresentativeToAccountCommand;
    private AddInstallationEntityToAccountCommand addInstallationEntityToAccountCommand;

    @Autowired
    private TestEntityManager entityManager;

    LocalDateTime current = LocalDateTime.of(2021, 11, 13, 10, 0);

    Account account;


    @BeforeEach
    public void setUp() {

        notificationModelTestHelper = new NotificationModelTestHelper(entityManager, definitionRepository);
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

    }


    @Test
    void test_changeAdHocNotificationsStatus() {
        List<Notification> before = notificationRepository.findAllWithDefinitions();
        assertEquals(2, before.stream()
            .filter(n -> n.getDefinition().getType().equals(NotificationType.AD_HOC)
                && n.getStatus().equals(NotificationStatus.ACTIVE)).count());
        assertEquals(1, before.stream()
            .filter(n -> !n.getDefinition().getType().equals(NotificationType.AD_HOC)
                && n.getStatus().equals(NotificationStatus.ACTIVE)).count());
        assertEquals(2, before.stream()
            .filter(n -> n.getDefinition().getType().equals(NotificationType.AD_HOC)
                && n.getStatus().equals(NotificationStatus.EXPIRED)).count());
        assertEquals(3, before.stream()
            .filter(n -> n.getDefinition().getType().equals(NotificationType.AD_HOC)
                && n.getStatus().equals(NotificationStatus.PENDING)).count());

        cut.changeAdHocNotificationsStatus(current);

        entityManager.flush();
        entityManager.clear();
        List<Notification> after = notificationRepository.findAllWithDefinitions();

        assertEquals(3, after.stream()
            .filter(n -> n.getDefinition().getType().equals(NotificationType.AD_HOC)
                && n.getStatus().equals(NotificationStatus.ACTIVE)).count());
        assertEquals(1, after.stream()
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
            cut.getBasicNotificationParameters(List.of(OHA_LABEL), List.of(AccountStatus.OPEN),
                List.of(UserStatus.ENROLLED),
                List.of(AccountAccessState.ACTIVE), List.of(
                    ComplianceStatus.C));

        assertThat(basicNotificationParameters).hasSize(1);
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
        List<InstallationParameters> installationParameters = cut.getInstallationParams(List.of(account.getId()));

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
            cut.getAircraftOperatorParameters(List.of(account.getId()));

        assertThat(aircraftOperatorParameters).hasSize(1);
        assertThat(aircraftOperatorParameters.get(0).getId()).isEqualTo(AIRCRAFT_OPERATOR_ID);
    }

}
