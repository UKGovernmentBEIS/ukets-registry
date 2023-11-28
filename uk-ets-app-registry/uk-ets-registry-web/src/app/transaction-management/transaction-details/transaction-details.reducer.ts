import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import { DomainEvent } from '@shared/model/event';
import { TransactionDetailsActions } from '@transaction-management/transaction-details/actions';
import {
  TransactionBlock,
  TransactionDetails,
  TransactionResponse,
} from '@transaction-management/model';

export const transactionDetailsFeatureKey = 'transactionDetails';

export interface TransactionDetailsState {
  transactionDetails: TransactionDetails;
  transactionDetailsLoaded: boolean;
  transactionBlocks: Array<TransactionBlock>;
  eventHistory: Array<DomainEvent>;
  transactionResponses: Array<TransactionResponse>;
  loading: boolean;
  loadingError?: string;
  transactionHeaderVisible: boolean;
  transactionHeaderBackToListVisible: boolean;
}

export const initialState: TransactionDetailsState = {
  transactionDetails: {
    identifier: null,
    taskIdentifier: null,
    type: null,
    status: null,
    quantity: null,
    acquiringAccountName: null,
    externalAcquiringAccount: null,
    acquiringAccountIdentifier: null,
    acquiringAccountType: null,
    acquiringAccountRegistryCode: null,
    acquiringAccountFullIdentifier: null,
    hasAccessToAcquiringAccount: null,
    transferringAccountName: null,
    externalTransferringAccount: null,
    transferringAccountIdentifier: null,
    transferringAccountType: null,
    transferringRegistryCode: null,
    transferringAccountFullIdentifier: null,
    hasAccessToTransferringAccount: null,
    transferringAccountStatus: null,
    acquiringAccountStatus: null,
    started: null,
    lastUpdated: null,
    blocks: null,
    responses: null,
    unitType: null,
    attributes: null,
    itlNotification: null,
    canBeReversed: false,
    executionDateTime: null,
  },
  transactionDetailsLoaded: false,
  transactionBlocks: [],
  eventHistory: [],
  transactionResponses: [],
  loading: false,
  transactionHeaderVisible: false,
  transactionHeaderBackToListVisible: false,
};

const transactionDetailsReducer = createReducer(
  initialState,
  mutableOn(TransactionDetailsActions.fetchTransaction, (state) => {
    state.transactionDetailsLoaded = false;
  }),
  mutableOn(
    TransactionDetailsActions.loadTransaction,
    (state, { transactionDetails }) => {
      state.transactionDetails = transactionDetails;
      state.transactionBlocks = transactionDetails.blocks;
      state.transactionResponses = transactionDetails.responses;
      state.transactionDetailsLoaded = true;
    }
  ),
  mutableOn(
    TransactionDetailsActions.fetchTransactionEventHistory,
    (state, { transactionIdentifier }) => {
      state.loading = true;
    }
  ),
  mutableOn(
    TransactionDetailsActions.fetchTransactionEventHistorySuccess,
    (state, { results }) => {
      state.eventHistory = results;
      state.loading = false;
    }
  ),
  mutableOn(TransactionDetailsActions.clearTransactionDetails, (state) => {
    resetState(state);
  })
);

export function reducer(
  state: TransactionDetailsState | undefined,
  action: Action
) {
  return transactionDetailsReducer(state, action);
}

function resetState(state: TransactionDetailsState) {
  state.transactionDetails = initialState.transactionDetails;
  state.transactionBlocks = initialState.transactionBlocks;
  state.eventHistory = initialState.eventHistory;
  state.transactionResponses = initialState.transactionResponses;
  state.loading = initialState.loading;
  state.transactionHeaderVisible = initialState.transactionHeaderVisible;
  state.transactionHeaderBackToListVisible =
    initialState.transactionHeaderBackToListVisible;
}
