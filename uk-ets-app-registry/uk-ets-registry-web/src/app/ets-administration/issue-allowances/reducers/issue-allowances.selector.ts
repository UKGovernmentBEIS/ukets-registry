import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  issueAllowanceReducer,
  IssueAllowancesState,
} from './issue-allowances.reducer';
import {
  TransactionSummary,
  TransactionType,
  UnitType,
} from '@shared/model/transaction';

const selectIssueAllowancesState = createFeatureSelector<IssueAllowancesState>(
  issueAllowanceReducer
);

export const selectSelectedQuantity = createSelector(
  selectIssueAllowancesState,
  (state) => state.selectedQuantity
);

export const selectAllowanceBlocks = createSelector(
  selectIssueAllowancesState,
  (state) => state.allowanceBlocks
);

export const selectAcquiringAccountInfo = createSelector(
  selectIssueAllowancesState,
  (state) => state.acquiringAccountInfo
);

export const selectEnrichedAllowanceTransactionSummaryReadyForSigning =
  createSelector(
    selectIssueAllowancesState,
    (state) => state.enrichedTransactionSummaryReadyForSigning
  );

export const selectTransactionReference = createSelector(
  selectIssueAllowancesState,
  (state) => state.transactionReference
);

export const selectAllowanceTransaction = createSelector(
  selectSelectedQuantity,
  selectAcquiringAccountInfo,
  selectEnrichedAllowanceTransactionSummaryReadyForSigning,
  selectTransactionReference,
  (quantity, acquiringAccountInfo, signedSummary, reference) => {
    return {
      type: TransactionType.IssueAllowances,
      acquiringAccountIdentifier: acquiringAccountInfo?.identifier,
      blocks: [
        {
          quantity,
          type: UnitType.ALLOWANCE,
        },
      ],
      identifier: signedSummary.identifier,
      reference: reference,
    } as TransactionSummary;
  }
);

export const selectBusinessCheckResult = createSelector(
  selectIssueAllowancesState,
  (state) => state.businessCheckResult
);
