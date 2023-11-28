import { createAction, props } from '@ngrx/store';
import {
  AcquiringAccountInfo,
  BusinessCheckResult,
  TransactionBlockSummaryResult,
  TransactionSummary,
} from '@shared/model/transaction';

// screen 1.

export const setAllowanceQuantity = createAction(
  '[Issue Allowances] Set Allowance Quantity',
  props<{ quantity: number; year: number }>()
);

export const loadAllowanceWizardData = createAction(
  '[Issue Allowances] Load Allowance Wizard Data'
);

export const loadAllowances = createAction(
  '[Issue Allowances] Load Allowance Table Data '
);

export const loadAllowancesSuccess = createAction(
  '[Issue Allowances] Load Allowance Table Data Success',
  props<{ result: TransactionBlockSummaryResult }>()
);

export const loadAcquiringAccount = createAction(
  '[Issue Allowances] Load Acquiring Account'
);

export const loadAcquiringAccountSuccess = createAction(
  '[Issue Allowances] Load Acquiring Account Success',
  props<{ accountInfo: AcquiringAccountInfo }>()
);

export const enrichAllowanceForSigning = createAction(
  '[Issue Allowances API] Enrich issuance of allowances Proposal to be ready for signing'
);

export const enrichAllowanceForSigningSuccess = createAction(
  '[Issue Allowances API] Enrich issuance of allowances Proposal to be ready for signing success',
  props<{ transactionSummary: TransactionSummary }>()
);

export const navigateToCheckAllowanceAndSignAction = createAction(
  '[Issue Allowances] Navigate to Check allowance and sign Action'
);

export const submitAllowanceProposal = createAction(
  '[Issue Allowances API] Submit Proposal'
);

export const cancelAllowanceProposalRequested = createAction(
  '[Issue Allowances] Cancel Proposal Requested'
);

export const cancelAllowanceProposalConfirmed = createAction(
  '[Issue Allowances] Cancel Proposal Confirmed'
);

export const submitAllowanceProposalSuccess = createAction(
  '[Issue Allowances] Submit Proposal Success',
  props<{ businessCheckResult: BusinessCheckResult }>()
);

export const submitOtpCode = createAction(
  '[Issue Allowances] Submit otpCode',
  props<{ otpCode: string }>()
);

export const setTransactionReference = createAction(
  '[Issue Allowances] Set Transaction Reference',
  props<{ reference: string }>()
);
