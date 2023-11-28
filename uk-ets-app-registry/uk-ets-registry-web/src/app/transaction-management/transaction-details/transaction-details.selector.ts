import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  transactionDetailsFeatureKey,
  TransactionDetailsState,
} from './transaction-details.reducer';
import { TRANSACTION_TYPES_VALUES } from '@shared/model/transaction';

const selectTransactionDetailsState =
  createFeatureSelector<TransactionDetailsState>(transactionDetailsFeatureKey);

export const selectTransaction = createSelector(
  selectTransactionDetailsState,
  (state) => state.transactionDetails
);

export const selectTransactionType = createSelector(
  selectTransactionDetailsState,
  (state) => state.transactionDetails?.type
);

export const selectIsETSTransaction = createSelector(
  selectTransactionType,
  (transaction) => {
    {
      return TRANSACTION_TYPES_VALUES[transaction].isETSTransaction;
    }
  }
);

export const selectTransactionBlock = createSelector(
  selectTransactionDetailsState,
  (state) => state.transactionBlocks
);

export const selectTransactionReference = createSelector(
  selectTransactionDetailsState,
  (state) => state.transactionDetails.reference
);

export const selectTransactionResponse = createSelector(
  selectTransactionDetailsState,
  (state) => state.transactionResponses
);

export const selectTransactionEventHistory = createSelector(
  selectTransactionDetailsState,
  (state) => state.eventHistory
);

export const areTransactionDetailsLoaded = createSelector(
  selectTransactionDetailsState,
  (state) => state.transactionDetailsLoaded
);
