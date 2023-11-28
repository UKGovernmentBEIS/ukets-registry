import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  allocationStatusFeatureKey,
  AllocationStatusState
} from '@account-management/account/allocation-status/reducers/allocation-status.reducer';

const selectAllocationStatusState = createFeatureSelector<
  AllocationStatusState
>(allocationStatusFeatureKey);

export const areAnnualAllocationStatusesLoaded = createSelector(
  selectAllocationStatusState,
  state => state.annualAllocationStatusesLoaded
);

export const selectAnnualAllocationStatuses = createSelector(
  selectAllocationStatusState,
  state => state.annualAllocationStatuses
);

export const selectUpdateRequest = createSelector(
  selectAllocationStatusState,
  state => state.updateRequest
);

export const selectUpdatedAccountId = createSelector(
  selectAllocationStatusState,
  state => state.updatedAccountId
);
