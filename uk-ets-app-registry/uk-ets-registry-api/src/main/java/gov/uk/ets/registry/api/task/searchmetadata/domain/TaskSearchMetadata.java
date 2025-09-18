package gov.uk.ets.registry.api.task.searchmetadata.domain;

import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.searchmetadata.domain.types.MetadataName;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the search metadata associated with a task.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"task", "metadataName", "metadataValue"})
public class TaskSearchMetadata {
	
	/**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "task_search_metadata_generator", sequenceName = "task_search_metadata_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_search_metadata_generator")
    private Long id;
    
    /**
     * The task associated with these metadata.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Task task;

    /**
     * Metadata name.
     */
    @Column(name = "metadata_name")
    @Enumerated(EnumType.STRING)
    private MetadataName metadataName;

    /**
     * Metadata value.
     */
    @Column(name = "metadata_value")
    private String metadataValue;
}
