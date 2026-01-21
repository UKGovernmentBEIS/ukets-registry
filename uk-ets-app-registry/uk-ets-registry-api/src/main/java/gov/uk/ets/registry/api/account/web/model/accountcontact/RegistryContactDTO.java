package gov.uk.ets.registry.api.account.web.model.accountcontact;

import gov.uk.ets.registry.api.account.domain.types.AccountContactType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RegistryContactDTO extends AccountContactDTO implements Serializable {

    private static final long serialVersionUID = -4512389765432109875L;

    @NotNull
    private AccountContactType contactType;
}
