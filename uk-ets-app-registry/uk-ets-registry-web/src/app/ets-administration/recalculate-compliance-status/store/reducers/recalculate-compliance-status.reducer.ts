import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import {
  clearRecalculatioComplianceStatuAllCompliantEntities,
  startRecalculatioComplianceStatuAllCompliantEntitiesSuccess,
} from '@recalculate-compliance-status/store/actions';
import { RecalculateComplianceRequestStatus } from '@recalculate-compliance-status/model';

export const recalculateComplianceStatusReducerFeatureKey =
  'recalculate-compliance-status';

export interface RecalculateComplianceStatusState {
  status: RecalculateComplianceRequestStatus;
  progress: number | null;
}

export const initialState: RecalculateComplianceStatusState = getInitialState();

const recalculateComplianceStatusReducer = createReducer(
  initialState,
  mutableOn(clearRecalculatioComplianceStatuAllCompliantEntities, (state) => {
    resetState(state);
  }),
  mutableOn(
    startRecalculatioComplianceStatuAllCompliantEntitiesSuccess,
    (state) => {
      state.status = 'SUBMITTED';
    }
  )
);

export function reducer(
  state: RecalculateComplianceStatusState | undefined,
  action: Action
) {
  return recalculateComplianceStatusReducer(state, action);
}

function resetState(state) {
  state.status = getInitialState().status;
  state.progress = getInitialState().progress;
}

function getInitialState(): RecalculateComplianceStatusState {
  return {
    status: 'UNKNOWN',
    progress: null,
  };
}
