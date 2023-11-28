package gov.uk.ets.reports.generator.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrustedAccount {

    private String number;
    private String status;
    private String description;
    private String name;
    private LocalDateTime activationDate;
    private String type;
}
