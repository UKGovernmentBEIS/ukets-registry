import { createAction, props } from '@ngrx/store';

export const submitBulkArRequest = createAction('[Bulk AR Wizard] Submit');

export const submitBulkArRequestSuccess = createAction(
  '[Bulk AR Wizard] Submit success',
  props<{ requestId: string }>()
);

export const cancelClicked = createAction(
  '[Bulk AR Wizard] Cancel clicked',
  props<{ route: string }>()
);

export const cancelBulkArWizard = createAction('[Bulk AR Wizard] Cancel');

export const clearBulkArWizard = createAction('[Bulk AR Wizard] Clear');
