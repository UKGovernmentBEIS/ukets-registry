package gov.uk.ets.registry.api.helper.integration;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestAccountData {
    private String accountIdentifier;
    private Long accountId;
    private Long holderId;
    private Long operatorId;
    private Long holderIdentifier;

    public String getRegistryId() {
        return operatorId.toString();
    }

    public String getOperatorIdMetsContacts() {
        return operatorId.toString();
    }
}
