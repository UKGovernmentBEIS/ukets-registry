package gov.uk.ets.publication.api.domain;

import gov.uk.ets.publication.api.model.PublicationFrequency;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
@Table(name = "publication_schedule")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PublicationSchedule {
    
    @Column(nullable = false)
    @Id
    @SequenceGenerator(name = "publication_schedule_id_generator", sequenceName = "publication_schedule_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "publication_schedule_id_generator")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    @ToString.Exclude
    private Section section;

    @Enumerated(EnumType.STRING)
    @Column(name = "publication_frequency")
    private PublicationFrequency publicationFrequency;
    
    @Column(name = "start_date")
    private LocalDateTime startDate;
    
    @Column(name = "next_report_date")
    private LocalDateTime nextReportDate;
    
    @Column(name = "every_x_days")
    private Integer everyXDays;
    
    @Column(name = "generation_date")
    private LocalDateTime generationDate;
}
