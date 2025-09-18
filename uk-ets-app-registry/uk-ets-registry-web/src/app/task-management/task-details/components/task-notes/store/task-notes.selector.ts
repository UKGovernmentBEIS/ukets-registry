import { createFeatureSelector, createSelector } from '@ngrx/store';
import { NoteType, NoteTypeLabel } from '@registry-web/shared/model/note';
import { TaskNotesState, taskNotesFeatureKey } from './task-notes.reducer';

const selectTaskNotesState =
  createFeatureSelector<TaskNotesState>(taskNotesFeatureKey);

export const selectTaskNotes = createSelector(selectTaskNotesState, (state) =>
  state.notes?.filter(
    (note) => String(note.domainType) === NoteType[NoteType.TASK]
  )
);

export const selectAddNotesType = createSelector(
  selectTaskNotesState,
  (state) => state.noteType
);

export const selectAddNotesTypeLabel = createSelector(
  selectTaskNotesState,
  (state) => NoteTypeLabel[state.noteType] || state.noteType
);

export const selectAddNotesDescription = createSelector(
  selectTaskNotesState,
  (state) => state.noteDescription
);

export const selectDeleteNoteId = createSelector(
  selectTaskNotesState,
  (state) => state.deleteNoteId
);
