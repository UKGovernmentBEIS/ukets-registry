import { createAction } from '@ngrx/store';

export const navigateToSelectCommitmentPeriod = createAction(
  '[Issue KP Units] Navigate to select commitment period'
);

export const navigateToSelectUnitTypeAndQuantity = createAction(
  '[Issue KP Units] Navigate to issue unit type and quantity'
);

export const navigateToSetTransactionReference = createAction(
  '[Issue KP Units] Navigate to set transaction reference'
);

export const navigateToCheckTransactionAndSign = createAction(
  '[Issue KP Units] Navigate to Check issuance and sign Action'
);

export const navigateToProposalSubmitted = createAction(
  '[Issue KP Units] Navigate to proposal submitted'
);
