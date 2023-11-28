package gov.uk.ets.registry.api.file.upload.wrappers;

import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BulkArAccountDTO {


    private String fullIdentifier;

    private String accountName;

    private AccountStatus accountStatus;

    private RegistryAccountType registryAccountType;

    private KyotoAccountType kyotoAccountType;

    public boolean isAccountStatusClosed() {
        return AccountStatus.CLOSED.equals(accountStatus);
    }

    public boolean isAccountStatusClosurePending() {
        return AccountStatus.CLOSURE_PENDING.equals(accountStatus);
    }

    public boolean isAccountStatusProposed() {
        return AccountStatus.PROPOSED.equals(accountStatus);
    }

    public boolean isAccountStatusTransferPending() {
        return AccountStatus.TRANSFER_PENDING.equals(accountStatus);
    }

    public boolean isAccountStatusRejected() {
        return AccountStatus.REJECTED.equals(accountStatus);
    }
}
