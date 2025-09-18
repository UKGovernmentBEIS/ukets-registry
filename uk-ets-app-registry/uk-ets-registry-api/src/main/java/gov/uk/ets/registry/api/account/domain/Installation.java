package gov.uk.ets.registry.api.account.domain;

import gov.uk.ets.registry.api.common.model.services.converter.StringTrimConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents an installation.
 */
@Getter
@Setter
@EqualsAndHashCode(of = "compliantEntity")
@Entity
@PrimaryKeyJoinColumn(name = "compliant_entity_id")
public class Installation extends CompliantEntity {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -7670556513289918396L;

    /**
     * The activity type.
     */
    @Column(name = "activity_type")
    private String activityType;

    /**
     * The installation name.
     */
    @Convert(converter = StringTrimConverter.class)
    @Column(name = "installation_name")
    private String installationName;

    /**
     * The permit identifier.
     */
    @Column(name = "permit_identifier")
    @Convert(converter = StringTrimConverter.class)
    private String permitIdentifier;

}