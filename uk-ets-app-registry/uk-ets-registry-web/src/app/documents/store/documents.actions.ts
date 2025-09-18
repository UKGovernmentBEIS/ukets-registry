import { createAction, props } from '@ngrx/store';
import { RegistryDocumentCategory } from '../models/document.model';
import { DocumentUpdateType } from '../models/document-update-type.model';

export const fetchCategories = createAction('[Documents] Fetch categories');

export const fetchCategoriesSuccess = createAction(
  '[Documents] Fetch categories success',
  props<{ categories: RegistryDocumentCategory[] }>()
);

export const navigateToUpdateDocumentsWizard = createAction(
  '[Documents] Navigate to update documents wizard'
);

export const fetchDocumentFile = createAction(
  '[Documents] Fetch document file',
  props<{ documentId: number }>()
);
