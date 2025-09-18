import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  convertDateForDatepicker,
  convertDateToUkDate,
} from '@registry-web/shared/shared.util';
import {
  requestDocumentFeatureKey,
  RequestDocumentState,
} from '@request-documents/wizard/reducers/request-document.reducer';
import { DocumentsRequestType } from '@shared/model/request-documents/documents-request-type';
import { RequestDocumentsOrigin } from '@shared/model/request-documents/request-documents-origin';

export const selectRequestDocumentsState =
  createFeatureSelector<RequestDocumentState>(requestDocumentFeatureKey);

export const selectRequestDocumentsOrigin = createSelector(
  selectRequestDocumentsState,
  (state) => state.origin
);

export const checkAccountHolderOrigin = createSelector(
  selectRequestDocumentsOrigin,
  (origin) => {
    return (
      origin === RequestDocumentsOrigin.ACCOUNT_DETAILS ||
      origin === RequestDocumentsOrigin.ACCOUNT_HOLDER_UPDATE_DETAILS_TASK
    );
  }
);

export const selectOriginatingPath = createSelector(
  selectRequestDocumentsState,
  (state) => state.originatingPath
);

export const selectParentRequestId = createSelector(
  selectRequestDocumentsState,
  (state) => state.parentRequestId
);

export const selectDocumentsRequestType = createSelector(
  selectRequestDocumentsState,
  (state) => state.documentsRequestType
);

export const selectDeadline = createSelector(
  selectRequestDocumentsState,
  (state) => convertDateForDatepicker(state.deadline)
);

export const selectDeadlineUKDate = createSelector(
  selectRequestDocumentsState,
  (state) => convertDateToUkDate(state.deadline)
);

export const selectDeadlineDate = createSelector(
  selectRequestDocumentsState,
  (state) => state.deadline
);

export const displayUserCommentsPage = createSelector(
  selectRequestDocumentsOrigin,
  selectDocumentsRequestType,
  (origin, documentRequestType) => {
    return (
      documentRequestType === DocumentsRequestType.USER ||
      origin === RequestDocumentsOrigin.ACCOUNT_OPENING_TASK
    );
  }
);

export const selectAccountName = createSelector(
  selectRequestDocumentsState,
  (state) => state.accountName
);

export const selectAccountFullIdentifier = createSelector(
  selectRequestDocumentsState,
  (state) => state.accountFullIdentifier
);

export const selectAccountHolderIdentifier = createSelector(
  selectRequestDocumentsState,
  (state) => state.accountHolderIdentifier
);

export const selectAccountHolderName = createSelector(
  selectRequestDocumentsState,
  (state) => state.accountHolderName
);

export const selectRecipientName = createSelector(
  selectRequestDocumentsState,
  (state) => state.recipientName
);

export const selectRecipientUrid = createSelector(
  selectRequestDocumentsState,
  (state) => state.recipientUrid
);

export const selectDocumentNames = createSelector(
  selectRequestDocumentsState,
  (state) => state.documentNames
);

export const selectCandidateRecipients = createSelector(
  selectRequestDocumentsState,
  (state) => state.candidateRecipients
);

export const selectComment = createSelector(
  selectRequestDocumentsState,
  (state) => state.comment
);

export const selectSubmittedRequestIdentifier = createSelector(
  selectRequestDocumentsState,
  (state) => state.submittedRequestIdentifier
);

export const calculateGoBackPathFromCheckPage = createSelector(
  selectDocumentsRequestType,
  selectRequestDocumentsOrigin,
  (documentsRequestType, origin) => {
    if (
      documentsRequestType === DocumentsRequestType.ACCOUNT_HOLDER &&
      (origin === RequestDocumentsOrigin.ACCOUNT_DETAILS ||
        origin === RequestDocumentsOrigin.ACCOUNT_HOLDER_UPDATE_DETAILS_TASK)
    ) {
      return 'select-recipient';
    } else {
      return 'assigning-user-comment';
    }
  }
);

export const selectDocumentsRequest = createSelector(
  selectRequestDocumentsOrigin,
  selectParentRequestId,
  selectDocumentsRequestType,
  selectDocumentNames,
  selectAccountHolderIdentifier,
  selectRecipientUrid,
  selectComment,
  selectAccountFullIdentifier,
  selectDeadlineDate,
  (
    requestDocumentsOrigin,
    parentRequestId,
    documentsRequestType,
    documentNames,
    accountHolderIdentifier,
    recipientUrid,
    comment,
    accountFullIdentifier,
    deadline
  ) => {
    return {
      type: documentsRequestType,
      documentNames,
      accountHolderIdentifier: accountHolderIdentifier
        ? accountHolderIdentifier.toString()
        : null,
      recipientUrid,
      comment,
      parentRequestId,
      accountFullIdentifier,
      deadline,
    };
  }
);
