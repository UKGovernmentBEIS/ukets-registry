import {
  AccountAllocationStatus,
  UpdateAllocationStatusRequest
} from '@account-management/account/allocation-status/model';
import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import {
  continueToUpdateRequestVerification,
  fetchAllocationStatus,
  fetchAllocationStatusSuccess,
  resetAllocationStatusState,
  updateAllocationStatusSuccess
} from '@account-management/account/allocation-status/actions/allocation-status.actions';

export const allocationStatusFeatureKey = 'allocation-status';

export interface AllocationStatusState {
  annualAllocationStatusesLoaded: boolean;
  updatedAccountId: string;
  annualAllocationStatuses: AccountAllocationStatus;
  updateRequest: UpdateAllocationStatusRequest;
}

export const initialState: AllocationStatusState = {
  annualAllocationStatusesLoaded: false,
  updatedAccountId: null,
  annualAllocationStatuses: null,
  updateRequest: null
};

const allocationStatusReducer = createReducer(
  initialState,
  mutableOn(fetchAllocationStatus, state => {
    state.annualAllocationStatusesLoaded = false;
  }),
  mutableOn(fetchAllocationStatusSuccess, (state, { allocationStatus }) => {
    state.annualAllocationStatuses = allocationStatus;
    state.annualAllocationStatusesLoaded = true;
  }),
  mutableOn(
    continueToUpdateRequestVerification,
    (state, { updateAllocationStatusRequest }) => {
      state.updateRequest = updateAllocationStatusRequest;
    }
  ),
  mutableOn(updateAllocationStatusSuccess, (state, { updatedAccountId }) => {
    state.updatedAccountId = updatedAccountId;
  }),
  mutableOn(resetAllocationStatusState, state => {
    state.updatedAccountId = null;
    state.annualAllocationStatusesLoaded = false;
    state.annualAllocationStatusesLoaded = null;
    state.updateRequest = null;
  })
);

export function reducer(
  state: AllocationStatusState | undefined,
  action: Action
) {
  return allocationStatusReducer(state, action);
}
