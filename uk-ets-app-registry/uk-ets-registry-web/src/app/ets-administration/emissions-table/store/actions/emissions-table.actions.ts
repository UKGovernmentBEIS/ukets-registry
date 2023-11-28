import { createAction, props } from '@ngrx/store';

export const submitEmissionsTableRequest = createAction(
  '[Emissions Table Wizard] Submit',
  props<{ otp: string }>()
);

export const submitEmissionsTableRequestSuccess = createAction(
  '[Emissions Table Wizard] Submit success',
  props<{ requestId: string }>()
);

export const cancelClicked = createAction(
  '[Emissions Table Wizard] Cancel clicked',
  props<{ route: string }>()
);

export const cancelEmissionsTableUpload = createAction(
  '[Emissions Table Wizard] Cancel'
);

export const clearEmissionsTableUpload = createAction(
  '[Emissions Table Wizard] Clear'
);

export const downloadErrorsCSV = createAction(
  '[Emissions Table Wizard] Download Errors CSV',
  props<{ fileId: number }>()
);
