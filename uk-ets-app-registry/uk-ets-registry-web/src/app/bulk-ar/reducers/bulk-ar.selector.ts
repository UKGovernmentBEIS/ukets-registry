import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  bulkArReducerFeatureKey,
  BulkArState,
} from '@registry-web/bulk-ar/reducers/bulk-ar.reducer';
import { UploadStatus } from '@shared/model/file';

const selectBulkArState = createFeatureSelector<BulkArState>(
  bulkArReducerFeatureKey
);

export const selectBulkArFile = createSelector(
  selectBulkArState,
  (state) => state.fileHeader
);

export const selectBulkArRequestId = createSelector(
  selectBulkArState,
  (state) => state.requestId
);

export const selectBulkArFileUploadProgress = createSelector(
  selectBulkArState,
  (state) => state.progress
);

export const selectBulkArFileUploadIsInProgress = createSelector(
  selectBulkArState,
  (state) => state.status === UploadStatus.Started && state.progress >= 0
);
