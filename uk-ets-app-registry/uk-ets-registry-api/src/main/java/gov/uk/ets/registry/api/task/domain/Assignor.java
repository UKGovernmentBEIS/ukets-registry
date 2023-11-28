package gov.uk.ets.registry.api.task.domain;

import gov.uk.ets.registry.api.user.domain.User;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * Value object for the task assignor.
 * It is composed by assignor {@link User} and his scopes on
 * the Task type.
 */
@Getter
@Setter
public class Assignor {
  /**
   * The user who assigns the task
   */
  private User user;
  /**
   * The scopes on task type (READ, WRITE, COMPLETE)
   */
  private Set<String> scopes;
}
