package gov.uk.ets.registry.api.file.upload.requesteddocs.domain;

import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.user.domain.User;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user_files")
public class UserFile {

    /**
     * this identifier is the same with the uploadedFile.id
     */
    @Id
    private Long id;

    /**
     * The foreign key is defined with on delete cascade so wheneven files are removed entries in this table shall be
     * removed as well.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private UploadedFile uploadedFile;

    @Column(name = "document_name")
    private String documentName;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}
