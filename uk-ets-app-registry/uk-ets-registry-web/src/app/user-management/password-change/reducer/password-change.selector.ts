import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  passwordChangeFeatureKey,
  PasswordChangeState
} from '@password-change/reducer/password-change.reducer';

const selectPasswordChangeState = createFeatureSelector<PasswordChangeState>(
  passwordChangeFeatureKey
);

export const selectEmail = createSelector(
  selectPasswordChangeState,
  state => state.email
);
