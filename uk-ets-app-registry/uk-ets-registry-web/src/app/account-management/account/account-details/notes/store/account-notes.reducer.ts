import { createReducer, Action } from '@ngrx/store';
import { mutableOn } from '@registry-web/shared/mutable-on';
import * as NotesActions from './account-notes.actions';
import { Note, NoteType } from '@registry-web/shared/model/note';

export const accountNotesFeatureKey = 'accountNotes';

export interface AccountNotesState {
  accountNotes: Note[];
  accountHolderNotes: Note[];
  noteType: NoteType;
  noteDescription: string;
  deleteNoteId: string;
}

export const initialState: AccountNotesState = {
  accountNotes: [],
  accountHolderNotes: [],
  noteType: null,
  noteDescription: null,
  deleteNoteId: null,
};

const notesReducer = createReducer(
  initialState,
  mutableOn(NotesActions.fetchAccountNotesSuccess, (state, { response }) => {
    state.accountNotes = response;
  }),
  mutableOn(
    NotesActions.fetchAccountHolderNotesSuccess,
    (state, { response }) => {
      state.accountHolderNotes = response;
    }
  ),
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

export function reducer(state: AccountNotesState | undefined, action: Action) {
  return notesReducer(state, action);
}
