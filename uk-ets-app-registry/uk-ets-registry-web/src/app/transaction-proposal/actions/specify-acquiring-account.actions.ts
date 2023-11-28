import { createAction, props } from '@ngrx/store';
import {
  AcquiringAccountInfo,
  BusinessCheckResult,
  CandidateAcquiringAccounts,
  TransactionSummary,
} from '@shared/model/transaction';
import { UserDefinedAccountParts } from '@shared/model/account';

export const setAcquiringAccount = createAction(
  '[Transaction Proposal] Specify Predefined Acquiring Account And Clear User Defined Selection',
  props<{ acquiringAccount: AcquiringAccountInfo }>()
);

export const setUserDefinedAcquiringAccount = createAction(
  '[Transaction Proposal] Specify User Defined Acquiring Account And Clear Predifined Selection',
  props<{ userDefinedAcquiringAccount: UserDefinedAccountParts }>()
);

export const getTrustedAccounts = createAction(
  '[Transaction Proposal] Get Trusted Accounts for Proposal',
  props<{ accountId: number }>()
);

export const getTrustedAccountsSuccess = createAction(
  '[Transaction Proposal] Get Trusted Accounts for Proposal Success',
  props<{ trustedAccountsResult: CandidateAcquiringAccounts }>()
);

export const validateTrustedAccount = createAction(
  '[Transaction Proposal] Validate Specified Account',
  props<{ transactionSummary: TransactionSummary }>()
);

export const validateTrustedAccountSuccess = createAction(
  '[Transaction Proposal] Validate Specified Account Success',
  props<{ businessCheckResult: BusinessCheckResult }>()
);

export const enrichUserDefinedAcquiringAccount = createAction(
  '[Transaction Proposal] Enrich User Defined Acquiring Account',
  props<{
    transferringAccountId: number;
    fullIdentifier: string;
  }>()
);

export const enrichUserDefinedAcquiringAccountSuccess = createAction(
  '[Transaction Proposal] Enrich Used Defined Acquiring Account Success',
  props<{ acquiringAccount: AcquiringAccountInfo }>()
);

export const populateAcquiringAccount = createAction(
  '[Transaction Proposal] Populate Acquiring Account In Skip Scenario'
);

export const populateExcessAllocationAcquiringAccounts = createAction(
  '[Transaction Proposal] Populate Excess Allocation Acquiring Account In Skip Scenario'
);

export const populateAcquiringAccountSuccess = createAction(
  '[Transaction Proposal] Populate Acquiring Account In Skip Scenario Success',
  props<{ acquiringAccountInfo: AcquiringAccountInfo }>()
);

export const populateExcessAllocationAcquiringAccountsSuccess = createAction(
  '[Transaction Proposal] Populate Excess Acquiring Accounts In Skip Scenario Success',
  props<{
    natAcquiringAccount: AcquiringAccountInfo;
    nerAcquiringAccount: AcquiringAccountInfo;
  }>()
);
