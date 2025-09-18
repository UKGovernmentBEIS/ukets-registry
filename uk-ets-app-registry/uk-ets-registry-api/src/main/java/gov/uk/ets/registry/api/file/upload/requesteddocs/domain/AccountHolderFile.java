package gov.uk.ets.registry.api.file.upload.requesteddocs.domain;

import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Holds submitted documents(binary files) related to the account holder.
 */
@Entity
@Getter
@Setter
@Table(name = "account_holder_files")
public class AccountHolderFile {

    /**
     * this identifier is the same with the uploadedFile.id
     */
    @Id
    private Long id;

    /**
     * The foreign key is defined with on delete cascade so wheneven files are removed entries in this table shall be
     * removed as well.
     */
    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    @MapsId
    @JoinColumn(name = "id")
    private UploadedFile uploadedFile;

    @Column(name = "document_name")
    private String documentName;

    @ManyToOne(fetch = FetchType.LAZY)
    private AccountHolder accountHolder;
}
