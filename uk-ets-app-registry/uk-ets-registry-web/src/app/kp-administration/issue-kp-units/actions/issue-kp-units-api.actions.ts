import { createAction, props } from '@ngrx/store';
import {
  BusinessCheckResult,
  TransactionSummary
} from '@shared/model/transaction';

export const issuanceTransactionValidate = createAction(
  '[Issue KP Units API] Validate issuance transaction',
  props<{ transactionSummary: TransactionSummary }>()
);

export const issuanceTransactionValidateSuccess = createAction(
  '[Issue KP Units API] Validate issuance transaction success',
  props<{ businessCheckResult: BusinessCheckResult }>()
);

export const issuanceTransactionEnrichProposalForSigning = createAction(
  '[Transaction Proposal API] Enrich issuance Proposal to be ready for signing'
);

export const issuanceTransactionEnrichProposalForSigningSuccess = createAction(
  '[Issue KP Units API] Enrich issuance Proposal to be ready for signing success',
  props<{ transactionSummary: TransactionSummary }>()
);
