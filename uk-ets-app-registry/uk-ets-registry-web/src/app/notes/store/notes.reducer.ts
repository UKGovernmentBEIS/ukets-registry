import { createReducer, Action } from '@ngrx/store';
import { mutableOn } from '@registry-web/shared/mutable-on';
import * as NotesActions from './notes.actions';
import { Note, NoteType } from '@registry-web/shared/model/note';

export const notesFeatureKey = 'notes';

export interface NotesState {
  notes: Note[];
  noteType: NoteType;
  noteDescription: string;
  deleteNoteId: string;
}

export const initialState: NotesState = {
  notes: [],
  noteType: null,
  noteDescription: null,
  deleteNoteId: null,
};

const accountReducer = createReducer(
  initialState,
  mutableOn(NotesActions.fetchNotesSuccess, (state, { response }) => {
    state.notes = response;
  }),
  mutableOn(NotesActions.saveNoteType, (state, { noteType }) => {
    state.noteType = noteType;
  }),
  mutableOn(NotesActions.saveNoteDescription, (state, { noteDescription }) => {
    state.noteDescription = noteDescription;
  }),
  mutableOn(NotesActions.saveDeleteNoteId, (state, { noteId }) => {
    state.deleteNoteId = noteId;
  }),
  mutableOn(NotesActions.clearNoteState, (state) => {
    state.noteType = null;
    state.noteDescription = null;
    state.deleteNoteId = null;
  })
);

export function reducer(state: NotesState | undefined, action: Action) {
  return accountReducer(state, action);
}
