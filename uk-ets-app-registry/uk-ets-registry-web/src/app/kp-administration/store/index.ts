import * as fromMessageList from './reducers/itl-messages.reducer';
import * as fromSendMessage from './reducers/itl-message-send.reducer';
import * as fromMessageDetails from './reducers/itl-message-details.reducer';
import * as fromNotices from './reducers/itl-notices.reducer';
import * as fromReconciliation from './reducers/itl-reconciliation.reducer';

export * from './reducers';
export * from './actions';
export * from './effects';
export * from './selectors';

export const itlMessagesReducers = {
  sendMessage: fromSendMessage.reducer,
  messagesList: fromMessageList.reducer,
  messageDetails: fromMessageDetails.reducer,
  notices: fromNotices.reducer,
  reconciliation: fromReconciliation.reducer,
};
