package gov.uk.ets.registry.api.document.management.domain;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table
public class Document {

    @Id
    @SequenceGenerator(name = "document_id_generator", sequenceName = "document_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "document_id_generator")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_category_id")
    private DocumentCategory category;

    private String title;

    private String name;

    @Basic(fetch = FetchType.LAZY)
    private byte[] data;

    private Integer position;

    private Date createdOn;

    private Date updatedOn;
}
