import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import * as IssueAllowanceActions from '../actions/issue-allowance.actions';
import {
  AcquiringAccountInfo,
  AllowanceTransactionBlockSummary,
  BusinessCheckResult,
  TransactionSummary,
  UnitType,
} from '@shared/model/transaction';

export const issueAllowanceReducer = 'issue-allowances';

export interface IssueAllowancesState {
  selectedQuantity: string;
  allowanceBlocks: Partial<AllowanceTransactionBlockSummary>[];
  acquiringAccountInfo: AcquiringAccountInfo;
  businessCheckResult: BusinessCheckResult;
  allocationYear: number;
  enrichedTransactionSummaryReadyForSigning: TransactionSummary;
  transactionReference: string;
}

export const initialState: IssueAllowancesState = getInitialState();

const issueAllowancesReducer = createReducer(
  initialState,
  mutableOn(
    IssueAllowanceActions.setAllowanceQuantity,
    (state, { quantity, year }) => {
      state.selectedQuantity = quantity.toString();
      state.allowanceBlocks = state.allowanceBlocks.map((content) =>
        content.year === year
          ? { ...content, quantity: quantity.toString() }
          : content
      );
    }
  ),
  mutableOn(
    IssueAllowanceActions.loadAllowancesSuccess,
    (state, { result }) => {
      state.allowanceBlocks = result.result.map((transactionSummary) => ({
        year: transactionSummary.year,
        issued: transactionSummary.issued,
        remaining: transactionSummary.remaining,
        quantity: transactionSummary.quantity,
        cap: transactionSummary.cap,
      }));
    }
  ),
  mutableOn(
    IssueAllowanceActions.setTransactionReference,
    (state, { reference }) => {
      state.transactionReference = reference;
    }
  ),
  mutableOn(
    IssueAllowanceActions.loadAcquiringAccountSuccess,
    (state, { accountInfo }) => {
      state.acquiringAccountInfo = accountInfo;
    }
  ),
  mutableOn(
    IssueAllowanceActions.enrichAllowanceForSigningSuccess,
    (state, { transactionSummary }) => {
      state.enrichedTransactionSummaryReadyForSigning = transactionSummary;
    }
  ),
  mutableOn(
    IssueAllowanceActions.submitAllowanceProposalSuccess,
    (state, { businessCheckResult }) => {
      resetState(state);
      state.businessCheckResult = businessCheckResult;
    }
  ),
  mutableOn(IssueAllowanceActions.cancelAllowanceProposalConfirmed, (state) => {
    resetState(state);
  })
);

// 'year' | 'cap' | 'issued' | 'remaining' | 'quantity' | 'type'
function resetState(state) {
  state.selectedQuantity = getInitialState().selectedQuantity;
  state.acquiringAccountInfo = getInitialState().acquiringAccountInfo;
  state.allowanceBlocks = getInitialState().allowanceBlocks;
  state.businessCheckResult = getInitialState().businessCheckResult;
  state.enrichedTransactionSummaryReadyForSigning =
    getInitialState().enrichedTransactionSummaryReadyForSigning;
  state.transactionReference = getInitialState().transactionReference;
}

export function reducer(
  state: IssueAllowancesState | undefined,
  action: Action
) {
  return issueAllowancesReducer(state, action);
}

function getInitialState(): IssueAllowancesState {
  return {
    selectedQuantity: null,
    businessCheckResult: null,
    allocationYear: null,
    acquiringAccountInfo: {
      fullIdentifier: '',
      accountHolderName: '',
      accountName: '',
      identifier: null,
      trusted: true,
    },
    allowanceBlocks: [
      {
        remaining: 0,
        issued: 0,
        year: 2022,
        cap: 10000,
        type: UnitType.ALLOWANCE,
      },
    ],
    enrichedTransactionSummaryReadyForSigning: {
      type: null,
      identifier: null,
      transferringAccountIdentifier: null,
      comment: null,
      blocks: [],
      acquiringAccountIdentifier: null,
      acquiringAccountFullIdentifier: null,
    },
    transactionReference: null,
  };
}
