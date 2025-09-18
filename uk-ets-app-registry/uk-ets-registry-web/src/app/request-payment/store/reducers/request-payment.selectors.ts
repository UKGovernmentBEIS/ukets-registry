import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  requestPaymentFeatureKey,
  RequestPaymentState,
} from '@request-payment/store/reducers';

const selectRequestPaymentState = createFeatureSelector<RequestPaymentState>(
  requestPaymentFeatureKey
);

export const selectSubmittedRequestIdentifier = createSelector(
  selectRequestPaymentState,
  (state) => state.submittedRequestIdentifier
);

export const selectCandidateRecipients = createSelector(
  selectRequestPaymentState,
  (state) => state.candidateRecipients
);

export const selectRecipientUrid = createSelector(
  selectRequestPaymentState,
  (state) => state.recipientUrid
);

export const selectRecipientName = createSelector(
  selectRequestPaymentState,
  (state) => state.recipientName
);

export const selectAmount = createSelector(
  selectRequestPaymentState,
  (state) => state.amount
);

export const selectDescription = createSelector(
  selectRequestPaymentState,
  (state) => state.description
);

export const selectOriginatingPath = createSelector(
  selectRequestPaymentState,
  (state) => state.originatingPath
);

export const selectParentRequestId = createSelector(
  selectRequestPaymentState,
  (state) => state.parentRequestId
);

export const selectPaymentRequest = createSelector(
  selectParentRequestId,
  selectAmount,
  selectDescription,
  selectRecipientUrid,
  (parentRequestId, amount, description, recipientUrid) => {
    return {
      parentRequestId,
      amount,
      description,
      recipientUrid,
    };
  }
);
