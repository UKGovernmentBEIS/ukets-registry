import { createAction, props } from '@ngrx/store';
import { AccountClaimDTO } from '@registry-web/shared/model/account';

export const ClaimAccountActions = {
  CLEAR_REQUEST: createAction('[Claim Account] Clear request'),

  SUBMIT_REQUEST: createAction(
    '[Claim Account] Submit request',
    props<{ accountClaimDTO: AccountClaimDTO }>()
  ),

  SUBMIT_REQUEST_SUCCESS: createAction(
    '[Claim Account] Submit request success',
    props<{ requestId: string }>()
  ),
};
