import { createAction, props } from '@ngrx/store';
import { Note } from '@registry-web/shared/model/note';

export const TaskNotesActions = {
  FETCH_TASK_NOTES: createAction(
    '[Task Notes] Fetch Task Notes',
    props<{ requestId: string }>()
  ),
  FETCH_TASK_NOTES_SUCCESS: createAction(
    '[Task Notes] Fetch Task Notes Success',
    props<{ response: Note[] }>()
  ),
  CREATE_NOTE: createAction('[Task Notes] Create Note'),
  CREATE_NOTE_SUCCESS: createAction(
    '[Task Notes] Create Note Success',
    props<{ response: any }>()
  ),
  DELETE_NOTE: createAction('[Task Notes] Delete Note'),
  DELETE_NOTE_SUCCESS: createAction(
    '[Task Notes] Delete Note Success',
    props<{ response: any }>()
  ),

  NAVIGATE_TO_CANCEL_ADD_NOTE: createAction(
    '[Task Notes] Navigate to Cancel Add Note',
    props<{ currentRoute: string }>()
  ),
  NAVIGATE_TO_ADD_NOTE: createAction('[Task Notes] Navigate to Add Note'),

  NAVIGATE_TO_CHECK_AND_CONFIRM: createAction(
    '[Task Notes] Navigate to Check and Confirm'
  ),
  NAVIGATE_TO_DELETE_NOTE: createAction(
    '[Task Notes] Navigate to Delete Note',
    props<{ noteId: string }>()
  ),
  NAVIGATE_TO_TASK_NOTES: createAction('[Task Notes] Navigate to Notes'),

  CANCEL_ADD_NOTE: createAction('[Task Notes] Cancel Add Note'),
  SAVE_NOTE_TYPE: createAction(
    '[Task Notes] Save Note Type',
    props<{ noteType: any }>()
  ),
  SAVE_NOTE_DESCRIPTION: createAction(
    '[Task Notes] Save Note Description',
    props<{ noteDescription: string }>()
  ),
  CLEAR_FORM_STATE: createAction('[Task Notes] Clear Form State'),

  SAVE_DELETE_NOTE_ID: createAction(
    '[Task Notes] Save Delete Note ID',
    props<{ noteId: string }>()
  ),
};
