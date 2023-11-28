import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import * as IssueAllowanceStatusActions from '../actions/issuance-allocation-status.actions';

import { AllowanceReport } from '../../model';
import { DomainEvent } from '@shared/model/event';

export const issuanceAllocationStatusFeatureKey = 'issuance-allocation-status';

export interface IssuanceAllocationStatusState {
  issuanceAllocationStatuses: AllowanceReport[];
  allocationTableEventHistory: DomainEvent[];
}

export const initialState: IssuanceAllocationStatusState = getInitialState();

const issuanceAllocationStatusReducer = createReducer(
  initialState,
  mutableOn(
    IssueAllowanceStatusActions.loadIssuanceAllocationStatusSuccess,
    (state, { results }) => {
      state.issuanceAllocationStatuses = results;
    }
  ),
  mutableOn(
    IssueAllowanceStatusActions.loadAllocationTableEventHistorySuccess,
    (state, { results }) => {
      state.allocationTableEventHistory = results;
    }
  )
);

export function reducer(
  state: IssuanceAllocationStatusState | undefined,
  action: Action
) {
  return issuanceAllocationStatusReducer(state, action);
}

function getInitialState(): IssuanceAllocationStatusState {
  return {
    issuanceAllocationStatuses: [],
    allocationTableEventHistory: []
  };
}
