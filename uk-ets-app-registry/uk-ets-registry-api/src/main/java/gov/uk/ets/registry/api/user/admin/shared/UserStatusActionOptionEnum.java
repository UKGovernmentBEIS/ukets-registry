package gov.uk.ets.registry.api.user.admin.shared;

import gov.uk.ets.registry.api.user.admin.web.model.UserStatusActionOptionDTO;
import gov.uk.ets.registry.api.user.domain.UserStatus;

/**
 * Enumerates possible options when changing user status.
 * 
 * @author fragkise
 *
 */
public enum UserStatusActionOptionEnum {

	OPTION_VALIDATE_USER(UserStatusActionOptionDTO.builder().value(UserStatusAction.VALIDATE)
					.label(UserStatusAction.VALIDATE.getDescription()).newStatus(UserStatus.VALIDATED)
					.message("A print letter with registry activation code task will be created").build()),
	OPTION_SUSPEND_USER(UserStatusActionOptionDTO.builder().value(UserStatusAction.SUSPEND)
			.label(UserStatusAction.SUSPEND.getDescription()).newStatus(UserStatus.SUSPENDED).build()),
	OPTION_UNSUSPEND_REGISTERED_USER(UserStatusActionOptionDTO.builder().value(UserStatusAction.RESTORE)
			.label(UserStatusAction.RESTORE.getDescription()).newStatus(UserStatus.REGISTERED).build()),
	OPTION_UNSUSPEND_VALIDATED_USER(UserStatusActionOptionDTO.builder().value(UserStatusAction.RESTORE)
			.label(UserStatusAction.RESTORE.getDescription()).newStatus(UserStatus.VALIDATED).build()),
	OPTION_UNSUSPEND_ENROLLED_USER(UserStatusActionOptionDTO.builder().value(UserStatusAction.RESTORE)
	.label(UserStatusAction.RESTORE.getDescription()).newStatus(UserStatus.ENROLLED).build());

	private UserStatusActionOptionDTO option;
	
	UserStatusActionOptionEnum(UserStatusActionOptionDTO option) {
		this.option = option;
	}

	public UserStatusActionOptionDTO getOption() {
		return option;
	}
}
