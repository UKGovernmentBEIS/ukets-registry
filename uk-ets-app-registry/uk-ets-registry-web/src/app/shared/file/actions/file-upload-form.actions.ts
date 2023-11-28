import { createAction, props } from '@ngrx/store';
import { UploadStatus } from '@shared/model/file';

export const requestUploadSelectedPublicationFile = createAction(
  '[File Upload Form] Request to upload publication report file',
  props<{ file: File; status: UploadStatus }>()
);

export const requestUploadSelectedAllocationTableFile = createAction(
  '[Allocation Table Upload Form] Request to upload allocation table file',
  props<{ file: File; status: UploadStatus }>()
);

export const requestUploadSelectedBulkArFile = createAction(
  '[Bulk AR File Upload Form] Request to upload bulk AR file',
  props<{ file: File; status: UploadStatus }>()
);

export const requestUploadSelectedEmissionsTableFile = createAction(
  '[Emmissions Table File Upload Form] Request to upload emissions table file',
  props<{ file: File; status: UploadStatus }>()
);

export const requestUploadSelectedFileForDocumentRequest = createAction(
  '[Document Submit Upload Form] Request to upload document file',
  props<{
    file: File;
    status: UploadStatus;
    documentName?: string;
    fileUploadIndex?: number;
    fileId?: number;
  }>()
);
