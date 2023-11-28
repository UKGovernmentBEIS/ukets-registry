import { createFeatureSelector, createSelector } from '@ngrx/store';
import { TimeoutState } from '@registry-web/timeout/store/timeout.reducer';

const selectShared = createFeatureSelector<TimeoutState>('timeout');

export const selectIsTimeoutDialogVisible = createSelector(
  selectShared,
  (state) => state.isTimeoutDialogVisible
);
