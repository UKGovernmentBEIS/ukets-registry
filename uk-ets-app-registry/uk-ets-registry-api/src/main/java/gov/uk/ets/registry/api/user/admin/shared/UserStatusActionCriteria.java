package gov.uk.ets.registry.api.user.admin.shared;

import gov.uk.ets.registry.api.user.domain.UserStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Holds information required to build user state available actions.
 * @author fragkise
 */
@Getter
@Setter
@Builder
public class UserStatusActionCriteria {

	  private UserStatus currentState;
	  private UserStatus previousState;
}
