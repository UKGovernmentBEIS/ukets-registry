package gov.uk.ets.registry.api.task.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.uk.ets.registry.api.authz.Scope;
import gov.uk.ets.registry.api.common.model.events.DomainEvent;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.service.TaskActionError;
import gov.uk.ets.registry.api.user.domain.User;
import java.util.Set;
import org.junit.Test;
import org.mockito.Mockito;

public class TaskTest {
    @Test
    public void claim_or_assign_task_should_return_invalid_event_if_task_status_is_COMPLETED() {
        String comment = null;
        final User user = Mockito.mock(User.class);
        user.setFirstName("Firstname");
        user.setLastName("Lastname");
        final Task task = new Task();
        task.setType(RequestType.ACCOUNT_OPENING_REQUEST);
        task.setStatus(RequestStateEnum.REJECTED);

        DomainEvent<gov.uk.ets.registry.api.auditevent.DomainEvent> invalidEvent = task.claim(user);
        assertThat(invalidEvent.isValid(), is(false));
        assertThat(invalidEvent.getErrors().get(0).getCode(), is(TaskActionError.TASK_COMPLETED));
        Assignor assignor = Mockito.mock(Assignor.class);
        Mockito.when(assignor.getUser()).thenReturn(user);
        invalidEvent = task.assign(assignor, Mockito.mock(User.class), "");
        assertThat(invalidEvent.isValid(), is(false));
        assertThat(invalidEvent.getErrors().get(0).getCode(), is(TaskActionError.TASK_COMPLETED));

        task.setStatus(null);
        DomainEvent validEvent = task.claim(user);
        assertThat(validEvent.isValid(), is(true));

        Mockito.when(assignor.getScopes()).thenReturn(Set.of(Scope.SCOPE_TASK_ACCOUNT_OPEN_COMPLETE.getScopeName()));
        validEvent = task.assign(assignor, Mockito.mock(User.class), "");
        assertThat(validEvent.isValid(), is(true));
    }

    @Test
    public void assignor_has_WRITE_permission_on_ACCOUNT_OPENING_REQUEST_taskType_and_is_not_the_claimant_of_task_should_return_invalid_error() {
        User user = Mockito.mock(User.class);
        user.setFirstName("Firstname");
        user.setLastName("Lastname");
        Assignor assignor = Mockito.mock(Assignor.class);
        Mockito.when(assignor.getUser()).thenReturn(user);
        Mockito.when(assignor.getScopes()).thenReturn(Set.of(Scope.SCOPE_TASK_ACCOUNT_OPEN_WRITE.getScopeName()));
        String comment = "a comment";
        User assignee = Mockito.mock(User.class);
        assignee.setFirstName("Firstname");
        assignee.setLastName("Lastname");
        Task task = new Task();
        task.setType(RequestType.ACCOUNT_OPENING_REQUEST);
        DomainEvent event = task.assign(assignor, assignee, comment);
        assertThat(event.isValid(), is(false));

        task.setClaimedBy(user);
        event = task.assign(assignor, assignee, comment);
        assertThat(event.isValid(), is(true));

        task.setClaimedBy(null);
        Mockito.when(assignor.getScopes()).thenReturn(Set.of(Scope.SCOPE_TASK_ACCOUNT_OPEN_COMPLETE.getScopeName()));
        event = task.assign(assignor, assignee, comment);
        assertThat(event.isValid(), is(true));

        task.setClaimedBy(user);
        event = task.assign(assignor, assignee, comment);
        assertThat(event.isValid(), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void claim_without_passing_a_claimant_should_throw_IllegalArgumentException() {
        Task task = new Task();
        task.setType(RequestType.ACCOUNT_OPENING_REQUEST);
        task.setRequestId(100L);

        task.claim(null);
    }

    @Test(expected = IllegalStateException.class)
    public void claim_a_task_which_has_not_a_type_should_throw_IllegalStateException() {
        Task task = new Task();
        task.setRequestId(100L);

        task.claim(Mockito.mock(User.class));
    }

    @Test(expected = IllegalStateException.class)
    public void assign_a_task_which_has_not_a_type_should_throw_IllegalStateException() {
        Task task = new Task();
        task.setRequestId(100L);
        Assignor assignor = Mockito.mock(Assignor.class);
        Mockito.when(assignor.getUser()).thenReturn(Mockito.mock(User.class));
        task.assign(assignor, Mockito.mock(User.class), "a comment");
    }

    @Test(expected = IllegalArgumentException.class)
    public void assign_a_task_without_passing_assignor_should_throw_IllegalArgumentException() {
        Task task = new Task();
        task.setType(RequestType.ACCOUNT_OPENING_REQUEST);
        task.setRequestId(100L);

        task.assign(null, Mockito.mock(User.class), "a comment");
    }

    @Test(expected = IllegalArgumentException.class)
    public void assign_a_task_without_passing_assignee_should_throw_IllegalArgumentException() {
        Task task = new Task();
        task.setType(RequestType.ACCOUNT_OPENING_REQUEST);
        task.setRequestId(100L);
        Assignor assignor = Mockito.mock(Assignor.class);
        Mockito.when(assignor.getUser()).thenReturn(Mockito.mock(User.class));
        task.assign(assignor, null, "a comment");
    }

    @Test(expected = IllegalArgumentException.class)
    public void assign_a_task_without_passing_comment_should_throw_IllegalArgumentException() {
        Task task = new Task();
        task.setType(RequestType.ACCOUNT_OPENING_REQUEST);
        task.setRequestId(100L);
        Assignor assignor = Mockito.mock(Assignor.class);
        Mockito.when(assignor.getUser()).thenReturn(Mockito.mock(User.class));
        task.assign(assignor, Mockito.mock(User.class), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void assign_without_passing_assignor_should_throw_IllegalArgumentException() {
        Task task = new Task();
        task.setType(RequestType.ACCOUNT_OPENING_REQUEST);
        task.setRequestId(100L);

        task.assign(null, Mockito.mock(User.class), "a comment");
    }

    @Test
    public void test_success_claim_result_validity() {
        Task task = new Task();
        task.setRequestId(1000L);
        task.setType(RequestType.ACCOUNT_OPENING_REQUEST);
        User claimant = Mockito.mock(User.class);
        Mockito.when(claimant.getUrid()).thenReturn("a-urid");
        Mockito.when(claimant.getFirstName()).thenReturn("FirstName");
        Mockito.when(claimant.getLastName()).thenReturn("LastName");

        String comment = "test comment.";
        DomainEvent<gov.uk.ets.registry.api.auditevent.DomainEvent> domainEvent = task.claim(claimant);
        gov.uk.ets.registry.api.auditevent.DomainEvent event = domainEvent.getPayload();

        assertThat(event.getWho(), is("a-urid"));
        assertThat(event.getWhat(), is("Open account task claimed."));
        assertThat(event.getWhen(), is(task.getClaimedDate()));
        assertThat(event.getDescription(), is(""));
    }

    @Test
    public void test_success_assign_result_validity() {
        Task task = new Task();
        task.setRequestId(1000L);
        task.setType(RequestType.ACCOUNT_OPENING_REQUEST);

        Assignor assignor = new Assignor();
        User user = new User();
        user.setLastName("Lastname");
        user.setFirstName("Firstname");
        user.setUrid("assignor-urid");
        assignor.setUser(user);
        assignor.setScopes(Set.of(Scope.SCOPE_TASK_ACCOUNT_OPEN_COMPLETE.getScopeName()));
        User assignee = new User();
        assignee.setLastName("Lastname");
        assignee.setFirstName("Firstname");
        assignee.setDisclosedName("Disclosure name");
        assignee.setUrid("assignee-urid");
        String comment = "a comment for test";

        DomainEvent<gov.uk.ets.registry.api.auditevent.DomainEvent> domainEvent =
            task.assign(assignor, assignee, comment);
        gov.uk.ets.registry.api.auditevent.DomainEvent event = domainEvent.getPayload();

        assertThat(event.getWho(), is("assignor-urid"));
        assertThat(event.getWhat(), is("Open account task assigned."));
        assertThat(event.getWhen(), is(task.getClaimedDate()));
        assertThat(event.getDescription(),
            is("The assignee user is " + assignee.getDisclosedName() + "."));
    }

    @Test
    public void testDescription() {
        Task task = new Task();
        task.setType(RequestType.CHANGE_TOKEN);
        assertTrue(task.generateEventTypeDescription("test").startsWith("Request to change 2FA"));
        assertTrue(task.generateEventTypeDescription("test").contains("test"));

        task.setType(RequestType.LOST_TOKEN);
        assertTrue(task.generateEventTypeDescription("test").startsWith("Emergency request to change 2FA"));
        assertTrue(task.generateEventTypeDescription("test").contains("test"));

        task.setType(RequestType.ACCOUNT_OPENING_INSTALLATION_TRANSFER_REQUEST);
        assertTrue(task.generateEventTypeDescription("test").startsWith("Open account with installation transfer task"));
        assertTrue(task.generateEventTypeDescription("test").contains("test"));
    }

}
