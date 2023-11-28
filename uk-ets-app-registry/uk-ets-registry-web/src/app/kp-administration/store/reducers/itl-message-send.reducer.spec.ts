import { SendMessageResponse } from '@kp-administration/itl-messages/model';
import {
  initialState,
  reducer
} from '@kp-administration/store/reducers/itl-message-send.reducer';
import { sendMessageSuccess } from '@kp-administration/store';

describe('ITL Send Message reducer', () => {
  it('sets the messageId', () => {
    const response: SendMessageResponse = {
      messageId: 12,
      success: true
    };

    const beforeSendMessageSuccessState = reducer(initialState, {} as any);
    expect(beforeSendMessageSuccessState.messageId).toBeNull();

    const sendMessageSuccessAction = sendMessageSuccess(response);

    const afterSendMessageSuccessState = reducer(
      beforeSendMessageSuccessState,
      sendMessageSuccessAction
    );
    expect(afterSendMessageSuccessState.messageId).toStrictEqual(
      response.messageId
    );
  });
});
