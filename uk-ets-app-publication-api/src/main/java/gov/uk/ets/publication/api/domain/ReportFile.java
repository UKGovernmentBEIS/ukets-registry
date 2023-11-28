package gov.uk.ets.publication.api.domain;

import gov.uk.ets.publication.api.model.ReportPublicationStatus;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "report_file")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportFile implements Serializable {

    private static final long serialVersionUID = -4059136416385547684L;

    @Column(nullable = false)
    @Id
    @SequenceGenerator(name = "report_file_id_generator", sequenceName = "report_file_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "report_file_id_generator")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    @ToString.Exclude
    private Section section;
    
    @Column(name = "file_name")
    private String fileName;
    
    @Column(name = "generated_on")
    private LocalDateTime generatedOn;
    
    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(name = "applicable_for_year")
    private Integer applicableForYear;
    
    @Column(name = "file_location")
    private String fileLocation;
    
    private String type;
    
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "file_data")
    @ToString.Exclude
    private byte[] fileData;

    @Column(name = "file_size")
    private String fileSize;

    @Column(name = "file_status")
    @Enumerated(EnumType.STRING)
    private ReportPublicationStatus fileStatus;
    
    @Column(name = "batch_id")
    private String batchId;
}
