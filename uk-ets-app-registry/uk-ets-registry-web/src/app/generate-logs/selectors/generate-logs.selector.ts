import { createFeatureSelector, createSelector } from '@ngrx/store';
import { GenerateLogsReducer } from '../reducers';

export const selectGenerateLogsState =
  createFeatureSelector<GenerateLogsReducer.LoggingState>(
    GenerateLogsReducer.generateLogsFeatureKey
  );

export const selectGeneratedLogs = createSelector(
  selectGenerateLogsState,
  (state) => state.logs
);

export const selectCorrelations = createSelector(
  selectGenerateLogsState,
  (state) => state.apiRequestCorrelations
);
