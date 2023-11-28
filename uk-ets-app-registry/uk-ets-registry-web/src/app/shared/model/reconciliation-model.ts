import { Status } from '@shared/model/status';

export interface Reconciliation {
  identifier: number | string;
  created: string;
  updated: string;
  status: string | ItlReconciliationStatus;
  readOnly?: boolean;
}

export type ItlReconciliationStatus =
  | 'CONFIRMED'
  | 'INITIATED'
  | 'VALIDATED'
  | 'ITL_TOTAL_INCON'
  | 'ITL_UNIT_INCON'
  | 'ITL_COMPLETED'
  | 'ITL_COMPLETED_MAN_INT';

export const reconciliationStatusMap: Record<
  ItlReconciliationStatus,
  Status
> = {
  CONFIRMED: { color: 'grey', label: 'CONFIRMED' },
  INITIATED: { color: 'blue', label: 'INITIATED' },
  VALIDATED: { color: 'blue', label: 'VALIDATED' },
  ITL_TOTAL_INCON: { color: 'red', label: 'ITL_TOTAL_INCONSISTENT' },
  ITL_UNIT_INCON: { color: 'red', label: 'ITL_UNIT_INCONSISTENT' },
  ITL_COMPLETED: { color: 'green', label: 'ITL_COMPLETED' },
  ITL_COMPLETED_MAN_INT: {
    color: 'orange',
    label: 'ITL_COMPLETED_MANUAL_INTERVENTION',
  },
};
