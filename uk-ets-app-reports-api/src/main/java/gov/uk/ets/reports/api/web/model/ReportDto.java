package gov.uk.ets.reports.api.web.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import gov.uk.ets.reports.model.ReportStatus;
import gov.uk.ets.reports.model.ReportType;
import java.time.LocalDateTime;
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
public class ReportDto {
    private final Long id;
    private final ReportType type;
    private final ReportStatus status;
    private final String requestingUser;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS+00:00")
    private final LocalDateTime requestDate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS+00:00")
    private final LocalDateTime generationDate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS+00:00")
    private final LocalDateTime expirationDate;

    private final Long fileSize;
    private final String fileName;

    private final byte[] data;

}
