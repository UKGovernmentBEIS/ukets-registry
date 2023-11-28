package gov.uk.ets.registry.api.user.admin.web.model;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import gov.uk.ets.registry.api.user.domain.UserStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserStatusChangeDTO implements Serializable {

	private static final long serialVersionUID = -1L;

    @NotNull(message = "urid is mandatory")
    private String urid;
	
    /**
     * The user state.
     */
    @NotNull(message = "The user state is mandatory")
    private UserStatus userStatus;

    @NotBlank(message = "The user state is mandatory")
    @NotNull(message = "The user state is mandatory")
    @Size(min=3, max=1024, message = "The comment is mandatory.")
    private String comment;
}
