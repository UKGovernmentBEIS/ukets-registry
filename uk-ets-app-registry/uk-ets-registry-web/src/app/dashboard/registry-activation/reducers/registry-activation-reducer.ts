import { mutableOn } from '@shared/mutable-on';
import {
  Action,
  createFeatureSelector,
  createReducer,
  createSelector
} from '@ngrx/store';
import { submitNewRegistryActivationCodeRequestSuccess } from '../actions/registry-activation.actions';

export const registryActivationFeatureKey = 'registry-activation';

export interface RegistryActivationState {
  requestId: string;
}

export const initialState: RegistryActivationState = {
  requestId: null
};

const registryActivationReducer = createReducer(
  initialState,
  mutableOn(
    submitNewRegistryActivationCodeRequestSuccess,
    (state, { requestId }) => {
      state.requestId = requestId;
    }
  )
  // mutableOn(clearState, state => {
  //   resetState(state);
  // })
);

export function reducer(
  state: RegistryActivationState | undefined,
  action: Action
) {
  return registryActivationReducer(state, action);
}

function resetState(state) {
  state.requestId = initialState.requestId;
}

const selectRegistryActivation = createFeatureSelector<RegistryActivationState>(
  registryActivationFeatureKey
);

export const selectRequestId = createSelector(
  selectRegistryActivation,
  state => state.requestId
);
