package gov.uk.ets.reports.generator.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Transaction {
    String id;
    String type;
    String status;
    LocalDateTime lastUpdated;
    Long quantity;
    String unitType;
    LocalDateTime transactionStart;
    String failureReasons;
    String reversesIdentifier;
    String reversedByIdentifier;
}
