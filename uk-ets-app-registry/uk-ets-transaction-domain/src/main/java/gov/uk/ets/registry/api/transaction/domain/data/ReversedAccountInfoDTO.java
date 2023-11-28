package gov.uk.ets.registry.api.transaction.domain.data;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReversedAccountInfoDTO {

    private final AccountInfo transferringAccountInfo;
    private final AccountInfo acquiringAccountInfo;
}
