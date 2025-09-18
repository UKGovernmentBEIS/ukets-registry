import { createAction, props } from '@ngrx/store';
import { RequestPaymentOrigin } from '@request-payment/model';
import { User } from '@shared/user';

export const enterRequestPaymentWizard = createAction(
  '[Request Payment] Enter wizard',
  props<{
    origin: RequestPaymentOrigin;
    originatingPath: string;
    parentRequestId: string;
    candidateRecipients: User[];
  }>()
);

export const setPaymentDetails = createAction(
  '[Request Payment] Set Payment Details',
  props<{
    recipientUrid: string;
    recipientName: string;
    amount: number;
    description: string;
  }>()
);

export const cancelRequestPayment = createAction(
  '[Request Payment] Cancel Request Payment',
  props<{ route: string }>()
);

export const cancelRequestPaymentConfirmed = createAction(
  '[Request Payment] Cancel Request Payment Confirmed'
);

export const clearRequestPayment = createAction(
  '[Request Payment] Clear Request Payment'
);

export const submitPaymentRequest = createAction(
  '[Request Payment] Submit Payment Request'
);

export const submitPaymentRequestSuccess = createAction(
  '[Request Payment] Submit Payment Request Success',
  props<{ submittedRequestIdentifier: string }>()
);

export const downloadInvoice = createAction(
  '[Request Payment] Download Invoice'
);

export const downloadInvoiceSuccess = createAction(
  '[Request Payment] Download Invoice Success',
  props<{ invoice: Blob }>()
);
