package gov.uk.ets.registry.api.account.web.model.accountcontact;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MetsContactDTO extends AccountContactDTO implements Serializable {

    private static final long serialVersionUID = -4512389765432109877L;

    @Builder.Default
    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<MetsAccountContactType> contactTypes = new HashSet<>();

    private OperatorType operatorType;
}
