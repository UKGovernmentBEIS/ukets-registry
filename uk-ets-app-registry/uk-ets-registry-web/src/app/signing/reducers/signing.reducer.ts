import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import { SigningActions, SigningApiActions } from '@signing/actions';
import { SignatureInfo, SignedDataType } from '@signing/model';

export const signingKey = 'signing';

export interface SigningState {
  signedDataType: SignedDataType;
  signatureInfo: SignatureInfo;
}

export const initialState: SigningState = {
  signatureInfo: null,
  signedDataType: null,
};

const signingReducer = createReducer(
  initialState,

  mutableOn(SigningApiActions.signDataSuccess, (state, { signature }) => {
    state.signatureInfo = signature;
  }),
  mutableOn(SigningActions.signTransaction, (state) => {
    state.signedDataType = SignedDataType.MAIN_TRANSACTION_WIZARD;
  }),
  mutableOn(SigningActions.signEtsTransaction, (state) => {
    state.signedDataType = SignedDataType.ETS_WIZARD;
  }),
  mutableOn(SigningActions.signKpTransaction, (state) => {
    state.signedDataType = SignedDataType.KP_WIZARD;
  }),
  mutableOn(SigningActions.signReturnExcessAllocationTransaction, (state) => {
    state.signedDataType = SignedDataType.RETURN_EXCESS_ALLOCATION_WIZARD;
  }),
  mutableOn(SigningActions.clearSignature, (state) => {
    state.signatureInfo = null;
    state.signedDataType = null;
  })
);

export function reducer(state: SigningState | undefined, action: Action) {
  return signingReducer(state, action);
}
