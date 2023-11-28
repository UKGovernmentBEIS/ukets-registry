package gov.uk.ets.reports.model.messaging;

import com.fasterxml.jackson.annotation.JsonCreator;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.ReportType;
import gov.uk.ets.reports.model.RequestingSystem;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;


@Getter
@ToString
@EqualsAndHashCode(callSuper = false, of = {"id"})
public class ReportGenerationCommand implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private ReportType type;
    private final Long id;
    private final RequestingSystem requestingSystem;
    private final LocalDateTime requestDate;
    private final ReportQueryInfoWithMetadata reportQueryInfo;

    @Builder
    @JsonCreator // needed for deserialization
    public ReportGenerationCommand(@NotNull ReportType type, @Valid ReportQueryInfoWithMetadata reportQueryInfo,
                                   RequestingSystem requestingSystem, Long id, LocalDateTime requestDate) {
        this.type = type;
        this.id = id;
        this.requestingSystem = requestingSystem;
        this.requestDate = requestDate;
        this.reportQueryInfo = reportQueryInfo;
    }
}
