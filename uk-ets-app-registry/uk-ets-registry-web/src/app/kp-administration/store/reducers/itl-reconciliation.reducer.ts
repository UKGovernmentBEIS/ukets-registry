import { Reconciliation } from '@shared/model/reconciliation-model';
import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import { updateLatestKpReconciliation } from '../actions';

export interface ReconciliationState {
  latestReconciliation: Reconciliation;
}

export const initialState: ReconciliationState = {
  latestReconciliation: undefined,
};

export const itlReconciliationReducer = createReducer(
  initialState,
  mutableOn(updateLatestKpReconciliation, (state, { reconciliation }) => {
    state.latestReconciliation = reconciliation;
  })
);

export function reducer(
  state: ReconciliationState | undefined,
  action: Action
) {
  return itlReconciliationReducer(state, action);
}
