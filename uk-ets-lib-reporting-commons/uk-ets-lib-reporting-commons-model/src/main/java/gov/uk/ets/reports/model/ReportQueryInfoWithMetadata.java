package gov.uk.ets.reports.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, of = {"urid"})
public class ReportQueryInfoWithMetadata extends ReportQueryInfo {
	private String urid;
	private ReportRequestingRole requestingRole;

}
