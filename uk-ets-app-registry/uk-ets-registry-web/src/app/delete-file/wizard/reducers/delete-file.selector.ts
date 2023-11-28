import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  deleteFileFeatureKey,
  DeleteFileState,
} from '@delete-file/wizard/reducers/delete-file.reducer';

const selectDeleteFileState = createFeatureSelector<DeleteFileState>(
  deleteFileFeatureKey
);

export const selectOriginatingPath = createSelector(
  selectDeleteFileState,
  (state) => state.originatingPath
);

export const selectId = createSelector(
  selectDeleteFileState,
  (state) => state.id
);

export const selectFile = createSelector(
  selectDeleteFileState,
  (state) => state.file
);

export const selectDocumentType = createSelector(
  selectDeleteFileState,
  (state) => state.documentsRequestType
);

export const selectFileName = createSelector(
  selectDeleteFileState,
  (state) => state.fileName
);
