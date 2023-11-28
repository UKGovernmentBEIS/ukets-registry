import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import { Draft } from 'immer';
import { UpdateExclusionStatusActions } from '@registry-web/account-management/account/exclusion-status-update-wizard/actions';
import { VerifiedEmissions } from '@registry-web/account-shared/model';

export const updateExclusionStatusFeatureKey = 'update-exclusion-status';

export interface UpdateExclusionStatusState {
  emissionEntries: VerifiedEmissions[];
  year: number;
  excluded: boolean;
}

export const initialState: UpdateExclusionStatusState = {
  emissionEntries: null,
  year: null,
  excluded: null,
};

const updateExclusionStatusReducer = createReducer(
  initialState,
  mutableOn(
    UpdateExclusionStatusActions.setCurrentAccountEmissionDetailsSuccess,
    (state: Draft<UpdateExclusionStatusState>, { entries }) => {
      state.emissionEntries = entries;
    }
  ),
  mutableOn(
    UpdateExclusionStatusActions.setExclusionYear,
    (state: Draft<UpdateExclusionStatusState>, { year }) => {
      state.year = year;
    }
  ),
  mutableOn(
    UpdateExclusionStatusActions.setExclusionStatus,
    (state: Draft<UpdateExclusionStatusState>, { excluded }) => {
      state.excluded = excluded;
    }
  ),
  mutableOn(
    UpdateExclusionStatusActions.clearUpdateExclusionStatus,
    (state) => {
      resetState(state);
    }
  )
);

export function reducer(
  state: UpdateExclusionStatusState | undefined,
  action: Action
) {
  return updateExclusionStatusReducer(state, action);
}

function resetState(state) {
  state.emissionEntries = initialState.emissionEntries;
  state.year = initialState.year;
  state.excluded = initialState.excluded;
}
