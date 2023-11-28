import { createAction, props } from '@ngrx/store';
import { MessageDetails } from '@kp-administration/itl-messages/model';

export const fetchMessage = createAction(
  '[Message Details] Fetch a message',
  props<{ messageId: string }>()
);

export const loadMessage = createAction(
  '[Message Details] Load a message',
  props<{ messageDetails: MessageDetails }>()
);

export const clearMessage = createAction(
  '[Message Details] Clear message details'
);
