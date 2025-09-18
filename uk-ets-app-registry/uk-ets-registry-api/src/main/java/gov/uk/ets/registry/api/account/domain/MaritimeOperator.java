package gov.uk.ets.registry.api.account.domain;

import gov.uk.ets.registry.api.common.model.services.converter.StringTrimConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * Represents a maritime operator.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "compliantEntity")
@Table(name = "maritime_operator")
@PrimaryKeyJoinColumn(name = "compliant_entity_id")
public class MaritimeOperator extends CompliantEntity {

    @Serial
    private static final long serialVersionUID = 5194492614945344576L;

    /**
     * The monitoring plan identifier.
     */
    @Column(name = "maritime_monitoring_plan_identifier")
    @Convert(converter = StringTrimConverter.class)
    private String maritimeMonitoringPlanIdentifier;

    /**
     * The Company IMO number
     */
    @Column(name = "imo")
    private String imo;

}