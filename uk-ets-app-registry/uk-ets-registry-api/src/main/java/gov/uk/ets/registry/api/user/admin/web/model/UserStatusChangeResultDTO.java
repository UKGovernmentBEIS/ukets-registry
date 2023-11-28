/**
 * 
 */
package gov.uk.ets.registry.api.user.admin.web.model;

import java.util.Optional;

import gov.uk.ets.registry.api.user.KeycloakUser;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Contains information about the result of a change user status operation.
 * 
 * @author fragkise
 */
@Getter
@Setter
@Builder
public class UserStatusChangeResultDTO {

	/** The new user status.*/
    private UserStatus userStatus;

    /** The previous user status.*/
    private UserStatus previousUserStatus;
    
	/** The task identifier if any.*/
    private Optional<Long> requestId;

    /** The iamIdentifier */
    private String iamIdentifier;

    private KeycloakUser user;
}
