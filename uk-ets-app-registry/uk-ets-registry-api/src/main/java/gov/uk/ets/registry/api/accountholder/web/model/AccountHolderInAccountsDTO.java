package gov.uk.ets.registry.api.accountholder.web.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AccountHolderInAccountsDTO implements Serializable {

    private static final long serialVersionUID = 2755835300051913518L;

    private String accountType;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long numberOfOwnedAccounts;
}
