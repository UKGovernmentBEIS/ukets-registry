package gov.uk.ets.registry.api.account.web.model;

import static gov.uk.ets.commons.logging.RequestParamType.COMPLIANT_ENTITY_ID;

import java.io.Serializable;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import gov.uk.ets.commons.logging.MDCParam;
import gov.uk.ets.registry.api.account.domain.types.InstallationActivityType;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * The installation / aircraft / maritime operator transfer object.
 */
@Getter
@Setter
@EqualsAndHashCode
public class OperatorDTO implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = -588778283641553229L;

    /**
     * The type.
     */
    @NotNull
    String type;

    @MDCParam(COMPLIANT_ENTITY_ID)
    Long identifier;

    /**
     * The name.
     */
    @Size(max = 256, message = "Installation name must not exceed 256 characters.")
    String name;


    /**
     * The regulator.
     */
    RegulatorType regulator;

    /**
     * The changed regulator. Inside the account opening task details the task asignee can change the regulator.
     * Upon approval this has to replace the original regulator set by the task initiator.
     */
    RegulatorType changedRegulator;

    /**
     * The first year.
     */
    @Digits(integer = 4, fraction = 0, message = "Not a valid year.")
    Integer firstYear;

    /**
     * The last year.
     */
    @Digits(integer = 4, fraction = 0, message = "Not a valid year.")
    Integer lastYear;

    /**
     * Flag to determine if the last year value has changed and is used in the
     * request operator update wizard
     */
    Boolean lastYearChanged;

    /**
     * The activity type. Installation only
     */
    InstallationActivityType activityType;

    /**
     * The permit. Installation only.
     */
    @Valid
    PermitDTO permit;

    /**
     * The monitoring plan. Aircraft operator only..
     */
    @Valid
    MonitoringPlanDTO monitoringPlan;
    
    /**
     * The acquiring account holder Identifier. 
     * It is used in the validation of an Account Opening with Installation Transfer.
     */
    Long acquiringAccountHolderIdentifier;

    /**
     * The IMO number for Matitime Operators
     */
    String imo;

    /**
     * The emitter id
     */
    String emitterId;
}
