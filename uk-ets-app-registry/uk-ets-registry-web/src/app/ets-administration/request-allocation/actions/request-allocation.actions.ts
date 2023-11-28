import { createAction, props } from '@ngrx/store';
import { AllocationCategory } from '@registry-web/shared/model/allocation';
import { BusinessCheckResult } from '@shared/model/transaction';

export const selectAllocationYear = createAction(
  '[Request Allocation] Select Allocation Year',
  props<{ year: number }>()
);

export const selectAllocationCategory = createAction(
  '[Request Allocation] Select Allocation Category',
  props<{ category: AllocationCategory }>()
);

export const cancelRequestAllocationRequested = createAction(
  '[Request Allocation] Cancel Request Allocation Requested'
);

export const cancelRequestAllocationConfirmed = createAction(
  '[Request Allocation] Cancel Request Allocation Confirmed'
);

export const submitAllocationRequest = createAction(
  '[Request Allocation] Submit Allocation Request'
);

export const submitAllocationRequestSuccess = createAction(
  '[Request Allocation] Submit Allocation Request Success',
  props<{ businessCheckResult: BusinessCheckResult }>()
);

export const downloadAllocationFile = createAction(
  '[Request Allocation] Download allocation file'
);

export const isPendingAllocationsRequested = createAction(
  '[Request Allocation] Is Pending Allocations Requested'
);

export const isPendingAllocationsRequestedSuccess = createAction(
  '[Request Allocation] Is Pending Allocations Requested SUCCESS',
  props<{ isPending: boolean }>()
);

export const isPendingAllocationsRequestedFailure = createAction(
  '[Request Allocation] Is Pending Allocations Requested FAILURE',
  props<{ error: any }>()
);

export const cancelPendingAllocationsRequested = createAction(
  '[Request Allocation] Cancel Pending Allocations Requested'
);

export const cancelPendingAllocationsRequestedSuccess = createAction(
  '[Request Allocation] Cancel Pending Allocations Requested SUCCESS'
);

export const cancelPendingAllocationsRequestedFailure = createAction(
  '[Request Allocation] Cancel Pending Allocations Requested FAILURE'
);

export const cancelPendingAllocationsMessageTimeout = createAction(
  '[Request Allocation] Cancel Pending Allocations Request Message Timeout'
);
