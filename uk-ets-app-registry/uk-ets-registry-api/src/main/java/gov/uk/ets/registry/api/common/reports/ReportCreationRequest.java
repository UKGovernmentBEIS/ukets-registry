package gov.uk.ets.registry.api.common.reports;

import com.fasterxml.jackson.annotation.JsonCreator;
import gov.uk.ets.reports.model.ReportQueryInfo;
import gov.uk.ets.reports.model.ReportRequestingRole;
import gov.uk.ets.reports.model.ReportType;
import gov.uk.ets.reports.model.ReportTypeHandler;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class ReportCreationRequest extends ReportTypeHandler {

    /**
     * The URID of the user that requested the report.
     */
    private String requesterUrid;
    
    /**
     * The role of the user that requested the report.
     */
    private ReportRequestingRole requestingRole;

    @Builder
    @JsonCreator // needed for deserialization
    public ReportCreationRequest(ReportType type, ReportRequestingRole requestingRole, 
           ReportQueryInfo queryInfo, String requesterUrid) {
        super(type, queryInfo);
        this.requesterUrid = requesterUrid;
        this.requestingRole = requestingRole;
    }
}
