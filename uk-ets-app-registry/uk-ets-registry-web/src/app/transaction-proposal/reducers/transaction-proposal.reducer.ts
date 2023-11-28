import { Action, createReducer } from '@ngrx/store';
import {
  AccountInfo,
  AcquiringAccountInfo,
  BusinessCheckResult,
  CandidateAcquiringAccounts,
  ProposedTransactionType,
  TRANSACTION_TYPES_VALUES,
  TransactionBlockSummary,
  TransactionBlockSummaryResult,
  TransactionSummary,
  TransactionType,
  TransactionTypesResult,
  ReturnExcessAllocationTransactionSummary,
} from '@shared/model/transaction';
import { mutableOn } from '@shared/mutable-on';
import {
  SelectUnitTypesActions,
  SpecifyAcquiringAccountActions,
  TransactionProposalActions,
} from '@transaction-proposal/actions';
import { UserDefinedAccountParts } from '@shared/model/account';
import { ItlNotification } from '@shared/model/transaction/itl-notification';
import { AccountActions } from '@account-management/account/account-details';
import { TransactionDetailsActions } from '@transaction-management/transaction-details/actions';
import { ReturnExcessAllocationType } from '@shared/model/allocation';
import { ExcessAmountPerAllocationAccount } from '@transaction-proposal/model/transaction-proposal-model';
import { goBackButtonInSpecifyUnitTypesAndQuantity } from '@transaction-proposal/actions/transaction-proposal.actions';

export const transactionProposalFeatureKey = 'transaction-proposal';

export interface TransactionProposalState {
  allowedTransactionTypes: TransactionTypesResult;
  transactionType: ProposedTransactionType;
  itlNotificationId: number;
  itlNotification: ItlNotification;
  allocationYear: number;
  allocationType: string;
  excessAmountPerAllocationAccount: ExcessAmountPerAllocationAccount;
  calculatedExcessAmountPerAllocationAccount: ExcessAmountPerAllocationAccount;
  returnExcessAllocationType: ReturnExcessAllocationType;
  transactionBlockSummaryResult: TransactionBlockSummaryResult;
  transactionBlocks: TransactionBlockSummary[];
  selectedTransactionBlocks: TransactionBlockSummary[];
  trustedAccountsResult: CandidateAcquiringAccounts;
  //TODO: Use this field for the filling of transferring account details (UKETS-4581)
  transferringAccountInfo: AccountInfo;
  toBeReplacedUnitsAccountParts: UserDefinedAccountParts;
  acquiringAccountInfo: AcquiringAccountInfo;
  userDefinedAccountParts: UserDefinedAccountParts;
  userDefinedAccountPartsEnrichedInfo: AcquiringAccountInfo;
  approvalRequired: boolean;
  calculatedTransactionTypeDescription: string;
  submissionBusinessCheckResult: BusinessCheckResult;
  comment: string;
  otpCode: string;
  enrichedTransactionSummaryForSigning: TransactionSummary;
  transactionReference: string;
  enrichedReturnExcessAllocationTransactionSummaryForSigning: ReturnExcessAllocationTransactionSummary;
}

export const initialState: TransactionProposalState = {
  allowedTransactionTypes: { result: [], accountId: null },
  transactionType: null,
  itlNotificationId: null,
  itlNotification: null,
  allocationYear: null,
  allocationType: null,
  excessAmountPerAllocationAccount: null,
  calculatedExcessAmountPerAllocationAccount: null,
  returnExcessAllocationType: null,
  transactionBlockSummaryResult: {
    result: [],
    accountId: null,
    transactionType: null,
  },
  transactionBlocks: [],
  selectedTransactionBlocks: [],
  trustedAccountsResult: {
    accountId: null,
    otherTrustedAccounts: [],
    trustedAccountsUnderTheSameHolder: [],
  },
  transferringAccountInfo: null,
  toBeReplacedUnitsAccountParts: null,
  acquiringAccountInfo: null,
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
  comment: null,
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

const transactionProposalReducer = createReducer(
  initialState,
  mutableOn(
    TransactionProposalActions.loadAllowedTransactionTypes,
    (state, { transactionTypes }) => {
      state.allowedTransactionTypes = transactionTypes;
    }
  ),
  mutableOn(
    TransactionProposalActions.setSelectedTransactionType,
    (
      state,
      { proposedTransactionType, itlNotificationId, clearNextStepsInWizard }
    ) => {
      state.transactionType = proposedTransactionType;
      state.itlNotificationId = itlNotificationId;
      /**
       * When selecting a new transaction Type initialize the selected values
       * in the following wizard steps if present
       */
      if (clearNextStepsInWizard) {
        state.selectedTransactionBlocks =
          initialState.selectedTransactionBlocks;
        state.userDefinedAccountPartsEnrichedInfo =
          initialState.userDefinedAccountPartsEnrichedInfo;
        state.acquiringAccountInfo = initialState.acquiringAccountInfo;
        state.userDefinedAccountParts = initialState.userDefinedAccountParts;
        state.itlNotification = initialState.itlNotification;
      }
    }
  ),
  mutableOn(
    SelectUnitTypesActions.getTransactionBlockSummaryResultSuccess,
    (state, { transactionBlockSummaryResult }) => {
      state.transactionBlockSummaryResult = transactionBlockSummaryResult;
    }
  ),
  mutableOn(
    SelectUnitTypesActions.setReturnExcessAmountsAndType,
    (state, { selectedExcessAmounts, returnExcessAllocationType }) => {
      state.calculatedExcessAmountPerAllocationAccount = selectedExcessAmounts;
      state.returnExcessAllocationType = returnExcessAllocationType;
      state.allocationType = returnExcessAllocationType;
    }
  ),
  mutableOn(
    SelectUnitTypesActions.setSelectedBlockSummaries,
    (
      state,
      {
        selectedTransactionBlockSummaries,
        clearNextStepsInWizard,
        toBeReplacedUnitsHoldingAccountParts,
      }
    ) => {
      /**
       * When selecting new transaction blocks initialize the selected values
       * in the following wizard steps if present
       */
      if (clearNextStepsInWizard) {
        state.userDefinedAccountPartsEnrichedInfo =
          initialState.userDefinedAccountPartsEnrichedInfo;
        state.acquiringAccountInfo = initialState.acquiringAccountInfo;
        state.userDefinedAccountParts = initialState.userDefinedAccountParts;
      }
      state.selectedTransactionBlocks = selectedTransactionBlockSummaries;
      state.toBeReplacedUnitsAccountParts =
        toBeReplacedUnitsHoldingAccountParts;
    }
  ),
  mutableOn(
    SpecifyAcquiringAccountActions.setAcquiringAccount,
    (state, { acquiringAccount }) => {
      state.acquiringAccountInfo = acquiringAccount;
      state.userDefinedAccountParts = null;
    }
  ),
  mutableOn(
    SpecifyAcquiringAccountActions.setUserDefinedAcquiringAccount,
    (state, { userDefinedAcquiringAccount }) => {
      state.userDefinedAccountParts = userDefinedAcquiringAccount;
      state.acquiringAccountInfo = null;
    }
  ),
  mutableOn(
    SpecifyAcquiringAccountActions.enrichUserDefinedAcquiringAccountSuccess,
    (state, { acquiringAccount }) => {
      state.userDefinedAccountPartsEnrichedInfo = acquiringAccount;
    }
  ),
  mutableOn(
    SpecifyAcquiringAccountActions.populateAcquiringAccountSuccess,
    (state, { acquiringAccountInfo }) => {
      state.acquiringAccountInfo = acquiringAccountInfo;
    }
  ),
  mutableOn(
    SpecifyAcquiringAccountActions.populateExcessAllocationAcquiringAccountsSuccess,
    (state, { natAcquiringAccount, nerAcquiringAccount }) => {
      state.enrichedReturnExcessAllocationTransactionSummaryForSigning.natAcquiringAccountInfo =
        natAcquiringAccount;
      state.enrichedReturnExcessAllocationTransactionSummaryForSigning.nerAcquiringAccountInfo =
        nerAcquiringAccount;
    }
  ),
  mutableOn(
    SpecifyAcquiringAccountActions.getTrustedAccountsSuccess,
    (state, { trustedAccountsResult }) => {
      state.trustedAccountsResult = trustedAccountsResult;
    }
  ),
  mutableOn(
    SpecifyAcquiringAccountActions.validateTrustedAccountSuccess,
    (state, { businessCheckResult }) => {
      state.approvalRequired = businessCheckResult.approvalRequired;
      state.calculatedTransactionTypeDescription =
        businessCheckResult.transactionTypeDescription;
    }
  ),
  mutableOn(
    TransactionProposalActions.validateProposalSuccess,
    (state, { businessCheckResult }) => {
      state.approvalRequired = businessCheckResult.approvalRequired;
    }
  ),
  mutableOn(
    TransactionProposalActions.enrichProposalForSigningSuccess,
    (state, { transactionSummary }) => {
      state.enrichedTransactionSummaryForSigning = transactionSummary;
    }
  ),
  mutableOn(
    TransactionProposalActions.enrichReturnExcessAllocationProposalForSigningSuccess,
    (state, { transactionSummary }) => {
      state.enrichedReturnExcessAllocationTransactionSummaryForSigning =
        transactionSummary;
    }
  ),
  mutableOn(
    TransactionProposalActions.validateITLNotificationSuccess,
    (state, { itlNotification }) => {
      state.itlNotification = itlNotification;
    }
  ),
  mutableOn(
    TransactionProposalActions.setTransactionReference,
    (state, { reference }) => {
      state.transactionReference = reference;
    }
  ),
  mutableOn(
    TransactionProposalActions.setCommentAndOtpCode,
    (state, { comment, otpCode }) => {
      state.comment = comment;
      state.otpCode = otpCode;
    }
  ),
  mutableOn(
    TransactionProposalActions.submitProposalSuccess,
    TransactionProposalActions.submitReturnExcessAllocationProposalSuccess,
    (state, { businessCheckResult }) => {
      state.submissionBusinessCheckResult = businessCheckResult;
    }
  ),
  mutableOn(TransactionProposalActions.clearTransactionProposal, (state) => {
    resetState(state);
  }),
  mutableOn(
    AccountActions.prepareTransactionStateForReturnOfExcess,
    (
      state,
      { allocationYear, allocationType, excessAmountPerAllocationAccount }
    ) => {
      state.transactionType = {
        ...state.transactionType,
        type: TransactionType.ExcessAllocation,
        category: null,
        description: 'return overallocated allowances',
        skipAccountStep: true,
        supportsNotification: null,
      };
      state.allocationYear = allocationYear;
      state.allocationType = allocationType;
      state.excessAmountPerAllocationAccount = excessAmountPerAllocationAccount;
      //Use a type assertion here to narrow
      state.returnExcessAllocationType =
        allocationType as ReturnExcessAllocationType;
    }
  ),
  mutableOn(
    TransactionDetailsActions.prepareTransactionProposalStateForReversal,
    (state, payload) => {
      state.enrichedTransactionSummaryForSigning = {
        ...state.enrichedTransactionSummaryForSigning,
        reversedIdentifier: payload.transactionReversedIdentifier,
        blocks: <TransactionBlockSummary[]>[
          ...state.selectedTransactionBlocks,
          { type: payload.blockType, quantity: payload.quantity },
        ],
      };
      state.selectedTransactionBlocks = <TransactionBlockSummary[]>[
        ...state.selectedTransactionBlocks,
        { type: payload.blockType, quantity: payload.quantity },
      ];
      state.allocationYear = +payload.attributes;
      state.transactionType = {
        ...state.transactionType,
        type: payload.transactionType,
        isReversal: true,
        skipAccountStep: true,
        description:
          TRANSACTION_TYPES_VALUES[payload.transactionType].label.defaultLabel,
      };
    }
  ),
  mutableOn(
    TransactionDetailsActions.prepareTransactionProposalStateForReversalSuccess,
    (state, { reversedAccountInfo }) => {
      state.transferringAccountInfo =
        reversedAccountInfo.transferringAccountInfo;
      state.acquiringAccountInfo = {
        identifier: reversedAccountInfo.acquiringAccountInfo.identifier,
        accountHolderName:
          reversedAccountInfo.acquiringAccountInfo.accountHolderName,
        fullIdentifier: reversedAccountInfo.acquiringAccountInfo.fullIdentifier,
        accountName: reversedAccountInfo.acquiringAccountInfo.accountName,
        accountType: reversedAccountInfo.acquiringAccountInfo.accountType,
        trusted: false,
      };
      state.enrichedTransactionSummaryForSigning.transferringAccountIdentifier =
        reversedAccountInfo.transferringAccountInfo.identifier;
      state.enrichedTransactionSummaryForSigning.acquiringAccountIdentifier =
        reversedAccountInfo.acquiringAccountInfo.identifier;
    }
  ),
  mutableOn(
    TransactionProposalActions.goBackButtonInSpecifyUnitTypesAndQuantity,
    (state) => {
      state.enrichedTransactionSummaryForSigning =
        initialState.enrichedTransactionSummaryForSigning;
      state.enrichedReturnExcessAllocationTransactionSummaryForSigning =
        initialState.enrichedReturnExcessAllocationTransactionSummaryForSigning;
    }
  )
);

export function reducer(
  state: TransactionProposalState | undefined,
  action: Action
) {
  return transactionProposalReducer(state, action);
}

function resetState(state) {
  state.allowedTransactionTypes = initialState.allowedTransactionTypes;
  state.transactionType = initialState.transactionType;
  state.itlNotificationId = initialState.itlNotificationId;
  state.itlNotification = initialState.itlNotification;
  state.allocationYear = initialState.allocationYear;
  state.allocationType = initialState.allocationType;
  state.returnExcessAllocationType = initialState.returnExcessAllocationType;
  state.excessAmountPerAllocationAccount =
    initialState.excessAmountPerAllocationAccount;
  state.selectedExcessAmountPerAllocationAccount =
    initialState.calculatedExcessAmountPerAllocationAccount;
  state.transactionBlockSummaryResult =
    initialState.transactionBlockSummaryResult;
  state.transferringAccountInfo = initialState.transferringAccountInfo;
  state.toBeReplacedUnitsAccountParts =
    initialState.toBeReplacedUnitsAccountParts;
  state.acquiringAccountInfo = initialState.acquiringAccountInfo;
  state.transactionBlocks = initialState.transactionBlocks;
  state.selectedTransactionBlocks = initialState.selectedTransactionBlocks;
  state.trustedAccountsResult = initialState.trustedAccountsResult;
  state.acquiringAccount = initialState.acquiringAccountInfo;
  state.userDefinedAccountParts = initialState.userDefinedAccountParts;
  state.approvalRequired = initialState.approvalRequired;
  state.submissionBusinessCheckResult =
    initialState.submissionBusinessCheckResult;
  state.enrichedTransactionSummaryForSigning =
    initialState.enrichedTransactionSummaryForSigning;
  state.transactionReference = initialState.transactionReference;
  state.enrichedReturnExcessAllocationTransactionSummaryForSigning =
    initialState.enrichedReturnExcessAllocationTransactionSummaryForSigning;
}
