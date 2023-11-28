import { createAction, props } from '@ngrx/store';
import { SigningRequestInfo, SignatureInfo } from '@signing/model';

export const signData = createAction(
  '[Signing API] Submit raw data and OTP code for signing',
  props<{ signingRequestInfo: SigningRequestInfo }>()
);

export const signDataSuccess = createAction(
  '[Signing API] Submit raw data and OTP code for signing success',
  props<{ signature: SignatureInfo }>()
);
