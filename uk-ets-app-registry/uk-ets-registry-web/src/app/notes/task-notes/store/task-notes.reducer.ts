import { createReducer, createFeature } from '@ngrx/store';
import { mutableOn } from '@registry-web/shared/mutable-on';
import { TaskNotesActions } from './task-notes.actions';
import { Note, NoteType } from '@registry-web/shared/model/note';

const featureKey = 'taskNotes';

export interface TaskNotesState {
  domainId: string;
  notes: Note[];
  noteType: NoteType;
  noteDescription: string;
  deleteNoteId: string;
}

const initialState: TaskNotesState = {
  domainId: null,
  notes: [],
  noteType: NoteType.TASK,
  noteDescription: null,
  deleteNoteId: null,
};

const notesReducer = createReducer(
  initialState,
  mutableOn(TaskNotesActions.FETCH_TASK_NOTES, (state, { requestId }) => {
    state.domainId = requestId;
  }),
  mutableOn(
    TaskNotesActions.FETCH_TASK_NOTES_SUCCESS,
    (state, { response }) => {
      state.notes = response;
    }
  ),
  mutableOn(
    TaskNotesActions.SAVE_NOTE_DESCRIPTION,
    (state, { noteDescription }) => {
      state.noteDescription = noteDescription;
    }
  ),
  mutableOn(TaskNotesActions.SAVE_DELETE_NOTE_ID, (state, { noteId }) => {
    state.deleteNoteId = noteId;
  }),
  mutableOn(TaskNotesActions.CLEAR_FORM_STATE, (state) => {
    state.noteDescription = null;
    state.deleteNoteId = null;
  })
);

export const taskNotesFeature = createFeature({
  name: featureKey,
  reducer: notesReducer,
});
