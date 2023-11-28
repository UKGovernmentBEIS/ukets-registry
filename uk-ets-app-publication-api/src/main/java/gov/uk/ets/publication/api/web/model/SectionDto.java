package gov.uk.ets.publication.api.web.model;


import gov.uk.ets.publication.api.model.DisplayType;
import gov.uk.ets.publication.api.model.PublicationFrequency;
import gov.uk.ets.reports.model.ReportType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class SectionDto {
	private Long id;
    private Integer displayOrder;
    private String title;
    private String summary;
    private ReportType reportType;
    private DisplayType displayType;
    private LocalDateTime lastUpdated;
    private PublicationFrequency publicationFrequency;
    private LocalDate publicationStart;
    private LocalTime publicationTime;
    private LocalDateTime publicationStartDateTime;
    private Integer everyXDays;
    private LocalDate generationDate;
    private LocalTime generationTime;
    private LocalDateTime lastReportPublished;
}
