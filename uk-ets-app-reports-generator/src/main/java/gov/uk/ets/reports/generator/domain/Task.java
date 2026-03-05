package gov.uk.ets.reports.generator.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class Task {
    Long taskId;
    String taskType;
    String initiatorName;
    String initiatorId;
    String claimantName;
    String claimantId;
    String authorisedRepresentative;
    LocalDateTime createdOn;
    LocalDateTime completedOn;
    String taskStatus;
    String transactionId;
}
