package gov.uk.ets.registry.api.account.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.AuthorisedRepresentativeDTO;
import gov.uk.ets.registry.api.common.view.RequestDTO;
import gov.uk.ets.registry.api.notification.AccountOpeningGroupNotification;
import gov.uk.ets.registry.api.notification.AccountProposalGroupNotification;
import gov.uk.ets.registry.api.notification.AccountUpdateGroupNotification;
import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.notification.NotificationService;
import gov.uk.ets.registry.api.notification.TrustedAccountUpdateDescriptionGroupNotification;
import gov.uk.ets.registry.api.tal.web.model.TrustedAccountDTO;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.UserDTO;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.domain.UserWorkContact;
import gov.uk.ets.registry.api.user.service.UserService;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AccountNotificationApplianceTest {

    private static final Long TEST_ACCOUNT_ID = 123L;
    private static final Long TEST_REQUEST_ID = 1L;
    public static final Set<String> RECIPIENTS = Set.of("test1@email.com", "test2@email.com");
    public static final Set<String> AR_RECIPIENTS = Set.of("test3@email.com", "test4@email.com");
    private static final String TEST_USER_ID = "UK123";
    private static final String REJECTION_COMMENT = "test rejection comment";
    public static final String ACCOUNT_FULL_IDENTIFIER = "1000078";
    public static final Long ACCOUNT_IDENTIFIER = 1000077L;
    public static final String TRUSTED_ACCOUNT_DESCRIPTION = "test test";
    private static final TrustedAccountDTO TRUSTED_ACCOUNT_DTO = TrustedAccountDTO.builder()
        .accountFullIdentifier(ACCOUNT_FULL_IDENTIFIER)
        .id(TEST_ACCOUNT_ID)
        .description(TRUSTED_ACCOUNT_DESCRIPTION)
        .underSameAccountHolder(false)
        .build();

    @Mock
    GroupNotificationClient groupNotificationClient;
    @Mock
    NotificationService notificationService;
    @InjectMocks
    AccountNotificationAppliance accountNotificationAppliance;

    @Mock
    JoinPoint joinPoint;
    @Mock
    EmitsGroupNotifications notificationAnnotation;

    @Captor
    ArgumentCaptor<AccountOpeningGroupNotification> notificationCaptor;

    @Mock
    UserService userService;

    @BeforeEach()
    public void setUp() {
        when(notificationService.findEmailsOfArsByAccountIdentifier(TEST_ACCOUNT_ID, false))
            .thenReturn(RECIPIENTS);
        when(notificationService.findAccountIdentifierByRequestId(TEST_REQUEST_ID)).thenReturn(TEST_ACCOUNT_ID);
        when(notificationService.findRequestTypeById(TEST_REQUEST_ID))
            .thenReturn(RequestType.ADD_TRUSTED_ACCOUNT_REQUEST);
        when(notificationService.findTaskByRequestId(TEST_REQUEST_ID)).thenReturn(createMockTask());
        when(notificationService.getEmailAddressesForAccountOpening(TaskOutcome.APPROVED, TEST_ACCOUNT_ID, null))
            .thenReturn(RECIPIENTS);
        when(notificationService.getEmailAddressesForAccountOpening(TaskOutcome.REJECTED, null, null))
            .thenReturn(RECIPIENTS);
        when(notificationService.findEmailsOfValidatedArs(any())).thenReturn(AR_RECIPIENTS);
        when(notificationService
            .findAccountIdentifierByIdAndAccountFullIdentifier(TEST_ACCOUNT_ID, ACCOUNT_FULL_IDENTIFIER))
            .thenReturn(ACCOUNT_IDENTIFIER);
        when(notificationService.findEmailsOfArsByAccountIdentifier(ACCOUNT_IDENTIFIER, false))
            .thenReturn(RECIPIENTS);

        List<UserWorkContact> userWorkContactList = RECIPIENTS.stream()
            .map(this::createUserWorkContact)
            .collect(Collectors.toList());
        when(userService.getUserWorkContacts(any())).thenReturn(userWorkContactList);
    }


    @Test
    void shouldCreateNotificationForUpdateProposal() {
        when(notificationAnnotation.value())
            .thenReturn(new GroupNotificationType[] {GroupNotificationType.ACCOUNT_UPDATE_PROPOSAL});

        accountNotificationAppliance.apply(joinPoint, notificationAnnotation, TEST_REQUEST_ID);

        AccountUpdateGroupNotification notification = AccountUpdateGroupNotification.builder()
            .type(GroupNotificationType.ACCOUNT_UPDATE_PROPOSAL)
            .accountIdentifier(String.valueOf(TEST_ACCOUNT_ID))
            .requestType(RequestType.ADD_TRUSTED_ACCOUNT_REQUEST)
            .requestId(String.valueOf(TEST_REQUEST_ID))
            .recipients(RECIPIENTS)
            .build();

        verify(groupNotificationClient, Mockito.times(1)).emitGroupNotification(notification);
    }

    @Test
    void shouldCreateNotificationForOutcome() {
        when(notificationAnnotation.value())
            .thenReturn(new GroupNotificationType[] {GroupNotificationType.TASK_COMPLETE_OUTCOME});
        when(joinPoint.getArgs()).thenReturn(new Object[] {TEST_REQUEST_ID, TaskOutcome.APPROVED});

        accountNotificationAppliance.apply(joinPoint, notificationAnnotation, TEST_REQUEST_ID);

        AccountUpdateGroupNotification notification = AccountUpdateGroupNotification.builder()
            .type(GroupNotificationType.TASK_COMPLETE_OUTCOME)
            .accountIdentifier(String.valueOf(TEST_ACCOUNT_ID))
            .requestType(RequestType.ADD_TRUSTED_ACCOUNT_REQUEST)
            .requestId(String.valueOf(TEST_REQUEST_ID))
            .recipients(RECIPIENTS)
            .taskOutcome(TaskOutcome.APPROVED)
            .build();

        verify(groupNotificationClient, Mockito.times(1)).emitGroupNotification(notification);
    }

    @Test
    void shouldCreateTwoNotificationsForAccountOpeningAndArs() {
        when(notificationAnnotation.value()).thenReturn(
            new GroupNotificationType[] {GroupNotificationType.ACCOUNT_OPENING_FINALISATION});
        when(joinPoint.getArgs()).thenReturn(new Object[] {TEST_REQUEST_ID, TaskOutcome.APPROVED, null});

        accountNotificationAppliance.apply(joinPoint, notificationAnnotation,
            TaskCompleteResponse.builder().requestIdentifier(TEST_REQUEST_ID).build());

        AccountOpeningGroupNotification accountOpeningNotification = AccountOpeningGroupNotification.builder()
            .type(GroupNotificationType.ACCOUNT_OPENING_FINALISATION)
            .requestId(String.valueOf(TEST_REQUEST_ID))
            .recipients(RECIPIENTS)
            .taskOutcome(TaskOutcome.APPROVED)
            .build();

        verify(groupNotificationClient, Mockito.times(1)).emitGroupNotification(notificationCaptor.capture());
    }

    @Test
    void shouldCreateNotificationForRejectedAccountOpening() {
        when(notificationAnnotation.value())
            .thenReturn(new GroupNotificationType[] {GroupNotificationType.ACCOUNT_OPENING_FINALISATION});
        when(joinPoint.getArgs()).thenReturn(new Object[] {TEST_REQUEST_ID, TaskOutcome.REJECTED, REJECTION_COMMENT});

        accountNotificationAppliance.apply(joinPoint, notificationAnnotation,
            TaskCompleteResponse.builder().requestIdentifier(TEST_REQUEST_ID).build());

        AccountOpeningGroupNotification notification = AccountOpeningGroupNotification.builder()
            .type(GroupNotificationType.ACCOUNT_OPENING_FINALISATION)
            .requestId(String.valueOf(TEST_REQUEST_ID))
            .recipients(RECIPIENTS)
            .taskOutcome(TaskOutcome.REJECTED)
            .rejectionComment(REJECTION_COMMENT)
            .build();


        verify(groupNotificationClient, Mockito.times(1)).emitGroupNotification(notification);
    }

    @Test
    void shouldCreateNotificationForUpdatingDescriptionOfTrustedAccount() {
        when(notificationAnnotation.value())
            .thenReturn(new GroupNotificationType[] {GroupNotificationType.TRUSTED_ACCOUNT_UPDATE_DESCRIPTION});
        when(joinPoint.getArgs()).thenReturn(new Object[] {TRUSTED_ACCOUNT_DTO});

        accountNotificationAppliance.apply(joinPoint, notificationAnnotation, TRUSTED_ACCOUNT_DTO);

        TrustedAccountUpdateDescriptionGroupNotification notification =
            TrustedAccountUpdateDescriptionGroupNotification.builder()
                .type(GroupNotificationType.TRUSTED_ACCOUNT_UPDATE_DESCRIPTION)
                .accountFullIdentifier(ACCOUNT_FULL_IDENTIFIER)
                .accountIdentifier(String.valueOf(ACCOUNT_IDENTIFIER))
                .description(TRUSTED_ACCOUNT_DESCRIPTION)
                .recipients(RECIPIENTS)
                .build();

        verify(groupNotificationClient, Mockito.times(1)).emitGroupNotification(notification);
    }

    @Test
    void shouldCreateNotificationForProposalIfUserIsEnrolled() {

        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setRequestId(TEST_REQUEST_ID);

        AccountDTO accountDTO = new AccountDTO();
        List<AuthorisedRepresentativeDTO> representatives = new ArrayList<>();
        AuthorisedRepresentativeDTO ar = new AuthorisedRepresentativeDTO();
        ar.setUrid("urid");
        representatives.add(ar);
        accountDTO.setAuthorisedRepresentatives(representatives);

        when(notificationAnnotation.value())
            .thenReturn(new GroupNotificationType[] {GroupNotificationType.ACCOUNT_PROPOSAL});

        when(joinPoint.getArgs()).thenReturn(new Object[] {accountDTO});

        UserDTO user = new UserDTO();
        user.setStatus(UserStatus.ENROLLED);
        when(userService.getUser(any())).thenReturn(user);

        accountNotificationAppliance.apply(joinPoint, notificationAnnotation, requestDTO);

        AccountProposalGroupNotification notification = AccountProposalGroupNotification.builder()
            .type(GroupNotificationType.ACCOUNT_PROPOSAL)
            .requestId(String.valueOf(TEST_REQUEST_ID))
            .recipients(RECIPIENTS)
            .build();

        verify(groupNotificationClient, Mockito.times(1)).emitGroupNotification(notification);
    }

    @Test
    void shouldNotCreateNotificationForProposalIfUserIsSuspended() {

        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setRequestId(TEST_REQUEST_ID);

        AccountDTO accountDTO = new AccountDTO();
        List<AuthorisedRepresentativeDTO> representatives = new ArrayList<>();
        AuthorisedRepresentativeDTO ar = new AuthorisedRepresentativeDTO();
        ar.setUrid("urid");
        representatives.add(ar);
        accountDTO.setAuthorisedRepresentatives(representatives);

        when(notificationAnnotation.value())
            .thenReturn(new GroupNotificationType[] {GroupNotificationType.ACCOUNT_PROPOSAL});

        when(joinPoint.getArgs()).thenReturn(new Object[] {accountDTO});

        UserDTO user = new UserDTO();
        user.setStatus(UserStatus.SUSPENDED);
        when(userService.getUser(any())).thenReturn(user);

        accountNotificationAppliance.apply(joinPoint, notificationAnnotation, requestDTO);

        AccountProposalGroupNotification notification = AccountProposalGroupNotification.builder()
            .type(GroupNotificationType.ACCOUNT_PROPOSAL)
            .requestId(String.valueOf(TEST_REQUEST_ID))
            .recipients(RECIPIENTS)
            .build();

        verify(groupNotificationClient, Mockito.times(0)).emitGroupNotification(notification);
    }

    private Task createMockTask() {
        Task task = new Task();
        Account account = new Account();
        account.setIdentifier(TEST_ACCOUNT_ID);
        task.setAccount(account);
        User user = new User();
        user.setUrid(TEST_USER_ID);
        task.setInitiatedBy(user);
        return task;
    }

    private UserWorkContact createUserWorkContact(String email) {
        UserWorkContact userWorkContact = new UserWorkContact();
        userWorkContact.setEmail(email);
        return userWorkContact;
    }
}
