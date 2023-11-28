import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';

import { clearMessage, loadMessage } from '../actions';
import { MessageDetails } from '@kp-administration/itl-messages/model';

export const messageDetailsFeatureKey = 'messageDetails';

export interface MessageDetailsState {
  messageDetails: MessageDetails;
}

export const initialState: MessageDetailsState = {
  messageDetails: null
};

const itlMessageDetailsReducer = createReducer(
  initialState,
  mutableOn(loadMessage, (state, { messageDetails }) => {
    state.messageDetails = messageDetails;
  }),
  mutableOn(clearMessage, state => {
    resetState(state);
  })
);

export function reducer(
  state: MessageDetailsState | undefined,
  action: Action
) {
  return itlMessageDetailsReducer(state, action);
}

function resetState(state: MessageDetailsState) {
  state.messageDetails = initialState.messageDetails;
}
