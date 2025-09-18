import { Status } from '@registry-web/shared/model/status';

export type AllocationJobSearchCriteria = {
  id: number;
  requestIdentifier: number;
  executionDateTo: Date;
  executionDateFrom: Date;
  allocationStatus: AllocationJobStatus;
};

export enum AllocationJobStatus {
  SCHEDULED = 'SCHEDULED',
  RUNNING = 'RUNNING',
  FAILED = 'FAILED',
  COMPLETED = 'COMPLETED',
  COMPLETED_WITH_FAILURES = 'COMPLETED_WITH_FAILURES',
  CANCELLED = 'CANCELLED',
}

export const ALLOCATION_JOB_STATUS_OPTIONS = [
  {
    label: '',
    value: null,
  },
  {
    label: 'Scheduled',
    value: 'SCHEDULED',
  },
  {
    label: 'Running',
    value: 'RUNNING',
  },
  {
    label: 'Failed',
    value: 'FAILED',
  },
  {
    label: 'Completed',
    value: 'COMPLETED',
  },
  {
    label: 'Completed with failures',
    value: 'COMPLETED_WITH_FAILURES',
  },
  {
    label: 'Cancelled',
    value: 'CANCELLED',
  },
];

export const allocationJobStatusMap: Record<AllocationJobStatus, Status> = {
  SCHEDULED: { color: 'yellow', label: 'Scheduled' },
  RUNNING: { color: 'yellow', label: 'Running' },
  FAILED: { color: 'red', label: 'Failed' },
  COMPLETED: { color: 'green', label: 'Completed' },
  COMPLETED_WITH_FAILURES: {
    color: 'orange',
    label: 'Completed with failures',
  },
  CANCELLED: { color: 'pink', label: 'Cancelled' },
};
