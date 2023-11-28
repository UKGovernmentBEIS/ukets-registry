import { createAction, props } from '@ngrx/store';

export const submitAllocationTableRequest = createAction(
  '[Allocation Table Wizard] Submit'
);

export const submitAllocationTableRequestSuccess = createAction(
  '[Allocation Table Wizard] Submit success',
  props<{ requestId: string }>()
);

export const cancelClicked = createAction(
  '[Allocation Table Wizard] Cancel clicked',
  props<{ route: string }>()
);

export const cancelAllocationTableWizard = createAction(
  '[Allocation Table Wizard] Cancel'
);

export const clearAllocationTableWizard = createAction(
  '[Allocation Table Wizard] Clear'
);

export const downloadAllocationTableErrorsCSV = createAction(
  '[Allocation Table Wizard] Download Errors CSV',
  props<{ fileId: number }>()
);
