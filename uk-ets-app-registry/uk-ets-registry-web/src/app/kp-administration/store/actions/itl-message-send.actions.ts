import { createAction, props } from '@ngrx/store';
import { SendMessageResponse } from '@kp-administration/itl-messages/model';

export const sendMessage = createAction(
  '[ITL Message] Send ITL Message',
  props<{ content: string }>()
);

export const sendMessageSuccess = createAction(
  '[ITL Message] Send ITL Message success',
  props<SendMessageResponse>()
);
