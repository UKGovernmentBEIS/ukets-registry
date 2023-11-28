import { createFeatureSelector, createSelector, select } from '@ngrx/store';
import {
  ReconciliationState,
  reconciliationFeatureKey
} from '@reconciliation-administration/reducers/reconciliation.reducer';

const selectReconciliationState = createFeatureSelector<ReconciliationState>(
  reconciliationFeatureKey
);

export const selectLastStartedReconciliation = createSelector(
  selectReconciliationState,
  state => state.lastStartedReconciliation
);
