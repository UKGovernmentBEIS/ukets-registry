export enum NotesWizardPathsModel {
  BASE_PATH = 'notes',
  SELECT_ENTITY = 'select-entity',
  ADD_NOTE = 'add-note',
  CHECK_AND_CONFIRM_ADD_NOTE = 'check-and-confirm-add-note',
  ADD_NOTE_SUCCESS = 'add-note-success',
  CANCEL_ADD_NOTE = 'cancel-add-note',
  DELETE_NOTE = 'delete-note',
  DELETE_NOTE_SUCCESS = 'delete-note-success',
}

export const NotesWizardPathsMap = new Map<string, NotesWizardPathsModel>([
  ['notes', NotesWizardPathsModel.BASE_PATH],
  ['select-entity', NotesWizardPathsModel.SELECT_ENTITY],
  ['add-note', NotesWizardPathsModel.ADD_NOTE],
  [
    'check-and-confirm-add-note',
    NotesWizardPathsModel.CHECK_AND_CONFIRM_ADD_NOTE,
  ],
  ['add-note-success', NotesWizardPathsModel.ADD_NOTE_SUCCESS],
  ['cancel-add-note', NotesWizardPathsModel.CANCEL_ADD_NOTE],
  ['delete-note', NotesWizardPathsModel.DELETE_NOTE],
  ['delete-note-success', NotesWizardPathsModel.DELETE_NOTE_SUCCESS],
]);
