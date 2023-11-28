import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  publicationReportFileReducerFeatureKey,
  PublicationReportFileState,
} from '@reports/report-publication/components/upload-publication-file/reducers/upload-publication-file.reducer';
import { UploadStatus } from '@shared/model/file';

const selectPublicationReportState =
  createFeatureSelector<PublicationReportFileState>(
    publicationReportFileReducerFeatureKey
  );

export const selectPublicationReportFile = createSelector(
  selectPublicationReportState,
  (state) => state.fileHeader
);

export const selectPublicationReportFileYear = createSelector(
  selectPublicationReportState,
  (state) => state.fileYear
);

export const selectPublicationReportFileUploadProgress = createSelector(
  selectPublicationReportState,
  (state) => state.progress
);

export const selectPublicationReportFileUploadIsInProgress = createSelector(
  selectPublicationReportState,
  (state) => state.status === UploadStatus.Started && state.progress >= 0
);
