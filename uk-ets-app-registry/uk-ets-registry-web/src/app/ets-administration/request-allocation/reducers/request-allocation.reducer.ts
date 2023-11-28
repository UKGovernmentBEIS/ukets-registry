import { Action, createReducer } from '@ngrx/store';
import * as RequestAllocationActions from '../actions/request-allocation.actions';
import { mutableOn } from '@shared/mutable-on';
import { Draft } from 'immer';
import { BusinessCheckResult } from '@shared/model/transaction';
import { AllocationCategory } from '@registry-web/shared/model/allocation';

export const requestAllocationFeatureKey = 'requestAllocation';

export interface RequestAllocationState {
  selectedAllocationYear;
  selectedAllocationCategory: AllocationCategory;
  businessCheckResult: BusinessCheckResult;
  isPendingAllocation: boolean;
  isCancelPendingAllocationsLoading: boolean;
  cancelPendingAllocationsResult: { success: boolean };
}

export const initialState: RequestAllocationState = {
  selectedAllocationYear: null,
  selectedAllocationCategory: null,
  businessCheckResult: null,
  isPendingAllocation: false,
  isCancelPendingAllocationsLoading: false,
  cancelPendingAllocationsResult: { success: null },
};

function resetState(state: Draft<RequestAllocationState>) {
  Object.keys(state).forEach((k) => (state[k] = initialState[k]));
}

export const requestAllocationReducer = createReducer(
  initialState,

  mutableOn(
    RequestAllocationActions.selectAllocationYear,
    (state, { year }) => {
      state.selectedAllocationYear = year;
    }
  ),
  mutableOn(
    RequestAllocationActions.selectAllocationCategory,
    (state, { category }) => {
      state.selectedAllocationCategory = category;
    }
  ),
  mutableOn(
    RequestAllocationActions.cancelRequestAllocationConfirmed,
    (state) => {
      resetState(state);
    }
  ),
  mutableOn(
    RequestAllocationActions.submitAllocationRequestSuccess,
    (state, { businessCheckResult }) => {
      resetState(state);
      state.businessCheckResult = businessCheckResult;
    }
  ),
  //Cancel Pending Allocations Actions
  mutableOn(
    RequestAllocationActions.cancelPendingAllocationsRequested,
    (state) => {
      state.isCancelPendingAllocationsLoading = true;
      state.cancelPendingAllocationsResult = {
        success: initialState.cancelPendingAllocationsResult.success,
      };
    }
  ),
  mutableOn(
    RequestAllocationActions.cancelPendingAllocationsRequestedSuccess,
    (state) => {
      state.isCancelPendingAllocationsLoading = false;
      state.cancelPendingAllocationsResult = { success: true };
    }
  ),
  mutableOn(
    RequestAllocationActions.cancelPendingAllocationsRequestedFailure,
    (state) => {
      state.isCancelPendingAllocationsLoading = false;
      state.cancelPendingAllocationsResult = { success: false };
    }
  ),
  mutableOn(
    RequestAllocationActions.cancelPendingAllocationsMessageTimeout,
    (state) => {
      state.isCancelPendingAllocationsLoading = false;
      state.cancelPendingAllocationsResult = {
        success: initialState.cancelPendingAllocationsResult.success,
      };
    }
  ),
  //Are there Pending Allocations Actions
  mutableOn(
    RequestAllocationActions.isPendingAllocationsRequestedSuccess,
    (state, { isPending }) => {
      state.isPendingAllocation = isPending;
    }
  )
);

export function reducer(
  state: RequestAllocationState | undefined,
  action: Action
) {
  return requestAllocationReducer(state, action);
}
