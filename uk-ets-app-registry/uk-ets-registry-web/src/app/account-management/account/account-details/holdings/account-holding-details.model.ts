import { ErrorSummary } from '@shared/error-summary';
import { UnitBlock } from '@shared/model/transaction/unit-block';

export interface AccountHoldingDetailsCriteria {
  accountId: string;
  unit: string;
  originalPeriodCode: number;
  applicablePeriodCode: number;
  subjectToSop: boolean;
}

export interface AccountHoldingDetails {
  unit: string;
  originalPeriod: string;
  applicablePeriod: string;
  results: UnitBlock[];
  errorSummary: ErrorSummary;
}
