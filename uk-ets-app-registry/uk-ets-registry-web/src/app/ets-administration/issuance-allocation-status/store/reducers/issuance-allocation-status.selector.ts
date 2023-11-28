import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  IssuanceAllocationStatusState,
  issuanceAllocationStatusFeatureKey
} from './issuance-allocation-status.reducer';

const selectIssueAllocationStatusesState = createFeatureSelector<
  IssuanceAllocationStatusState
>(issuanceAllocationStatusFeatureKey);

export const selectIssuanceAllocationStatuses = createSelector(
  selectIssueAllocationStatusesState,
  state => state.issuanceAllocationStatuses
);

export const selectAllocationTableEventHistory = createSelector(
  selectIssueAllocationStatusesState,
  state => state.allocationTableEventHistory
);
