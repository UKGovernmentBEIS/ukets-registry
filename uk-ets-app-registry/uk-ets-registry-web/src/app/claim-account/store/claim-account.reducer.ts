import { createFeature, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import { ClaimAccountActions } from '@claim-account/store/claim-account.actions';

const featureName = 'claimAccount';

export interface ClaimAccountState {
  submittedRequestIdentifier: string;
}

export const initialState: ClaimAccountState = {
  submittedRequestIdentifier: null,
};

export const claimAccountFeature = createFeature({
  name: featureName,
  reducer: createReducer(
    initialState,

    mutableOn(ClaimAccountActions.CLEAR_REQUEST, (state) => resetState(state)),

    mutableOn(
      ClaimAccountActions.SUBMIT_REQUEST_SUCCESS,
      (state, { requestId }) => {
        state.submittedRequestIdentifier = requestId;
      }
    )
  ),
});

function resetState(state: ClaimAccountState) {
  state.submittedRequestIdentifier = initialState.submittedRequestIdentifier;
}
