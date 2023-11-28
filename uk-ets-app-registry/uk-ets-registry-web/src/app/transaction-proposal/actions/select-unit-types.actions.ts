import { createAction, props } from '@ngrx/store';
import { UserDefinedAccountParts } from '@shared/model/account';
import { ReturnExcessAllocationType } from '@shared/model/allocation';
import {
  BusinessCheckResult,
  ReturnExcessAllocationTransactionSummary,
  TransactionBlockSummary,
  TransactionBlockSummaryResult,
  TransactionSummary,
} from '@shared/model/transaction';
import { ExcessAmountPerAllocationAccount } from '@transaction-proposal/model/transaction-proposal-model';

export const getTransactionBlockSummariesResult = createAction(
  '[Transaction Proposal] Get Transaction Block Summaries Result',
  props<{
    accountId: string;
    transactionType: string;
    itlNotificationIdentifier: number;
  }>()
);

export const getTransactionBlockSummaryResultSuccess = createAction(
  '[Transaction Proposal] Get Transaction Block Summaries Result Success',
  props<{ transactionBlockSummaryResult: TransactionBlockSummaryResult }>()
);

export const setSelectedBlockSummaries = createAction(
  '[Transaction Proposal] Set Selected Unit types and quantities',
  props<{
    selectedTransactionBlockSummaries: TransactionBlockSummary[];
    clearNextStepsInWizard: boolean;
    toBeReplacedUnitsHoldingAccountParts: UserDefinedAccountParts;
  }>()
);

export const setCalculatedReturnExcessAllocationQuantitiesAndType =
  createAction(
    '[Transaction Proposal] Set Calculated Excess Amounts Per Allocation Account and Return Excess Allocation Type'
  );

export const setReturnExcessAmountsAndType = createAction(
  '[Transaction Proposal] Set Selected Excess Amounts Per Allocation Account and Return Excess Allocation Type',
  props<{
    selectedExcessAmounts: ExcessAmountPerAllocationAccount;
    returnExcessAllocationType: ReturnExcessAllocationType;
  }>()
);

export const validateSelectedBlockSummaries = createAction(
  '[Transaction Proposal] Validate Selected Unit types and quantities',
  props<{ transactionSummary: TransactionSummary }>()
);

export const validateSelectedBlockSummariesSuccess = createAction(
  '[Transaction Proposal] Validate Selected Unit types and quantities success',
  props<{ businessCheckResult: BusinessCheckResult }>()
);

export const validateSelectedBlockSummariesNatAndNer = createAction(
  '[Transaction Proposal] Validate Selected Unit types and quantities NAT and NER',
  props<{
    returnExcessAllocationTransactionSummary: ReturnExcessAllocationTransactionSummary;
  }>()
);

export const validateSelectedBlockSummariesNatAndNerSuccess = createAction(
  '[Transaction Proposal] Validate Selected Unit types and quantities success  NAT and NER',
  props<{ businessCheckResult: BusinessCheckResult }>()
);
