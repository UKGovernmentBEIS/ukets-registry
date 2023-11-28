import { Action, createReducer } from '@ngrx/store';
import { FileDetails } from '@registry-web/shared/model/file/file-details.model';
import { mutableOn } from '@registry-web/shared/mutable-on';
import { Draft } from 'immer';
import {
  clearDeleteFile,
  clearDeleteFileName,
  enterDeleteFileWizard,
  setDeleteFileName,
} from '@delete-file/wizard/actions/delete-file.actions';
import { DocumentsRequestType } from '@registry-web/shared/model/request-documents/documents-request-type';

export const deleteFileFeatureKey = 'deleteFile';

export interface DeleteFileState {
  originatingPath: string;
  id: string;
  file: FileDetails;
  fileName: string;
  documentsRequestType: DocumentsRequestType;
}

export const initialState: DeleteFileState = {
  originatingPath: null,
  id: null,
  file: null,
  fileName: null,
  documentsRequestType: null,
};

function resetState(state: Draft<DeleteFileState>) {
  state.originatingPath = initialState.originatingPath;
  state.id = initialState.id;
  state.file = initialState.file;
  state.documentsRequestType = initialState.documentsRequestType;
}

export const deleteFileReducer = createReducer(
  initialState,

  mutableOn(
    enterDeleteFileWizard,
    (state, { originatingPath, id, file, documentsRequestType }) => {
      state.originatingPath = originatingPath;
      state.id = id;
      state.file = file;
      state.documentsRequestType = documentsRequestType;
    }
  ),
  mutableOn(setDeleteFileName, (state, { fileName }) => {
    state.fileName = fileName;
  }),
  mutableOn(clearDeleteFileName, (state) => {
    state.fileName = null;
  }),
  mutableOn(clearDeleteFile, (state) => {
    resetState(state);
  })
);

export function reducer(state: DeleteFileState | undefined, action: Action) {
  return deleteFileReducer(state, action);
}
