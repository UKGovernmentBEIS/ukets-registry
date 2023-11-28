import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  transactionProposalFeatureKey,
  TransactionProposalState,
} from './transaction-proposal.reducer';
import {
  ProposedTransactionType,
  TRANSACTION_TYPES_VALUES,
  TransactionBlockSummary,
  TransactionSummary,
  ReturnExcessAllocationTransactionSummary,
  AccountInfo,
  AcquiringAccountInfo,
} from '@shared/model/transaction';
import { selectAccountId } from '@account-management/account/account-details/account.selector';
import { formatUserDefinedFullAccountIdentifier } from '@shared/model/account';
import { ItlNotification } from '@registry-web/shared/model/transaction/itl-notification';
import { ReturnExcessAllocationType } from '@shared/model/allocation';
import { ExcessAmountPerAllocationAccount } from '@transaction-proposal/model/transaction-proposal-model';

const selectTransactionProposalState =
  createFeatureSelector<TransactionProposalState>(
    transactionProposalFeatureKey
  );

export const selectAllowedTransactionTypes = createSelector(
  selectTransactionProposalState,
  (state) => state.allowedTransactionTypes
);

export const selectTransactionType = createSelector(
  selectTransactionProposalState,
  (state) => state.transactionType
);

export const selectTransactionTypeLabel = createSelector(
  selectTransactionProposalState,
  (state) => {
    {
      return TRANSACTION_TYPES_VALUES[state.transactionType.type].label
        .transactionProposalLabel;
    }
  }
);

export const selectITLNotificationId = createSelector(
  selectTransactionProposalState,
  (state) => state.itlNotificationId
);

export const selectITLNotification = createSelector(
  selectTransactionProposalState,
  (state) => state.itlNotification
);

export const selectIsETSTransaction = createSelector(
  selectTransactionType,
  (transactionType: ProposedTransactionType) => {
    {
      if (!transactionType) {
        return false;
      }
      return TRANSACTION_TYPES_VALUES[transactionType.type].isETSTransaction;
    }
  }
);

export const selectCalculatedTransactionTypeDescription = createSelector(
  selectTransactionProposalState,
  (state) => state.calculatedTransactionTypeDescription
);

export const transactionBlockSummaryResult = createSelector(
  selectTransactionProposalState,
  (state) => state.transactionBlockSummaryResult
);

export const trustedAccountsResult = createSelector(
  selectTransactionProposalState,
  (state) => state.trustedAccountsResult
);

export const selectedTransactionBlocks = createSelector(
  selectTransactionProposalState,
  (state) => state.selectedTransactionBlocks
);

export const selectTotalQuantityOfSelectedTransactionBlocks = createSelector(
  selectedTransactionBlocks,
  (selectedTransactionBlocks) => {
    let total = 0;
    for (const block of selectedTransactionBlocks) {
      total = total + parseInt(block.quantity);
    }
    return total;
  }
);

export const acquiringAccount = createSelector(
  selectTransactionProposalState,
  (state) => state.acquiringAccountInfo
);

export const userDefinedAccountPartsEnrichedInfo = createSelector(
  selectTransactionProposalState,
  (state) => state.userDefinedAccountPartsEnrichedInfo
);

export const selectUserDefinedAccountParts = createSelector(
  selectTransactionProposalState,
  (state) => state.userDefinedAccountParts
);

export const selectToBeReplacedUnitsAccountParts = createSelector(
  selectTransactionProposalState,
  (state) => state.toBeReplacedUnitsAccountParts
);

export const approvalRequired = createSelector(
  selectTransactionProposalState,
  (state) => state.approvalRequired
);

export const selectProposalComment = createSelector(
  selectTransactionProposalState,
  (state) => state.comment
);

export const submissionBusinessCheckResult = createSelector(
  selectTransactionProposalState,
  (state) => state.submissionBusinessCheckResult
);

export const selectUserDefinedAccountIdentifier = createSelector(
  selectUserDefinedAccountParts,
  formatUserDefinedFullAccountIdentifier
);

export const selectToBeReplacedUnitsAccountFullIdentifier = createSelector(
  selectToBeReplacedUnitsAccountParts,
  formatUserDefinedFullAccountIdentifier
);

export const selectTransferringAccountInfo = createSelector(
  selectTransactionProposalState,
  (state) => state.transferringAccountInfo
);

export const selectAcquiringAccountIdentifierOrUserDefinedAccountIdentifier =
  createSelector(
    acquiringAccount,
    selectUserDefinedAccountIdentifier,
    (accountInfo, userDefinedAccountIdentifier) => {
      let result: string;
      if (accountInfo) {
        result = accountInfo.fullIdentifier;
      } else {
        result = userDefinedAccountIdentifier;
      }
      return result;
    }
  );

export const selectAcquiringAccountInfoOrEnrichedUserDefinedAccountInfo =
  createSelector(
    acquiringAccount,
    userDefinedAccountPartsEnrichedInfo,
    (accountInfo, userDefinedEnrichedInfo) => {
      if (accountInfo) {
        return accountInfo;
      } else {
        return userDefinedEnrichedInfo;
      }
    }
  );

export const selectEnrichedTransactionSummaryForSigning = createSelector(
  selectTransactionProposalState,
  (state) => state.enrichedTransactionSummaryForSigning
);

export const selectEnrichedReturnExcessAllocationTransactionSummaryForSigning =
  createSelector(
    selectTransactionProposalState,
    (state) => state.enrichedReturnExcessAllocationTransactionSummaryForSigning
  );

export const selectEnrichedTransactionType = createSelector(
  selectEnrichedTransactionSummaryForSigning,
  selectEnrichedReturnExcessAllocationTransactionSummaryForSigning,
  (
    transactionSummary: TransactionSummary,
    returnExcessAllocationTransactionSummary: ReturnExcessAllocationTransactionSummary
  ) => {
    if (transactionSummary && transactionSummary.type) {
      return transactionSummary.type;
    } else {
      return returnExcessAllocationTransactionSummary.type;
    }
  }
);

export const selectOtpCode = createSelector(
  selectTransactionProposalState,
  (state) => state.otpCode
);

export const selectAllocationYear = createSelector(
  selectTransactionProposalState,
  (state) => state.allocationYear
);
export const selectAllocationType = createSelector(
  selectTransactionProposalState,
  (state) => state.allocationType
);
export const selectExcessAmount = createSelector(
  selectTransactionProposalState,
  (state) => state.excessAmountPerAllocationAccount
);

export const selectTotalExcessAmount = createSelector(
  selectExcessAmount,
  (excessAmount) => {
    if (excessAmount) {
      if (
        typeof excessAmount.returnToAllocationAccountAmount === 'number' &&
        typeof excessAmount.returnToNewEntrantsReserveAccount === 'number'
      ) {
        return (
          excessAmount.returnToAllocationAccountAmount +
          excessAmount.returnToNewEntrantsReserveAccount
        );
      } else if (
        typeof excessAmount.returnToAllocationAccountAmount === 'number'
      ) {
        return excessAmount.returnToAllocationAccountAmount;
      } else if (
        typeof excessAmount.returnToNewEntrantsReserveAccount === 'number'
      ) {
        return excessAmount.returnToNewEntrantsReserveAccount;
      }
    } else {
      return null;
    }
  }
);

export const selectCalculatedExcessAmountPerAllocationAccount = createSelector(
  selectTransactionProposalState,
  (state) => state.calculatedExcessAmountPerAllocationAccount
);

export const selectReturnExcessAllocationType = createSelector(
  selectTransactionProposalState,
  (state) => state.returnExcessAllocationType
);

export const selectTransactionReference = createSelector(
  selectTransactionProposalState,
  (state) => state.transactionReference
);

export const selectTransactionSummary = createSelector(
  selectedTransactionBlocks,
  selectTransactionType,
  selectAcquiringAccountIdentifierOrUserDefinedAccountIdentifier,
  //TODO: Remove account selector from transaction proposal (UKETS-4581)
  selectAccountId,
  selectToBeReplacedUnitsAccountFullIdentifier,
  selectProposalComment,
  selectEnrichedTransactionSummaryForSigning,
  selectTransactionProposalState,
  (
    transactionBlocks,
    proposedTransactionType,
    acquiringAcountInfo,
    transferringAccountIdentifier,
    toBeReplacedUnitsAccountFullIdentifier,
    comment,
    enrichedTransactionSummaryForSigning,
    state
  ) =>
    createTransactionSummary(
      transactionBlocks,
      proposedTransactionType,
      acquiringAcountInfo,
      transferringAccountIdentifier,
      toBeReplacedUnitsAccountFullIdentifier,
      comment,
      enrichedTransactionSummaryForSigning.identifier,
      enrichedTransactionSummaryForSigning.reversedIdentifier,
      state.allocationYear,
      state.allocationType,
      state.itlNotification,
      state.transactionReference,
      enrichedTransactionSummaryForSigning.returnExcessAllocationType
    )
);

export const selectReturnExcessAllocationTransactionSummary = createSelector(
  selectedTransactionBlocks,
  selectTransactionType,
  selectAccountId,
  selectProposalComment,
  selectEnrichedReturnExcessAllocationTransactionSummaryForSigning,
  selectCalculatedExcessAmountPerAllocationAccount,
  selectTransactionProposalState,
  (
    transactionBlocks,
    proposedTransactionType,
    transferringAccountIdentifier,
    comment,
    enrichedTransactionSummaryForSigning,
    calculatedExcessAmountPerAllocationAccount,
    state
  ) =>
    createReturnExcessAllocationTransactionSummary(
      calculatedExcessAmountPerAllocationAccount,
      transactionBlocks,
      proposedTransactionType,
      enrichedTransactionSummaryForSigning.natReturnTransactionIdentifier,
      enrichedTransactionSummaryForSigning.nerReturnTransactionIdentifier,
      enrichedTransactionSummaryForSigning.natAcquiringAccountInfo,
      enrichedTransactionSummaryForSigning.nerAcquiringAccountInfo,
      transferringAccountIdentifier,
      comment,
      state.allocationYear,
      state.transactionReference,
      enrichedTransactionSummaryForSigning.returnExcessAllocationType
    )
);

function createTransactionSummary(
  transactionBlocks: TransactionBlockSummary[],
  proposedTransactionType: ProposedTransactionType,
  acquiringAccountFullIdentifier: string,
  transferringAccountIdentifier: string,
  toBeReplacedBlocksAccountFullIdentifier: string,
  comment: string,
  identifier: string,
  reversedIdentifier: string,
  allocationYear: number,
  allocationType: string,
  itlNotification: ItlNotification,
  reference: string,
  returnExcessAllocationType: ReturnExcessAllocationType
): TransactionSummary {
  return {
    acquiringAccountFullIdentifier,
    toBeReplacedBlocksAccountFullIdentifier,
    transferringAccountIdentifier: +transferringAccountIdentifier,
    type: proposedTransactionType ? proposedTransactionType.type : null,
    blocks: transactionBlocks ? transactionBlocks : [],
    comment,
    identifier,
    reversedIdentifier,
    allocationYear,
    allocationType,
    itlNotification,
    reference,
    returnExcessAllocationType,
  };
}

function createReturnExcessAllocationTransactionSummary(
  calculatedExcessAmountPerAllocationAccount: ExcessAmountPerAllocationAccount,
  transactionBlocks: TransactionBlockSummary[],
  proposedTransactionType: ProposedTransactionType,
  natReturnTransactionIdentifier: string,
  nerReturnTransactionIdentifier: string,
  natAcquiringAccountInfo: AcquiringAccountInfo,
  nerAcquiringAccountInfo: AcquiringAccountInfo,
  transferringAccountIdentifier: string,
  comment: string,
  allocationYear: number,
  reference: string,
  returnExcessAllocationType: ReturnExcessAllocationType
): ReturnExcessAllocationTransactionSummary {
  return {
    natQuantity:
      calculatedExcessAmountPerAllocationAccount != null
        ? calculatedExcessAmountPerAllocationAccount.returnToAllocationAccountAmount
        : null,
    nerQuantity:
      calculatedExcessAmountPerAllocationAccount != null
        ? calculatedExcessAmountPerAllocationAccount.returnToNewEntrantsReserveAccount
        : null,
    natReturnTransactionIdentifier,
    nerReturnTransactionIdentifier,
    natAcquiringAccountInfo,
    nerAcquiringAccountInfo,
    transferringAccountIdentifier: +transferringAccountIdentifier,
    type: proposedTransactionType ? proposedTransactionType.type : null,
    blocks: transactionBlocks ? transactionBlocks : [],
    comment,
    allocationYear,
    reference,
    returnExcessAllocationType,
  };
}
