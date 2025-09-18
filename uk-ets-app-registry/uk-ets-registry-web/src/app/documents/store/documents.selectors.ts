import { createFeatureSelector, createSelector } from '@ngrx/store';
import { DocumentState, documentsReducerFeatureKey } from './documents.reducer';

const selectDocumentsState = createFeatureSelector<DocumentState>(
  documentsReducerFeatureKey
);

export const selectCategories = createSelector(
  selectDocumentsState,
  (state) => state.categories
);
