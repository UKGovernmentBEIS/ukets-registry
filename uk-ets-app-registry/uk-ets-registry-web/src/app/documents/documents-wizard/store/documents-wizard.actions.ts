import { createAction, props } from '@ngrx/store';
import { DocumentUpdateType } from '@registry-web/documents/models/document-update-type.model';
import {
  UpdateRegistryDocumentCategoryDTO,
  RegistryDocumentCategory,
  RegistryDocument,
  SaveRegistryDocumentDTO,
} from '@registry-web/documents/models/document.model';
import { UploadStatus } from '@registry-web/shared/model/file';

export const resetState = createAction('[Documents] Reset state');

export const createCategory = createAction('[Documents] Create category');

export const updateCategory = createAction('[Documents] Update category');

export const deleteCategory = createAction('[Documents] Delete category');

export const createDocument = createAction('[Documents] Create document');

export const updateDocument = createAction('[Documents] Update document');

export const deleteDocument = createAction('[Documents] Delete document');

export const fetchStoredDocumentFile = createAction(
  '[Documents] Fetch stored document file'
);

export const removeStoredDocumentFile = createAction(
  '[Documents] Remove stored document file'
);

export const createDocumentSuccess = createAction(
  '[Documents] Create document success',
  props<{ response: any }>()
);

export const updateDocumentSuccess = createAction(
  '[Documents] Update document success',
  props<{ response: any }>()
);

export const deleteDocumentSuccess = createAction(
  '[Documents] Delete document success',
  props<{ response: any }>()
);

export const createCategorySuccess = createAction(
  '[Documents] Create category success',
  props<{ response: any }>()
);

export const updateCategorySuccess = createAction(
  '[Documents] Update category success',
  props<{ response: any }>()
);

export const deleteCategorySuccess = createAction(
  '[Documents] Delete category success',
  props<{ response: any }>()
);

export const navigateToUpdateDocumentsWizard = createAction(
  '[Documents] Navigate to update documents wizard'
);

export const navigateToAddDocument = createAction(
  '[Documents] Navigate to add document'
);

export const navigateToAddDocumentCategory = createAction(
  '[Documents] Navigate to add document category'
);

export const navigateToSuccess = createAction(
  '[Documents] Navigate to success'
);

export const navigateToUploadDocumentUpdateRequest = createAction(
  '[Documents] Navigate to upload document'
);

export const navigateToUpdateDocumentCategoryDetails = createAction(
  '[Documents] Navigate to update document category details'
);

export const setCategoryId = createAction(
  '[Documents] Set category id',
  props<{ selectedCategoryId: number }>()
);

export const setDocument = createAction(
  '[Documents] Set document',
  props<{
    id: number;
    categoryId: number;
    order: number;
    title: string;
    name?: string;
  }>()
);

export const setUpdateType = createAction(
  '[Documents] Select update type',
  props<{ selectedUpdateType: DocumentUpdateType }>()
);

export const addDocumentCategory = createAction(
  '[Documents] Add document category',
  props<{ documentCategory: UpdateRegistryDocumentCategoryDTO }>()
);

export const patchDocumentCategory = createAction(
  '[Documents] Update document category',
  props<{ documentCategory: UpdateRegistryDocumentCategoryDTO }>()
);

export const populateDocumentCategoryById = createAction(
  '[Documents] Populate document category by id',
  props<{
    selectedCategoryId: number;
    categories: RegistryDocumentCategory[];
  }>()
);

export const navigateTo = createAction(
  '[Documents] Navigate to route',
  props<{ route: string; extras: any }>()
);

export const navigateToCheckAndSubmitDocumentUpdateRequest = createAction(
  '[Documents] Navigate to check and submit document update request'
);

export const navigateToCancelDocumentUpdateRequest = createAction(
  '[Documents] Navigate to cancel document update request',
  props<{ route: string }>()
);

export const navigateToChooseDisplayOrder = createAction(
  '[Documents] Navigate to choose display order'
);

export const cancelDocumentUpdateRequestConfirm = createAction(
  '[Documents] Cancel document update request confirm'
);

export const setDocumentFile = createAction(
  '[Documents] Set document file',
  props<{ file: File; title: string; fileUpdated: boolean }>()
);

export const setDocumentOrder = createAction(
  '[Documents] Set document order',
  props<{ selectedOrder: number }>()
);

export const populateDocumentFile = createAction(
  '[Documents] Populate document file',
  props<{ documentId: number; filename: string }>()
);

export const populateDocumentFileSuccess = createAction(
  '[Documents] Populate document file success',
  props<{ blob: Blob; filename: string }>()
);
