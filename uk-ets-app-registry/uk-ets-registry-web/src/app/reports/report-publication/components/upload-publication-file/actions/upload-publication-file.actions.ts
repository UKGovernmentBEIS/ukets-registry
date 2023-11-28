import { createAction, props } from '@ngrx/store';
import { FileBase } from '@shared/model/file';

export const submitPublicationFileRequest = createAction(
  '[Publication File Wizard] Submit'
);

export const submitPublicationFileRequestSuccess = createAction(
  '[Publication File Wizard] Submit success'
);

export const submitFileYear = createAction(
  '[Publication File Wizard] Submit file year',
  props<{ fileYear: number }>()
);

export const cancelClicked = createAction(
  '[Publication File Wizard] Cancel clicked',
  props<{ route: string }>()
);

export const cancelPublicationFileWizard = createAction(
  '[Publication File Wizard] Cancel'
);

export const clearPublicationFileWizard = createAction(
  '[Publication File Wizard] Clear'
);
