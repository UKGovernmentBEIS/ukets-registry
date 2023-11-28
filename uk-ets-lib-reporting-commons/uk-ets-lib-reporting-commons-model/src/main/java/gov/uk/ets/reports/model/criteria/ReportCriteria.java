package gov.uk.ets.reports.model.criteria;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Common Report Criteria parent.
 *
 * @deprecated Criteria will not be used since the whole query is passed to the generator.
 */
@JsonIgnoreProperties(ignoreUnknown = false)
@Deprecated(forRemoval = true)
public interface ReportCriteria {
}
