package gov.uk.ets.registry.api.file.upload.domain;

import gov.uk.ets.registry.api.file.upload.types.FileStatus;
import gov.uk.ets.registry.api.task.domain.Task;
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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

// TODO:fileName as an ID  in EqualsAndHashCode shall be updated. This convention is possibly valid only for allocation
//  tables files
@EqualsAndHashCode(of = "fileName")
@Getter
@Setter
@Entity
@Table(name = "files")
public class UploadedFile implements Serializable {

    private static final long serialVersionUID = 2215391381325869271L;

    @Id
    @SequenceGenerator(
        name = "files_id_generator",
        sequenceName = "files_seq",
        allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "files_id_generator")
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_identifier", referencedColumnName = "request_identifier")
    private Task task;

    @Column(name = "file_name")
    private String fileName;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "file_data")
    private byte[] fileData;

    @Column(name = "file_status")
    @Enumerated(EnumType.STRING)
    private FileStatus fileStatus;

    @Column(name = "created_on", columnDefinition = "TIMESTAMP")
    private LocalDateTime creationDate;

    @Column(name = "file_size")
    private String fileSize;
}
