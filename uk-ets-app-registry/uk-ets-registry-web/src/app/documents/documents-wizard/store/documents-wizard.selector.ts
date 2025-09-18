import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  DocumentsWizardState,
  documentsWizardReducerFeatureKey,
} from './documents-wizard.reducer';
import {
  DocumentState,
  documentsReducerFeatureKey,
} from '@registry-web/documents/store/documents.reducer';
import { UploadStatus } from '@registry-web/shared/model/file';
import { DocumentUpdateType } from '@registry-web/documents/models/document-update-type.model';

const selectDocumentsWizardState = createFeatureSelector<DocumentsWizardState>(
  documentsWizardReducerFeatureKey
);

const selectDocumentsState = createFeatureSelector<DocumentState>(
  documentsReducerFeatureKey
);

export const selectCategoryOrderOptions = createSelector(
  selectDocumentsState,
  selectDocumentsWizardState,
  (documentsState, wizardState) =>
    Array.from(
      {
        length:
          documentsState.categories?.length +
          (wizardState.selectedUpdateType ===
          DocumentUpdateType.ADD_DOCUMENT_CATEGORY
            ? 1
            : 0),
      },
      (x, i) => i + 1
    )
);

export const selectDocumentCategory = createSelector(
  selectDocumentsWizardState,
  (state) => state.documentCategory
);

export const selectUpdateType = createSelector(
  selectDocumentsWizardState,
  (state) => state.selectedUpdateType
);

export const selectCategoryId = createSelector(
  selectDocumentsWizardState,
  (state) => state.document.categoryId
);

export const selectDocumentId = createSelector(
  selectDocumentsWizardState,
  (state) => state.document.id
);

export const selectDocumentOrderOptions = createSelector(
  selectDocumentsState,
  selectDocumentsWizardState,
  (documentsState, wizardState) =>
    Array.from(
      {
        length:
          documentsState.categories?.find(
            (c) => c.id === wizardState.document.categoryId
          )?.documents?.length +
          (wizardState.selectedUpdateType === DocumentUpdateType.ADD_DOCUMENT
            ? 1
            : 0),
      },
      (x, i) => i + 1
    )
);

export const selectDocumentOrder = createSelector(
  selectDocumentsWizardState,
  (state) => state.document?.order
);

export const selectDocument = createSelector(
  selectDocumentsWizardState,
  (state) => state.document
);

export const selectDocumentFile = createSelector(
  selectDocumentsWizardState,
  (state) => state.document?.file
);

export const selectUploading = createSelector(
  selectDocumentsWizardState,
  (state) => state.uploading
);

export const selectLoadingFile = createSelector(
  selectDocumentsWizardState,
  (state) => state.loadingFile
);

export const selectFileUpdated = createSelector(
  selectDocumentsWizardState,
  (state) => state.fileUpdated
);
