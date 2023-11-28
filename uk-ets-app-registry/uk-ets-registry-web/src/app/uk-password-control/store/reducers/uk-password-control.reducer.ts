import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import * as PasswordStrengthActions from '@uk-password-control/store/actions';
import { Score } from '@uk-password-control/model';

export const passwordStrengthFeatureKey = 'password-strength';

export interface PasswordStrengthState {
  score: Score | null;
}

export const initialState: PasswordStrengthState = {
  score: null,
};

const passwordStrengthReducer = createReducer(
  initialState,
  mutableOn(
    PasswordStrengthActions.loadPasswordScoreSuccess,
    (state, { score }) => {
      state.score = score;
    }
  ),
  mutableOn(PasswordStrengthActions.clearPasswordStrength, (state) => {
    resetState(state);
  })
);

export function reducer(
  state: PasswordStrengthState | undefined,
  action: Action
) {
  return passwordStrengthReducer(state, action);
}

function resetState(state) {
  state.score = initialState.score;
}
