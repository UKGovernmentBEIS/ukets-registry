package gov.uk.ets.registry.api.account.domain.types;

/**
 * Enumerates the regulator types.
 */
public enum RegulatorType {

    /**
     * Environment Agency (EA)
     */
    EA,

    /**
     * Natural Resources Wales (NRW)
     */
    NRW,

    /**
     * Scottish Environment Protection Agency (SEPA)
     */
    SEPA,

    /**
     * Department of Agriculture, Environment and Rural Affairs (DAERA)
     */
    DAERA,

    /**
     * Offshore Petroleum Regulator for Environment and Decommissioning (OPRED)
     */
    OPRED,

    @Deprecated
    BEIS_OPRED
}
