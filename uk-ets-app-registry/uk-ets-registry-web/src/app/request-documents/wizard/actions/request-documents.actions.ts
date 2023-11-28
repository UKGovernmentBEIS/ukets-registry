import { createAction, props } from '@ngrx/store';
import { RequestDocumentsOrigin } from '@shared/model/request-documents/request-documents-origin';
import { User } from '@shared/user';
import { DocumentsRequestType } from '@shared/model/request-documents/documents-request-type';

export const enterRequestDocumentsWizard = createAction(
  '[Request Documents] Enter wizard',
  props<{
    origin: RequestDocumentsOrigin;
    originatingPath: string;
    parentRequestId?: string;
    documentsRequestType: DocumentsRequestType;
    accountName?: string;
    accountFullIdentifier?: string;
    accountHolderIdentifier?: number;
    accountHolderName?: string;
    recipientName?: string;
    recipientUrid?: string;
  }>()
);

export const setDocumentNames = createAction(
  '[Request Documents] Set document names',
  props<{ documentNames: string[] }>()
);

export const setRecipient = createAction(
  '[Request Documents] Set recipient',
  props<{ recipientUrid: string; recipientName: string; comment: string }>()
);

export const setComment = createAction(
  '[Request Documents] Set comment',
  props<{ comment: string }>()
);

export const fetchCandidateRecipients = createAction(
  '[Request Documents] Fetch candidate recipients'
);

export const fetchCandidateRecipientsSuccess = createAction(
  '[Request Documents] Fetch candidate recipients success',
  props<{ candidateRecipients: User[] }>()
);

export const cancelRequestDocuments = createAction(
  '[Request Documents] Cancel Request Documents',
  props<{ route: string }>()
);

export const cancelRequestDocumentsConfirmed = createAction(
  '[Request Documents] Cancel Request Documents Confirmed'
);

export const clearRequestDocuments = createAction(
  '[Request Documents] Clear Request Documents'
);

export const navigateToSelectDocuments = createAction(
  '[Request Documents] Navigate to Select Documents'
);

export const navigateToSelectRecipient = createAction(
  '[Request Documents] Navigate to Select Recipient'
);

export const navigateToAssignUserComment = createAction(
  '[Request Documents] Navigate to Assign User comment'
);

export const submitDocumentsRequest = createAction(
  '[Request Documents] Submit Documents Request'
);

export const submitDocumentsRequestSuccess = createAction(
  '[Request Documents] Submit Documents Request Success',
  props<{ submittedRequestIdentifier: string }>()
);
