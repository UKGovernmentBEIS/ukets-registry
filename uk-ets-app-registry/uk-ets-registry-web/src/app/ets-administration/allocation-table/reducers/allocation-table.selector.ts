import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  allocationTableReducerFeatureKey,
  AllocationTableState
} from '@allocation-table/reducers/allocation-table.reducer';
import { UploadStatus } from '@shared/model/file';

const selectAllocationTableState = createFeatureSelector<AllocationTableState>(
  allocationTableReducerFeatureKey
);

export const selectAllocationTableFile = createSelector(
  selectAllocationTableState,
  state => state.fileHeader
);

export const selectAllocationTableRequestId = createSelector(
  selectAllocationTableState,
  state => state.requestId
);

export const selectUploadFileProgress = createSelector(
  selectAllocationTableState,
  state => state.progress
);

export const selectUploadFileIsInProgress = createSelector(
  selectAllocationTableState,
  state => state.status === UploadStatus.Started && state.progress >= 0
);
