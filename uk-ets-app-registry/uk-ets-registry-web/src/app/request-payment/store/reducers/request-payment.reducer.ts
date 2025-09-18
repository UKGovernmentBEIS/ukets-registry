import { createReducer, on } from '@ngrx/store';
import { RequestPaymentOrigin } from '@request-payment/model';
import { User } from '@shared/user';
import {
  clearRequestPayment,
  enterRequestPaymentWizard,
  setPaymentDetails,
  submitPaymentRequestSuccess,
} from '@request-payment/store/actions';

export const requestPaymentFeatureKey = 'requestPayment';

export interface RequestPaymentState {
  origin: RequestPaymentOrigin;
  originatingPath: string;
  parentRequestId: string;
  candidateRecipients: User[];
  amount: number;
  description: string;
  recipientName: string;
  recipientUrid: string;
  submittedRequestIdentifier: string;
}

export const initialState: RequestPaymentState = {
  origin: null,
  originatingPath: null,
  parentRequestId: null,
  candidateRecipients: [],
  amount: null,
  description: null,
  recipientName: null,
  recipientUrid: null,
  submittedRequestIdentifier: null,
};

export const requestPaymentReducer = createReducer(
  initialState,
  on(
    clearRequestPayment,
    (state, action): RequestPaymentState => ({
      origin: initialState.origin,
      originatingPath: initialState.originatingPath,
      parentRequestId: initialState.parentRequestId,
      amount: initialState.amount,
      description: initialState.description,
      recipientName: initialState.recipientName,
      recipientUrid: initialState.recipientUrid,
      candidateRecipients: initialState.candidateRecipients,
      submittedRequestIdentifier: initialState.submittedRequestIdentifier,
    })
  ),
  on(
    enterRequestPaymentWizard,
    (state, action): RequestPaymentState => ({
      ...state,
      origin: action.origin,
      originatingPath: action.originatingPath,
      parentRequestId: action.parentRequestId,
      candidateRecipients: action.candidateRecipients,
    })
  ),
  on(
    setPaymentDetails,
    (state, action): RequestPaymentState => ({
      ...state,
      amount: action.amount,
      description: action.description,
      recipientUrid: action.recipientUrid,
      recipientName: action.recipientName,
    })
  ),
  on(
    submitPaymentRequestSuccess,
    (state, action): RequestPaymentState => ({
      ...state,
      submittedRequestIdentifier: action.submittedRequestIdentifier,
    })
  )
);
