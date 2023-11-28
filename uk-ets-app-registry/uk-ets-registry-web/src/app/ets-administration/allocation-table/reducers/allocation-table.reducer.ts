import { FileBase, UploadStatus } from "@shared/model/file";
import { Action, createReducer } from "@ngrx/store";
import { mutableOn } from "@shared/mutable-on";
import {
  processSelectedFileError,
  uploadAllocationTableFileSuccess,
  uploadSelectedFileHasStarted,
  uploadSelectedFileInProgress
} from "@shared/file/actions/file-upload-api.actions";
import {
  cancelAllocationTableWizard,
  clearAllocationTableWizard,
  submitAllocationTableRequestSuccess
} from "@allocation-table/actions/allocation-table.actions";

export const allocationTableReducerFeatureKey = 'allocation-table';

export interface AllocationTableState {
  fileHeader: FileBase;
  status: UploadStatus;
  progress: number | null;
  requestId: string;
}

export const initialState: AllocationTableState = getInitialState();

const allocationTableReducer = createReducer(
  initialState,
  mutableOn(uploadSelectedFileHasStarted, (state, { status }) => {
    state.progress = 0;
    state.status = status;
  }),
  mutableOn(uploadSelectedFileInProgress, (state, { progress }) => {
    state.progress = progress;
  }),
  mutableOn(uploadAllocationTableFileSuccess, (state, { fileHeader }) => {
    state.fileHeader = fileHeader;
    state.progress = 100;
    state.status = UploadStatus.Completed;
  }),
  mutableOn(processSelectedFileError, (state) => {
    state.status = UploadStatus.Failed;
  }),
  mutableOn(cancelAllocationTableWizard, (state) => {
    resetState(state);
  }),
  mutableOn(clearAllocationTableWizard, (state) => {
    resetState(state);
  }),
  mutableOn(submitAllocationTableRequestSuccess, (state, { requestId }) => {
    state.requestId = requestId;
    state.status = UploadStatus.Completed;
  })
);

export function reducer(
  state: AllocationTableState | undefined,
  action: Action
) {
  return allocationTableReducer(state, action);
}

function resetState(state) {
  state.fileHeader = getInitialState().fileHeader;
  state.status = getInitialState().status;
  state.progress = getInitialState().progress;
  state.result = getInitialState().requestId;
}

function getInitialState(): AllocationTableState {
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
