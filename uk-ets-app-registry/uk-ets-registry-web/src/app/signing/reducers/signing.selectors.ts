import { createFeatureSelector, createSelector } from '@ngrx/store';
import { signingKey, SigningState } from '@signing/reducers/signing.reducer';

const selectSigningState = createFeatureSelector<SigningState>(signingKey);

export const selectSignature = createSelector(
  selectSigningState,
  state => state.signatureInfo
);

export const selectSignedTransactionType = createSelector(
  selectSigningState,
  state => state.signedDataType
);

export const selectSignatureExists = createSelector(
  selectSigningState,
  state => state.signatureInfo !== null
);
