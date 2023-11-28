import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  recalculateComplianceStatusReducerFeatureKey,
  RecalculateComplianceStatusState,
} from './recalculate-compliance-status.reducer';

export * from './recalculate-compliance-status.reducer';

const selectRecalculateComplianceStatusState =
  createFeatureSelector<RecalculateComplianceStatusState>(
    recalculateComplianceStatusReducerFeatureKey
  );

export const selectRecalculateComplianceRequestStatus = createSelector(
  selectRecalculateComplianceStatusState,
  (state) => state.status
);
