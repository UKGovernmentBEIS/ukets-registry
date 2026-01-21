package gov.uk.ets.registry.api.integration.changelog.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class IntegrationChangeLog {

    /**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "integration_change_log_id_generator", sequenceName = "integration_change_log_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "integration_change_log_id_generator")
    private Long id;

    /**
     * The modified field.
     */
    private String fieldChanged;

    /**
     * The old value of the field.
     */
    private String oldValue;

    /**
     * The new value of the field.
     */
    private String newValue;

    /**
     * The entity in which the change is applied (DB table).
     */
    private String entity;

    /**
     * The id of the entity.
     */
    private Long entityId;

    /**
     * The account full identifier.
     */
    private String accountNumber;

    /**
     * The operator identifier.
     */
    private Long operatorId;

    /**
     * The date of the update.
     */
    private Date updatedAt;

    /**
     * The integration source system.
     */
    private String updatedBy;
}
