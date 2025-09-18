import { createReducer, Action } from '@ngrx/store';
import { mutableOn } from '@registry-web/shared/mutable-on';
import * as TaskNotesActions from './task-notes.actions';
import { Note, NoteType } from '@registry-web/shared/model/note';

export const taskNotesFeatureKey = 'taskNotes';

export interface TaskNotesState {
  notes: Note[];
  noteType: NoteType;
  noteDescription: string;
  deleteNoteId: string;
}

export const initialState: TaskNotesState = {
  notes: [],
  noteType: NoteType.TASK,
  noteDescription: null,
  deleteNoteId: null,
};

const notesReducer = createReducer(
  initialState,
  mutableOn(TaskNotesActions.fetchTaskNotesSuccess, (state, { response }) => {
    state.notes = response;
  }),
  mutableOn(
    TaskNotesActions.saveNoteDescription,
    (state, { noteDescription }) => {
      state.noteDescription = noteDescription;
    }
  ),
  mutableOn(TaskNotesActions.saveDeleteNoteId, (state, { noteId }) => {
    state.deleteNoteId = noteId;
  }),
  mutableOn(TaskNotesActions.clearNoteState, (state) => {
    state.noteDescription = null;
    state.deleteNoteId = null;
  })
);

export function reducer(state: TaskNotesState | undefined, action: Action) {
  return notesReducer(state, action);
}
