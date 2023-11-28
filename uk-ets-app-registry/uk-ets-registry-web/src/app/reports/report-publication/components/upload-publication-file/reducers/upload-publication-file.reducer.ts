import { FileBase, UploadStatus } from '@shared/model/file';
import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import {
  processSelectedFileError,
  uploadReportPublicationFileSuccess,
  uploadSelectedFileHasStarted,
  uploadSelectedFileInProgress,
} from '@shared/file/actions/file-upload-api.actions';
import {
  cancelPublicationFileWizard,
  clearPublicationFileWizard,
  submitFileYear,
  submitPublicationFileRequestSuccess,
} from '@reports/report-publication/components/upload-publication-file/actions/upload-publication-file.actions';

export const publicationReportFileReducerFeatureKey = 'publication-report-file';

export interface PublicationReportFileState {
  fileHeader: FileBase;
  status: UploadStatus;
  progress: number | null;
  fileYear: number | null;
}

const initialState: PublicationReportFileState = getInitialState();

const publicationReportFileReducer = createReducer(
  initialState,
  mutableOn(uploadSelectedFileHasStarted, (state, { status }) => {
    state.progress = 0;
    state.status = status;
  }),
  mutableOn(uploadSelectedFileInProgress, (state, { progress }) => {
    state.progress = progress;
  }),
  mutableOn(uploadReportPublicationFileSuccess, (state, { fileHeader }) => {
    state.fileHeader = fileHeader;
    state.progress = 100;
    state.status = UploadStatus.Completed;
  }),
  mutableOn(processSelectedFileError, (state) => {
    state.status = UploadStatus.Failed;
  }),
  mutableOn(cancelPublicationFileWizard, (state) => {
    resetState(state);
  }),
  mutableOn(clearPublicationFileWizard, (state) => {
    resetState(state);
  }),
  mutableOn(submitPublicationFileRequestSuccess, (state) => {
    state.status = UploadStatus.Completed;
  }),
  mutableOn(submitFileYear, (state, { fileYear }) => {
    state.fileYear = fileYear;
  })
);

export function reducer(state: PublicationReportFileState, action: Action) {
  return publicationReportFileReducer(state, action);
}

function resetState(state) {
  state.fileHeader = getInitialState().fileHeader;
  state.status = getInitialState().status;
  state.progress = getInitialState().progress;
  state.fileYear = getInitialState().fileYear;
}

function getInitialState(): PublicationReportFileState {
  return {
    fileHeader: {
      id: null,
      fileName: null,
      fileSize: null,
    },
    status: UploadStatus.Ready,
    progress: null,
    fileYear: null,
  };
}
