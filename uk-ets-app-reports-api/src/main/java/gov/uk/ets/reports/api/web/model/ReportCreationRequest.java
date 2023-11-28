package gov.uk.ets.reports.api.web.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import gov.uk.ets.commons.logging.MDCParam;
import gov.uk.ets.reports.model.ReportQueryInfo;
import gov.uk.ets.reports.model.ReportRequestingRole;
import gov.uk.ets.reports.model.ReportType;
import gov.uk.ets.reports.model.ReportTypeHandler;
import gov.uk.ets.reports.model.criteria.ReportCriteria;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static gov.uk.ets.commons.logging.RequestParamType.URID;

@NoArgsConstructor
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class ReportCreationRequest extends ReportTypeHandler {

    /**
     * The URID of the user that requested the report.
     */
    @MDCParam(URID)
    private String requesterUrid;
    
    /**
     * The role of the user that requested the report.
     */
    private ReportRequestingRole requestingRole;    

    @Builder
    @JsonCreator // needed for deserialization
    public ReportCreationRequest(ReportType type, ReportRequestingRole requestingRole,
    		ReportCriteria criteria, ReportQueryInfo queryInfo, String requesterUrid) {
        super(type, criteria, queryInfo);
        this.requesterUrid = requesterUrid;
        this.requestingRole = requestingRole;
    }
}
