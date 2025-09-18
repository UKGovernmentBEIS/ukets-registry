package gov.uk.ets.registry.api.account.web.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class AccountOperatorDetailsUpdateDTO implements Serializable {

    private static final long serialVersionUID = 8566306592755679070L;

    @NotNull
    private OperatorDTO current;
    @NotNull
    private OperatorDTO diff;
}
