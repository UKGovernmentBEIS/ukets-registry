package gov.uk.ets.publication.api.web.model;

import java.time.LocalDateTime;
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
public class ReportFileDto {
	private Long id;
    private String fileName;
    private Integer applicableForYear;
    private LocalDateTime publishedOn;
    private byte[] data;
}
