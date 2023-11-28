package gov.uk.ets.registry.api.user.admin.web.model;

import gov.uk.ets.registry.api.account.shared.Options;
import gov.uk.ets.registry.api.user.UserActionError;
import gov.uk.ets.registry.api.user.UserActionException;
import gov.uk.ets.registry.api.user.admin.shared.UserStatusActionCriteria;
import gov.uk.ets.registry.api.user.admin.shared.UserStatusActionOptionEnum;
import gov.uk.ets.registry.api.user.admin.shared.UserStatusActionOptions;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * This class is responsible for creating available change user state options
 * and for selecting which of them are valid for a particular case based 
 * on the provided criteria. 
 * 
 * @author fragkise
 * @since v.0.8.0
 */
public final class UserStateActionOptionsFactory {

	/**
	 * Key : UserState
	 * Value: Set of valid change user state actions.
	 * 
	 * This map is the single source of truth for all UserState change transitions.
	 */
	public static final Map<UserStatus, Set<UserStatusActionOptionDTO>> VALID_USER_STATE_ACTIONS = Map.of(
			UserStatus.REGISTERED,
			Set.of(UserStatusActionOptionEnum.OPTION_VALIDATE_USER.getOption(),
					UserStatusActionOptionEnum.OPTION_SUSPEND_USER.getOption()),
			UserStatus.VALIDATED,
			Set.of(UserStatusActionOptionEnum.OPTION_SUSPEND_USER.getOption()),
			UserStatus.ENROLLED,
			Set.of(UserStatusActionOptionEnum.OPTION_SUSPEND_USER.getOption()),
			UserStatus.SUSPENDED,
			Set.of(UserStatusActionOptionEnum.OPTION_UNSUSPEND_REGISTERED_USER.getOption(),
					UserStatusActionOptionEnum.OPTION_UNSUSPEND_VALIDATED_USER.getOption(),
					UserStatusActionOptionEnum.OPTION_UNSUSPEND_ENROLLED_USER.getOption()));	
	
	// Do not allow instantiation
	private UserStateActionOptionsFactory() {

	}

	/**
	 * The available options depend on the current state. The new state is
	 * determined by both the current state & the previous one in case of UNSUSPEND.
	 * 
	 * @return
	 */
	public static Options<UserStatusActionOptionDTO> createAvailableUserStateActions(UserStatusActionCriteria criteria) {

		UserStatusActionOptions options = new UserStatusActionOptions();
		Set<UserStatusActionOptionDTO> actions = VALID_USER_STATE_ACTIONS.get(criteria.getCurrentState());
		
		if (UserStatus.SUSPENDED.equals(criteria.getCurrentState())) {
			if (!Optional.ofNullable(criteria.getPreviousState()).isPresent()) {
				throw UserActionException.create(UserActionError.PREVIOUS_USER_STATUS_UNDEFINED);
			}
			//Pick the correct action based on the previous state
			actions.stream().filter(a->a.getNewStatus().equals(criteria.getPreviousState())).forEach(a->options.addOption(a));
		} else {
			actions.forEach(a->options.addOption(a));
		}

		return options;
	}

}
