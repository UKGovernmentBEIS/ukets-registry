import { createActionGroup, emptyProps } from '@ngrx/store';

export const BulkClaimAccountNavigationActions = createActionGroup({
  source: 'BulkClaimAccountNavigation',
  events: {
    'Navigate To Bulk Account Claim Check Request and Submit': emptyProps(),
    'Navigate To Bulk Account Claim Submitted': emptyProps(),
    'Navigate To Bulk Account Claim Cancel': emptyProps(),
    'Navigate To Bulk Account Claim': emptyProps(),
  },
});
