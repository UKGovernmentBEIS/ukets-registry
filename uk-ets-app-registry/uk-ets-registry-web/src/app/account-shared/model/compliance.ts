import { Status } from '@shared/model/status';

export type ComplianceStatus =
  | 'A' // previously: COMPLIANT
  | 'B' // previously: NEEDS_TO_SURRENDER
  | 'C' // previously: NEEDS_TO_ENTER_EMISSIONS
  | 'EXCLUDED'
  | 'NOT_APPLICABLE' // previously: NOT_CALCULATED
  | 'ERROR';

export const complianceStatusMap: Record<ComplianceStatus, Status> = {
  A: { color: 'green', label: 'A' },
  B: { color: 'yellow', label: 'B' },
  C: {
    color: 'red',
    label: 'C',
  },
  EXCLUDED: { color: 'blue', label: 'Excluded' },
  NOT_APPLICABLE: { color: 'grey', label: 'Not currently applicable' },
  ERROR: { color: 'red', label: 'Error' },
};

export interface ComplianceStatusHistoryResult {
  readonly complianceStatusHistory: ComplianceStatusHistory[];
  readonly lastYearOfVerifiedEmissions?: number;
}

export interface ComplianceStatusHistory {
  readonly year: number;
  readonly status: ComplianceStatus;
}

export interface ComplianceOverviewResult {
  readonly totalVerifiedEmissions: number;
  readonly totalNetSurrenders: number;
  readonly currentComplianceStatus: ComplianceStatus;
}
