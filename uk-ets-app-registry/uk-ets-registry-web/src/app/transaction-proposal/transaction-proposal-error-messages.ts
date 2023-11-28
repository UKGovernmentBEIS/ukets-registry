import { ErrorDetail } from '@registry-web/shared/error-summary';

export const TransactionProposalErrorsMap: Record<string, ErrorDetail> = {
  NO_ACCOUNT_AND_TRANSFERS_OUTSIDE_TAL_NOT_ALLOWED: new ErrorDetail(
    'continue',
    'You cannot make transfers to an account that is not on the Trusted Account List. You can update the transaction rule to allow transfers to accounts that are not on the Trusted Account List.'
  ),
};