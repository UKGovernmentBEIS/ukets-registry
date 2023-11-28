import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  issueKpUnitsFeatureKey,
  IssueKpUnitsState,
} from '@issue-kp-units/reducers/issue-kp-units.reducer';
import {
  AccountInfo,
  RegistryLevelInfo,
  TransactionSummary,
  TransactionType,
} from '@shared/model/transaction';

const selectKPUnitState = createFeatureSelector<IssueKpUnitsState>(
  issueKpUnitsFeatureKey
);

export const selectCommitmentPeriodList = createSelector(
  selectKPUnitState,
  (state) => state.commitmentPeriodList
);

export const selectSelectedCommitmentPeriod = createSelector(
  selectKPUnitState,
  (state) => state.selectedCommitmentPeriod
);

export const selectAcquiringAccountsForCommitmentPeriod = createSelector(
  selectKPUnitState,
  (state) => state.acquiringAccountsForCommitmentPeriod
);

export const selectRegistryLevelInfoList = createSelector(
  selectKPUnitState,
  (state) => state.registryLevelInfoList
);

export const selectSelectedAcquiringAccountForCommitmentPeriod = createSelector(
  selectKPUnitState,
  (state) => state.selectedAcquiringAccount
);

export const selectSelectedRegistryLevel = createSelector(
  selectKPUnitState,
  (state) => state.selectedRegistryLevel
);

export const selectSelectedQuantity = createSelector(
  selectKPUnitState,
  (state) => state.selectedQuantity
);

export const selectEnrichedTransactionSummaryReadyForSigning = createSelector(
  selectKPUnitState,
  (state) => state.enrichedTransactionSummaryReadyForSigning
);

export const selectTransactionReference = createSelector(
  selectKPUnitState,
  (state) => state.transactionReference
);

export const selectTransactionSummary = createSelector(
  selectSelectedCommitmentPeriod,
  selectSelectedRegistryLevel,
  selectSelectedQuantity,
  selectSelectedAcquiringAccountForCommitmentPeriod,
  selectEnrichedTransactionSummaryReadyForSigning,
  selectTransactionReference,
  (
    commitmentPeriod,
    registryLevelInfo,
    quantity,
    acquiringAcountInfo,
    signedSummary,
    transactionReference
  ) =>
    createTransactionSummary(
      commitmentPeriod,
      registryLevelInfo,
      quantity,
      acquiringAcountInfo,
      signedSummary.identifier,
      transactionReference
    )
);

export const selectBusinessCheckResult = createSelector(
  selectKPUnitState,
  (state) => state.businessCheckResult
);

function createTransactionSummary(
  commitmentPeriod,
  registryLevelInfo: RegistryLevelInfo,
  quantity,
  acquiringAccountInfo: AccountInfo,
  identifier: string,
  reference: string
): TransactionSummary {
  if (commitmentPeriod && registryLevelInfo && quantity) {
    return {
      identifier,
      acquiringAccountIdentifier: acquiringAccountInfo.identifier,
      type: TransactionType.IssueOfAAUsAndRMUs,
      blocks: [
        {
          applicablePeriod: commitmentPeriod,
          originalPeriod: commitmentPeriod,
          environmentalActivity: registryLevelInfo.environmentalActivity,
          quantity,
          type: registryLevelInfo.unitType,
        },
      ],
      comment: null,
      reference: reference,
    };
  }
}
