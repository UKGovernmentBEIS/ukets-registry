package gov.uk.ets.registry.api.user.admin.web.model;

import gov.uk.ets.registry.api.user.admin.shared.UserStatusAction;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserStatusActionOptionDTO {

	  private String label;
	  private UserStatusAction value;
	  private final boolean enabled= true;
	  private UserStatus newStatus;
	  private String message;
}
