import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  accountStatusFeatureKey,
  AccountStatusState
} from './account-status.reducer';

const selectAccountStatusState = createFeatureSelector<AccountStatusState>(
  accountStatusFeatureKey
);

export const selectAllowedAccountStatusActions = createSelector(
  selectAccountStatusState,
  state => state.allowedAccountStatusActions
);

export const selectAccountStatusAction = createSelector(
  selectAccountStatusState,
  state => state.accountStatusAction
);
