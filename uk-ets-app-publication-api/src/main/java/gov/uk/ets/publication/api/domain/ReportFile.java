package gov.uk.ets.publication.api.domain;

import gov.uk.ets.publication.api.model.ReportPublicationStatus;
import java.io.Serializable;
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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
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
