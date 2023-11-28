import { ComplianceStatus } from '@account-shared/model';
import { CommonTransactionSummary, UnitType } from '../transaction';

export interface AccountHolding extends CommonTransactionSummary {
  availableQuantity: number;
  reservedQuantity: number;
}

export interface AccountHoldingsResult {
  totalAvailableQuantity: number;
  totalReservedQuantity: number;
  currentComplianceStatus: ComplianceStatus;
  shouldMeetEmissionsTarget: boolean;
  items: AccountHolding[];
  reservedUnitType: UnitType;
  availableUnitType: UnitType;
}
