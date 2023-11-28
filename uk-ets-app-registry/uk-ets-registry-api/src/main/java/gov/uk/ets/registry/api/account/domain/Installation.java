package gov.uk.ets.registry.api.account.domain;

import gov.uk.ets.registry.api.account.domain.types.PermitStatus;
import gov.uk.ets.registry.api.common.model.services.converter.StringTrimConverter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

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
     * The permit expiry date.
     */
    @Column(name = "permit_expiry_date")
    @Temporal(TemporalType.DATE)
    private Date permitExpiryDate;

    /**
     * The permit identifier.
     */
    @Column(name = "permit_identifier")
    @Convert(converter = StringTrimConverter.class)
    private String permitIdentifier;

    /**
     * The permit status.
     */
    @Column(name = "permit_status")
    @Enumerated(EnumType.STRING)
    private PermitStatus permitStatus;

}