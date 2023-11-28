package gov.uk.ets.publication.api.domain;

import gov.uk.ets.publication.api.model.DisplayType;
import gov.uk.ets.publication.api.model.SectionStatus;
import gov.uk.ets.publication.api.model.SectionType;
import gov.uk.ets.reports.model.ReportType;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "section")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Section {

    @Column(nullable = false)
    @Id
    @SequenceGenerator(name = "section_id_generator", sequenceName = "section_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "section_id_generator")
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "section_type")
    private SectionType sectionType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "report_type")
    private ReportType reportType;
    
    private String title;
    
    private String summary;
    
    @Column(name = "display_order")
    private Integer displayOrder;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "display_type")
    private DisplayType displayType;
    
    @Column(name = "published_on")
    private LocalDateTime publishedOn;      
    
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SectionStatus status;
    
    @OneToOne(mappedBy = "section", fetch = FetchType.LAZY)
    @ToString.Exclude
    private PublicationSchedule publicationSchedule;
    
    @OneToMany(mappedBy = "section", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<ReportFile> reportFiles;

    /**
     * Needed for synchronizing bidirectional relationship between section and report files.
     *
     * @param reportFile the report file object.
     */
    public void addReportFile(ReportFile reportFile) {
        reportFiles.add(reportFile);
        reportFile.setSection(this);
    }
}
