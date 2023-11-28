import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import * as SendMessageActions from '../actions/itl-message-send.actions';

export interface SendMessageState {
  messageId: number;
}

export const initialState: SendMessageState = {
  messageId: null
};

const itlMessageSendReducer = createReducer(
  initialState,
  mutableOn(SendMessageActions.sendMessageSuccess, (state, { messageId }) => {
    state.messageId = messageId;
  })
);

export function reducer(state: SendMessageState | undefined, action: Action) {
  return itlMessageSendReducer(state, action);
}
