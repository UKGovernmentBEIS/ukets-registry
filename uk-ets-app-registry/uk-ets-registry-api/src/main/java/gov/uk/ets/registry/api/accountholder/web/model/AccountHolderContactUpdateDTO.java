package gov.uk.ets.registry.api.accountholder.web.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import gov.uk.ets.registry.api.account.web.model.AccountHolderRepresentativeDTO;
import java.io.Serializable;
import javax.validation.constraints.NotNull;

import gov.uk.ets.registry.api.task.domain.types.RequestType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class AccountHolderContactUpdateDTO implements Serializable {

    private static final long serialVersionUID = 8566306592756799070L;

    @NotNull
    private AccountHolderRepresentativeDTO currentAccountHolder;
    @NotNull
    private AccountHolderContactUpdateDiffDTO accountHolderDiff;
    @NotNull
    private RequestType updateType;
}
