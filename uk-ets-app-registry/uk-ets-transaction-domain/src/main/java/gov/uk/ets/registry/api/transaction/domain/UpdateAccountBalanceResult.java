package gov.uk.ets.registry.api.transaction.domain;

import java.util.Date;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@EqualsAndHashCode(of = {"transactionIdentifier","transferringAccountBalance","acquiringAccountBalance"})
public class UpdateAccountBalanceResult {

    private String transactionIdentifier;
    private AccountBalance transferringAccountBalance;
    private AccountBalance acquiringAccountBalance;
    private Date lastUpdated;
}
