import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  accountFeatureKey,
  AccountState,
} from '@account-management/account/account-details/account.reducer';

const selectAccountState = createFeatureSelector<AccountState>(
  accountFeatureKey
);

export const selectCompliantEntityIdentifier = createSelector(
  selectAccountState,
  (state) => state.account.operator?.identifier
);

export const selectVerifiedEmissions = createSelector(
  selectAccountState,
  (state) => state.verifiedEmissions
);

export const selectComplianceStatusHistory = createSelector(
  selectAccountState,
  (state) => state.complianceStatusHistory
);

export const selectComplianceOverview = createSelector(
  selectAccountState,
  (state) => state.complianceOverview
);

export const selectComplianceHistoryComments = createSelector(
  selectAccountState,
  (state) => state.complianceHistoryComments
);
