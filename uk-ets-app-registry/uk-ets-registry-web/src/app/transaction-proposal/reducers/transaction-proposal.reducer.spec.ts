import { reducer, initialState, TransactionProposalState } from './index';
import {
  clearTransactionProposal,
  setCommentAndOtpCode,
} from '@transaction-proposal/actions/transaction-proposal.actions';
import {
  CommitmentPeriod,
  TransactionType,
  UnitType,
} from '@shared/model/transaction';
import { prepareTransactionStateForReturnOfExcess } from '@account-management/account/account-details/account.actions';
import { setReturnExcessAmountsAndType } from '../actions/select-unit-types.actions';

describe('Transaction Proposal reducer', () => {
  it('sets the comment', () => {
    const beforeSetCommentActionState = reducer(initialState, {} as any);
    expect(beforeSetCommentActionState.comment).toBeNull();

    const setCommentAction = setCommentAndOtpCode({
      comment: 'Proposing a transaction comment',
      otpCode: 'otpCode',
    });
    const afterSetCommentActionState = reducer(initialState, setCommentAction);
    expect(afterSetCommentActionState.comment).toEqual(
      'Proposing a transaction comment'
    );
  });

  it('prepares the transaction proposal for excess allocation', () => {
    const beforePrepareTransactionStateForReturnOfExcessState = reducer(
      initialState,
      {} as any
    );
    expect(
      beforePrepareTransactionStateForReturnOfExcessState.transactionType
    ).toBeNull();
    expect(
      beforePrepareTransactionStateForReturnOfExcessState.allocationYear
    ).toBeNull();
    expect(
      beforePrepareTransactionStateForReturnOfExcessState.allocationType
    ).toBeNull();
    expect(
      beforePrepareTransactionStateForReturnOfExcessState.excessAmountPerAllocationAccount
    ).toBeNull();
    expect(
      beforePrepareTransactionStateForReturnOfExcessState.returnExcessAllocationType
    ).toBeNull();

    const prepareTransactionStateForReturnOfExcessAction =
      prepareTransactionStateForReturnOfExcess({
        routeSnapshotUrl: 'A_URL',
        allocationYear: 2023,
        allocationType: 'NAT_AND_NER',
        excessAmountPerAllocationAccount: {
          returnToAllocationAccountAmount: 8348,
          returnToNewEntrantsReserveAccount: 1323,
        },
      });
    const afterPrepareTransactionStateForReturnOfExcessState = reducer(
      initialState,
      prepareTransactionStateForReturnOfExcessAction
    );
    expect(
      afterPrepareTransactionStateForReturnOfExcessState.transactionType.type
    ).toEqual(TransactionType.ExcessAllocation);
    expect(
      afterPrepareTransactionStateForReturnOfExcessState.transactionType
        .description
    ).toEqual('return overallocated allowances');
    expect(
      afterPrepareTransactionStateForReturnOfExcessState.transactionType
        .skipAccountStep
    ).toBeTruthy();
    expect(
      afterPrepareTransactionStateForReturnOfExcessState.transactionType
        .supportsNotification
    ).toBeNull();
    expect(
      afterPrepareTransactionStateForReturnOfExcessState.allocationYear
    ).toEqual(2023);
    expect(
      afterPrepareTransactionStateForReturnOfExcessState.allocationType
    ).toEqual('NAT_AND_NER');
    expect(
      afterPrepareTransactionStateForReturnOfExcessState
        .excessAmountPerAllocationAccount.returnToAllocationAccountAmount
    ).toEqual(8348);
    expect(
      afterPrepareTransactionStateForReturnOfExcessState
        .excessAmountPerAllocationAccount.returnToNewEntrantsReserveAccount
    ).toEqual(1323);
    expect(
      afterPrepareTransactionStateForReturnOfExcessState.returnExcessAllocationType
    ).toEqual('NAT_AND_NER');
  });

  it('sets the return excess amounts and type', () => {
    const beforeSetReturnExcessAmountsAndTypeActionState = reducer(
      initialState,
      {} as any
    );
    expect(
      beforeSetReturnExcessAmountsAndTypeActionState.calculatedExcessAmountPerAllocationAccount
    ).toBeNull();
    expect(
      beforeSetReturnExcessAmountsAndTypeActionState.returnExcessAllocationType
    ).toBeNull();
    expect(
      beforeSetReturnExcessAmountsAndTypeActionState.allocationType
    ).toBeNull();

    const setCommentAction = setReturnExcessAmountsAndType({
      selectedExcessAmounts: {
        returnToAllocationAccountAmount: null,
        returnToNewEntrantsReserveAccount: 8733,
      },
      returnExcessAllocationType: 'NER',
    });
    const afterSetReturnExcessAmountsAndTypeActionState = reducer(
      initialState,
      setCommentAction
    );
    expect(
      afterSetReturnExcessAmountsAndTypeActionState.allocationType
    ).toEqual('NER');
    expect(
      afterSetReturnExcessAmountsAndTypeActionState.returnExcessAllocationType
    ).toEqual('NER');
    expect(
      afterSetReturnExcessAmountsAndTypeActionState
        .calculatedExcessAmountPerAllocationAccount
        .returnToAllocationAccountAmount
    ).toBeNull();
    expect(
      afterSetReturnExcessAmountsAndTypeActionState
        .calculatedExcessAmountPerAllocationAccount
        .returnToNewEntrantsReserveAccount
    ).toEqual(8733);
  });

  it('clears the state', () => {
    const nonEmptyState: TransactionProposalState = {
      allowedTransactionTypes: {
        result: [],
        accountId: null,
      },
      transactionType: {
        type: TransactionType.ExcessAllocation,
        category: null,
        description: 'return overallocated allowances',
        skipAccountStep: true,
        supportsNotification: null,
      },
      itlNotificationId: null,
      itlNotification: null,
      allocationYear: 2021,
      allocationType: 'NER',
      excessAmountPerAllocationAccount: {
        returnToAllocationAccountAmount: null,
        returnToNewEntrantsReserveAccount: 54,
      },
      calculatedExcessAmountPerAllocationAccount: {
        returnToAllocationAccountAmount: 0,
        returnToNewEntrantsReserveAccount: 54,
      },
      returnExcessAllocationType: 'NER',
      transactionBlockSummaryResult: {
        accountId: '10000088',
        transactionType: 'ExcessAllocation',
        result: [
          {
            type: UnitType.ALLOWANCE,
            originalPeriod: CommitmentPeriod.CP0,
            applicablePeriod: CommitmentPeriod.CP0,
            availableQuantity: 14134,
            environmentalActivity: null,
            subjectToSop: false,
          },
        ],
      },
      transactionBlocks: [],
      selectedTransactionBlocks: [
        {
          type: UnitType.ALLOWANCE,
          originalPeriod: CommitmentPeriod.CP0,
          applicablePeriod: CommitmentPeriod.CP0,
          availableQuantity: 14134,
          environmentalActivity: null,
          subjectToSop: false,
          index: 0,
          quantity: '54',
        },
      ],
      trustedAccountsResult: {
        accountId: null,
        otherTrustedAccounts: [],
        trustedAccountsUnderTheSameHolder: [],
      },
      transferringAccountInfo: null,
      toBeReplacedUnitsAccountParts: null,
      acquiringAccountInfo: {
        identifier: 10000003,
        fullIdentifier: 'UK-100-10000003-0-46',
        accountName: 'UK New Entrants Reserve Account',
        accountHolderName: 'UK ETS Authority',
        // registryCode: 'UK',
        isGovernment: false,
        trusted: false,
      },
      userDefinedAccountParts: null,
      userDefinedAccountPartsEnrichedInfo: null,
      approvalRequired: false,
      calculatedTransactionTypeDescription: '',
      submissionBusinessCheckResult: {
        transactionIdentifier: null,
        requestIdentifier: null,
        approvalRequired: null,
        executionDate: null,
        executionTime: null,
        transactionTypeDescription: null,
      },
      comment: 'A comment',
      otpCode: null,
      enrichedTransactionSummaryForSigning: {
        acquiringAccountFullIdentifier: null,
        acquiringAccountIdentifier: null,
        blocks: [],
        comment: null,
        identifier: null,
        transferringAccountIdentifier: null,
        type: null,
      },
      transactionReference: null,
      enrichedReturnExcessAllocationTransactionSummaryForSigning: {
        natQuantity: null,
        nerQuantity: null,
        natReturnTransactionIdentifier: null,
        nerReturnTransactionIdentifier: null,
        natAcquiringAccountInfo: null,
        nerAcquiringAccountInfo: null,
        transferringAccountIdentifier: null,
        attributes: null,
        type: null,
        blocks: null,
        comment: null,
        allocationYear: null,
        reference: null,
        returnExcessAllocationType: null,
      },
    };

    const beforeClearTransactionProposalState = reducer(
      nonEmptyState,
      {} as any
    );
    expect(beforeClearTransactionProposalState.transactionType).toBeTruthy();
    expect(beforeClearTransactionProposalState.allocationYear).toBeTruthy();
    expect(beforeClearTransactionProposalState.allocationType).toBeTruthy();
    expect(
      beforeClearTransactionProposalState.excessAmountPerAllocationAccount
    ).toBeTruthy();
    expect(
      beforeClearTransactionProposalState.calculatedExcessAmountPerAllocationAccount
    ).toBeTruthy();
    expect(
      beforeClearTransactionProposalState.returnExcessAllocationType
    ).toBeTruthy();
    expect(beforeClearTransactionProposalState.comment).toBeTruthy();

    const clearTransactionProposalAction = clearTransactionProposal();
    const afterClearTransactionProposalState = reducer(
      initialState,
      clearTransactionProposalAction
    );

    expect(afterClearTransactionProposalState.transactionType).toBeNull();
    expect(afterClearTransactionProposalState.allocationYear).toBeNull();
    expect(afterClearTransactionProposalState.allocationType).toBeNull();
    expect(
      afterClearTransactionProposalState.excessAmountPerAllocationAccount
    ).toBeNull();
    expect(
      afterClearTransactionProposalState.calculatedExcessAmountPerAllocationAccount
    ).toBeNull();
    expect(
      afterClearTransactionProposalState.returnExcessAllocationType
    ).toBeNull();
    expect(afterClearTransactionProposalState.comment).toBeNull();
  });
});
