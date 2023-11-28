import { createFeatureSelector, createSelector } from '@ngrx/store';
import { UploadStatus } from '@shared/model/file';
import {
  emissionsTableReducerFeatureKey,
  EmissionsTableState,
} from './emissions-table.reducer';

export * from './emissions-table.reducer';

const selectEmissionsTableState = createFeatureSelector<EmissionsTableState>(
  emissionsTableReducerFeatureKey
);

export const selectEmissionsTableFile = createSelector(
  selectEmissionsTableState,
  (state) => state.fileHeader
);

export const selectEmissionsTableRequestId = createSelector(
  selectEmissionsTableState,
  (state) => state.requestId
);

export const selectUploadFileProgress = createSelector(
  selectEmissionsTableState,
  (state) => state.progress
);

export const selectUploadFileIsInProgress = createSelector(
  selectEmissionsTableState,
  (state) => state.status === UploadStatus.Started && state.progress >= 0
);
