import { createReducer, Action } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import * as DocumentsWizardActions from './documents-wizard.actions';
import { DocumentUpdateType } from '@registry-web/documents/models/document-update-type.model';
import {
  SaveRegistryDocumentDTO,
  UpdateRegistryDocumentCategoryDTO,
} from '@registry-web/documents/models/document.model';
import * as FileUploadApiActions from '@registry-web/shared/file/actions/file-upload-api.actions';
import { errors } from '@registry-web/shared/shared.action';

export const documentsWizardReducerFeatureKey = 'documents-wizard';

export interface DocumentsWizardState {
  selectedUpdateType: DocumentUpdateType;
  documentCategory: UpdateRegistryDocumentCategoryDTO;
  document: SaveRegistryDocumentDTO;
  uploading: boolean;
  loadingFile: boolean;
  fileUpdated: boolean;
}

export const initialState: DocumentsWizardState = getInitialState();

const documentsReducer = createReducer(
  initialState,
  mutableOn(
    DocumentsWizardActions.setUpdateType,
    (state, { selectedUpdateType }) => {
      if (state.selectedUpdateType != selectedUpdateType) resetState(state);
      state.selectedUpdateType = selectedUpdateType;
    }
  ),
  mutableOn(
    DocumentsWizardActions.cancelDocumentUpdateRequestConfirm,
    (state) => {
      resetState(state);
    }
  ),
  mutableOn(
    DocumentsWizardActions.addDocumentCategory,
    (state, { documentCategory }) => {
      state.documentCategory = documentCategory;
    }
  ),
  mutableOn(
    DocumentsWizardActions.patchDocumentCategory,
    (state, { documentCategory }) => {
      state.documentCategory.name = documentCategory.name;
      state.documentCategory.order = documentCategory.order;
    }
  ),
  mutableOn(
    DocumentsWizardActions.populateDocumentCategoryById,
    (state, { selectedCategoryId, categories }) => {
      state.documentCategory = categories.find(
        (c) => c.id === selectedCategoryId
      );
    }
  ),
  mutableOn(
    DocumentsWizardActions.setCategoryId,
    (state, { selectedCategoryId }) => {
      state.document.categoryId = selectedCategoryId;
    }
  ),
  mutableOn(
    DocumentsWizardActions.setDocument,
    (state, { id, categoryId, order, title }) => {
      state.document.id = id;
      state.document.categoryId = categoryId;
      state.document.order = order;
      state.document.title = title;
    }
  ),
  mutableOn(
    DocumentsWizardActions.setDocumentFile,
    (state, { file, title, fileUpdated }) => {
      state.document.title = title;
      if (file) {
        state.document.file = file;
      }
      state.fileUpdated = fileUpdated;
    }
  ),
  mutableOn(
    DocumentsWizardActions.setDocumentOrder,
    (state, { selectedOrder }) => {
      state.document.order = selectedOrder;
    }
  ),
  mutableOn(DocumentsWizardActions.updateDocumentSuccess, (state) => {
    state.uploading = false;
  }),
  mutableOn(DocumentsWizardActions.createDocumentSuccess, (state) => {
    state.uploading = false;
  }),
  mutableOn(FileUploadApiActions.uploadSelectedFileHasStarted, (state) => {
    state.uploading = true;
  }),
  mutableOn(
    FileUploadApiActions.uploadSelectedFileInProgress,
    (state, { progress }) => {
      state.uploading = progress < 100;
    }
  ),
  mutableOn(FileUploadApiActions.uploadSelectedFileError, (state) => {
    state.uploading = false;
  }),
  mutableOn(DocumentsWizardActions.removeStoredDocumentFile, (state) => {
    state.document.file = null;
  }),
  mutableOn(DocumentsWizardActions.populateDocumentFile, (state) => {
    state.document.file = null;
    state.loadingFile = true;
  }),
  mutableOn(
    DocumentsWizardActions.populateDocumentFileSuccess,
    (state, { blob, filename }) => {
      state.document.file = new File([blob], filename);
      state.loadingFile = false;
      state.fileUpdated = false;
    }
  ),
  mutableOn(errors, (state) => {
    state.loadingFile = false;
  }),
  mutableOn(DocumentsWizardActions.resetState, (state) => {
    resetState(state);
  })
);

function resetState(state) {
  state.selectedUpdateType = null;
  state.documentCategory = null;
  state.document = {
    categoryId: null,
    title: null,
    documentId: null,
    order: null,
    file: null,
  };
  state.uploading = false;
  state.loadingFile = false;
  state.fileUpdated = false;
}

export function reducer(
  state: DocumentsWizardState | undefined,
  action: Action
) {
  return documentsReducer(state, action);
}

function getInitialState(): DocumentsWizardState {
  return {
    selectedUpdateType: null,
    documentCategory: null,
    document: {
      categoryId: null,
      title: null,
      id: null,
      order: null,
      file: null,
    },
    uploading: false,
    loadingFile: false,
    fileUpdated: false,
  };
}
