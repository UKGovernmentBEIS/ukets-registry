package gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.compliance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.compliance.service.ComplianceService;
import gov.uk.ets.registry.api.compliance.web.model.ComplianceOverviewDTO;
import gov.uk.ets.registry.api.notification.userinitiated.domain.Notification;
import gov.uk.ets.registry.api.notification.userinitiated.domain.NotificationDefinition;
import gov.uk.ets.registry.api.notification.userinitiated.domain.SelectionCriteria;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.AircraftOperatorParameters;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.BaseNotificationParameters;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.InstallationParameters;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.NotificationParameterHolder;
import gov.uk.ets.registry.api.notification.userinitiated.repository.NotificationSchedulingRepository;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.domain.UserWorkContact;
import gov.uk.ets.registry.api.user.domain.UserWorkContactRepository;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NotificationParameterHolderRetrieverTest {

    private static final String FIRST_NAME_1 = "first1";
    private static final String LAST_NAME_1 = "last1";
    private static final String AH_FIRST_NAME_1 = "ah_first1";
    private static final String AH_LAST_NAME_1 = "ah_last1=";
    private static final Long ACCOUNT_ID_1 = 1L;
    private static final Long ACCOUNT_ID_2 = 2L;
    private static final String URID_1 = "UK1234";
    private static final String FIRST_NAME_2 = "first2";
    private static final String LAST_NAME_2 = "last2";
    private static final String URID_2 = "UK5678";
    private static final String EMAIL_1 = "a@a.com";
    private static final String EMAIL_2 = "b@b.com";
    private static final String PERMIT_1 = "perm1";
    private static final String PERMIT_2 = "perm2";
    private static final String INSTALLATION_1 = "insta1";
    private static final String INSTALLATION_2 = "insta2";
    private static final Long OPERATOR_1 = 3L;
    private static final String PLAN_1 = "plan1";
    private static final Long OPERATOR_2 = 4L;
    private static final String PLAN_2 = "plan2";

    @Mock
    private NotificationSchedulingRepository schedulingRepository;
    @Mock
    private UserWorkContactRepository userWorkContactRepository;
    @Mock
    private ComplianceService complianceService;

    @InjectMocks
    private NotificationParameterRetriever cut;

    Notification n1;

    @BeforeEach
    private void setUp() {
        n1 = new Notification();
        NotificationDefinition def1 = new NotificationDefinition();
        def1.setSelectionCriteria(SelectionCriteria.builder()
            .accountStatuses(List.of(AccountStatus.OPEN))
            .accountTypes(List.of(AccountType.OPERATOR_HOLDING_ACCOUNT.getLabel()))
            .accountAccessStates(List.of(AccountAccessState.ACTIVE))
            .complianceStatuses(List.of(ComplianceStatus.A))
            .userStatuses(List.of(UserStatus.ENROLLED))
            .build());
        n1.setDefinition(def1);

        SelectionCriteria sc = def1.getSelectionCriteria();
        when(schedulingRepository.getBasicNotificationParameters(
            sc.getAccountTypes(),
            sc.getAccountStatuses(),
            sc.getUserStatuses(),
            sc.getAccountAccessStates(),
            sc.getComplianceStatuses()
        )).thenReturn(List.of(
            BaseNotificationParameters.builder()
                .firstName(FIRST_NAME_1)
                .lastName(LAST_NAME_1)
                .accountHolderFirstName(AH_FIRST_NAME_1)
                .accountHolderLastName(AH_LAST_NAME_1)
                .currentDate(new Date())
                .accountId(ACCOUNT_ID_1)
                .accountIdentifier(ACCOUNT_ID_1)
                .urid(URID_1)
                .build(),
            BaseNotificationParameters.builder()
                .firstName(FIRST_NAME_2)
                .lastName(LAST_NAME_2)
                .accountHolderFirstName(AH_FIRST_NAME_1)
                .accountHolderLastName(AH_LAST_NAME_1)
                .currentDate(new Date())
                .accountId(ACCOUNT_ID_2)
                .accountIdentifier(ACCOUNT_ID_2)
                .urid(URID_2)
                .build()
        ));

        UserWorkContact contact1 = new UserWorkContact();
        contact1.setUrid(URID_1);
        contact1.setEmail(EMAIL_1);

        UserWorkContact contact2 = new UserWorkContact();
        contact2.setUrid(URID_2);
        contact2.setEmail(EMAIL_2);

        when(userWorkContactRepository.fetch(Set.of(URID_1, URID_2), true)).thenReturn(List.of(
            contact1, contact2
        ));

        when(schedulingRepository.getInstallationParams(List.of(ACCOUNT_ID_1, ACCOUNT_ID_2))).thenReturn(
            List.of(
                InstallationParameters.builder()
                    .accountId(ACCOUNT_ID_2)
                    .permitId(PERMIT_2)
                    .name(INSTALLATION_2)
                    .build(),
                InstallationParameters.builder()
                    .accountId(ACCOUNT_ID_1)
                    .permitId(PERMIT_1)
                    .name(INSTALLATION_1)
                    .build()
            )
        );

        when(schedulingRepository.getAircraftOperatorParameters(List.of(ACCOUNT_ID_1, ACCOUNT_ID_2))).thenReturn(
            List.of(
                AircraftOperatorParameters.builder()
                    .accountId(ACCOUNT_ID_1)
                    .id(OPERATOR_1)
                    .monitoringPlan(PLAN_1)
                    .build(),
                AircraftOperatorParameters.builder()
                    .accountId(ACCOUNT_ID_2)
                    .id(OPERATOR_2)
                    .monitoringPlan(PLAN_2)
                    .build()
            )
        );

        ComplianceOverviewDTO complianceOverviewDTO = new ComplianceOverviewDTO();
        complianceOverviewDTO.setTotalNetSurrenders(10L);
        complianceOverviewDTO.setTotalVerifiedEmissions(2L);
        when(complianceService.getComplianceOverview(ACCOUNT_ID_1)).thenReturn(complianceOverviewDTO);
        when(complianceService.getComplianceOverview(ACCOUNT_ID_2)).thenReturn(new ComplianceOverviewDTO());
    }

    @Test
    void shouldRetrieveAllParametersForANotification() {


        List<NotificationParameterHolder> notificationParameterHolders = cut.getNotificationParameters(n1);

        assertThat(notificationParameterHolders).hasSize(2);

        assertThat(notificationParameterHolders).extracting(NotificationParameterHolder::getEmail)
            .containsOnly(EMAIL_1, EMAIL_2);

        NotificationParameterHolder recipient =
            notificationParameterHolders.stream().filter(r -> r.getEmail().equals(EMAIL_1)).findFirst().get();

        assertThat(recipient.getInstallationParameters().getAccountId()).isEqualTo(ACCOUNT_ID_1);
        assertThat(recipient.getInstallationParameters().getName()).isEqualTo(INSTALLATION_1);
        assertThat(recipient.getInstallationParameters().getPermitId()).isEqualTo(PERMIT_1);

        assertThat(recipient.getAircraftOperatorParameters().getAccountId()).isEqualTo(ACCOUNT_ID_1);
        assertThat(recipient.getAircraftOperatorParameters().getId()).isEqualTo(OPERATOR_1);
        assertThat(recipient.getAircraftOperatorParameters().getMonitoringPlan()).isEqualTo(PLAN_1);
        assertThat(recipient.getBalance()).isEqualTo(8L);
    }

    @Test
    void shouldFilterOuTForAccountsWithoutInstallationOrAircraftOperator() {

        when(schedulingRepository.getAircraftOperatorParameters(List.of(ACCOUNT_ID_1, ACCOUNT_ID_2)))
            .thenReturn(List.of());

        when(schedulingRepository.getInstallationParams(List.of(ACCOUNT_ID_1, ACCOUNT_ID_2)))
            .thenReturn(List.of(
                InstallationParameters.builder()
                    .accountId(ACCOUNT_ID_2)
                    .permitId(PERMIT_2)
                    .name(INSTALLATION_2)
                    .build()
            ));

        List<NotificationParameterHolder> notificationParameterHolders = cut.getNotificationParameters(n1);

        assertThat(notificationParameterHolders).hasSize(1);

        assertThat(notificationParameterHolders.get(0).getInstallationParameters().getPermitId()).isEqualTo(PERMIT_2);

    }
}
