package gov.uk.ets.registry.api.account.web.model;

import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AccountUpdateDetailsDTO implements Serializable {

    private RegulatorType regulatorType;
    private Long accountHolderId;
    private String requestId;
}
