import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import { RegistryDocumentCategory } from '../models/document.model';
import * as DocumentActions from './documents.actions';

export const documentsReducerFeatureKey = 'documents';

export interface DocumentState {
  categories: RegistryDocumentCategory[];
}

export const initialState: DocumentState = getInitialState();

const documentsReducer = createReducer(
  initialState,
  mutableOn(DocumentActions.fetchCategoriesSuccess, (state, { categories }) => {
    state.categories = categories;
  })
);

export function reducer(state: DocumentState | undefined, action: Action) {
  return documentsReducer(state, action);
}

function getInitialState(): DocumentState {
  return {
    categories: [],
  };
}
