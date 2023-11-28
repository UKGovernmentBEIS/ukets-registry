import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  requestAllocationFeatureKey,
  RequestAllocationState,
} from './request-allocation.reducer';

export const selectRequestAllocationState =
  createFeatureSelector<RequestAllocationState>(requestAllocationFeatureKey);

export const selectedAllocationYear = createSelector(
  selectRequestAllocationState,
  (state) => state.selectedAllocationYear
);

export const selectedAllocationCategory = createSelector(
  selectRequestAllocationState,
  (state) => state.selectedAllocationCategory
);

export const businessCheckResult = createSelector(
  selectRequestAllocationState,
  (state) => state.businessCheckResult
);

export const isAllocationPending = createSelector(
  selectRequestAllocationState,
  (state) => state.isPendingAllocation
);

export const isCancelPendingAllocationsLoading = createSelector(
  selectRequestAllocationState,
  (state) => state.isCancelPendingAllocationsLoading
);

export const cancelPendingAllocationsResult = createSelector(
  selectRequestAllocationState,
  (state) => state.cancelPendingAllocationsResult
);

export const isCancelAllocationSuccessAndPendingAllocationsNotLoading =
  createSelector(
    selectRequestAllocationState,
    (state) =>
      state?.cancelPendingAllocationsResult?.success &&
      !state.isCancelPendingAllocationsLoading
  );
export const isAllocationPendingAndNoNotification = createSelector(
  isAllocationPending,
  isCancelAllocationSuccessAndPendingAllocationsNotLoading,
  (allocationPending, cancelAllocationSuccess) =>
    allocationPending && !cancelAllocationSuccess
);
