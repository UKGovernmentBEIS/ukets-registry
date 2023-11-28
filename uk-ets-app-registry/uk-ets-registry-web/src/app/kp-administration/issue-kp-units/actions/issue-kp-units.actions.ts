import { createAction, props } from '@ngrx/store';

import {
  AccountInfo,
  BusinessCheckResult,
  CommitmentPeriod,
  RegistryLevelInfo,
  RegistryLevelInfoResults,
  RegistryLevelType,
  SignedTransactionSummary,
} from '@shared/model/transaction';

// screen 1.

/**
 * When the user specifies the commitment period then an action loads the Acquiring acounts from the server
 */
export const loadAccountsForCommitmentPeriod = createAction(
  '[Issue KP Units] Load Accounts For Commitment Period',
  props<{ commitmentPeriod: CommitmentPeriod }>()
);

export const loadAccountsForCommitmentPeriodSuccess = createAction(
  '[Issue KP Units] Load Account For Commitment Period Success',
  props<{ acquiringAccountInfoList: AccountInfo[] }>()
);

/**
 * An acquiring account selected
 */
export const selectAcquiringAccount = createAction(
  '[Issue KP Units] Select Acquiring Account',
  props<{ selectedAcquiringAccountIdentifier: number }>()
);

// Screen 2
/**
 * initiate the loading of the registry levels
 */
export const loadRegistryLevels = createAction(
  '[Issue KP Units] Load Registry Levels'
);

/**
 * fetch all registry levels for a selected commitment period . The registry level type is not control
 * by the user and is set to ISSUANCE_KYOTO_LEVEL for now
 */
export const loadRegistryLevelsForCommitmentPeriodAndType = createAction(
  '[Issue KP Units] Load Registry Levels For Commitment Period and Type',
  props<{
    commitmentPeriod: CommitmentPeriod;
    registryLevelType: RegistryLevelType;
  }>()
);

export const loadRegistryLevelsForCommitmentPeriodAndTypeSuccess = createAction(
  '[Issue KP Units] Load Registry Levels For Commitment Period and Type Success',
  props<{ registryLevelResult: RegistryLevelInfoResults }>()
);

// the validation process starts when selecting the registy level an quantity

export const selectRegistryLevelAndQuantity = createAction(
  '[Issue KP Units] Select Registry Level and Quantity',
  props<{ selectedRegistryLevel: RegistryLevelInfo; quantity: number }>()
);

export const setTransactionReference = createAction(
  '[Issue KP Units] Set Transaction Reference',
  props<{ reference: string }>()
);

// Screen check request and sign
export const submitIssuanceProposal = createAction(
  '[Issue KP Units] Start Issuance Proposal'
);

export const issuanceTransactionPropose = createAction(
  '[Issue KP Units] Propose issuance transaction',
  props<{ signedTransactionSummary: SignedTransactionSummary }>()
);

export const issuanceTransactionProposeSuccess = createAction(
  '[Issue KP Units] Propose issuance transaction success',
  props<{ businessCheckResult: BusinessCheckResult }>()
);

export const submitOtpCode = createAction(
  '[Issue KP Units] Submit otpCode',
  props<{ otpCode: string }>()
);
