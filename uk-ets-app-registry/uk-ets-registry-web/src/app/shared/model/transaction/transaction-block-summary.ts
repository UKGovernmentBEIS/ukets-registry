import { CommitmentPeriod } from './commitment-period';
import { UnitType } from './unit-type.enum';
import {
  AcquiringAccountInfo,
  EnvironmentalActivity,
  ProjectTrack,
  ProposedTransactionType
} from '@shared/model/transaction';

export interface CommonTransactionSummary {
  type: UnitType;
  originalPeriod: CommitmentPeriod;
  applicablePeriod: CommitmentPeriod;
  subjectToSop?: boolean | null;
}

export interface TransactionBlockSummary extends CommonTransactionSummary {
  // index for selection in the UI
  index?: number;
  quantity?: string;
  availableQuantity?: number;
  environmentalActivity: EnvironmentalActivity;
  projectNumbers?: string[];
  environmentalActivities?: EnvironmentalActivity[];
  projectNumber?: string;
  projectTrack?: ProjectTrack;
  year?: number;
  cap?: number;
  issued?: number;
  remaining?: number;
}

// type needed to render table for Allowance wizard and API call
export type AllowanceTransactionBlockSummaryType = Pick<
  TransactionBlockSummary,
  'year' | 'cap' | 'issued' | 'remaining' | 'quantity' | 'type'
>;

export interface AllowanceTransactionBlockSummary
  extends AllowanceTransactionBlockSummaryType {
  type: UnitType.ALLOWANCE;
}

export interface TransactionBlockSummaryResult {
  accountId: string;
  transactionType: string;
  result: TransactionBlockSummary[];
}

export interface TransactionTypesResult {
  accountId: string;
  result: ProposedTransactionType[];
}

export interface CandidateAcquiringAccounts {
  accountId: number;
  trustedAccountsUnderTheSameHolder: AcquiringAccountInfo[];
  otherTrustedAccounts: AcquiringAccountInfo[];
  predefinedCandidateAccounts?: AcquiringAccountInfo[];
  candidateListPredefined?: boolean;
  predefinedCandidateAccountsDescription?: string;
}
