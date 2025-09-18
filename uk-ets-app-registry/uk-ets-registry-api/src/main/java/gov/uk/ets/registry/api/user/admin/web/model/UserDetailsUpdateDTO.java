package gov.uk.ets.registry.api.user.admin.web.model;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class UserDetailsUpdateDTO implements Serializable {

    private static final long serialVersionUID = 2210845924321608373L;
    
    @NotNull
    private UserDetailsDTO current;
    @NotNull
    private UserDetailsDTO diff;

}
