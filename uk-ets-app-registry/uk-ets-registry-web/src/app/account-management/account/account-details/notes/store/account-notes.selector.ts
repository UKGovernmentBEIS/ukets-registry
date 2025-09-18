import { createFeatureSelector, createSelector } from '@ngrx/store';
import { NoteTypeLabel } from '@registry-web/shared/model/note';
import {
  AccountNotesState,
  accountNotesFeatureKey,
} from './account-notes.reducer';

const selectAccountNotesState = createFeatureSelector<AccountNotesState>(
  accountNotesFeatureKey
);

export const selectAccountNotes = createSelector(
  selectAccountNotesState,
  (state) => state.accountNotes
);

export const selectAccountHolderNotes = createSelector(
  selectAccountNotesState,
  (state) => state.accountHolderNotes
);

export const selectAddNotesType = createSelector(
  selectAccountNotesState,
  (state) => state.noteType
);

export const selectAddNotesTypeLabel = createSelector(
  selectAccountNotesState,
  (state) => NoteTypeLabel[state.noteType] || state.noteType
);

export const selectAddNotesDescription = createSelector(
  selectAccountNotesState,
  (state) => state.noteDescription
);

export const selectDeleteNoteId = createSelector(
  selectAccountNotesState,
  (state) => state.deleteNoteId
);
