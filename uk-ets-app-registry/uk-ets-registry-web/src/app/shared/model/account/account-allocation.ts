export enum AllocationStatus {
  ALLOWED = 'ALLOWED',
  WITHHELD = 'WITHHELD',
}

export const ALLOCATION_STATUS_LABELS: Record<
  AllocationStatus,
  { label: string; description?: string }
> = {
  ALLOWED: { label: 'Allowed' },
  WITHHELD: { label: 'Withheld' },
};

export interface AnnualAllocation {
  year: number;
  entitlement: number;
  allocated: number;
  remaining: number;
  status: AllocationStatus;
  eligibleForReturn: boolean;
  excluded: boolean;
}

export interface AllocationTotals {
  entitlement: number;
  allocated: number;
  remaining: number;
}

export interface AggregatedAllocation {
  annuals: AnnualAllocation[];
  totals: AllocationTotals;
}

export interface GroupedAllocationOverview {
  groupedAllocations: GroupedAllocation[];
  totals: AllocationTotals;
  allocationClassification?: AllocationStatus;
}

export interface GroupedAllocation {
  summedAnnualAllocationStandardAndNer: AnnualAllocation;
  standardAnnualAllocation: AnnualAllocation;
  nerAnnualAllocation: AnnualAllocation;
  allocationClassification?: AllocationStatus;
}

export interface AccountAllocation {
  standard: AggregatedAllocation;
  underNewEntrantsReserve: AggregatedAllocation;
  groupedAllocations: GroupedAllocation[];
  totals: AllocationTotals;
  allocationClassification?: AllocationStatus;
}
