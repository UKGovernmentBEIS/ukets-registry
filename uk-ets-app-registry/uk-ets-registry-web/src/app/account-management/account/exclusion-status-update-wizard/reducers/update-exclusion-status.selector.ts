import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  updateExclusionStatusFeatureKey,
  UpdateExclusionStatusState,
} from './update-exclusion-status.reducer';

const selectUpdateExclusionStatusState = createFeatureSelector<UpdateExclusionStatusState>(
  updateExclusionStatusFeatureKey
);

export const selectCurrentAccountEmissionDetails = createSelector(
  selectUpdateExclusionStatusState,
  (state) => state.emissionEntries
);

export const selectExclusionYear = createSelector(
  selectUpdateExclusionStatusState,
  (state) => state.year
);

export const selectExclusionStatus = createSelector(
  selectUpdateExclusionStatusState,
  (state) => state.excluded
);
