package gov.uk.ets.registry.api.file.upload.wrappers;

import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BulkArTaskBuilder {

    private final String accountFullIdentifier;
    private final String userUrid;
    private final AccountAccessRight permissions;
}
