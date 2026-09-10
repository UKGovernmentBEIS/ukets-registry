package gov.uk.ets.registry.api.user.migration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.common.UserDetailsUtil;
import gov.uk.ets.registry.api.migration.domain.MigratorHistory;
import gov.uk.ets.registry.api.migration.domain.MigratorHistoryRepository;
import gov.uk.ets.registry.api.migration.domain.MigratorName;
import gov.uk.ets.registry.api.user.domain.AgentType;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import jakarta.ws.rs.ClientErrorException;

@ExtendWith(MockitoExtension.class)
class UserAgentCrcMigratorTest {


    @Mock
    private UserRepository userRepository;

    @Mock
    private ServiceAccountAuthorizationService serviceAccountAuthorizationService;
    
    @Mock
    private MigratorHistoryRepository migratorHistoryRepository;

    @InjectMocks
    private UserAgentCrcMigrator migrator;

    @BeforeEach
    void setUp() {
        when(migratorHistoryRepository.findByMigratorName(MigratorName.USER_AGENT_CRC_MIGRATOR))
                .thenReturn(List.of());
    }

    @Test
    void call_whenAlreadyMigrated_shouldSkipProcessing() throws Throwable {
        when(migratorHistoryRepository.findByMigratorName(MigratorName.USER_AGENT_CRC_MIGRATOR))
                .thenReturn(List.of(new MigratorHistory()));

        migrator.call();

        verify(userRepository, never()).findAll();
        verify(migratorHistoryRepository, never()).save(any(MigratorHistory.class));
    }

    @Test
    void call_whenUserProcessedSuccessfully_shouldUpdateUserAgentNoCrcTrueAndKeycloakAndSaveHistory() throws Throwable {
        User user = mock(User.class);
        Date enrolmentDate = Date.from(LocalDate.of(2024, 1, 1).atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        when(user.getEnrolmentKeyDate()).thenReturn(enrolmentDate);
        when(user.getIamIdentifier()).thenReturn("iam-1");
        when(user.getUrid()).thenReturn("UK74323874");
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userRepository.findUsersByStatusAndActiveAccountAccessRights(UserAgentCrcMigrator.VALIDATED_OR_ENROLLED_STATUS, UserAgentCrcMigrator.INITIATE_OR_APPROVE_RIGHTS))
        .thenReturn(List.of(user));

        UserRepresentation representation = new UserRepresentation();
        representation.setAttributes(new HashMap<>());
        when(serviceAccountAuthorizationService.getUser("iam-1")).thenReturn(representation);

        migrator.call();

        LocalDateTime expectedDate = enrolmentDate.toInstant()
                .atOffset(ZoneOffset.UTC)
                .toLocalDateTime();


        verify(user).setCrc(true);
        verify(user).setCrcIssuanceDate(enrolmentDate);        
        
        verify(user).setAgent(AgentType.NO);
        verify(serviceAccountAuthorizationService).updateUserDetails(representation);
        assertEquals(List.of(AgentType.NO.name()), representation.getAttributes().get("agent"));
        assertEquals(List.of("true"), representation.getAttributes().get("crc"));
        assertEquals(List.of(expectedDate.format(UserDetailsUtil.CRC_DATE_FORMATTER)), representation.getAttributes().get("crcIssuanceDate"));
        
        ArgumentCaptor<MigratorHistory> captor = ArgumentCaptor.forClass(MigratorHistory.class);
        verify(migratorHistoryRepository).save(captor.capture());
        assertEquals(MigratorName.USER_AGENT_CRC_MIGRATOR, captor.getValue().getMigratorName());
    }

    @Test
    void call_whenUserProcessedSuccessfully_shouldUpdateUserAgentNoCrcFalseAndKeycloakAndSaveHistory() throws Throwable {
        User user = mock(User.class);
        when(user.getIamIdentifier()).thenReturn("iam-1");
        when(user.getUrid()).thenReturn("UK74323874");
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userRepository.findUsersByStatusAndActiveAccountAccessRights(UserAgentCrcMigrator.VALIDATED_OR_ENROLLED_STATUS, UserAgentCrcMigrator.INITIATE_OR_APPROVE_RIGHTS))
        .thenReturn(List.of(user));
        
        UserRepresentation representation = new UserRepresentation();
        representation.setAttributes(new HashMap<>());
        when(serviceAccountAuthorizationService.getUser("iam-1")).thenReturn(representation);

        migrator.call();

        verify(user).setCrc(false);      
        verify(user).setCrcIssuanceDate(null);
        verify(user).setAgent(AgentType.NO);
        verify(serviceAccountAuthorizationService).updateUserDetails(representation);
        assertEquals(List.of(AgentType.NO.name()), representation.getAttributes().get("agent"));
        assertEquals(List.of("false"), representation.getAttributes().get("crc"));
        assertNull(representation.getAttributes().get("crcIssuanceDate"));
        
        ArgumentCaptor<MigratorHistory> captor = ArgumentCaptor.forClass(MigratorHistory.class);
        verify(migratorHistoryRepository).save(captor.capture());
        assertEquals(MigratorName.USER_AGENT_CRC_MIGRATOR, captor.getValue().getMigratorName());
    }    
    
    @Test
    void call_whenKeycloakUserNotFound_shouldSkipUpdateButStillSaveHistory() throws Throwable {
        User user = mock(User.class);
        when(user.getUrid()).thenReturn("URID2");
        when(user.getIamIdentifier()).thenReturn("iam-2");
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(serviceAccountAuthorizationService.getUser("iam-2")).thenReturn(null);

        migrator.call();

        verify(serviceAccountAuthorizationService, never()).updateUserDetails(any());
        verify(migratorHistoryRepository).save(any(MigratorHistory.class));
    }

    @Test
    void call_whenGetKeycloakUserThrowsClientErrorException_shouldHandleGracefully() throws Throwable {
        User user = mock(User.class);
        when(user.getUrid()).thenReturn("URID3");
        when(user.getIamIdentifier()).thenReturn("iam-3");
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(serviceAccountAuthorizationService.getUser("iam-3"))
                .thenThrow(new ClientErrorException(404));

        assertDoesNotThrow(() -> migrator.call());

        verify(serviceAccountAuthorizationService, never()).updateUserDetails(any());
        verify(migratorHistoryRepository).save(any(MigratorHistory.class));
    }

    @Test
    void call_whenUpdateUserDetailsThrows_shouldHandleGracefullyAndStillSaveHistory() throws Throwable {
        User user = mock(User.class);
        when(user.getUrid()).thenReturn("URID4");
        when(user.getIamIdentifier()).thenReturn("iam-4");
        when(userRepository.findAll()).thenReturn(List.of(user));

        UserRepresentation representation = new UserRepresentation();
        representation.setAttributes(new HashMap<>());
        when(serviceAccountAuthorizationService.getUser("iam-4")).thenReturn(representation);
        doThrow(new RuntimeException("Keycloak failure"))
                .when(serviceAccountAuthorizationService).updateUserDetails(representation);

        assertDoesNotThrow(() -> migrator.call());

        verify(migratorHistoryRepository).save(any(MigratorHistory.class));
    }

    @Test
    void call_whenNoUsersFound_shouldOnlySaveHistory() throws Throwable {
        when(userRepository.findAll()).thenReturn(List.of());

        migrator.call();

        verify(serviceAccountAuthorizationService, never()).getUser(anyString());
        verify(migratorHistoryRepository).save(any(MigratorHistory.class));
    }
}
