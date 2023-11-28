package gov.uk.ets.registry.api.authz.ruleengine.features.user.profile;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputStore;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputType;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.user.admin.web.model.UserStatusChangeDTO;
import gov.uk.ets.registry.api.user.domain.UserStatus;

@ExtendWith(MockitoExtension.class)
class StatusChangeSecurityStoreSliceLoaderTest {
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private RuleInputStore ruleInputStore;

    private BusinessSecurityStore businessSecurityStore = new BusinessSecurityStore();

    private StatusChangeSecurityStoreSliceLoader loader;

    @BeforeEach
    void setUp() {
        loader = new StatusChangeSecurityStoreSliceLoader(taskRepository);
        loader.setRuleInputStore(ruleInputStore);
        loader.setBusinessSecurityStore(businessSecurityStore);
    }

    @Test
    @DisplayName("If the suspension has been raised by the system, the user is not allowed to restore the User status.")
    void loadUserIsSuspendedByTheSystem() {
        //given
        UserStatusChangeDTO dto = new UserStatusChangeDTO();
        dto.setUrid("UK198859311111");
        dto.setUserStatus(UserStatus.UNENROLLED);
        dto.setComment("reason to unsuspend user");

        given(ruleInputStore.containsKey(RuleInputType.NEW_STATUS)).willReturn(true);
        given(ruleInputStore.get(RuleInputType.NEW_STATUS)).willReturn(dto);

        Task task = new Task();
        task.setRequestId(1L);
        task.setType(RequestType.LOST_TOKEN);
        given(taskRepository.findPendingTasksByTypesAndUser(RequestType.getTasksCausingUserSuspension(), dto.getUrid())).willReturn(List.of(task));

        // when
        loader.load();
        // then
        StatusChangeSecurityStoreSlice slice = businessSecurityStore.getStatusChangeSecurityStoreSlice();
        assertTrue(slice.isUserSuspendedByTheSystem());
    }

    @Test
    @DisplayName("If the suspension has been raised by the user (RA), the user is allowed to restore the User status.")
    void loadUserIsNotSuspendedByTheSystem() {
        //given
        UserStatusChangeDTO dto = new UserStatusChangeDTO();
        dto.setUrid("UK198859311111");
        dto.setUserStatus(UserStatus.UNENROLLED);
        dto.setComment("reason to unsuspend user");

        given(ruleInputStore.containsKey(RuleInputType.NEW_STATUS)).willReturn(true);
        given(ruleInputStore.get(RuleInputType.NEW_STATUS)).willReturn(dto);

        given(taskRepository.findPendingTasksByTypesAndUser(RequestType.getTasksCausingUserSuspension(), dto.getUrid())).willReturn(Collections.emptyList());

        // when
        loader.load();
        // then
        StatusChangeSecurityStoreSlice slice = businessSecurityStore.getStatusChangeSecurityStoreSlice();
        assertFalse(slice.isUserSuspendedByTheSystem());
    }
}