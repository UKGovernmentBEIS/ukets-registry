import { AllocationStatus } from '@shared/model/account';

export interface AccountAllocationStatus {
  [key: string]: AllocationStatus;
}

export interface UpdateAllocationStatusRequest {
  changedStatus: AccountAllocationStatus;
  justification: string;
}
