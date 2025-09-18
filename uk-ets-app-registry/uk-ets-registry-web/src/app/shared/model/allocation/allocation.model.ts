export interface AllocationOverviewRow {
  allocationType: AllocationType;
  accounts: number;
  quantity: number;
  excludedAccounts: number;
  withheldAccounts: number;
  closedAndFullySuspendedAccounts: number;
  transferPendingAccounts: number;
}

export enum AllocationType {
  NAT = 'NAT',
  NAVAT = 'NAVAT',
  NER = 'NER',
}

export const ALLOCATION_TYPE_LABELS: Record<
  AllocationType,
  { label: string; description?: string }
> = {
  NAT: { label: 'Installations - National Allocation Table (NAT)' },
  NAVAT: { label: 'Aviation - National Aviation Allocation Table (NAVAT)' },
  NER: { label: 'Installations - New Entrants Reserve (NER)' },
};

export interface AllocationOverview {
  year: number;
  category?: AllocationCategory;
  totalQuantity: number;
  total: AllocationOverviewRow;
  rows: Map<AllocationType, AllocationOverviewRow>;
}

export type ReturnExcessAllocationType =
  | 'NAT'
  | 'NER'
  | 'NAT_AND_NER'
  | 'NAVAT';

export enum AllocationCategory {
  Installation = 'INSTALLATION',
  AircraftOperator = 'AIRCRAFT_OPERATOR',
}

export const AllocationCategoryLabel = {
  [AllocationCategory.Installation]: 'Installations',
  [AllocationCategory.AircraftOperator]:
  'Aviation',
};
