import { createAction, props } from '@ngrx/store';
import { Note } from '@registry-web/shared/model/note';

export enum NotesActionTypes {
  FETCH_ACCOUNT_NOTES = '[Account Notes] Fetch Account Notes',
  FETCH_ACCOUNT_NOTES_SUCCESS = '[Account Notes] Fetch Account Notes Success',
  FETCH_ACCOUNT_HOLDER_NOTES = '[Account Notes] Fetch Account Holder Notes',
  FETCH_ACCOUNT_HOLDER_NOTES_SUCCESS = '[Account Notes] Fetch Account Holder Notes Success',
  CREATE_NOTE = '[Account Notes] Create Note',
  CREATE_NOTE_SUCCESS = '[Account Notes] Create Note Success',
  DELETE_NOTE = '[Account Notes] Delete Note',
  DELETE_NOTE_SUCCESS = '[Account Notes] Delete Note Success',
  NAVIGATE_CANCEL_ADD_NOTE = '[Account Notes] Navigate to Cancel Add Note',
  NAVIGATE_SELECT_ENTITY = '[Account Notes] Navigate to Select Entity',
  NAVIGATE_ADD_NOTE = '[Account Notes] Navigate to Add Note',
  NAVIGATE_CHECK_AND_CONFIRM = '[Account Notes] Navigate to Check and Confirm',
  NAVIGATE_ADD_NOTE_SUCCESS = '[Account Notes] Navigate to Add Note Success',
  NAVIGATE_DELETE_NOTE = '[Account Notes] Navigate to Delete Note',
  NAVIGATE_ACCOUNT_NOTES = '[Account Notes] Navigate to Notes',
  CANCEL_ADD_NOTE = '[Account Notes] Cancel Add Note',
  SAVE_NOTE_TYPE = '[Account Notes] Save Note Type',
  SAVE_NOTE_DESCRIPTION = '[Account Notes] Save Note Description',
  CLEAR_STATE = '[Account Notes] Clear State',
  SAVE_DELETE_NOTE_ID = '[Account Notes] Save Delete Note ID',
}

export const fetchAccountNotes = createAction(
  NotesActionTypes.FETCH_ACCOUNT_NOTES,
  props<{ accountId: string }>()
);
export const fetchAccountNotesSuccess = createAction(
  NotesActionTypes.FETCH_ACCOUNT_NOTES_SUCCESS,
  props<{ response: Note[] }>()
);
export const fetchAccountHolderNotes = createAction(
  NotesActionTypes.FETCH_ACCOUNT_HOLDER_NOTES,
  props<{ accountId: string }>()
);
export const fetchAccountHolderNotesSuccess = createAction(
  NotesActionTypes.FETCH_ACCOUNT_HOLDER_NOTES_SUCCESS,
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
