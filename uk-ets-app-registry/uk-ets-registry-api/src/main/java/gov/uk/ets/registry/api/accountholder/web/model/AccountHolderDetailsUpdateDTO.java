package gov.uk.ets.registry.api.accountholder.web.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import java.io.Serializable;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class AccountHolderDetailsUpdateDTO implements Serializable {

    private static final long serialVersionUID = 8566306592755699070L;

    @NotNull
    private AccountHolderDTO currentAccountHolder;
    @NotNull
    private AccountHolderDetailsUpdateDiffDTO accountHolderDiff;

}
