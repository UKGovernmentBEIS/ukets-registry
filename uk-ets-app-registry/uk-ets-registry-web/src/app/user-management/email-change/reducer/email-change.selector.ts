import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  emailChangeFeatureKey,
  EmailChangeState
} from '@email-change/reducer/email-change.reducer';

const selectEmailChangeState = createFeatureSelector<EmailChangeState>(
  emailChangeFeatureKey
);

export const selectState = createSelector(
  selectEmailChangeState,
  state => state
);

export const selectNewEmail = createSelector(
  selectEmailChangeState,
  state => state.newEmail
);

export const selectConfirmationLoaded = createSelector(
  selectEmailChangeState,
  state => state.confirmationLoaded
);

export const selectConfirmation = createSelector(
  selectEmailChangeState,
  state => state.confirmation
);
