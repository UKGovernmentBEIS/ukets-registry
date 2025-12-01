package gov.uk.ets.registry.api.account.domain;

import gov.uk.ets.registry.api.common.model.services.converter.StringTrimConverter;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

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
     * The activity types.
     */
    @OneToMany(mappedBy = "installation", fetch = FetchType.LAZY)
    private Set<ActivityType> activityTypes;

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