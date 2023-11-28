package gov.uk.ets.registry.api.messaging.domain;

import gov.uk.ets.registry.api.account.domain.Account;
import java.io.Serializable;
import java.util.Date;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountNotification implements Serializable {
    private static final long serialVersionUID = -5697043025664402556L;

    private String accountName;
    private Long oldIdentifier;
    private Long identifier;
    private String fullIdentifier;
    private Integer commitmentPeriodCode;
    private Integer checkDigits;
    private Date openingDate;

    /**
     * A converter from Account to AccountNotification.
     * @param account the account.
     * @return an account notification.
     */
    public static AccountNotification convert(Account account) {
        return AccountNotification.builder()
            .accountName(account.getAccountName())
            .identifier(account.getIdentifier())
            .fullIdentifier(account.getFullIdentifier())
            .commitmentPeriodCode(account.getCommitmentPeriodCode())
            .checkDigits(account.getCheckDigits())
            .openingDate(account.getOpeningDate())
            .build();
    }
}
