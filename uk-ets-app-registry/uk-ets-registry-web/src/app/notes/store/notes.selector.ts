import { createFeatureSelector, createSelector } from '@ngrx/store';
import { NoteType, NoteTypeLabel } from '@registry-web/shared/model/note';
import { NotesState, notesFeatureKey } from './notes.reducer';

const selectNotesState = createFeatureSelector<NotesState>(notesFeatureKey);

export const selectAccountNotes = createSelector(selectNotesState, (state) =>
  state.notes?.filter(
    (note) => String(note.domainType) === NoteType[NoteType.ACCOUNT]
  )
);

export const selectAccountHolderNotes = createSelector(
  selectNotesState,
  (state) =>
    state.notes?.filter(
      (note) => String(note.domainType) === NoteType[NoteType.ACCOUNT_HOLDER]
    )
);

export const selectAddNotesType = createSelector(
  selectNotesState,
  (state) => state.noteType
);

export const selectAddNotesTypeLabel = createSelector(
  selectNotesState,
  (state) => NoteTypeLabel[state.noteType] || state.noteType
);

export const selectAddNotesDescription = createSelector(
  selectNotesState,
  (state) => state.noteDescription
);

export const selectDeleteNoteId = createSelector(
  selectNotesState,
  (state) => state.deleteNoteId
);
