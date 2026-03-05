package gov.uk.ets.registry.api.user.migration;

import gov.uk.ets.registry.api.common.reports.ReportRequestAddRemoveRoleService;
import gov.uk.ets.registry.api.migration.domain.MigratorHistory;
import gov.uk.ets.registry.api.migration.domain.MigratorHistoryRepository;
import gov.uk.ets.registry.api.migration.domain.MigratorName;
import gov.uk.ets.registry.api.user.UserRoleDetails;
import gov.uk.ets.registry.api.user.domain.UserRole;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ARReportsUserRoleMigratorTest {

    private static final String IAM_ID_1 = "iam-1";
    private static final String IAM_ID_2 = "iam-2";

    @Mock
    ReportRequestAddRemoveRoleService reportRequestAddRemoveRoleService;

    @Mock
    UserRepository userRepository;

    @Mock
    MigratorHistoryRepository migratorHistoryRepository;

    @InjectMocks
    ARReportsUserRoleMigrator migrator;

    @Test
    void shouldAddReportsRoleAndPersistMigrationHistory() {

        UserRoleDetails user1 = mock(UserRoleDetails.class);
        when(user1.getIamIdentifier()).thenReturn(IAM_ID_1);

        UserRoleDetails user2 = mock(UserRoleDetails.class);
        when(user2.getIamIdentifier()).thenReturn(IAM_ID_2);

        when(userRepository.findUsersByStatusAndRoleExcludingReportsUser(
                UserStatus.ENROLLED,
                List.of(UserRole.AUTHORISED_REPRESENTATIVE.getKeycloakLiteral(),
                        UserRole.SENIOR_REGISTRY_ADMINISTRATOR.getKeycloakLiteral(),
                        UserRole.JUNIOR_REGISTRY_ADMINISTRATOR.getKeycloakLiteral(),
                        UserRole.READONLY_ADMINISTRATOR.getKeycloakLiteral())
        )).thenReturn(List.of(user1, user2));

        when(migratorHistoryRepository.findByMigratorName(MigratorName.AR_REPORTS_USER_ROLE_MIGRATOR))
                .thenReturn(List.of());

        migrator.migrate();

        verify(reportRequestAddRemoveRoleService)
                .requestReportsApiAddRole(IAM_ID_1);
        verify(reportRequestAddRemoveRoleService)
                .requestReportsApiAddRole(IAM_ID_2);
    }

    @Test
    void shouldSkipMigrationIfAlreadyExecuted() {

        when(migratorHistoryRepository.findByMigratorName(MigratorName.AR_REPORTS_USER_ROLE_MIGRATOR))
                .thenReturn(List.of(new MigratorHistory()));

        migrator.migrate();

        verifyNoInteractions(userRepository);
        verifyNoInteractions(reportRequestAddRemoveRoleService);

    }
}
