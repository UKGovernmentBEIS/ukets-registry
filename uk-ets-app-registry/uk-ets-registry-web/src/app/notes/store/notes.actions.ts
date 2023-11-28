import { createAction, props } from '@ngrx/store';
import { Note, NoteType } from '@registry-web/shared/model/note';

export enum NotesActionTypes {
  FETCH_NOTES = '[Notes] Fetch Notes',
  FETCH_NOTES_SUCCESS = '[Notes] Fetch Notes Success',
  CREATE_NOTE = '[Notes] Create Note',
  CREATE_NOTE_SUCCESS = '[Notes] Create Note Success',
  DELETE_NOTE = '[Notes] Delete Note',
  DELETE_NOTE_SUCCESS = '[Notes] Delete Note Success',
  NAVIGATE_CANCEL_ADD_NOTE = '[Notes] Navigate to Cancel Add Note',
  NAVIGATE_SELECT_ENTITY = '[Notes] Navigate to Select Entity',
  NAVIGATE_ADD_NOTE = '[Notes] Navigate to Add Note',
  NAVIGATE_CHECK_AND_CONFIRM = '[Notes] Navigate to Check and Confirm',
  NAVIGATE_ADD_NOTE_SUCCESS = '[Notes] Navigate to Add Note Success',
  NAVIGATE_DELETE_NOTE = '[Notes] Navigate to Delete Note',
  NAVIGATE_ACCOUNT_NOTES = '[Notes] Navigate to Notes',
  CANCEL_ADD_NOTE = '[Notes] Cancel Add Note',
  SAVE_NOTE_TYPE = '[Notes] Save Note Type',
  SAVE_NOTE_DESCRIPTION = '[Notes] Save Note Description',
  CLEAR_STATE = '[Notes] Clear State',
  SAVE_DELETE_NOTE_ID = '[Notes] Save Delete Note ID',
}

export const fetchNotes = createAction(
  NotesActionTypes.FETCH_NOTES,
  props<{ domainId: string }>()
);
export const fetchNotesSuccess = createAction(
  NotesActionTypes.FETCH_NOTES_SUCCESS,
  props<{ response: Note[] }>()
);
export const createNote = createAction(NotesActionTypes.CREATE_NOTE);
export const createNoteSuccess = createAction(
  NotesActionTypes.CREATE_NOTE_SUCCESS,
  props<{ response: any }>()
);
export const deleteNote = createAction(NotesActionTypes.DELETE_NOTE);
export const deleteNoteSuccess = createAction(
  NotesActionTypes.DELETE_NOTE_SUCCESS,
  props<{ response: any }>()
);

export const navigateCancelAddNote = createAction(
  NotesActionTypes.NAVIGATE_CANCEL_ADD_NOTE,
  props<{ currentRoute: string }>()
);
export const navigateAddNote = createAction(NotesActionTypes.NAVIGATE_ADD_NOTE);
export const navigateSelectEntity = createAction(
  NotesActionTypes.NAVIGATE_SELECT_ENTITY
);
export const navigateCheckAndConfirm = createAction(
  NotesActionTypes.NAVIGATE_CHECK_AND_CONFIRM
);
export const navigateDeleteNote = createAction(
  NotesActionTypes.NAVIGATE_DELETE_NOTE,
  props<{ noteId: string }>()
);
export const navigateToAccountNotes = createAction(
  NotesActionTypes.NAVIGATE_ACCOUNT_NOTES
);

export const cancelAddNote = createAction(NotesActionTypes.CANCEL_ADD_NOTE);
export const saveNoteType = createAction(
  NotesActionTypes.SAVE_NOTE_TYPE,
  props<{ noteType: any }>()
);
export const saveNoteDescription = createAction(
  NotesActionTypes.SAVE_NOTE_DESCRIPTION,
  props<{ noteDescription: string }>()
);
export const clearNoteState = createAction(NotesActionTypes.CLEAR_STATE);

export const saveDeleteNoteId = createAction(
  NotesActionTypes.SAVE_DELETE_NOTE_ID,
  props<{ noteId: string }>()
);
