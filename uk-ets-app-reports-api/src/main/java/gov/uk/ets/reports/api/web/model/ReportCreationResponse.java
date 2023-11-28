package gov.uk.ets.reports.api.web.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@Builder
@ToString
@EqualsAndHashCode
public class ReportCreationResponse {
    private final Long reportId;
}
