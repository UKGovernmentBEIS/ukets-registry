import { createAction, props } from '@ngrx/store';
import { DomainEvent } from '@shared/model/event';
import { TransactionDetails } from '@transaction-management/model';
import {
  ReversedAccountInfo,
  TransactionType,
  UnitType,
} from '@shared/model/transaction';

export const fetchTransaction = createAction(
  '[Transaction Details] Fetch a transaction',
  props<{ transactionIdentifier: string }>()
);

export const fetchTransactionDetailsReport = createAction(
  '[Transaction Details] Fetch a transaction details report',
  props<{ transactionIdentifier: string }>()
);

export const loadTransaction = createAction(
  '[Transaction Details] Load a transaction',
  props<{ transactionDetails: TransactionDetails }>()
);

export const clearTransactionDetails = createAction(
  '[Transaction Details] Clear transaction details'
);

export const fetchTransactionEventHistory = createAction(
  '[Transaction Event History] Fetch transaction events history',
  props<{ transactionIdentifier: string }>()
);

export const fetchTransactionEventHistorySuccess = createAction(
  '[Transaction Event History] Fetch transaction events history success',
  props<{ results: DomainEvent[] }>()
);

export const navigateToCancelTransaction = createAction(
  '[Transaction Details] Navigate to Cancel Transaction'
);

export const cancelTransaction = createAction(
  '[Transaction Details] Cancel transaction',
  props<{ comment: string }>()
);

export const cancelTransactionSuccess = createAction(
  '[Transaction Details] Cancel transaction success',
  props<{ comment: string }>()
);

export const prepareTransactionProposalStateForReversal = createAction(
  '[Transaction Details] Prepare transaction proposal state for reversal',
  props<{
    routeSnapshotUrl: string;
    quantity: string;
    blockType: UnitType;
    transferringAccountIdentifier: string;
    acquiringAccountIdentifier: string;
    transactionReversedIdentifier: string;
    attributes: string;
    transactionType: TransactionType;
  }>()
);

export const prepareTransactionProposalStateForReversalSuccess = createAction(
  '[Transaction Details] Prepare transaction proposal state for reversal success',
  props<{
    reversedAccountInfo: ReversedAccountInfo;
  }>()
);
