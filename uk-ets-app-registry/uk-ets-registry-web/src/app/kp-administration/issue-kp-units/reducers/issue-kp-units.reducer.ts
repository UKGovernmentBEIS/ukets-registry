import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import {
  AccountInfo,
  BusinessCheckResult,
  CommitmentPeriod,
  RegistryLevelInfo,
  TransactionSummary,
} from '@shared/model/transaction';
import {
  IssueKpUnitsActions,
  IssueKpUnitsApiActions,
} from '@issue-kp-units/actions';

export const issueKpUnitsFeatureKey = 'kp-administration';

export interface IssueKpUnitsState {
  commitmentPeriodList: CommitmentPeriod[];
  selectedCommitmentPeriod: CommitmentPeriod;
  acquiringAccountsForCommitmentPeriod: AccountInfo[];
  selectedAcquiringAccount: AccountInfo;
  registryLevelInfoList: RegistryLevelInfo[];
  selectedRegistryLevel: RegistryLevelInfo;
  selectedQuantity: number;
  businessCheckResult: BusinessCheckResult;
  enrichedTransactionSummaryReadyForSigning: TransactionSummary;
  transactionReference: string;
}

export const initialState: IssueKpUnitsState = getInitialState();

const issueKpUnitsReducer = createReducer(
  initialState,
  mutableOn(
    IssueKpUnitsActions.loadAccountsForCommitmentPeriod,
    (state, { commitmentPeriod }) => {
      state.selectedCommitmentPeriod = commitmentPeriod;
    }
  ),
  mutableOn(
    IssueKpUnitsActions.loadAccountsForCommitmentPeriodSuccess,
    (state, { acquiringAccountInfoList }) => {
      state.acquiringAccountsForCommitmentPeriod = acquiringAccountInfoList;
    }
  ),
  mutableOn(
    IssueKpUnitsActions.selectAcquiringAccount,
    (state, { selectedAcquiringAccountIdentifier }) => {
      state.selectedAcquiringAccount =
        state.acquiringAccountsForCommitmentPeriod.find(
          (a) => a.identifier === selectedAcquiringAccountIdentifier
        );
    }
  ),
  mutableOn(
    IssueKpUnitsActions.loadRegistryLevelsForCommitmentPeriodAndTypeSuccess,
    (state, { registryLevelResult }) => {
      state.registryLevelInfoList = registryLevelResult.result;
    }
  ),
  mutableOn(
    IssueKpUnitsActions.selectRegistryLevelAndQuantity,
    (state, { selectedRegistryLevel, quantity }) => {
      state.selectedRegistryLevel = selectedRegistryLevel;
      state.selectedQuantity = quantity;
    }
  ),
  mutableOn(
    IssueKpUnitsActions.setTransactionReference,
    (state, { reference }) => {
      state.transactionReference = reference;
    }
  ),
  mutableOn(
    IssueKpUnitsApiActions.issuanceTransactionEnrichProposalForSigningSuccess,
    (state, { transactionSummary }) => {
      state.enrichedTransactionSummaryReadyForSigning = transactionSummary;
    }
  ),
  mutableOn(
    IssueKpUnitsActions.issuanceTransactionProposeSuccess,
    (state, { businessCheckResult }) => {
      resetState(state);
      state.businessCheckResult = businessCheckResult;
    }
  )
);

function resetState(state) {
  state.commitmentPeriodList = getInitialState().commitmentPeriodList;
  state.acquiringAccountsForCommitmentPeriod =
    getInitialState().acquiringAccountsForCommitmentPeriod;
  state.selectedCommitmentPeriod = getInitialState().selectedCommitmentPeriod;
  state.selectedAcquiringAccount = getInitialState().selectedAcquiringAccount;
  state.registryLevelInfoList = getInitialState().registryLevelInfoList;
  state.selectedRegistryLevel = getInitialState().selectedRegistryLevel;
  state.selectedQuantity = getInitialState().selectedQuantity;
  state.enrichedTransactionSummaryReadyForSigning =
    getInitialState().enrichedTransactionSummaryReadyForSigning;
  state.transactionReference = getInitialState().transactionReference;
}

export function reducer(state: IssueKpUnitsState | undefined, action: Action) {
  return issueKpUnitsReducer(state, action);
}

function getInitialState(): IssueKpUnitsState {
  return {
    commitmentPeriodList: [CommitmentPeriod.CP1, CommitmentPeriod.CP2],
    selectedCommitmentPeriod: null,
    selectedAcquiringAccount: {
      accountName: null,
      fullIdentifier: null,
      identifier: null,
      accountHolderName: null,
    },
    acquiringAccountsForCommitmentPeriod: [],
    registryLevelInfoList: [],
    selectedRegistryLevel: {
      pendingQuantity: null,
      consumedQuantity: null,
      initialQuantity: null,
      id: null,
      unitType: null,
      environmentalActivity: null,
      EnvironmentalActivity: null,
    },
    selectedQuantity: null,
    businessCheckResult: null,
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
