package gov.uk.ets.registry.api.user.profile.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * The payload that exists encoded inside the confirmation token and as 
 * difference in the created task.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmailChangeDTO implements Serializable {
    

    private static final long serialVersionUID = -8971930969585803577L;
    
    /**
     * The unique business identifier of the user that will change email.
     */
    private String urid;
    
    /**
     * The new email that replaces the current email of the user.
     */
    @NotNull
    @Email
    @Size(max = 256, message = "Email must not exceed 256 characters.")
    private String newEmail;

    /**
     * The old email that was replaced by newEmail.
     */
    @Email
    @Size(max = 256, message = "Email must not exceed 256 characters.")
    private String oldEmail;
    
    /**
     * The urid of the requester who may be different than the user 
     * that shall change email.
     */
    private String requesterUrid;
}
