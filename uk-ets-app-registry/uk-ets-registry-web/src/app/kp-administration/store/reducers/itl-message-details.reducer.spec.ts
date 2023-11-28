import { MessageDetails } from '@kp-administration/itl-messages/model';
import {
  initialState,
  reducer
} from '@kp-administration/store/reducers/itl-message-details.reducer';
import { loadMessage } from '@kp-administration/store';

describe('ITL Message Details reducer', () => {
  it('loads the details', () => {
    const messageDetails: MessageDetails = {
      messageId: 12,
      messageDate: '2020-11-04T16:39:09.00',
      content: 'A sample content'
    };

    const beforeLoadMessageState = reducer(initialState, {} as any);
    expect(beforeLoadMessageState.messageDetails).toBeNull();

    const loadMessageAction = loadMessage({ messageDetails });

    const afterLoadMessageState = reducer(
      beforeLoadMessageState,
      loadMessageAction
    );
    expect(afterLoadMessageState.messageDetails).toStrictEqual(messageDetails);
  });
});
