package gov.uk.ets.registry.api.file.upload.domain;

import gov.uk.ets.registry.api.file.upload.types.FileStatus;
import gov.uk.ets.registry.api.task.domain.Task;
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
