import { createAction, props } from '@ngrx/store';
import { FileBase, UploadStatus } from '@shared/model/file';

export const uploadSelectedFileHasStarted = createAction(
  '[File Upload API] Request started',
  props<{ status: UploadStatus }>()
);

export const uploadSelectedAllocationTableFile = createAction(
  '[File Upload API] Allocation table file upload started',
  props<{
    file: File;
  }>()
);

export const uploadSelectedEmissionsTableFile = createAction(
  '[File Upload API] Emissions table file upload started',
  props<{
    file: File;
  }>()
);

export const uploadSelectedBulkArFile = createAction(
  '[File Upload API] Bulk AR file upload started',
  props<{ file: File }>()
);

export const uploadSelectedPublicationReportFile = createAction(
  '[File Upload API] Publication Report file upload started',
  props<{
    file: File;
  }>()
);

export const uploadSelectedFileForDocumentRequest = createAction(
  '[File Upload API] Document request file upload started',
  props<{
    file: File;
    documentName?: string;
    fileUploadIndex?: number;
    fileId?: number;
  }>()
);

export const uploadSelectedFileInProgress = createAction(
  '[File Upload API] Progress',
  props<{ progress: number }>()
);

export const uploadAllocationTableFileSuccess = createAction(
  '[File Upload API] Allocation table upload success',
  props<{ fileHeader: FileBase }>()
);

export const uploadBulkARFileSuccess = createAction(
  '[File Upload API] Bulk AR upload success',
  props<{ fileHeader: FileBase }>()
);

export const uploadEmissionsTableFileSuccess = createAction(
  '[File Upload API] Emissions table upload success',
  props<{ fileHeader: FileBase }>()
);

export const uploadReportPublicationFileSuccess = createAction(
  '[File Upload API] Report publication upload success',
  props<{ fileHeader: FileBase }>()
);

export const processSelectedFileError = createAction(
  '[File Upload API] Process error',
  props<{ status: UploadStatus }>()
);

export const uploadSelectedFileError = createAction(
  '[File Upload API] Upload error',
  props<{ status: UploadStatus }>()
);
