package uk.gov.ets.transaction.log.messaging.types;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"identifier"})
public class AccountNotification {

    private String accountName;
    private Long oldIdentifier;
    private Long identifier;
    private String fullIdentifier;
    private Integer commitmentPeriodCode;
    private Integer checkDigits;
    private Date openingDate;
}
