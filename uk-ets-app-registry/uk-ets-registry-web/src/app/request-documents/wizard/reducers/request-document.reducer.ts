import { Draft } from 'immer';
import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import {
  clearRequestDocuments,
  enterRequestDocumentsWizard,
  fetchCandidateRecipientsSuccess,
  setComment,
  setDeadline,
  setDocumentNames,
  setRecipient,
  submitDocumentsRequestSuccess,
} from '../actions';
import { RequestDocumentsOrigin } from '@shared/model/request-documents/request-documents-origin';
import { User } from '@shared/user';
import { DocumentsRequestType } from '@shared/model/request-documents/documents-request-type';
import dayjs from 'dayjs';
import { parseDeadline } from '@registry-web/shared/shared.util';

export const requestDocumentFeatureKey = 'requestDocument';

export interface RequestDocumentState {
  origin: RequestDocumentsOrigin;
  originatingPath: string;
  parentRequestId: string;
  documentsRequestType: DocumentsRequestType;
  accountName: string;
  accountFullIdentifier: string;
  accountHolderIdentifier: number;
  accountHolderName: string;
  recipientName: string;
  recipientUrid: string;
  documentNames: string[];
  candidateRecipients: User[];
  comment: string;
  submittedRequestIdentifier: string;
  deadline: Date;
}

export const initialState: RequestDocumentState = {
  origin: null,
  originatingPath: null,
  parentRequestId: null,
  documentsRequestType: null,
  accountName: null,
  accountFullIdentifier: null,
  accountHolderIdentifier: null,
  accountHolderName: null,
  recipientName: null,
  recipientUrid: null,
  documentNames: [],
  candidateRecipients: [],
  comment: null,
  submittedRequestIdentifier: null,
  deadline: dayjs().add(4, 'weeks').toDate(),
};

function resetState(state: Draft<RequestDocumentState>) {
  state.origin = initialState.origin;
  state.originatingPath = initialState.originatingPath;
  state.parentRequestId = initialState.parentRequestId;
  state.documentsRequestType = initialState.documentsRequestType;
  state.accountName = initialState.accountName;
  state.accountFullIdentifier = initialState.accountFullIdentifier;
  state.accountHolderIdentifier = initialState.accountHolderIdentifier;
  state.accountHolderName = initialState.accountHolderName;
  state.recipientName = initialState.recipientName;
  state.recipientUrid = initialState.recipientUrid;
  state.documentNames = initialState.documentNames;
  state.candidateRecipients = initialState.candidateRecipients;
  state.comment = initialState.comment;
  state.submittedRequestIdentifier = initialState.submittedRequestIdentifier;
  state.deadline = initialState.deadline;
}

export const requestDocumentReducer = createReducer(
  initialState,

  mutableOn(
    enterRequestDocumentsWizard,
    (
      state,
      {
        origin,
        originatingPath,
        parentRequestId,
        documentsRequestType,
        accountName,
        accountHolderIdentifier,
        accountHolderName,
        recipientName,
        recipientUrid,
        accountFullIdentifier,
      }
    ) => {
      state.origin = origin;
      state.originatingPath = originatingPath;
      state.parentRequestId = parentRequestId;
      state.documentsRequestType = documentsRequestType;
      state.accountName = accountName;
      state.accountFullIdentifier = accountFullIdentifier;
      state.accountHolderIdentifier = accountHolderIdentifier;
      state.accountHolderName = accountHolderName;
      state.recipientName = recipientName;
      state.recipientUrid = recipientUrid;
    }
  ),
  mutableOn(setDocumentNames, (state, { documentNames }) => {
    state.documentNames = documentNames;
  }),
  mutableOn(
    setRecipient,
    (state, { recipientUrid, recipientName, comment }) => {
      state.recipientUrid = recipientUrid;
      state.recipientName = recipientName;
      state.comment = comment;
    }
  ),
  mutableOn(setComment, (state, { comment }) => {
    state.comment = comment;
  }),
  mutableOn(setDeadline, (state, { deadline }) => {
    state.deadline = parseDeadline(deadline);
  }),
  mutableOn(
    fetchCandidateRecipientsSuccess,
    (state, { candidateRecipients }) => {
      state.candidateRecipients = candidateRecipients;
    }
  ),
  mutableOn(clearRequestDocuments, (state) => {
    resetState(state);
  }),
  mutableOn(
    submitDocumentsRequestSuccess,
    (state, { submittedRequestIdentifier }) => {
      state.submittedRequestIdentifier = submittedRequestIdentifier;
    }
  )
);

export function reducer(
  state: RequestDocumentState | undefined,
  action: Action
) {
  return requestDocumentReducer(state, action);
}
