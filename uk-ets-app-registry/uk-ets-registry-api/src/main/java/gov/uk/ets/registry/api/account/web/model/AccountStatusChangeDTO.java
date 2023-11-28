package gov.uk.ets.registry.api.account.web.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountStatusChangeDTO implements Serializable {

	private static final long serialVersionUID = -9209388930122778836L;

    /**
     * The account status.
     */
    @NotNull(message = "The account status is mandatory")
    private AccountStatus accountStatus;
    
    /**
     * The comment.
     */
    @NotNull
    @Size(min=3,max=1024,message = "The comment is mandatory.")
    private String comment;
}
