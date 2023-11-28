import { verifyBeforeAndAfterActionDispatched } from '../../../testing/helpers/reducer.test.helper';

import { TransactionDetails } from '../model';
import {
  reducer,
  TransactionDetailsState,
} from './transaction-details.reducer';
import { TransactionDetailsActions } from '@transaction-management/transaction-details/actions';

describe('Transaction Details reducer', () => {
  const details: TransactionDetails = {
    identifier: 'test-identifier',
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
    transferringAccountName: null,
    externalTransferringAccount: null,
    transferringAccountIdentifier: null,
    transferringAccountType: null,
    transferringRegistryCode: null,
    transferringAccountFullIdentifier: null,
    started: null,
    lastUpdated: null,
    blocks: null,
    responses: null,
    unitType: null,
    hasAccessToAcquiringAccount: null,
    hasAccessToTransferringAccount: null,
    executionDateTime: null,
  };

  const fetchedTransactionDetails: TransactionDetails = {
    identifier: 'fetched transaction test identifier',
    taskIdentifier: '12321',
    type: 'A TYPE FOR TEST',
    status: null,
    quantity: null,
    acquiringAccountName: null,
    externalAcquiringAccount: null,
    acquiringAccountIdentifier: null,
    acquiringAccountType: null,
    acquiringAccountRegistryCode: null,
    acquiringAccountFullIdentifier: null,
    transferringAccountName: null,
    externalTransferringAccount: null,
    transferringAccountIdentifier: null,
    transferringAccountType: null,
    transferringRegistryCode: null,
    transferringAccountFullIdentifier: null,
    started: null,
    lastUpdated: null,
    blocks: null,
    responses: null,
    unitType: null,
    hasAccessToTransferringAccount: null,
    hasAccessToAcquiringAccount: null,
    executionDateTime: null,
  };

  it('After fetch transaction details action have been dispatched, the transaction details loaded flag should be set to false', () => {
    verifyBeforeAndAfterActionDispatched<TransactionDetailsState>(
      reducer,
      {
        transactionDetails: details,
        transactionDetailsLoaded: true,
        transactionBlocks: [],
        eventHistory: [],
        transactionResponses: [],
        loading: false,
        transactionHeaderVisible: false,
        transactionHeaderBackToListVisible: false,
      },
      (state) => {
        expect(state.transactionDetailsLoaded).toBeTruthy();
      },
      TransactionDetailsActions.fetchTransaction({
        transactionIdentifier: '213213',
      }),
      (state) => expect(state.transactionDetailsLoaded).toBeFalsy()
    );
  });

  it(`After load transaction details action have been dispatched the transaction details loaded flag should be set to true
  and the new transaction should be loaded to store.`, () => {
    verifyBeforeAndAfterActionDispatched<TransactionDetailsState>(
      reducer,
      {
        transactionDetails: details,
        transactionDetailsLoaded: false,
        transactionBlocks: [],
        eventHistory: [],
        transactionResponses: [],
        loading: false,
        transactionHeaderVisible: false,
        transactionHeaderBackToListVisible: false,
      },
      (state) => {
        expect(state.transactionDetailsLoaded).toBeFalsy();
        expect(state.transactionDetails).toBe(details);
      },
      TransactionDetailsActions.loadTransaction({
        transactionDetails: fetchedTransactionDetails,
      }),
      (state) => {
        expect(state.transactionDetails).toBe(fetchedTransactionDetails);
        expect(state.transactionDetailsLoaded).toBeTruthy();
      }
    );
  });
});
