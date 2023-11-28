import { createAction, props } from '@ngrx/store';
import { FileDetails } from '@registry-web/shared/model/file/file-details.model';
import { DocumentsRequestType } from '@registry-web/shared/model/request-documents/documents-request-type';

export const enterDeleteFileWizard = createAction(
  '[Delete File] Enter wizard',
  props<{
    originatingPath: string;
    id: string;
    file: FileDetails;
    documentsRequestType: DocumentsRequestType;
  }>()
);

export const clearDeleteFile = createAction('[Delete File] Clear Delete File');

export const setDeleteFileName = createAction(
  '[Delete File] Set Delete File Name',
  props<{
    fileName: string;
  }>()
);

export const clearDeleteFileName = createAction(
  '[Delete File] Clear Delete File Name'
);

export const navigateToDeleteFile = createAction(
  '[Delete File] Navigate to Delete File'
);

export const submitDeleteFile = createAction(
  '[Delete File] Delete File',
  props<{
    id: string;
    fileId: string;
    fileName: string;
    documentsRequestType: DocumentsRequestType;
  }>()
);
