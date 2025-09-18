import { createAction, props } from '@ngrx/store';
import { Note } from '@registry-web/shared/model/note';

export enum TaskNotesActionTypes {
  FETCH_TASK_NOTES = '[Task Notes] Fetch Task Notes',
  FETCH_TASK_NOTES_SUCCESS = '[Task Notes] Fetch Task Notes Success',
  CREATE_NOTE = '[Task Notes] Create Note',
  CREATE_NOTE_SUCCESS = '[Task Notes] Create Note Success',
  DELETE_NOTE = '[Task Notes] Delete Note',
  DELETE_NOTE_SUCCESS = '[Task Notes] Delete Note Success',
  NAVIGATE_CANCEL_ADD_NOTE = '[Task Notes] Navigate to Cancel Add Note',
  NAVIGATE_ADD_NOTE = '[Task Notes] Navigate to Add Note',
  NAVIGATE_CHECK_AND_CONFIRM = '[Task Notes] Navigate to Check and Confirm',
  NAVIGATE_ADD_NOTE_SUCCESS = '[Task Notes] Navigate to Add Note Success',
  NAVIGATE_DELETE_NOTE = '[Task Notes] Navigate to Delete Note',
  NAVIGATE_TASK_NOTES = '[Task Notes] Navigate to Notes',
  CANCEL_ADD_NOTE = '[Task Notes] Cancel Add Note',
  SAVE_NOTE_TYPE = '[Task Notes] Save Note Type',
  SAVE_NOTE_DESCRIPTION = '[Task Notes] Save Note Description',
  CLEAR_STATE = '[Task Notes] Clear State',
  SAVE_DELETE_NOTE_ID = '[Task Notes] Save Delete Note ID',
}

export const fetchTaskNotes = createAction(
  TaskNotesActionTypes.FETCH_TASK_NOTES,
  props<{ requestId: string }>()
);
export const fetchTaskNotesSuccess = createAction(
  TaskNotesActionTypes.FETCH_TASK_NOTES_SUCCESS,
  props<{ response: Note[] }>()
);
export const createNote = createAction(TaskNotesActionTypes.CREATE_NOTE);
export const createNoteSuccess = createAction(
  TaskNotesActionTypes.CREATE_NOTE_SUCCESS,
  props<{ response: any }>()
);
export const deleteNote = createAction(TaskNotesActionTypes.DELETE_NOTE);
export const deleteNoteSuccess = createAction(
  TaskNotesActionTypes.DELETE_NOTE_SUCCESS,
  props<{ response: any }>()
);

export const navigateCancelAddNote = createAction(
  TaskNotesActionTypes.NAVIGATE_CANCEL_ADD_NOTE,
  props<{ currentRoute: string }>()
);
export const navigateAddNote = createAction(
  TaskNotesActionTypes.NAVIGATE_ADD_NOTE
);

export const navigateCheckAndConfirm = createAction(
  TaskNotesActionTypes.NAVIGATE_CHECK_AND_CONFIRM
);
export const navigateDeleteNote = createAction(
  TaskNotesActionTypes.NAVIGATE_DELETE_NOTE,
  props<{ noteId: string }>()
);
export const navigateToTaskNotes = createAction(
  TaskNotesActionTypes.NAVIGATE_TASK_NOTES
);

export const cancelAddNote = createAction(TaskNotesActionTypes.CANCEL_ADD_NOTE);
export const saveNoteType = createAction(
  TaskNotesActionTypes.SAVE_NOTE_TYPE,
  props<{ noteType: any }>()
);
export const saveNoteDescription = createAction(
  TaskNotesActionTypes.SAVE_NOTE_DESCRIPTION,
  props<{ noteDescription: string }>()
);
export const clearNoteState = createAction(TaskNotesActionTypes.CLEAR_STATE);

export const saveDeleteNoteId = createAction(
  TaskNotesActionTypes.SAVE_DELETE_NOTE_ID,
  props<{ noteId: string }>()
);
