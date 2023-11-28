package gov.uk.ets.registry.api.file.reference.domain;

import gov.uk.ets.registry.api.file.reference.domain.type.ReferenceDocument;
import gov.uk.ets.registry.api.file.reference.domain.type.ReferenceFileType;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "reference_files")
public class ReferenceFile {

    /**
     * this identifier is the same with the uploadedFile.id
     */
    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private UploadedFile uploadedFile;

    @Column(name = "document_name")
    private ReferenceDocument document;

    @Column(name = "reference_type")
    @Enumerated(EnumType.STRING)
    private ReferenceFileType referenceType;

}
