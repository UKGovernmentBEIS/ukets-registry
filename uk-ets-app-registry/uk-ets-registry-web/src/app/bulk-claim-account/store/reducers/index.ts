import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  bulkClaimAccountReducerFeatureKey,
  BulkClaimAccountState,
} from './bulk-claim-account.reducer';

export * from './bulk-claim-account.reducer';

const selectBulkClaimAccountState =
  createFeatureSelector<BulkClaimAccountState>(
    bulkClaimAccountReducerFeatureKey
  );

export const selectNumberAffectedAccounts = createSelector(
  selectBulkClaimAccountState,
  (state) => state.numberOfAffectedAccounts
);
