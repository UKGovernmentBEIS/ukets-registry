/**
 * 
 */
package gov.uk.ets.registry.api.user.admin.shared;

import gov.uk.ets.registry.api.user.admin.web.model.UserStateActionOptionsFactory;
import gov.uk.ets.registry.api.user.admin.web.model.UserStatusActionOptionDTO;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import lombok.Builder;

/**
 * This class implements a simple interface for checking the validity of a changing a User state.
 * Each instance of this class represents a single user state change operation.
 * 
 * @author fragkise
 * @since v.0.8.0
 */
@Builder
public final class UserStatusTransition {

	private UserStatus currentState;
	private UserStatus newState;
	private String comment;

	/**
	 * Checks if the User State transition represented by this object is valid.
	 * 
	 * @return true if the transition is valid according to the specifications  false otherwise
	 */
	public boolean isValid() {
		return UserStateActionOptionsFactory.
				VALID_USER_STATE_ACTIONS.
				get(currentState).
				stream().
				map(UserStatusActionOptionDTO::getNewStatus).
				anyMatch(s-> s.equals(newState));
	}
}
