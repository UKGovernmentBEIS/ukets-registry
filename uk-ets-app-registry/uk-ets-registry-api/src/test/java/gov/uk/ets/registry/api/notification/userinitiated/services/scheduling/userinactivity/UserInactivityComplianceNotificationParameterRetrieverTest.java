package gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.userinactivity;

import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.compliance.service.ComplianceService;
import gov.uk.ets.registry.api.notification.userinitiated.domain.Notification;
import gov.uk.ets.registry.api.notification.userinitiated.domain.NotificationDefinition;
import gov.uk.ets.registry.api.notification.userinitiated.domain.SelectionCriteria;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.BaseNotificationParameters;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.NotificationParameterHolder;
import gov.uk.ets.registry.api.notification.userinitiated.repository.NotificationDefinitionRepository;
import gov.uk.ets.registry.api.notification.userinitiated.repository.NotificationSchedulingRepository;
import gov.uk.ets.registry.api.notification.userinitiated.services.UserInitiatedNotificationService;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.domain.UserWorkContact;
import gov.uk.ets.registry.api.user.domain.UserWorkContactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserInactivityComplianceNotificationParameterRetrieverTest {
    @Autowired
    private NotificationDefinitionRepository definitionRepository;

    @Mock
    private UserInitiatedNotificationService userInitiatedNotificationService;
    @Mock
    private NotificationSchedulingRepository schedulingRepository;
    @Mock
    private UserWorkContactRepository userWorkContactRepository;
    @Mock
    private ComplianceService complianceService;

    @InjectMocks
    private UserInactivityNotificationParameterRetriever notificationParameterReceiver;

    Notification notificationEntity;
    NotificationDefinition inactiveUserDefinition;    
    List<UserWorkContact> testUserContacts;
    List<String> testUrIds;
    List<BaseNotificationParameters> testBaseNotificationParameters;
    

    @BeforeEach
    public void setUp() {
        testUserContacts = new ArrayList<>();
        testUrIds = new ArrayList<>();
        testBaseNotificationParameters = new ArrayList<>();

        notificationEntity = new Notification();
        NotificationDefinition def1 = new NotificationDefinition();
        def1.setSelectionCriteria(SelectionCriteria.builder()
                .accountStatuses(List.of(AccountStatus.OPEN,AccountStatus.CLOSURE_PENDING,AccountStatus.ALL_TRANSACTIONS_RESTRICTED,AccountStatus.SOME_TRANSACTIONS_RESTRICTED))
                .accountTypes(List.of(AccountType.OPERATOR_HOLDING_ACCOUNT.getLabel(),
                                      AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT.getLabel(),
                                      AccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT.getLabel(),
                                      AccountType.TRADING_ACCOUNT.getLabel()))
                .accountAccessStates(List.of(AccountAccessState.ACTIVE))
                .userStatuses(List.of(UserStatus.ENROLLED,UserStatus.VALIDATED,UserStatus.DEACTIVATION_PENDING))
                .build());
        notificationEntity.setDefinition(def1);


        for(Integer x=0;x<500;x++){
            var nextUser = new UserWorkContact();
            nextUser.setUrid("URID_"+x.toString());
            nextUser.setEmail("EMAIL_"+x.toString());
            nextUser.setFirstName("FIRST_NAME_"+x.toString());
            nextUser.setLastName("LAST_NAME_"+x.toString());

            var nextParam = BaseNotificationParameters.builder()
                .currentDate(new Date())
                .firstName("FIRST_NAME_"+x.toString())
                .lastName("LAST_NAME_"+x.toString())
                .urid("URID_"+x.toString())
                .accountId(Long.parseLong(x.toString()))
                .accountHolderFirstName("AH_FIRST_NAME_"+x.toString())
                .accountHolderLastName("AH_LAST_NAME_"+x.toString())
                .accountHolderName("AH_LAST_NAME_"+x.toString())
                .accountIdentifier(Long.parseLong(x.toString()))
                .build();

            testBaseNotificationParameters.add(nextParam);
            testUrIds.add("URID_"+x.toString());
            testUserContacts.add(nextUser);
        }

        Set<String> urIds = testUserContacts.stream()
                .map(UserWorkContact::getUrid)
                .collect(toSet());
        when(userWorkContactRepository.fetchUserWorkContactsInBatches(anySet())).thenAnswer((invocation) -> {
            Set<String> ids = invocation.getArgument(0);
            return testUserContacts.stream().filter(nextContact -> ids.contains(nextContact.getUrid())).toList();
        });

        when(schedulingRepository.getUserInactivityNotificationParameters(testUrIds,
            List.of(AccountStatus.OPEN,AccountStatus.CLOSURE_PENDING,AccountStatus.ALL_TRANSACTIONS_RESTRICTED,AccountStatus.SOME_TRANSACTIONS_RESTRICTED),
            List.of(UserStatus.ENROLLED,UserStatus.VALIDATED,UserStatus.DEACTIVATION_PENDING),
            List.of(AccountAccessState.ACTIVE))).thenReturn(
            testBaseNotificationParameters
        );

        when(userInitiatedNotificationService.getInactiveUsers()).thenReturn(
                testUserContacts.stream()
                        .map(UserWorkContact::getUrid)
                        .collect(toList())
        );
    }

    @Test
    void shouldRetrieveAllParametersForUserInactivityNotification() {
        List<NotificationParameterHolder> notificationParameterHolders = notificationParameterReceiver.getNotificationParameters(notificationEntity);

        assertThat(notificationParameterHolders).hasSize(500);

        assertThat(notificationParameterHolders).extracting(NotificationParameterHolder::getEmail)
                .contains(testUserContacts.get(5).getEmail(), testUserContacts.get(245).getEmail(),testUserContacts.get(245).getEmail());

        NotificationParameterHolder recipient =
            notificationParameterHolders.stream().filter(r -> r.getEmail().equals(testUserContacts.get(68).getEmail())).findFirst().get();

        assertThat(recipient.getBaseNotificationParameters().getFirstName()).isEqualTo(testUserContacts.get(68).getFirstName());
        assertThat(recipient.getBaseNotificationParameters().getLastName()).isEqualTo(testUserContacts.get(68).getLastName());
        assertThat(recipient.getBaseNotificationParameters().getUrid()).isEqualTo(testUserContacts.get(68).getUrid());
    }
}
