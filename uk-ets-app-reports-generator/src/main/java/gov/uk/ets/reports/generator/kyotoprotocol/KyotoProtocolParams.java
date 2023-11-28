package gov.uk.ets.reports.generator.kyotoprotocol;

import gov.uk.ets.reports.model.RequestingSystem;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.Optional;

@Builder
@Getter
public class KyotoProtocolParams {

    private short reportedYear;
    private Optional<Long> commitmentPeriod;
    private Date reportEndDate;
    private short submissionYear;
    private String jdbcUrl;
    private RequestingSystem requestingSystem;
    private String username;
    private String password;
    private Long reportGeneratorCommandId;

}
