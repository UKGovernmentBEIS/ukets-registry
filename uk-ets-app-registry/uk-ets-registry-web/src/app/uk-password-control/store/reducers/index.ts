import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  passwordStrengthFeatureKey,
  PasswordStrengthState,
} from '@uk-password-control/store/reducers/uk-password-control.reducer';

export * from '@uk-password-control/store/reducers/uk-password-control.reducer';

const selectPasswordStrengthState = createFeatureSelector<PasswordStrengthState>(
  passwordStrengthFeatureKey
);

export const selectPasswordStrengthScore = createSelector(
  selectPasswordStrengthState,
  (state) => state.score
);
