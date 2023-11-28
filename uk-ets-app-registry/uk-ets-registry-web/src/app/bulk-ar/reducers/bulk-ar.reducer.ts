import { FileBase, UploadStatus } from "@shared/model/file";
import { Action, createReducer } from "@ngrx/store";
import { mutableOn } from "@shared/mutable-on";
import {
  processSelectedFileError,
  uploadBulkARFileSuccess,
  uploadSelectedFileHasStarted,
  uploadSelectedFileInProgress
} from "@shared/file/actions/file-upload-api.actions";
import {
  cancelBulkArWizard,
  clearBulkArWizard,
  submitBulkArRequestSuccess
} from "@registry-web/bulk-ar/actions/bulk-ar.actions";

export const bulkArReducerFeatureKey = 'bulk-ar';

export interface BulkArState {
  fileHeader: FileBase;
  status: UploadStatus;
  progress: number | null;
  requestId: string;
}

const initialState: BulkArState = getInitialState();

const bulkArReducer = createReducer(
  initialState,
  mutableOn(uploadSelectedFileHasStarted, (state, { status }) => {
    state.progress = 0;
    state.status = status;
  }),
  mutableOn(uploadSelectedFileInProgress, (state, { progress }) => {
    state.progress = progress;
  }),
  mutableOn(uploadBulkARFileSuccess, (state, { fileHeader }) => {
    state.fileHeader = fileHeader;
    state.progress = 100;
    state.status = UploadStatus.Completed;
  }),
  mutableOn(processSelectedFileError, (state) => {
    state.status = UploadStatus.Failed;
  }),
  mutableOn(cancelBulkArWizard, (state) => {
    resetState(state);
  }),
  mutableOn(clearBulkArWizard, (state) => {
    resetState(state);
  }),
  mutableOn(submitBulkArRequestSuccess, (state, { requestId }) => {
    state.requestId = requestId;
    state.status = UploadStatus.Completed;
  })
);

export function reducer(state: BulkArState, action: Action) {
  return bulkArReducer(state, action);
}

function resetState(state) {
  state.fileHeader = getInitialState().fileHeader;
  state.status = getInitialState().status;
  state.progress = getInitialState().progress;
  state.result = getInitialState().requestId;
}

function getInitialState(): BulkArState {
  return {
    fileHeader: {
      id: null,
      fileName: null,
      fileSize: null,
    },
    status: UploadStatus.Ready,
    progress: null,
    requestId: null,
  };
}
