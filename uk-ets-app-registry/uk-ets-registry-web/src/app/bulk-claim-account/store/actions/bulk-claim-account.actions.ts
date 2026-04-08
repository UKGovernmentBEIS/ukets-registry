import { createActionGroup, emptyProps, props } from '@ngrx/store';
import { BulkClaimAccountResult } from '@registry-web/bulk-claim-account/model';

export const BulkClaimAccountActions = createActionGroup({
  source: 'BulkClaimAccount',
  events: {
    'Cancel Bulk Claim Account Request': emptyProps(),
    'Clear Bulk Claim Account Request': emptyProps(),
    'Count Eligible Bulk Claim Accounts': emptyProps(),
    'Count Eligible Bulk Claim Accounts Success': props<{
      numberOfAffectedAccounts: number;
    }>(),
    'Count Eligible Bulk Claim Accounts Error': props<{ message: string }>(),
    'Send Bulk Claim Account': emptyProps(),
    'Send Bulk Claim Account Success': props<{
      result: BulkClaimAccountResult;
    }>(),
    'Send Bulk Claim Account Error': props<{ message: string }>(),
  },
});
