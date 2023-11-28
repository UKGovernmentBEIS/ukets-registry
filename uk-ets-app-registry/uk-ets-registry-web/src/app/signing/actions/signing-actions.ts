import { createAction, props } from '@ngrx/store';
import { SignatureInfo, TransactionSigningInfo } from '@signing/model';
import { ReturnExcessAllocationTransactionSigningInfo } from '@signing/model/signing-models';

export const signTransaction = createAction(
  '[Signing] Submit Main Transaction and OTP code for signing',
  props<{ transactionSignInfo: TransactionSigningInfo }>()
);

export const signTransactionSuccess = createAction(
  '[Signing] Submit Main Transaction and OTP code for signing success',
  props<{ signature: SignatureInfo }>()
);

export const signReturnExcessAllocationTransaction = createAction(
  '[Signing] Submit Main Transaction and OTP code for signing NAT and NER',
  props<{ transactionSignInfo: ReturnExcessAllocationTransactionSigningInfo }>()
);

export const signReturnExcessAllocationTransactionSuccess = createAction(
  '[Signing] Submit Main Transaction and OTP code for signing success NAT and NER',
  props<{ signature: SignatureInfo }>()
);

export const signKpTransaction = createAction(
  '[Signing] Submit KP Transaction - IssueOfAAUsAndRMUs  and OTP code for signing',
  props<{ transactionSignInfo: TransactionSigningInfo }>()
);

export const signKpTransactionSuccess = createAction(
  '[Signing] Submit KP Transaction - IssueOfAAUsAndRMUs and OTP code for signing success',
  props<{ signature: SignatureInfo }>()
);

export const signEtsTransaction = createAction(
  '[Signing] Submit ETS - IssueAllowances and OTP code for signing',
  props<{ transactionSignInfo: TransactionSigningInfo }>()
);

export const signEtsTransactionSuccess = createAction(
  '[Signing] Submit ETS - IssueAllowances and OTP code for signing success',
  props<{ signature: SignatureInfo }>()
);

export const clearSignature = createAction(
  '[Signing] Clear signature after signed request submitted successfully to registry'
);
