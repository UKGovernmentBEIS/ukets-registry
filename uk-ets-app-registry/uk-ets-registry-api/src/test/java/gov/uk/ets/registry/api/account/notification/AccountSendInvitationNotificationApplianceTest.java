package gov.uk.ets.registry.api.account.notification;

import gov.uk.ets.registry.api.account.domain.types.AccountContactType;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDetailsDTO;
import gov.uk.ets.registry.api.account.web.model.DetailsDTO;
import gov.uk.ets.registry.api.account.web.model.OperatorDTO;
import gov.uk.ets.registry.api.account.web.model.accountcontact.AccountContactSendInvitationDTO;
import gov.uk.ets.registry.api.account.web.model.accountcontact.MetsContactDTO;
import gov.uk.ets.registry.api.account.web.model.accountcontact.RegistryContactDTO;
import gov.uk.ets.registry.api.notification.AccountSendInvitationGroupNotification;
import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountSendInvitationNotificationApplianceTest {

    @Mock
    private GroupNotificationClient groupNotificationClient;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    @Mock
    private EmitsGroupNotifications emitsGroupNotifications;

    @InjectMocks
    private AccountSendInvitationNotificationAppliance appliance;

    private static final Long ACCOUNT_IDENTIFIER = 1001L;


    @Test
    void apply_shouldEmitNotificationsForMetsAndRegistryContacts() throws NoSuchMethodException {

        MetsContactDTO mets1 = new MetsContactDTO();
        mets1.setEmail("mets1@test.com");
        mets1.setFullName("METS One");
        mets1.setInvitationStatus(true);

        RegistryContactDTO registry1 = new RegistryContactDTO();
        registry1.setEmail("reg1@test.com");
        registry1.setFullName("Registry One");
        registry1.setInvitationStatus(true);

        AccountContactSendInvitationDTO sendInvitationDTO = new AccountContactSendInvitationDTO();
        sendInvitationDTO.setMetsContacts(Set.of(mets1));
        sendInvitationDTO.setRegistryContacts(Set.of(registry1));



        when(emitsGroupNotifications.value())
                .thenReturn(new GroupNotificationType[]{
                        GroupNotificationType.SEND_INVITATION_TO_CONTACTS
                });

        // Mock accountDTO returned by accountService
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountClaimCode("ABC123");

        DetailsDTO holerDetailsDTO = new DetailsDTO();
        holerDetailsDTO.setName("AH name");
        AccountHolderDTO holderDTO = new AccountHolderDTO();
        holderDTO.setType(AccountHolderType.ORGANISATION);
        holderDTO.setDetails(holerDetailsDTO);
        accountDTO.setAccountHolder(holderDTO);

        AccountDetailsDTO detailsDTO = new AccountDetailsDTO();
        detailsDTO.setAccountNumber("UK-123-456");
        detailsDTO.setAccountType("Operator Holding Account");
        accountDTO.setAccountDetails(detailsDTO);

        OperatorDTO operatorDTO = new OperatorDTO();
        operatorDTO.setEmitterId("EM-555");
        operatorDTO.setAutoSendClaimInvitationEnabled(true);
        accountDTO.setOperator(operatorDTO);
        accountDTO.setMetsContacts(List.of(mets1));
        accountDTO.setRegistryContacts(List.of(registry1));

        Method method = TestService.class.getMethod(
                "sendInvitation",
                Long.class,
                AccountDTO.class,
                AccountContactSendInvitationDTO.class,
                boolean.class
        );


        when(methodSignature.getMethod()).thenReturn(method);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(joinPoint.getArgs()).thenReturn(
                new Object[]{ACCOUNT_IDENTIFIER, accountDTO, sendInvitationDTO, true}
        );

        // ----- Act -----
        appliance.apply(joinPoint, emitsGroupNotifications, "returned value");

        // ----- Assert -----
        ArgumentCaptor<AccountSendInvitationGroupNotification> captor =
                ArgumentCaptor.forClass(AccountSendInvitationGroupNotification.class);

        verify(groupNotificationClient, times(2))
                .emitGroupNotification(captor.capture());

        List<AccountSendInvitationGroupNotification> accountSendInvitationGroupNotifications = captor.getAllValues();

        // Validate emitted notifications
        AccountSendInvitationGroupNotification metsNotification = accountSendInvitationGroupNotifications.get(0);
        AccountSendInvitationGroupNotification registryNotification = accountSendInvitationGroupNotifications.get(1);

        // METS contact
        assertTrue(metsNotification.getRecipients().contains("mets1@test.com"));
        assertEquals("METS One", metsNotification.getContactFullName());
        assertEquals("ABC123", metsNotification.getAccountClaimCode());
        assertEquals("EM-555", metsNotification.getEmitterId());
        assertEquals("AH name", metsNotification.getOperatorName());
        assertEquals("UK-123-456", metsNotification.getAccountNumber());
        assertEquals("Operator Holding Account", metsNotification.getAccountType());
        assertTrue(metsNotification.getIsMetsContact());
        assertEquals(GroupNotificationType.SEND_INVITATION_TO_CONTACTS,  metsNotification.getType());

        // Registry contact
        assertTrue(registryNotification.getRecipients().contains("reg1@test.com"));
        assertEquals("Registry One", registryNotification.getContactFullName());
        assertFalse(registryNotification.getIsMetsContact());
    }

    @Test
    void apply_shouldNotEmitNotifications_auto_send_disabled() throws NoSuchMethodException {

        MetsContactDTO mets1 = new MetsContactDTO();
        mets1.setEmail("mets1@test.com");
        mets1.setFullName("METS One");

        RegistryContactDTO registry1 = new RegistryContactDTO();
        registry1.setEmail("reg1@test.com");
        registry1.setFullName("Registry One");

        AccountContactSendInvitationDTO sendInvitationDTO = new AccountContactSendInvitationDTO();
        sendInvitationDTO.setMetsContacts(Set.of(mets1));
        sendInvitationDTO.setRegistryContacts(Set.of(registry1));



        when(emitsGroupNotifications.value())
                .thenReturn(new GroupNotificationType[]{
                        GroupNotificationType.SEND_INVITATION_TO_CONTACTS
                });

        // Mock accountDTO returned by accountService
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountClaimCode("ABC123");

        DetailsDTO holerDetailsDTO = new DetailsDTO();
        holerDetailsDTO.setName("AH name");
        AccountHolderDTO holderDTO = new AccountHolderDTO();
        holderDTO.setType(AccountHolderType.ORGANISATION);
        holderDTO.setDetails(holerDetailsDTO);
        accountDTO.setAccountHolder(holderDTO);

        AccountDetailsDTO detailsDTO = new AccountDetailsDTO();
        detailsDTO.setAccountNumber("UK-123-456");
        detailsDTO.setAccountType("Operator Holding Account");
        accountDTO.setAccountDetails(detailsDTO);

        OperatorDTO operatorDTO = new OperatorDTO();
        operatorDTO.setEmitterId("EM-555");
        operatorDTO.setAutoSendClaimInvitationEnabled(false);
        accountDTO.setOperator(operatorDTO);

        Method method = TestService.class.getMethod(
                "sendInvitation",
                Long.class,
                AccountDTO.class,
                AccountContactSendInvitationDTO.class,
                boolean.class
        );


        when(methodSignature.getMethod()).thenReturn(method);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(joinPoint.getArgs()).thenReturn(
                new Object[]{ACCOUNT_IDENTIFIER, accountDTO, sendInvitationDTO, false}
        );

        appliance.apply(joinPoint, emitsGroupNotifications, "returned value");

        verifyNoInteractions(groupNotificationClient);
    }
    
    @Test
    void apply_shouldEmitNotificationsForEligibleContactsOnly() throws NoSuchMethodException {
        MetsContactDTO mets1 = new MetsContactDTO();
        mets1.setEmail("mets1@test.com");
        mets1.setFullName("METS One");
        mets1.setInvitationStatus(true);

        RegistryContactDTO registry1 = new RegistryContactDTO();
        registry1.setEmail("reg1@test.com");
        registry1.setFullName("Registry One");
        registry1.setInvitationStatus(false);

        AccountContactSendInvitationDTO sendInvitationDTO = new AccountContactSendInvitationDTO();
        sendInvitationDTO.setMetsContacts(Set.of(mets1));
        sendInvitationDTO.setRegistryContacts(Set.of(registry1));



        when(emitsGroupNotifications.value())
                .thenReturn(new GroupNotificationType[]{
                        GroupNotificationType.SEND_INVITATION_TO_CONTACTS
                });

        // Mock accountDTO returned by accountService
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountClaimCode("ABC123");

        DetailsDTO holerDetailsDTO = new DetailsDTO();
        holerDetailsDTO.setName("AH name");
        AccountHolderDTO holderDTO = new AccountHolderDTO();
        holderDTO.setType(AccountHolderType.ORGANISATION);
        holderDTO.setDetails(holerDetailsDTO);
        accountDTO.setAccountHolder(holderDTO);

        AccountDetailsDTO detailsDTO = new AccountDetailsDTO();
        detailsDTO.setAccountNumber("UK-123-456");
        detailsDTO.setAccountType("Operator Holding Account");
        accountDTO.setAccountDetails(detailsDTO);

        OperatorDTO operatorDTO = new OperatorDTO();
        operatorDTO.setEmitterId("EM-555");
        
        accountDTO.setOperator(operatorDTO);
        accountDTO.setMetsContacts(List.of(mets1));
        accountDTO.setRegistryContacts(List.of(registry1));

        Method method = TestService.class.getMethod(
                "sendInvitation",
                Long.class,
                AccountDTO.class,
                AccountContactSendInvitationDTO.class,
                boolean.class
        );


        when(methodSignature.getMethod()).thenReturn(method);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(joinPoint.getArgs()).thenReturn(
                new Object[]{ACCOUNT_IDENTIFIER, accountDTO, sendInvitationDTO, true}
        );

        // ----- Act -----
        appliance.apply(joinPoint, emitsGroupNotifications, "returned value");

        // ----- Assert -----
        ArgumentCaptor<AccountSendInvitationGroupNotification> captor =
                ArgumentCaptor.forClass(AccountSendInvitationGroupNotification.class);

        verify(groupNotificationClient, times(1))
                .emitGroupNotification(captor.capture());

        List<AccountSendInvitationGroupNotification> accountSendInvitationGroupNotifications = captor.getAllValues();

        // Validate emitted notifications
        AccountSendInvitationGroupNotification metsNotification = accountSendInvitationGroupNotifications.get(0);

        // METS contact
        assertTrue(metsNotification.getRecipients().contains("mets1@test.com"));
        assertEquals("METS One", metsNotification.getContactFullName());
        assertEquals("ABC123", metsNotification.getAccountClaimCode());
        assertEquals("EM-555", metsNotification.getEmitterId());
        assertEquals("AH name", metsNotification.getOperatorName());
        assertEquals("UK-123-456", metsNotification.getAccountNumber());
        assertEquals("Operator Holding Account", metsNotification.getAccountType());
        assertTrue(metsNotification.getIsMetsContact());
        assertEquals(GroupNotificationType.SEND_INVITATION_TO_CONTACTS,  metsNotification.getType());
    }

    @Test
    void apply_shouldFailIfInvitationPayloadIsMissing() throws Exception {

        when(emitsGroupNotifications.value())
                .thenReturn(new GroupNotificationType[]{
                        GroupNotificationType.SEND_INVITATION_TO_CONTACTS
                });

        Method method = TestServiceWithoutPayload.class.getMethod(
                "sendInvitation",
                Long.class,
                AccountDTO.class
        );

        when(methodSignature.getMethod()).thenReturn(method);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(joinPoint.getArgs()).thenReturn(
                new Object[]{ACCOUNT_IDENTIFIER, new AccountDTO()}
        );

        assertThrows(
                MissingInvitationNotificationDataException.class,
                () -> appliance.apply(joinPoint, emitsGroupNotifications, "returned")
        );

        verifyNoInteractions(groupNotificationClient);
    }


    @Test
    void apply_shouldEmitNotificationForAlternativeContactWhenSameEmailAsDisabledPrimary()
            throws NoSuchMethodException {

        final String sharedEmail = "shared@test.com";

        RegistryContactDTO primaryContact = new RegistryContactDTO();
        primaryContact.setEmail(sharedEmail);
        primaryContact.setFullName("Primary Contact");
        primaryContact.setContactType(AccountContactType.PRIMARY);
        primaryContact.setInvitationStatus(false);

        RegistryContactDTO alternativeContact = new RegistryContactDTO();
        alternativeContact.setEmail(sharedEmail);
        alternativeContact.setFullName("Alternative Contact");
        alternativeContact.setContactType(AccountContactType.ALTERNATIVE);
        alternativeContact.setInvitationStatus(true);

        AccountContactSendInvitationDTO payload =
                new AccountContactSendInvitationDTO();
        payload.setMetsContacts(Set.of());
        payload.setRegistryContacts(Set.of(alternativeContact));

        when(emitsGroupNotifications.value())
                .thenReturn(new GroupNotificationType[]{
                        GroupNotificationType.SEND_INVITATION_TO_CONTACTS
                });

        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountClaimCode("ABC123");
        accountDTO.setRegistryContacts(List.of(primaryContact, alternativeContact));

        DetailsDTO holderDetailsDTO = new DetailsDTO();
        holderDetailsDTO.setName("AH name");

        AccountHolderDTO holderDTO = new AccountHolderDTO();
        holderDTO.setType(AccountHolderType.ORGANISATION);
        holderDTO.setDetails(holderDetailsDTO);
        accountDTO.setAccountHolder(holderDTO);

        AccountDetailsDTO detailsDTO = new AccountDetailsDTO();
        detailsDTO.setAccountNumber("UK-123-456");
        detailsDTO.setAccountType("Operator Holding Account");
        accountDTO.setAccountDetails(detailsDTO);

        OperatorDTO operatorDTO = new OperatorDTO();
        operatorDTO.setEmitterId("EM-555");
        accountDTO.setOperator(operatorDTO);

        Method method = TestService.class.getMethod(
                "sendInvitation",
                Long.class,
                AccountDTO.class,
                AccountContactSendInvitationDTO.class,
                boolean.class
        );

        when(methodSignature.getMethod()).thenReturn(method);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(joinPoint.getArgs()).thenReturn(
                new Object[]{ACCOUNT_IDENTIFIER, accountDTO, payload, true}
        );

        // Act
        appliance.apply(joinPoint, emitsGroupNotifications, "returned value");

        // Assert
        ArgumentCaptor<AccountSendInvitationGroupNotification> captor =
                ArgumentCaptor.forClass(AccountSendInvitationGroupNotification.class);

        verify(groupNotificationClient, times(1))
                .emitGroupNotification(captor.capture());

        AccountSendInvitationGroupNotification notification =
                captor.getValue();

        assertTrue(notification.getRecipients().contains(sharedEmail));
        assertEquals("Alternative Contact", notification.getContactFullName());
        assertFalse(notification.getIsMetsContact());
        assertEquals(
                GroupNotificationType.SEND_INVITATION_TO_CONTACTS,
                notification.getType()
        );
    }


    static class TestService {

        @EmitsGroupNotifications(GroupNotificationType.SEND_INVITATION_TO_CONTACTS)
        public void sendInvitation(
                Long accountIdentifier,
                @InvitationAccount AccountDTO accountDTO,
                @InvitationPayload AccountContactSendInvitationDTO payload,
                boolean isManual
        ) {

        }
    }

    static class TestServiceWithoutPayload {

        @EmitsGroupNotifications(GroupNotificationType.SEND_INVITATION_TO_CONTACTS)
        public void sendInvitation(
                Long accountIdentifier,
                @InvitationAccount AccountDTO accountDTO
        ) {

        }
    }
}