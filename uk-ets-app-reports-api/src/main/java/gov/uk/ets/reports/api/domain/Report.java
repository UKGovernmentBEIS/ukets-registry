package gov.uk.ets.reports.api.domain;

import gov.uk.ets.reports.model.ReportRequestingRole;
import gov.uk.ets.reports.model.ReportStatus;
import gov.uk.ets.reports.model.ReportType;
import java.time.LocalDateTime;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    @Id
    @SequenceGenerator(name = "report_id_generator", sequenceName = "report_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "report_id_generator")
    private Long id;

    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    @Enumerated(EnumType.STRING)
    private ReportType type;

    @Column(name = "request_date")
    private LocalDateTime requestDate;

    @Column(name = "generation_date")
    private LocalDateTime generationDate;

    @Column(name = "requesting_user")
    private String requestingUser;

    @Column(name = "file_location")
    private String fileLocation;

    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "requesting_role")
    @Enumerated(EnumType.STRING)
    private ReportRequestingRole requestingRole;
    
    @Basic(fetch = FetchType.LAZY)
    private String criteria;

    public String getFileName() {
        return fileLocation == null ? "" : fileLocation.substring(fileLocation.lastIndexOf('/') + 1);
    }
}
