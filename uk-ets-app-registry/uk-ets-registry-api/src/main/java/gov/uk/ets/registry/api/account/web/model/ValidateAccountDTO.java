package gov.uk.ets.registry.api.account.web.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ValidateAccountDTO {

    private Boolean validAccount;
    private Boolean kyotoAccountType;
}
