package gov.uk.ets.registry.api.allocation.type;

public enum AllocationClassification {

    NOT_YET_ALLOCATED,

    UNDER_ALLOCATED,

    OVER_ALLOCATED,

    @Deprecated
    /*
      This type may be removed in the future as the only {@link AllocationType#values()  allocationTypes} available for
      now are NAT, NER and NAVAT. They are coped with, as independent allocation types and we do not demand
      to filter about allocation status indiscreetly. We compute the total remaining quantity by summing it and we induce the
      {@link AllocationClassification allocation classification}
     */
    MULTIPLE,

    FULLY_ALLOCATED;
}
