import { Action, createReducer } from '@ngrx/store';
import { Reconciliation } from '@shared/model/reconciliation-model';
import { mutableOn } from '@shared/mutable-on';
import { updateLatestReconciliation } from '@reconciliation-administration/actions/reconciliation.actions';
import { Draft } from 'immer';

export const reconciliationFeatureKey = 'reconciliation-reducer';

export interface ReconciliationState {
  lastStartedReconciliation: Reconciliation;
}

export const initialState: ReconciliationState = getInitialState();

const reconciliationReducer = createReducer(
  initialState,
  mutableOn(
    updateLatestReconciliation,
    (state: Draft<ReconciliationState>, { reconciliation }) => {
      state.lastStartedReconciliation = reconciliation;
    }
  )
);

export function reducer(
  state: ReconciliationState | undefined,
  action: Action
) {
  return reconciliationReducer(state, action);
}

function getInitialState(): ReconciliationState {
  return {
    lastStartedReconciliation: null,
  };
}
