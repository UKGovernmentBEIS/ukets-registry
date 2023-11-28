package gov.uk.ets.registry.api.file.upload.wrappers;

import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BulkArAccountAccessDTO {

    private String userUrid;
    private String accountIdentifier;
    private AccountAccessState accessState;
}
